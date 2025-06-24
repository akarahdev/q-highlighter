package dev.akarah.qh.client.net;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.akarah.qh.Util;
import dev.akarah.qh.packets.C2SMessage;
import dev.akarah.qh.packets.S2CMessage;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import dev.akarah.qh.server.S2CEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class ClientImpl extends WebSocketClient {
    ClientState clientState = new ClientState();
    C2SEntity entity = new C2SEntity(this, this.clientState);
    String groupName;

    public ClientImpl(URI serverUri, String groupName) {
        super(serverUri);
        this.groupName = groupName;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened!");

        var player = Minecraft.getInstance().player;
        if(player == null) {
            this.close();
            return;
        }
        var packet = C2SMessage.of(
                new C2SClientDataPacket(
                        player.getGameProfile().getName(),
                        this.groupName,
                        player.getGameProfile().getId(),
                        Util.PROTOCOL_VERSION
                )
        );
        this.entity.writePacket(packet);
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        var byteBuf = Unpooled.wrappedBuffer(buffer);
        var packet = S2CMessage.STREAM_CODEC.decode(byteBuf);
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
