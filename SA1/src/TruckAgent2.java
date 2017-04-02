import jade.core.*;
import jade.core.behaviours.*;
import jade.core.messaging.*;
import jade.lang.acl.*;

public class TruckAgent2 extends Agent {

	private static final long serialVersionUID = 1L;
	private AID workTopic;
	private MessageTemplate messageTemplate;

	protected void setup(){
		
		try {
			TopicManagementHelper topicHelper = (TopicManagementHelper)getHelper(TopicManagementHelper.SERVICE_NAME);
			workTopic = topicHelper.createTopic("Work");
			topicHelper.register(workTopic);
			messageTemplate = MessageTemplate.MatchTopic(workTopic);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Behaviour b = new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			public void action() {		
				ACLMessage rcv = receive(messageTemplate);
				if(rcv !=null) {
					System.out.println(getAID().getName() +": Work message has been received.");
					ACLMessage response = rcv.createReply();
					response.setPerformative(ACLMessage.INFORM);
					response.setContent("Reply for work message");
					send(response);
				} 
				else{
					block();
				}
			}
		};
		addBehaviour(b);
	}
	
	protected void takeDown(){
		super.takeDown();
	}
}