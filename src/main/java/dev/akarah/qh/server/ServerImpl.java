package dev.akarah.qh.server;

import dev.akarah.qh.Main;
import dev.akarah.qh.util.Util;
import dev.akarah.qh.packets.C2SPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

public class ServerImpl extends WebSocketServer {
    int idIndex = 0;
    ServerState state;

    public S2CEntity createEntity(WebSocket conn) {
        return new S2CEntity(conn, this.state);
    }

    public ServerImpl() throws UnknownHostException {
        super(new InetSocketAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}), Util.PORT));
        this.state = new ServerState(this);
    }

    public ServerState state() {
        return this.state;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(idIndex);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        var entity = createEntity(conn);
        var data = entity.clientData();
        if (data == null) {
            return;
        }
        this.state.removeFromGroup(data.groupName(), data.uuid());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buffer) {
        var byteBuf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(buffer),
                Main.getRegistryAccess()
        );
        var packet = C2SPacket.STREAM_CODEC.decode(byteBuf);
        this.createEntity(conn).handlePacket(packet);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    public List<S2CEntity> entities() {
        return this.getConnections()
                .stream()
                .map(this::createEntity)
                .toList();
    }
}
