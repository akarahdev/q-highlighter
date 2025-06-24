package dev.akarah.qh.client.net;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientState {
    List<UUID> groupMembers = new ArrayList<>();
    List<Vec3> waypoints = new ArrayList<>();

    public List<UUID> groupMembers() {
        return groupMembers;
    }

    public void groupMembers(List<UUID> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<Vec3> waypoints() {
        return this.waypoints;
    }

    public void waypoints(List<Vec3> waypoints) {
        this.waypoints = waypoints;
    }
}
