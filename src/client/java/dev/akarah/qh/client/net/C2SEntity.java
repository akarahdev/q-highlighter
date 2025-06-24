package dev.akarah.qh.client.net;

import com.mojang.serialization.JsonOps;
import dev.akarah.qh.packets.C2SMessage;
import dev.akarah.qh.packets.S2CMessage;
import dev.akarah.qh.packets.s2c.S2CGroupInfoPacket;
import org.java_websocket.WebSocket;

public record C2SEntity(
        WebSocket conn,
        ClientState state
) {
    public void writePacket(C2SMessage message) {
        var json = C2SMessage.CODEC.encodeStart(JsonOps.INSTANCE, message).getOrThrow();
        Thread.startVirtualThread(() -> this.conn.send(json.toString()));
    }

    public void handlePacket(S2CMessage message) {
        message.groupInfo().ifPresent(this::handleGroupInfo);
    }

    public void handleGroupInfo(S2CGroupInfoPacket packet) {
        this.state().groupMembers(packet.clients());
    }
}
