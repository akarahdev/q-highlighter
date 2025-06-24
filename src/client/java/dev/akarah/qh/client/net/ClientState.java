package dev.akarah.qh.client.net;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientState {
    List<UUID> groupMembers = new ArrayList<>();

    public List<UUID> groupMembers() {
        return groupMembers;
    }

    public void groupMembers(List<UUID> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
