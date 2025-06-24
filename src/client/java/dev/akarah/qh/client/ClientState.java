package dev.akarah.qh.client;

import java.util.List;
import java.util.UUID;

public class ClientState {
    List<UUID> groupMembers;

    public List<UUID> groupMembers() {
        return groupMembers;
    }

    public void groupMembers(List<UUID> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
