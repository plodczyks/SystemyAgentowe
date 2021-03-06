import java.util.Date;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
//import jade.core.messaging.*;
import jade.lang.acl.*;

public class EMAAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	//receive
//	private MessageTemplate mcaTemplate;
	private int mcaCount;
	//send
//	private AID emaTopic;
		
	private Date startDate; 
	
	protected void setup(){
		
		Object[] args=getArguments();
		mcaCount= Integer.parseInt((String)(args[0]));
		
//		try {	
//			TopicManagementHelper topicHelper=(TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
//			//receive
//			AID mcaTopic = topicHelper.createTopic("MCA");
//			topicHelper.register(mcaTopic);
//			mcaTemplate = MessageTemplate.MatchTopic(mcaTopic);
//			//send
//			emaTopic=topicHelper.createTopic("EMA");
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
		
		addBehaviour(new WakerBehaviour(this,30000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onWake() {
		        AMSAgentDescription [] agents = null;
		         try {
		            SearchConstraints c = new SearchConstraints();
		            c.setMaxResults ( new Long(-1) );
		            agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				
		         for(int i=0;i<agents.length;i++){
		        	 if(agents[i].getName().getLocalName().contains("SpammerAgent")){
		        		 ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						 msg.setContent("Start spamming");
						 msg.addReceiver(agents[i].getName());
						 send(msg);
		        	 }
		         }
		         startDate=new Date();
//				for(int i=1;i<=mcaCount;i++){
//					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//					msg.setContent("Start spamming");
//					msg.addReceiver(new AID("SpammerAgent"+i,AID.ISLOCALNAME));
//					send(msg);
//				}
//				System.out.println(getAID().getName() +": Start spamming messages were sended.");
			}
		});
		
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			private int receivedMCACount=0;
			@Override
			public void action() {
				ACLMessage rcv = receive();//mcaTemplate);
				if(rcv !=null) {
					receivedMCACount++;
					if(receivedMCACount==mcaCount){
						Date endDate=new Date();
						System.out.println(getAID().getName() +": Statistics: ");
						System.out.println(getAID().getName() +": Start date: "+ startDate);
						System.out.println(getAID().getName() +": End date: " + endDate);
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
