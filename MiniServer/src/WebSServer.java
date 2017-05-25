import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WebSServer extends WebSocketServer {

    Set<WebSocket> connections;
    WebSocket jscript;
    int noOfConnections;
    private static final int TCP_PORT = 8888;

    public WebSServer(){
        super(new InetSocketAddress("localhost",TCP_PORT));
        connections = new HashSet<>();
        noOfConnections = 0;
        System.out.println(this.getAddress().toString());
    }
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake){
        connections.add(connection);
        noOfConnections++;
        if(noOfConnections==1) jscript = connection;
        System.out.println("Opened:" + connection.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections.remove(webSocket);
        noOfConnections--;
        System.out.println("Closed:" + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Message:" + s + " " + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
        if(jscript.getReadyState()== WebSocket.READYSTATE.OPEN)
            jscript.send(s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Error:" + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }
}
