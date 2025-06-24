package dev.akarah.qh.client.net;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import org.java_websocket.WebSocket;

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
        this.conn.send(buf.nioBuffer());
    }

    public void handlePacket(S2CPacket message) {
        switch (message) {
            case S2CPacket.GroupInfoPacket groupInfoPacket -> handleKnownPacket(groupInfoPacket);
            case S2CPacket.RegisterWaypointPacket registerWaypointPacket -> handleKnownPacket(registerWaypointPacket);
        }
    }

    public void handleKnownPacket(S2CPacket.GroupInfoPacket packet) {
        this.state().groupMembers(packet.clients());
    }

    public void handleKnownPacket(S2CPacket.RegisterWaypointPacket packet) {
        synchronized (this.state.waypoints()) {
            this.state().waypoints().add(packet.waypoint());
        }
        if(Minecraft.getInstance().player == null) {
            return;
        }
        Minecraft.getInstance().player.displayClientMessage(Component.literal(packet.registrar() + " registered waypoint at " + packet.waypoint()), false);
        assert Minecraft.getInstance().level != null;
        Minecraft.getInstance().level.playSound(
                Minecraft.getInstance().player,
                new BlockPos(
                        (int) Minecraft.getInstance().player.position().x,
                        (int) Minecraft.getInstance().player.position().y,
                        (int) Minecraft.getInstance().player.position().z
                ),
                SoundType.AMETHYST.getBreakSound(),
                SoundSource.MASTER
        );

    }
}
