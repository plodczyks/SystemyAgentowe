package Agents;

import Helpers.Point;
import Animation.Sender;
import Animation.Utilities;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.net.URISyntaxException;


//from location - 38.181435, 15.592607
//to location - 38.227809, 15.616367


public class ShipAgent extends Agent {
    //to correct
    private String ferryName="Ferry1";

    //road parameters
    private Point fromLocation=new Point(38.181435, 15.592607);
    private Point toLocation=new Point(38.227809, 15.616367);

    //strait order request parameters
    private int wakeTime;
    private int reserveTime=5;
    private int beforeTime=10;

    //for animation
    private Sender animationSender;


    protected void setup(){
        try {
            animationSender= Utilities.getConnectedSender();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object[] args=getArguments();
       // fromLocation=new Point(Double.parseDouble((String)args[0]),Double.parseDouble((String)args[1]));
       // toLocation=new Point(Double.parseDouble((String)args[2]),Double.parseDouble((String)args[3]));
        wakeTime=Integer.parseInt((String)args[0]);
       // reserveTime=Integer.parseInt((String)args[5]);
       // beforeTime=Integer.parseInt((String)args[6]);

        addSendStraitOrderBehaviour();
    }

    //region Strait Order Request

    private void addSendStraitOrderBehaviour(){
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

    //endregion
}
