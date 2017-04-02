import java.util.LinkedList;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
//import jade.core.messaging.*;
import jade.lang.acl.*;

public class SAAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
//	private AID emaTopic;
//	private AID saTopic;
//	
//	private MessageTemplate emaTemplate;
	
	private LinkedList<AID> mcaAgents; 
	
	private int messageSize;
	private int messageCount;
	private String messageContent;

//	private AID spamTopic;
	
	protected void setup(){
		
		Object[] args=getArguments();
		messageCount= Integer.parseInt((String)(args[0]));
		messageSize= Integer.parseInt((String)(args[1]));

		StringBuilder sb=new StringBuilder();
		for(int i=0;i<messageSize;i++){
			sb.append('A');			
		}
		messageContent=sb.toString();
		mcaAgents=new LinkedList<AID>();
		
//		try {
//			TopicManagementHelper topicHelper = (TopicManagementHelper)getHelper(TopicManagementHelper.SERVICE_NAME);
//			emaTopic = topicHelper.createTopic("EMA");
//			topicHelper.register(emaTopic);
//			emaTemplate = MessageTemplate.MatchTopic(emaTopic);
//			
//			saTopic=topicHelper.createTopic("SA");
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage rcv = receive();//emaTemplate);
				if(rcv !=null) {
			        AMSAgentDescription [] allAgents = null;
			        try {
			        	SearchConstraints c = new SearchConstraints();
			        	c.setMaxResults ( new Long(-1) );
			        	allAgents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
					} 	catch (FIPAException e) {
							e.printStackTrace();
					}
			        for(int i=0;i<allAgents.length;i++){
			       	 	if(allAgents[i].getName().getLocalName().contains("MessageConsumingAgent")){
			       	 		mcaAgents.add(allAgents[i].getName());
			       	 	}
			        }
					System.out.println(getAID().getName() +": Spamming is starting. ");
					for(int i=0;i<messageCount;i++){
						for(int j=0;j<mcaAgents.size();j++){
							ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
							msg.setContent(messageContent);
							msg.addReceiver(mcaAgents.get(j));
							System.out.println(getAID().getName() +": Spam message to MessageConsumingAgent"+j+" was sended.");
							send(msg);
						}
					}
					removeBehaviour(this);					
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
