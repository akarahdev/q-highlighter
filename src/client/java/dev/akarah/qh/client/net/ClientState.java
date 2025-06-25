package dev.akarah.qh.client.net;

import dev.akarah.qh.util.Locked;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientState {
    List<UUID> groupMembers = new ArrayList<>();
    Locked<List<Vec3>> waypoints = Locked.of(new ArrayList<>());

    public List<UUID> groupMembers() {
        return groupMembers;
    }

    public void groupMembers(List<UUID> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Locked<List<Vec3>> waypoints() {
        return this.waypoints;
    }

    public void waypoints(List<Vec3> waypoints) {
        this.waypoints.set(waypoints);
    }
}
