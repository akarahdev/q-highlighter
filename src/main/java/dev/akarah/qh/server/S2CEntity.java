package dev.akarah.qh.server;

import com.mojang.serialization.JsonOps;
import dev.akarah.qh.packets.C2SMessage;
import dev.akarah.qh.packets.S2CMessage;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import org.java_websocket.WebSocket;

public record S2CEntity(
        WebSocket conn,
        ServerState server
) {
    public void write(S2CMessage message) {
        System.out.println(this.conn.getAttachment() + " << " + message);
        var json = S2CMessage.CODEC.encodeStart(JsonOps.INSTANCE, message).getOrThrow();
        Thread.startVirtualThread(() -> this.conn.send(json.toString()));
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
