import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.MobilityOntology;
//import jade.core.messaging.*;
import jade.lang.acl.*;
import java.util.LinkedList;

public class EMAAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	private int computerCount;
	private int teamCount;
	private int lapCount;
	
	private int messageHandledCount;		
	private Date startDate;

	protected void setup(){
		
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage. FIPA_SL0);
		getContentManager().registerOntology(MobilityOntology.getInstance());
		
		Object[] args=getArguments();
		computerCount= Integer.parseInt((String)(args[0]));
		teamCount= Integer.parseInt((String)(args[1]));
		lapCount= Integer.parseInt((String)(args[2]));

		messageHandledCount=0;
		
		addBehaviourAskLocation();
	}
	
	private void addBehaviourAskLocation(){
		addBehaviour(new WakerBehaviour(this,40000) {
			
			private static final long serialVersionUID = 1L;

			protected void onWake() {	
				
				sendLocationsQuery();
				
				ACLMessage response=blockingReceive();
				LinkedList<Location> locations=handleLocationsResponse(response);
				sendLocationsToRunners(locations);
				addBehaviourStartRace();
			}
		});
	}

	private void sendLocationsQuery(){
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(getAMS());
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		request.setOntology(MobilityOntology.NAME);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
						
		// creates the content of the ACLMessage
		Action act =new Action();
		act.setActor(getAMS());
		QueryPlatformLocationsAction action =new QueryPlatformLocationsAction();
		act.setAction(action);
		try{
			getContentManager().fillContent(request, act);
		} 
		catch(CodecException e) {e.printStackTrace();return;} 
		catch(OntologyException e) {e.printStackTrace();return;}
		send(request);
	}
	
	private LinkedList<Location> handleLocationsResponse(ACLMessage response){
		LinkedList<Location> locations=new LinkedList<Location>();
		Result results = null;
		try{
			results = (Result)getContentManager().extractContent(response);
		}
		catch(UngroundedException e) {e.printStackTrace();return null;} 
		catch(CodecException e) {e.printStackTrace();return null;} 
		catch(OntologyException e) {e.printStackTrace();return null;}
		Iterator it = results.getItems().iterator();

		while(it.hasNext()){
			Location location=(Location) it.next();
			if(!location.getName().contains("Main-Container"))
				locations.add(location);
		}
		locations.sort(new Comparator<Location>() {
	         @Override
	         public int compare(Location o1, Location o2) {
	             return o1.getName().compareTo(o2.getName());
	         }
		});
		return locations;
	}
	
	private void sendLocationsToRunners(LinkedList<Location> locations){
		try {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setContentObject(locations);
	        for(int i=1;i<=teamCount;i++){
	        	for(int j=1;j<=computerCount+1;j++){
	        		msg.addReceiver(new AID("Runner"+j+"Team"+i,AID.ISLOCALNAME));	
	        	}					        	
	        }
			send(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addBehaviourStartRace(){
		addBehaviour(new WakerBehaviour(this,2000) {

			private static final long serialVersionUID = 1L;

			public void onWake() {				
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("Go! Step: 0");
		        for(int i=1;i<=teamCount;i++){
					msg.addReceiver(new AID("Runner1Team"+i,AID.ISLOCALNAME));		        	
		        }
				send(msg);
		        startDate=new Date();
		        addBehaviourHandleResponses();
			}
		});
	}
	
	private void addBehaviourHandleResponses(){
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			public void action() {
				ACLMessage rcv = receive();
				if(rcv !=null) {
					if(rcv.getContent().contains("Ok")){return;}
					messageHandledCount++;
					System.out.println(getAID().getName() +": Message handled.");
					if(messageHandledCount==teamCount){
						Date endDate=new Date();
						long elapsedTime=endDate.getTime()-startDate.getTime();
						System.out.println(getAID().getName() +": Statistics: ");
						System.out.println(getAID().getName() +": Start date: "+ startDate);
						System.out.println(getAID().getName() +": End date: " + endDate);
						System.out.println(getAID().getName() +": Elapsed time: " + elapsedTime);
						removeBehaviour(this);
					}
				}
				else {
					block();
				}
			}
		});
	}
		
	protected void takeDown(){
		super.takeDown();
	}
}
