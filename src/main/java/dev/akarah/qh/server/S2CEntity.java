package dev.akarah.qh.server;

import com.mojang.serialization.JsonOps;
import dev.akarah.qh.packets.C2SMessage;
import dev.akarah.qh.packets.S2CMessage;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import io.netty.buffer.Unpooled;
import org.java_websocket.WebSocket;

public record S2CEntity(
        WebSocket conn,
        ServerState server
) {
    public void write(S2CMessage message) {
        var buf = Unpooled.buffer();
        S2CMessage.STREAM_CODEC.encode(buf, message);
        Thread.startVirtualThread(() -> this.conn.send(buf.nioBuffer()));
    }

    public void clientData(C2SClientDataPacket packet) {
        conn.setAttachment(packet);
    }

    public C2SClientDataPacket clientData() {
        return conn.getAttachment();
    }

    public void handlePacket(C2SMessage message) {
        message.clientData().ifPresent(this::handleGroupInfo);
    }

    public void handleGroupInfo(C2SClientDataPacket packet) {
        this.clientData(packet);
        this.server().insertIntoGroup(packet.groupName(), packet.uuid());
        this.server().updateAllGroupInfo();
    }
}
