package Animation;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Sender extends WebSocketClient {

    public Sender(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.print("Opened");
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Closed for some reason");
    }

    @Override
    public void onError(Exception e) {

    }
    public void sendMessage(String message){
        send(message);
    }
}
