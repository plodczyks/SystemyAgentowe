import jade.core.Agent;

public class TestAgent extends Agent {

	protected void setup(){
		System.out.println("My name is "+getAID().getName());
	}
	
	protected void takeDown(){
		super.takeDown();
	}
}
