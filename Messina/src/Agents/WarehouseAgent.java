package Agents;

import Helpers.Point;
import Helpers.Utilities;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.net.URISyntaxException;


//first location - 38.211311, 15.691547
//second location - 38.116347, 15.649820
//third location - 38.257003, 15.752489
//coast(1) - 38.222810, 15.632982
//coast(2) - 38.210375, 15.561670


public class WarehouseAgent extends Agent {

	//road parameters
	private Point location;
	private Point coastLocation;
	private int roadTime;
	
	//request parameters
	private int limitTime;
	private int vehicleCount;

	//to correct
	String ferryName="Ferry1";
	
	private int transportVehicleIndex;
	
	protected void setup(){
		
		Object[] args=getArguments();
		location=new Point(Double.parseDouble((String)args[0]),Double.parseDouble((String)args[1]));
		coastLocation=new Point(Double.parseDouble((String)args[2]),Double.parseDouble((String)args[3]));
		roadTime=Integer.parseInt((String)args[4]);
		limitTime=Integer.parseInt((String)args[5]);
		vehicleCount=Integer.parseInt((String)args[6]);

		try {
			Utilities.addWarehouseMarker(location);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		transportVehicleIndex=1;
		SendVehiclesOrder();
		
		addBehaviour(new CyclicBehaviour(this){
			private static final long serialVersionUID = 1L;
			@Override
			public void action() {
				ACLMessage rcv = receive();
				if(rcv !=null) {
					if(rcv.getConversationId().contains("Vehicles Order")){
						HandleResponseForVehiclesOrder(rcv);
					}
				}
				else{
					block();
				}
			}
		});
	}
		
	public void SendVehiclesOrder(){
		
		AID receiver=new AID(ferryName,AID.ISLOCALNAME);
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setConversationId("Vehicles Order");
		String content= "Coast location: "+coastLocation.lat+","+coastLocation.lng+"\n"+
				"Road time: "+ roadTime+"\n"+
                "Deadline time: "+ limitTime+"\n"+
				"Vehicle count: "+vehicleCount;
		msg.setContent(content);
		msg.addReceiver(receiver);
		System.out.println(getAID().getName() +": Send Vehicles Order Request to "+receiver.getLocalName());
		send(msg);
	}
	
	public void HandleResponseForVehiclesOrder(ACLMessage msg){
		
		String[] description=msg.getContent().split("\n");
		int handledVehicleCount=Integer.parseInt((description[0].split(":")[1]).trim());
		int timeToStart=Integer.parseInt((description[1].split(":")[1]).trim());
		
		vehicleCount-=handledVehicleCount;
		if(handledVehicleCount>0){	
			addBehaviour(new WakerBehaviour(this,timeToStart*1000){
					private static final long serialVersionUID = 1L;
					@Override
					protected void onWake() {
						for(int i=0;i<handledVehicleCount;i++){
							ContainerController cc = getContainerController();
							AgentController ac;
							try {
								ac = cc.createNewAgent(getLocalName()+"TransportVehicle"+transportVehicleIndex, "Agents.TransportVehicleAgent",
										new Object[]{location.lat,location.lng,coastLocation.lat,coastLocation.lng,roadTime});
								ac.start();
							} catch (StaleProxyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}								
							transportVehicleIndex++;
						}
					}
			});
		}
		System.out.println(getAID().getName() +": Handle Vehicles Order Response from "+msg.getSender().getLocalName());
	}
}
