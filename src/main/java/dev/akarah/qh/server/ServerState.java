package dev.akarah.qh.server;

import dev.akarah.qh.packets.S2CPacket;

import java.util.*;
import java.util.stream.Stream;

public class ServerState {
    public ServerState(ServerImpl server) {
        this.server = server;
    }

    ServerImpl server;
    Map<String, Set<UUID>> groups = new HashMap<>();

    public Set<UUID> getGroup(String name) {
        if (!groups.containsKey(name)) {
            setGroup(name, new HashSet<>());
        }
        return groups.get(name);
    }

    public void insertIntoGroup(String name, UUID uuid) {
        getGroup(name).add(uuid);
    }

    public void removeFromGroup(String name, UUID uuid) {
        getGroup(name).remove(uuid);
        if (getGroup(name).isEmpty()) {
            this.groups.remove(name);
        }
    }

    public void setGroup(String name, Set<UUID> uuids) {
        this.groups.put(name, uuids);
    }

    public void updateAllGroupInfo() {
        for (var group : this.groups.keySet()) {
            updateGroupInfoForGroup(group);
        }
    }

    public void updateGroupInfoForGroup(String name) {
        var entities = this.server.entities();
        var groupUuids = this.server.entities()
                .stream()
                .flatMap(x -> {
                    if (x.clientData() == null) {
                        System.out.println("Skipping client " + x.conn());
                        return Stream.empty();
                    }
                    if (!x.clientData().groupName().equals(name)) {
                        return Stream.empty();
                    }
                    return Stream.of(x.clientData().uuid());
                })
                .toList();
        for (var entity : entities) {
            if (entity.clientData() == null) {
                continue;
            }

            var data = entity.clientData();
            if (groupUuids.contains(data.uuid())) {
                entity.writePacket(new S2CPacket.GroupInfoPacket(groupUuids));
            }
        }
    }
}
