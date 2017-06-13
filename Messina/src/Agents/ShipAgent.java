package Agents;

import Helpers.Point;
import Helpers.Sender;
import Helpers.Utilities;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.net.URISyntaxException;


//from location - 38.181435, 15.592607
//to location - 38.227809, 15.616367


public class ShipAgent extends Agent {

    //road parameters
    private Point fromLocation;
    private Point toLocation;

    //request parameters
    private int wakeTime=19;
    private int reserveTime=5;
    private int beforeTime=10;

    //to correct
    String ferryName="Ferry1";

    //for animation
    Sender animationSender;


    protected void setup(){
        try {
            animationSender= Utilities.getConnectedSender();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object[] args=getArguments();
        fromLocation=new Point(Double.parseDouble((String)args[0]),Double.parseDouble((String)args[1]));
        toLocation=new Point(Double.parseDouble((String)args[2]),Double.parseDouble((String)args[3]));
        wakeTime=Integer.parseInt((String)args[4]);
       // reserveTime=Integer.parseInt((String)args[5]);
       // beforeTime=Integer.parseInt((String)args[6]);

        addBehaviour(new WakerBehaviour(this,wakeTime*1000){
            private static final long serialVersionUID = 1L;
            @Override
            public void onWake() {
                SendStraitRequest();
            }
        });
    }

    public void SendStraitRequest() {

        AID receiver = new AID(ferryName, AID.ISLOCALNAME);
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId("Strait Order");
        String content =
                "Time to get the strait: " + beforeTime + "\n" +
                        "Reservation time: " + reserveTime;
        msg.setContent(content);
        msg.addReceiver(receiver);
        System.out.println(getAID().getName() + ": Send Strait Order Request to " + receiver.getLocalName());
        send(msg);
        try {
            Utilities.startSimulationShip(animationSender,fromLocation, toLocation, beforeTime + reserveTime);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void HandleResponseForVehiclesOrder(ACLMessage msg){
//
//        String[] description=msg.getContent().split("\n");
//        int handledVehicleCount=Integer.parseInt((description[0].split(":")[1]).trim());
//        int timeToStart=Integer.parseInt((description[1].split(":")[1]).trim());
//
//        vehicleCount-=handledVehicleCount;
//        if(handledVehicleCount>0){
//            addBehaviour(new WakerBehaviour(this,timeToStart*1000){
//                private static final long serialVersionUID = 1L;
//                @Override
//                protected void onWake() {
//                    for(int i=0;i<handledVehicleCount;i++){
//                        ContainerController cc = getContainerController();
//                        AgentController ac;
//                        try {
//                            ac = cc.createNewAgent(getLocalName()+"TransportVehicle"+transportVehicleIndex, "Agents.TransportVehicleAgent",
//                                    new Object[]{location.lat,location.lng,coastLocation.lat,coastLocation.lng,roadTime});
//                            ac.start();
//                        } catch (StaleProxyException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        transportVehicleIndex++;
//                    }
//                }
//            });
//        }
//        System.out.println(getAID().getName() +": Handle Vehicles Order Response from "+msg.getSender().getLocalName());
//    }
}
