package dev.akarah.qh.client.net;

import dev.akarah.qh.Main;
import dev.akarah.qh.client.ClientUtil;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
        try {
            this.conn.send(buf.nioBuffer());
        } catch (Exception ignored) {

        }
    }

    public void handlePacket(S2CPacket message) {
        switch (message) {
            case S2CPacket.GroupInfoPacket groupInfoPacket -> {
                this.state().groupMembers(groupInfoPacket.clients());
            }
            case S2CPacket.RegisterWaypointPacket registerWaypointPacket -> {
                this.state().waypoints().with(x -> x.add(registerWaypointPacket.waypoint()));

                ClientUtil.localPlayer().ifPresent(localPlayer -> {
                    localPlayer.displayClientMessage(Component.literal(registerWaypointPacket.registrar() + " registered waypoint at " + registerWaypointPacket.waypoint()), false);
                    localPlayer.level().playSound(
                            localPlayer,
                            new BlockPos(
                                    (int) localPlayer.position().x,
                                    (int) localPlayer.position().y,
                                    (int) localPlayer.position().z
                            ),
                            SoundType.AMETHYST.getBreakSound(),
                            SoundSource.MASTER
                    );
                });
            }
            case S2CPacket.ChatMessagePacket chatMessagePacket -> {
                ClientUtil.localPlayer().ifPresent(localPlayer -> {
                    localPlayer.displayClientMessage(
                            Component.empty()
                                    .append(Component.literal(chatMessagePacket.username()))
                                    .append(Component.literal(" >> "))
                                    .append(Component.literal(chatMessagePacket.message())), false);
                });
            }
        }
    }
}
