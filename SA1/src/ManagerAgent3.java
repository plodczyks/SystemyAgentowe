import jade.core.*;
import jade.core.behaviours.*;
import jade.core.messaging.*;
import jade.lang.acl.*;

public class ManagerAgent3 extends Agent {

	private static final long serialVersionUID = 1L;
	private AID workTopic;
//	private AID spamTopic;
	
	protected void setup(){
		try {
			TopicManagementHelper topicHelper=(TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			workTopic=topicHelper.createTopic("Work");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Behaviour b = new TickerBehaviour(this,60000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
				msg.setContent("I have work to do");
				msg.addReceiver(workTopic);
				System.out.println(getAID().getName() +": Work message is sending.");
				send(msg);
			}
		};
		addBehaviour(b);
	}
	
	protected void takeDown(){
		super.takeDown();
	}
}
