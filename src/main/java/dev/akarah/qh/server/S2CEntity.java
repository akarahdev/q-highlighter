package dev.akarah.qh.server;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import dev.akarah.qh.sim.VirtualAccess;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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
            case C2SPacket.C2SClientDataPacket c2SClientDataPacket -> handleKnownPacket(c2SClientDataPacket);
        }
    }

    public void handleKnownPacket(C2SPacket.C2SClientDataPacket packet) {
        this.clientData(packet);
        this.server().insertIntoGroup(packet.groupName(), packet.uuid());
        this.server().updateAllGroupInfo();
    }
}
