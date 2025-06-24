package dev.akarah.qh.server;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;

public record S2CEntity(
        WebSocket conn,
        ServerState server
) {
    public <P extends S2CPacket> void writePacket(P message) {
        var buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                Main.getRegistryAccess()
        );
        S2CPacket.STREAM_CODEC.encode(buf, message);
        Thread.startVirtualThread(() -> this.conn.send(buf.nioBuffer()));
    }

    public void clientData(C2SPacket.C2SClientDataPacket packet) {
        conn.setAttachment(packet);
    }

    public C2SPacket.C2SClientDataPacket clientData() {
        return conn.getAttachment();
    }

    public void handlePacket(C2SPacket message) {
        switch (message) {
            case C2SPacket.C2SClientDataPacket clientDataPacket -> {
                this.clientData(clientDataPacket);
                this.server().insertIntoGroup(clientDataPacket.groupName(), clientDataPacket.uuid());
                this.server().updateAllGroupInfo();
            }
            case C2SPacket.RequestWaypoint requestWaypoint -> {
                for(var entity : server().server.entities()) {
                    entity.writePacket(
                            new S2CPacket.RegisterWaypointPacket(this.clientData().username(), requestWaypoint.waypoint())
                    );
                }
            }
            case C2SPacket.RequestMessage requestMessage -> {
                for(var entity : server().server.entities()) {
                    entity.writePacket(
                            new S2CPacket.ChatMessagePacket(this.clientData().username(), requestMessage.message())
                    );
                }
            }
        }
    }
}
