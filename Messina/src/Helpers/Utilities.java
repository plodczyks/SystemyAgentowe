package Helpers;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by asd on 12.06.2017.
 */
public class Utilities {
    public static final Point coast1Left = new Point(38.210362, 15.561726);
    //public static final Point coast2Left = new Point(38.188213, 15.562708);
    public static final Point coast1Right = new Point(38.222881, 15.632940);
    //public static final Point coast2Right = new Point(38.221144, 15.632822);

    public static void startSimulationFerry(int shoreNr,int roadTime) throws URISyntaxException, InterruptedException {
        Sender sender = new Sender(new URI("ws://localhost:8888"));
        sender.connect();
        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            Thread.sleep(500);
            System.out.println("not connected");
        }
        if (1== shoreNr)
            sender.sendMessage((new Message(coast1Left, coast1Right, VehicleType.FERRY,roadTime)).toString());
        else
            sender.sendMessage((new Message(coast1Right, coast1Left, VehicleType.FERRY,roadTime).toString()));
    }
    public static void startSimulationTruck(Point from, Point to,int roadTime) throws URISyntaxException, InterruptedException {
        Sender sender = new Sender(new URI("ws://localhost:8888"));
        sender.connect();
        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            Thread.sleep(500);
            System.out.println("not connected");
        }
        sender.sendMessage((new Message(from, to, VehicleType.TRUCK,roadTime)).toString());
    }
}
