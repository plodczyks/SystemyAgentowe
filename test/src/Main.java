import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        Sender sender = new Sender(new URI("ws://localhost:8888"));
        sender.connect();
        while(sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED){Thread.sleep(500);}

        List<Message> messages = new LinkedList<>();
        messages.add(new Message(38.210590, 15.716753,38.221341, 15.633189));
        messages.add(new Message(38.195476, 15.633623, 38.221341, 15.633189));
        messages.add(new Message(38.234058, 15.661057,38.221341, 15.633189));
        messages.add(new Message(38.182127, 15.545100,38.187794, 15.561236));

        for(Message msg : messages)
        {
            //Thread.sleep(5000);
            sender.sendMessage(msg.toString());
        }
    }
}
