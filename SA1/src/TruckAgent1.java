import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

public class TruckAgent1 extends Agent {

	private static final long serialVersionUID = 1L;

	protected void setup(){

		Behaviour b = new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
				ACLMessage rcv = receive(mt);
				if(rcv !=null) {
					ACLMessage response = rcv.createReply();
					response.setPerformative(ACLMessage.INFORM);
					response.setContent("Reply for query if");
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