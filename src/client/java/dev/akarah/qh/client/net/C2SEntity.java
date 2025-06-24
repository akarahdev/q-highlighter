package dev.akarah.qh.client.net;

import com.mojang.serialization.JsonOps;
import dev.akarah.qh.packets.C2SMessage;
import dev.akarah.qh.packets.S2CMessage;
import dev.akarah.qh.packets.s2c.S2CGroupInfoPacket;
import io.netty.buffer.Unpooled;
import org.java_websocket.WebSocket;

public record C2SEntity(
        WebSocket conn,
        ClientState state
) {
    public void writePacket(C2SMessage message) {
        var buf = Unpooled.buffer();
        C2SMessage.STREAM_CODEC.encode(buf, message);
        Thread.startVirtualThread(() -> this.conn.send(buf.nioBuffer()));
    }

    public void handlePacket(S2CMessage message) {
        message.groupInfo().ifPresent(this::handleGroupInfo);
    }

    public void handleGroupInfo(S2CGroupInfoPacket packet) {
        this.state().groupMembers(packet.clients());
    }
}
