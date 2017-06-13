package Agents;

import Helpers.Point;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;


public class TransportVehicleAgent extends Agent {

    //to correct
    private String ferryName="Ferry1";

    //road parameters
	private Point location;
	private Point coastLocation;
	private int roadTime;
	private int positionIndex;
	
	protected void setup() {

        Object[] args = getArguments();
        location = new Point((double) args[0], (double) args[1]);
        coastLocation = new Point((double) args[2], (double) args[3]);
        roadTime = (int) args[4];
        positionIndex = 0;

        addTickerBahviour();
    }

    //region ticker handling

    private void addTickerBahviour() {
        addBehaviour(new TickerBehaviour(this, 1000) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onTick() {
                positionIndex++;
                if (positionIndex > roadTime) removeBehaviour(this);
                else HandleTickTime();
            }
        });
    }

    private void HandleTickTime() {
        if(positionIndex==roadTime){
            AID receiver=new AID(ferryName,AID.ISLOCALNAME);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setConversationId("Vehicle at coast");
            String content= "Coast location: "+coastLocation.lat+","+coastLocation.lng+"\n"+
                    "Vehicle ready for trip";
            msg.setContent(content);
            msg.addReceiver(receiver);
            System.out.println(getAID().getName() +": Send Vehicles At Coast Inform to "+receiver.getLocalName());
            send(msg);
        }
    }
    //endregion
}
