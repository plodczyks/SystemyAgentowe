import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
//import jade.core.messaging.*;
import jade.lang.acl.*;

public class MCAAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	//receive
//	private MessageTemplate saTemplate;
	private int mcaCount;
	private int messageCount;
	
	private AID emAgent;
	
	//send
//	private AID mcaTopic;
	
	protected void setup(){
		
		Object[] args=getArguments();
		mcaCount= Integer.parseInt((String)(args[0]));
		messageCount= Integer.parseInt((String)(args[1]));

		
        AMSAgentDescription [] allAgents = null;
        try {
        	SearchConstraints c = new SearchConstraints();
        	c.setMaxResults ( new Long(-1) );
        	allAgents = AMSService.search( this, new AMSAgentDescription (), c );
		} 	catch (FIPAException e) {
				e.printStackTrace();
		}
        for(int i=0;i<allAgents.length;i++){
       	 	if(allAgents[i].getName().getLocalName().contains("ExperimentMasterAgent")){
       	 		emAgent=allAgents[i].getName();
       	 	}
        }
//		try {
//			TopicManagementHelper topicHelper = (TopicManagementHelper)getHelper(TopicManagementHelper.SERVICE_NAME);
//			//receive
//			AID saTopic = topicHelper.createTopic("SA");
//			topicHelper.register(saTopic);
//			saTemplate = MessageTemplate.MatchTopic(saTopic);
//			//send
//			mcaTopic=topicHelper.createTopic("MCA");
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			private int receivedSACount=0;
			
			@Override
			public void action() {
				ACLMessage rcv = receive();//saTemplate);
				if(rcv !=null) {
					receivedSACount++;
					System.out.println(getAID().getName() +": Spam message was handled.");
					if(receivedSACount==mcaCount*messageCount){
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setContent("Done");
						msg.addReceiver(emAgent);
						System.out.println(getAID().getName() +": All spam messages were handled.");
						send(msg);
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