import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

public class ManagerAgent1 extends Agent {

	private static final long serialVersionUID = 1L;

	protected void setup(){

		Behaviour b = new TickerBehaviour(this,1000) {

			private static final long serialVersionUID = 1L;
			
			private int i=1;

			@Override
			protected void onTick() {
				ACLMessage msg;
				if(i%5!=0){
					msg= new ACLMessage(ACLMessage.INFORM);
					msg.setContent("Inform message");
				}
				else{
					msg= new ACLMessage(ACLMessage.QUERY_IF);
					msg.setContent("Query if message");
				}
				msg.addReceiver(new AID("Worker1", AID.ISLOCALNAME));
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
