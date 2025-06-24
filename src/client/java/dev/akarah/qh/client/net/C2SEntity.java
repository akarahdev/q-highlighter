package dev.akarah.qh.client.net;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.java_websocket.WebSocket;

import java.util.Arrays;

public record C2SEntity(
        WebSocket conn,
        ClientState state
) {
    public <P extends C2SPacket> void writePacket(P message) {
        var buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                Main.getRegistryAccess()
        );
        C2SPacket.STREAM_CODEC.encode(buf, message);
        Thread.startVirtualThread(() -> this.conn.send(buf.nioBuffer()));
    }

    public void handlePacket(S2CPacket message) {
        switch (message) {
            case S2CPacket.S2CGroupInfoPacket packet -> handleKnownPacket(packet);
        }
    }

    public void handleKnownPacket(S2CPacket.S2CGroupInfoPacket packet) {
        this.state().groupMembers(packet.clients());
    }
}
