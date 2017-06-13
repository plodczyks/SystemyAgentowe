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

    public static Sender getConnectedSender() throws URISyntaxException, InterruptedException {
        Sender sender = new Sender(new URI("ws://localhost:8888"));
        sender.connect();
        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            Thread.sleep(100);
            //System.out.println("not connected");
        }
        return sender;
    }


    public static void startSimulationFerry(Sender sender,int shoreNr,int roadTime) throws URISyntaxException, InterruptedException {
        if (1 == shoreNr)
            sender.sendMessage((new Message(coast1Left, coast1Right, IconType.FERRY,roadTime)).toString());
        else
            sender.sendMessage((new Message(coast1Right, coast1Left, IconType.FERRY,roadTime)).toString());
    }
	
    public static void startSimulationTruck(Sender sender,Point from, Point to,int roadTime) throws URISyntaxException, InterruptedException {
//        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
//            Thread.sleep(500);
//            System.out.println("not connected");
//        }
        sender.sendMessage((new Message(from, to, IconType.TRUCK,roadTime)).toString());
    }

    public static void startSimulationShip(Sender sender,Point from, Point to,int roadTime) throws URISyntaxException, InterruptedException {
//        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
//            Thread.sleep(500);
//            System.out.println("not connected");
//        }
        sender.sendMessage((new Message(from, to, IconType.SHIP,roadTime)).toString());
    }
	
    public static void addWarehouseMarker(Sender sender,Point Location) throws URISyntaxException, InterruptedException {
//        while (sender.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
//            Thread.sleep(500);
//            System.out.println("not connected");
//        }
        sender.sendMessage(new Message(Location, null, IconType.WAREHOUSE,0).toString());
    }
}
