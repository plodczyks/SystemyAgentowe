import java.util.Iterator;
import java.util.Random;

import jade.content.lang.Codec.*;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.content.onto.basic.Result;
import jade.core.*;
import jade.core.behaviours.*;
import jade.core.messaging.*;
import jade.domain.*;
import jade.domain.JADEAgentManagement.*;
import jade.domain.mobility.*;
import jade.lang.acl.*;
import jade.wrapper.ControllerException;

public class TruckAgent3 extends Agent {

	public static final long serialVersionUID = 1L;
	public AID workTopic;
	public MessageTemplate managerTemplate;
	public MessageTemplate amsTemplate;
	public Random random;

	protected void setup(){
		
		baseInitialize();
		
		random=new Random();
		Behaviour b = new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			public void action() {		
				ACLMessage rcv = receive(managerTemplate);
				if(rcv !=null) {
					if(random.nextInt(100)>15) {
						System.out.println(getAID().getName() +": I don't want to move to Manager.");
						return;
					}
					System.out.println(getAID().getName() +": I want to move to Manager.");
					try {
						System.out.println(getAID().getName() +": My actual location is: " + getContainerController().getContainerName());
					} catch (ControllerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
					request.addReceiver(getAMS());
					request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					request.setOntology(MobilityOntology.NAME);
					request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					
					// creates the content of the ACLMessage
					Action act =new Action();
					act.setActor(getAMS());
					WhereIsAgentAction action =new WhereIsAgentAction();
					action.setAgentIdentifier(rcv.getSender());
					act.setAction(action);
					try{
							getContentManager().fillContent(request, act);
					} 
					catch(CodecException e) {e.printStackTrace();return;} 
					catch(OntologyException e) {e.printStackTrace();return;}
					send(request);
				} 
				else{
					rcv=receive(amsTemplate);
					if(rcv !=null) {
						Result results = null;
						try{
						results = (Result)getContentManager().extractContent(rcv);
						} 
						catch(UngroundedException e) {e.printStackTrace();return;} 
						catch(CodecException e) {e.printStackTrace();return;} 
						catch(OntologyException e) {e.printStackTrace();return;}
						Iterator it = results.getItems().iterator();
						Location loc = null;
						if(it.hasNext()){
							loc = (Location) it.next();
						}
						doMove(loc);
					}
					else {
					block();
					}
				}
			}
		};
		addBehaviour(b);
	}
	
	protected void baseInitialize(){
		// register the SL0 content language
		getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0 );
		// register the mobility ontology
		getContentManager() .registerOntology(MobilityOntology.getInstance());
		
		try {
			TopicManagementHelper topicHelper = (TopicManagementHelper)getHelper(TopicManagementHelper.SERVICE_NAME);
			workTopic = topicHelper.createTopic("Work");
			topicHelper.register(workTopic);
			managerTemplate = MessageTemplate.MatchTopic(workTopic);
			amsTemplate=MessageTemplate.MatchSender(getAMS());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void afterMove(){
		baseInitialize();
		try {
			System.out.println(getAID().getName() +": My new location is: " + getContainerController().getContainerName());
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	protected void takeDown(){
		super.takeDown();
	}
}