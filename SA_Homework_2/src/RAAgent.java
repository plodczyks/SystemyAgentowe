import java.util.LinkedList;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.*;

public class RAAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	private int runnerIndex;
	private int teamIndex;
	private int computerCount;
	private int lapCount;

	private LinkedList<Location> locations;
	private int locationIndex;
	
	private int actualMoveIndex;
	
	protected void setup(){
		
		Object[] args=getArguments();
		
		runnerIndex= Integer.parseInt((String)(args[0]));
		teamIndex= Integer.parseInt((String)(args[1]));
		computerCount= Integer.parseInt((String)(args[2]));
		lapCount= Integer.parseInt((String)(args[3]));
	
		ACLMessage response=blockingReceive();
		try {
			locations=(LinkedList<Location>)response.getContentObject();
		} catch (UnreadableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		locationIndex=((runnerIndex-1)%computerCount);
		
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage rcv = receive();		
				if(rcv !=null) {
					if(rcv.getContent().contains("Ok")){return;}
					actualMoveIndex=Integer.parseInt(rcv.getContent().split(":")[1].trim());
					ACLMessage request = rcv.createReply();
					request.setPerformative(ACLMessage.INFORM);
					request.setContent("Ok");
					send(request);
					
					locationIndex=(locationIndex+1)%computerCount;
					Location location=locations.get(locationIndex);
					doMove(location);	
				}
				else {
					block();
				}
			}
		});
	}
	
	@Override
	protected void afterMove() {
		actualMoveIndex++;
		if(actualMoveIndex==lapCount*computerCount){
			ACLMessage request2 = new ACLMessage(ACLMessage.INFORM);
			request2.addReceiver(new AID("ExperimentMasterAgent",AID.ISLOCALNAME));
			request2.setContent("Team ends running");
			send(request2);
		}
		else{
			ACLMessage request = new ACLMessage(ACLMessage.INFORM);
			request.addReceiver(new AID("Runner"+(1+(runnerIndex%(computerCount+1)))+"Team"+teamIndex,AID.ISLOCALNAME));
			request.setContent("Go! Step: "+actualMoveIndex);
			send(request);
		}
	}
	
	
	protected void takeDown(){
		super.takeDown();
	}
}
