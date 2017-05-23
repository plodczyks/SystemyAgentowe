import java.util.LinkedList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class FerryAgent extends Agent {

	
	//starting params
	private static final int CAPACITY=20;
	private FerryState state=FerryState.SHORE_1;
	
	//extra position params
	private Point coast1Location;
	private Point coast2Location;
	private int roadTime;
	private int positionIndex;
	
	//extra capacity params
	private int freePlacesFrom1To2=CAPACITY;
	private int freePlacesFrom2To1=CAPACITY;
	private int handlePlacesFrom1To2=0;
	private int handlePlacesFrom2To1=0;
	

	
	protected void setup(){
		
		Object[] args=getArguments();
		coast1Location=new Point(Double.parseDouble((String)args[0]),Double.parseDouble((String)args[1]));
		coast2Location=new Point(Double.parseDouble((String)args[2]),Double.parseDouble((String)args[3]));
		roadTime=Integer.parseInt((String)args[4]);
		positionIndex=0;
		
		addBehaviour(new CyclicBehaviour(this){
			private static final long serialVersionUID = 1L;
			@Override
			public void action() {
				ACLMessage rcv = receive();
				if(rcv !=null) {
					if(rcv.getConversationId().contains("Vehicles Order")){
						HandleRequestForNewSupply(rcv);
					}
					else if(rcv.getConversationId().contains("Vehicle at coast")){
						HandleVehicleInform(rcv);
					}
				}
				else{
					block();
				}
			}
		});
		
		addBehaviour(new TickerBehaviour(this,1000){
			private static final long serialVersionUID = 1L;
			@Override
			public void onTick() {
				HandleTimeElapsed();
			}
		});
	}
	
	private void HandleRequestForNewSupply(ACLMessage msg){
		String[] description=msg.getContent().split("\n");
		
		double latitude=Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
		double longitude=Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());	
		int shoreNr=2;
		if(coast1Location.Latitude==latitude && coast1Location.Longitude==longitude){
			shoreNr=1;
		}
		
		int roadTime=Integer.parseInt((description[1].split(":")[1]).trim());
		
		int vehicleCount=Integer.parseInt((description[2].split(":")[1]).trim());

		SupplyRequest response=CalculateFerryPossibility(new SupplyRequest(shoreNr,vehicleCount,roadTime));
		
		System.out.println(getAID().getName() +": Handle Vehicles Order Request from "+msg.getSender().getLocalName());
		SendResponseForNewSupply(msg.getSender(),response);
	}

	private void SendResponseForNewSupply(AID receiver,SupplyRequest response) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setConversationId("Vehicles Order");
		String content= "Vehicle count: "+response.VehicleCount+"\n"+
						"Time to start: "+ response.RequestedTime;
		msg.setContent(content);
		msg.addReceiver(receiver);
		System.out.println(getAID().getName() +": Send Vehicles Order Response to "+receiver.getLocalName());
		send(msg);
	}
	
	private SupplyRequest CalculateFerryPossibility(SupplyRequest request){
		SupplyRequest response=new SupplyRequest(request.ShoreNr,0,0);
		if(request.ShoreNr==1){
			if(state==FerryState.SHORE_1){
				response.RequestedTime=0;
			}
			else if(state==FerryState.TRIP_FROM_2_TO_1){		
				response.RequestedTime=java.lang.Math.max(0,positionIndex-request.RequestedTime);
			}
			else {
				return response;
			}
				
			if(freePlacesFrom1To2>request.VehicleCount){
				response.VehicleCount=request.VehicleCount;
				freePlacesFrom1To2-=request.VehicleCount;
			}
			else{
				response.VehicleCount=freePlacesFrom1To2;
				freePlacesFrom1To2=0;
			}
		}
		else{  //request.ShoreNr==2
			if(state==FerryState.SHORE_2){
				response.RequestedTime=0;
			}
			else if(state==FerryState.TRIP_FROM_1_TO_2){
				response.RequestedTime=java.lang.Math.max(0,roadTime-1- positionIndex-request.RequestedTime);
			}
			else {
				return response;
			}
				
			if(freePlacesFrom2To1>request.VehicleCount){
				response.VehicleCount=request.VehicleCount;
				freePlacesFrom2To1-=request.VehicleCount;
			}
			else{
				response.VehicleCount=freePlacesFrom2To1;
				freePlacesFrom2To1=0;
			}
		}
		return response;
	}
	
	private void HandleVehicleInform(ACLMessage msg){
		String[] description=msg.getContent().split("\n");
		
		double latitude=Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
		double longitude=Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());	
		int shoreNr=2;
		if(coast1Location.Latitude==latitude && coast1Location.Longitude==longitude){
			shoreNr=1;
		}
		
		if(shoreNr==1){
			handlePlacesFrom1To2++;
		}
		else{
			handlePlacesFrom2To1++;
		}
		
		System.out.println(getAID().getName() +": Handle Vehicle Inform from "+msg.getSender().getLocalName());
	}
	
	private void HandleTimeElapsed(){
		switch(state){
			case SHORE_1:
				if(handlePlacesFrom1To2==CAPACITY){
					state=FerryState.TRIP_FROM_1_TO_2;
					freePlacesFrom1To2=CAPACITY;
					handlePlacesFrom1To2=0;
				}
				break;
			case TRIP_FROM_1_TO_2:
				if(positionIndex<roadTime-1){
					positionIndex++;
				}
				else{
					state=FerryState.SHORE_2;
				}
				break;
			case SHORE_2:
				if(handlePlacesFrom2To1==CAPACITY){
					state=FerryState.TRIP_FROM_2_TO_1;
					freePlacesFrom2To1=CAPACITY;
					handlePlacesFrom2To1=0;
				}
				break;
			case TRIP_FROM_2_TO_1:
				if(positionIndex>0){
					positionIndex--;
				}
				else{
					state=FerryState.SHORE_1;
				}
				break;
		}
	//	System.out.println(getAID().getName() +": My location is "+allPositions.get(positionIndex));
	//	System.out.println(getAID().getName() +": My state is "+state);
	}
	
}
