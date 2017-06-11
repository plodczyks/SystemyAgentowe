package Agents;

import java.util.LinkedList;

import Helpers.Point;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;


public class TransportVehicleAgent extends Agent {
	
	//road parameters
	//public LinkedList<Helpers.Point> allPositions;
	private int positionIndex;
	private Point location;
	private Point coastLocation;
	private int roadTime;
	
	//to correct
	private String ferryName="Ferry1";	
	
	protected void setup(){
		
		Object[] args=getArguments();
		location=new Point((double)args[0],(double)args[1]);
		coastLocation=new Point((double)args[2],(double)args[3]);
		roadTime=(int)args[4];
		
		positionIndex=0;
		
		addBehaviour(new TickerBehaviour(this,1000){
			@Override
			public void onTick() {
				if(positionIndex==roadTime){
					removeBehaviour(this);
				}
				else{
					HandleTimeElapsed();
				}
			}
		});
	}
	
	private void HandleTimeElapsed(){
		positionIndex++;
		if(positionIndex<roadTime){
			//System.out.println(getAID().getName() +": My location is "+allPositions.get(positionIndex));
		}
		else{
			AID receiver=new AID(ferryName,AID.ISLOCALNAME);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("Vehicle at coast");
			String content= "Coast location: "+coastLocation.Latitude+","+coastLocation.Longitude+"\n"+
							"Vehicle ready for trip";
			msg.setContent(content);
			msg.addReceiver(receiver);
			System.out.println(getAID().getName() +": Send Vehicles At Coast Inform to "+receiver.getLocalName());
			send(msg);
		}
	}	
}
