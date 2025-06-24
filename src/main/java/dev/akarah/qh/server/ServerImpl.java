package dev.akarah.qh.server;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.akarah.qh.Util;
import dev.akarah.qh.packets.C2SMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ServerImpl extends WebSocketServer {
    int idIndex = 0;
    ServerState state;

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
        var entity = new S2CEntity(conn, this.state);
        var data = entity.clientData();
        if(data == null) {
            return;
        }
        this.state.removeFromGroup(data.groupName(), data.uuid());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn.getAttachment() + " >> " + message);
        try {
            var json = JsonParser.parseString(message);
            System.out.println(json);
            var packet = C2SMessage.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
            System.out.println(packet);
            new S2CEntity(conn, this.state).handlePacket(packet);
        } catch (Exception ignored) {

        }
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
                .map(x -> new S2CEntity(x, this.state))
                .toList();
    }
}
