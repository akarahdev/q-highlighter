package dev.akarah.qh.client.net;

import dev.akarah.qh.Main;
import dev.akarah.qh.packets.GroupMember;
import dev.akarah.qh.packets.MemberStatus;
import dev.akarah.qh.util.Util;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public class ClientImpl extends WebSocketClient {
    ClientState clientState = new ClientState();
    C2SEntity entity = new C2SEntity(this, this.clientState);
    String groupName;

    public C2SEntity entity() {
        return entity;
    }

    public ClientImpl(URI serverUri, String groupName) {
        super(serverUri);
        this.groupName = groupName;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened!");

        var player = Minecraft.getInstance().player;
        if (player == null) {
            this.close();
            return;
        }
        this.entity.writePacket(new C2SPacket.C2SClientDataPacket(
                new GroupMember(
                        player.getGameProfile().getName(),
                        player.getGameProfile().getId(),
                        Optional.empty()
                ),
                this.groupName,
                Util.PROTOCOL_VERSION
        ));
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        var byteBuf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(buffer),
                Main.getRegistryAccess()
        );
        var packet = S2CPacket.STREAM_CODEC.decode(byteBuf);
        this.entity.handlePacket(packet);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        System.out.println(ex);
        assert Minecraft.getInstance()
                .player != null;
        Minecraft.getInstance()
                .player
                .displayClientMessage(Component.literal(ex.getMessage()), false);
    }

    public ClientState state() {
        return this.clientState;
    }
}
