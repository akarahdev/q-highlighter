package dev.akarah.qh.client.net;

import dev.akarah.qh.packets.GroupMember;
import dev.akarah.qh.util.Locked;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class ClientState {
    Locked<List<GroupMember>> groupMembers = Locked.of(new ArrayList<>());
    Locked<List<Vec3>> waypoints = Locked.of(new ArrayList<>());

    public Locked<List<GroupMember>> groupMembers() {
        return groupMembers;
    }

    public void groupMembers(List<GroupMember> groupMembers) {
        this.groupMembers.set(groupMembers);
    }

    public Locked<List<Vec3>> waypoints() {
        return this.waypoints;
    }

    public void waypoints(List<Vec3> waypoints) {
        this.waypoints.set(waypoints);
    }
}
