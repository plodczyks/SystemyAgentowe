import jade.core.*;
import jade.core.behaviours.*;
import jade.core.messaging.*;
import jade.lang.acl.*;

public class ManagerAgent2 extends Agent {

	private static final long serialVersionUID = 1L;
	private AID workTopic;
	private AID spamTopic;
	
	protected void setup(){
		try {
			TopicManagementHelper topicHelper=(TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			workTopic=topicHelper.createTopic("Work");
			spamTopic=topicHelper.createTopic("Spam");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Behaviour b = new TickerBehaviour(this,1000) {

			private static final long serialVersionUID = 1L;
			
			private int i=1;

			@Override
			protected void onTick() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				if(i%5!=0){
					msg.setContent("Spam message");
					msg.addReceiver(spamTopic);
					System.out.println(getAID().getName() +": Spam message is sending.");
				}
				else{
					msg.setContent("Work message");
					msg.addReceiver(workTopic);
					System.out.println(getAID().getName() +": Work message is sending.");
				}
				send(msg);
				i++;
			}
		};
		addBehaviour(b);
	}
	
	protected void takeDown(){
		super.takeDown();
	}
}
