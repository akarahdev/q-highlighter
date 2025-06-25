package dev.akarah.qh.server;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.GroupMember;
import dev.akarah.qh.packets.S2CPacket;
import dev.akarah.qh.util.Util;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.java_websocket.WebSocket;

import java.util.Optional;

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
        try {
            this.conn.send(buf.nioBuffer());
        } catch (Exception ignored) {

        }
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
                if(clientDataPacket.protocolVersion() != Util.PROTOCOL_VERSION) {
                    this.conn().close();
                }
                this.clientData(clientDataPacket);
                this.server().insertIntoGroup(clientDataPacket.groupName(), clientDataPacket.memberData());
                this.server().updateAllGroupInfo();
            }
            case C2SPacket.RequestWaypoint requestWaypoint -> {
                for(var entity : server().server.entities()) {
                    entity.writePacket(
                            new S2CPacket.RegisterWaypointPacket(this.clientData().memberData().username(), requestWaypoint.waypoint())
                    );
                }
            }
            case C2SPacket.RequestMessage requestMessage -> {
                for(var entity : server().server.entities()) {
                    entity.writePacket(
                            new S2CPacket.ChatMessagePacket(this.clientData().memberData().username(), requestMessage.message())
                    );
                }
            }
            case C2SPacket.UpdateStatus updateStatus -> {
                var newDataPacket = new C2SPacket.C2SClientDataPacket(
                        new GroupMember(
                                this.clientData().memberData().username(),
                                this.clientData().memberData().uuid(),
                                Optional.of(updateStatus.status())
                        ),
                        this.clientData().groupName(),
                        this.clientData().protocolVersion()
                );
                this.server().removeFromGroup(newDataPacket.groupName(), this.clientData().memberData());
                this.server().insertIntoGroup(newDataPacket.groupName(), newDataPacket.memberData());
                this.clientData(newDataPacket);
                this.server().updateAllGroupInfo();
            }
        }
    }
}
