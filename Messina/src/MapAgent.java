

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class MapAgent extends GuiAgent {

	  int     cnt;   // this is the counter
	  public boolean cntEnabled;  // this flag indicates if counting is enabled
	  transient protected GuiAgentWindow gui;  // this is the gui

	  // These constants are used by the Gui to post Events to the Agent
	  public static final int EXIT = 1000;
	  public static final int MOVE_EVENT = 1001;
	  public static final int STOP_EVENT = 1002;
	  public static final int CONTINUE_EVENT = 1003;
	  public static final int REFRESH_EVENT = 1004;
	  public static final int CLONE_EVENT = 1005;

	  public void setup() {
		  // register the SL0 content language
//		  getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
		  // register the mobility ontology
//		  getContentManager().registerOntology(MobilityOntology.getInstance());

		  // creates and shows the GUI
		  gui = new GuiAgentWindow(this);
		  gui.setVisible(true); 

		  // get the list of available locations and show it in the GUI
		 // addBehaviour(new GetAvailableLocationsBehaviour(this));

		  // initialize the counter and the flag
		 // cnt = 0;
		//  cntEnabled = true;
//
		  ///////////////////////
		  // Add agent behaviours to increment the counter and serve
		  // incoming messages
//		  Behaviour b1 = new CounterBehaviour(this);
//		  addBehaviour(b1);	
//		  Behaviour b2 = new ServeIncomingMessagesBehaviour(this);
//		  addBehaviour(b2);	
		}

		public void takeDown() {
		  if (gui!=null) {
	            gui.dispose();
		    gui.setVisible(false);
		  }
	          System.out.println(getLocalName()+" is now shutting down.");
		}
	
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub

	}

}
