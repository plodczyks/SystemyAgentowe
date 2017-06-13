package Agents;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.LinkedList;

import Helpers.*;
import com.sun.jndi.toolkit.url.Uri;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.java_websocket.WebSocket;


public class FerryAgent extends Agent {

    //starting params
    private static final int CAPACITY = 20;
    private FerryState state = FerryState.SHORE_2;

    private LinkedList<Event> bestScenario = null;
    private int bestValue = Integer.MAX_VALUE;

    //extra position params
    private Point coast1Location;
    private Point coast2Location;
    private int roadTime;
    private int positionIndex;

    //new coast locations


    //database of requests
    private LinkedList<WarehouseRequest> coast1Requests = new LinkedList<>();
    private LinkedList<WarehouseRequest> coast2Requests = new LinkedList<>();


    private int time = 0;
    private LinkedList<StartEvent> agentScenario = new LinkedList<>();

    protected void setup() {
        Object[] args = getArguments();
        coast1Location = new Point(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));
        coast2Location = new Point(Double.parseDouble((String) args[2]), Double.parseDouble((String) args[3]));
        roadTime = Integer.parseInt((String) args[4]);
        positionIndex = roadTime;
        //add to database vehicle orders
        addBehaviour(new WakerBehaviour(this, 1500) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onWake() {
                while (true) {
                    ACLMessage rcv = receive();
                    if (rcv != null) {
                        if (rcv.getConversationId().contains("Vehicles Order")) {
                            HandleVehiclesOrder(rcv);
                        }
                    } else {
                        break;
                    }
                }
                CalculateScenario();
                //send responses for vehiclesOrder
                for (Event e : bestScenario) {
                    if (e instanceof HandleRequestEvent) {
                        ResponseForVehiclesOrder((HandleRequestEvent) e);
                    }
                }

                System.out.println(getAID().getName() + ": Actual time is " + time);
                System.out.println(getAID().getName() + ": My location index is " + positionIndex);
                System.out.println(getAID().getName() + ": My state is " + state);
                RealizeAnimationStep();
                addTimeElapsedBehaviour();
                addStraitOrderBehaviour();
            }
        });
//
//
//         else if (rcv.getConversationId().contains("Vehicle at coast")) {
//            HandleVehicleInform(rcv);
//        }
    }

    private void HandleVehiclesOrder(ACLMessage msg) {
        String[] description = msg.getContent().split("\n");

        double warehouseLatitude = Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
        double warehouseLongitude = Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());
        Point warehouseLocation=new Point(warehouseLatitude,warehouseLongitude);

        int shoreNr = 2;
        double latitude = Double.parseDouble((description[1].split(":")[1]).split(",")[0].trim());
        double longitude = Double.parseDouble((description[1].split(":")[1]).split(",")[1].trim());
        if (coast1Location.lat == latitude && coast1Location.lng == longitude) {
            shoreNr = 1;
        }
        Point coastLocation=new Point(latitude,longitude);

        int roadTime = Integer.parseInt((description[2].split(":")[1]).trim());

        int limitTime = Integer.parseInt((description[3].split(":")[1]).trim());

        int vehicleCount = Integer.parseInt((description[4].split(":")[1]).trim());

        WarehouseRequest request = new WarehouseRequest(vehicleCount, limitTime, roadTime,warehouseLocation,coastLocation, msg.getSender());
        if (shoreNr == 1) coast1Requests.add(request);
        else coast2Requests.add(request);

        System.out.println(getAID().getName() + ": Save Vehicles Order Request from " + msg.getSender().getLocalName());
    }

    private void ResponseForVehiclesOrder(HandleRequestEvent event) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId("Vehicles Order");
        String content = "Vehicle count: " + event.VehicleCount + "\n" +
                "Time to start: " + event.StartTime;
        msg.setContent(content);
        msg.addReceiver(event.Demander);
        System.out.println(getAID().getName() + ": Send Vehicles Order Response to " + event.Demander.getLocalName());
        send(msg);
    }

    //region animation & ferry logic behaviour

    private void addTimeElapsedBehaviour() {
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            public void onTick()  {
                time++;
                RealizeAnimationStep();
                switch (state) {
                    case SHORE_1:
                        if (agentScenario.size() > 0) {
                            if (agentScenario.getFirst().ShoreNr == 1 && agentScenario.getFirst().StartTime == time) {
                                agentScenario.removeFirst();
                                state = FerryState.TRIP_FROM_1_TO_2;
                                // positionIndex++;
                            }
                        }
                        break;
                    case TRIP_FROM_1_TO_2:
                        if (positionIndex == roadTime) state = FerryState.SHORE_2;
                        else positionIndex++;
                        break;
                    case SHORE_2:
                        if (agentScenario.size() > 0) {
                            if (agentScenario.getFirst().ShoreNr == 2 && agentScenario.getFirst().StartTime == time) {
                                agentScenario.removeFirst();
                                state = FerryState.TRIP_FROM_2_TO_1;
                                //  positionIndex--;
                            }
                        }
                        break;
                    case TRIP_FROM_2_TO_1:
                        if (positionIndex == 0) state = FerryState.SHORE_1;
                        else positionIndex--;
                        break;
                }
                System.out.println(getAID().getName() + ": Actual time is " + time);
                System.out.println(getAID().getName() + ": My location index is " + positionIndex);
                System.out.println(getAID().getName() + ": My state is " + state);
                //TODO: mamy w zasadzie 4 shore'y (patrz mapa)
            }
        });


    }

    private void RealizeAnimationStep(){
        while(bestScenario.size()>0 && bestScenario.getFirst().StartTime==time){
            Event ev=bestScenario.removeFirst();
            if (ev instanceof HandleRequestEvent) {
                HandleRequestEvent vehicleEvent=((HandleRequestEvent) ev);
                try {
                    Utilities.startSimulationTruck(vehicleEvent.Location,vehicleEvent.CoastLocation,vehicleEvent.RoadTime);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(ev instanceof StartEvent){
                StartEvent ferryEvent=((StartEvent) ev);
                try {
                    Utilities.startSimulationFerry(ferryEvent.ShoreNr,roadTime);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //endregion

    //region ship behaviours
    private void addStraitOrderBehaviour() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action()  {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    if (rcv.getConversationId().contains("Strait Order")) {
                        HandleStraitOrder(rcv);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void HandleStraitOrder(ACLMessage msg) {
        String[] description = msg.getContent().split("\n");

        int beforeTime = Integer.parseInt(description[0].split(":")[1]);
        int reservationTime = Integer.parseInt(description[1].split(":")[1]);

//        WarehouseRequest request = new WarehouseRequest(vehicleCount, limitTime, roadTime,warehouseLocation,coastLocation, msg.getSender());
//        if (shoreNr == 1) coast1Requests.add(request);
//        else coast2Requests.add(request);

        System.out.println(getAID().getName() + ": Strait Order Request from " + msg.getSender().getLocalName() + " is handling");
    }

    //endregion
    //region Scenario calculations

    private void CalculateScenario() {

        //TO CORRECT!!
        int startShoreNr = 2;
        LinkedList<Event> events = new LinkedList<>();
        RealizeScenario(0, startShoreNr, coast1Requests, coast2Requests, events);

        //here we have in bestValue and bestScenario the best solution
        bestScenario.sort(new Comparator<Event>()
                          {public int compare(Event o1, Event o2)
                              { return o1.StartTime-o2.StartTime;
                              }
                          });
        System.out.println(getAID().getName() + ": Scenario description:");
        for (Event e : bestScenario) {
            if (e instanceof StartEvent) {
                StartEvent startEvent = ((StartEvent) e);
                agentScenario.add(startEvent);
                System.out.println(getAID().getName() + ": Ferry start at " + startEvent.StartTime + " from coast " + startEvent.ShoreNr);
            }
        }
    }

    private void RealizeScenario(int actualTime, int shoreNr, LinkedList<WarehouseRequest> leftCoast, LinkedList<WarehouseRequest> rightCoast, LinkedList<Event> events) {

        if (leftCoast.size() == 0 && rightCoast.size() == 0) {
            SummarizeScenario(events);
            return;
        }

        LinkedList<WarehouseRequest> actualDemands;
        if (shoreNr == 1) actualDemands = CreateDeepCopy(leftCoast);
        else actualDemands = CreateDeepCopy(rightCoast);

        actualDemands.sort(new WarehouseRequestComparator(actualTime));

        int actualCapacity = 0;
        LinkedList<HandleRequestEvent> newEvents = new LinkedList<HandleRequestEvent>();

        while (actualDemands.size() > 0) {
            if (actualCapacity == CAPACITY) {
                ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, events, newEvents);
                return;
            }
            WarehouseRequest firstDemand = actualDemands.getFirst();
            if (actualTime - firstDemand.getTrackTime() >= 0) {
                int limitTime = firstDemand.getLimitTime();
                int startVehicleTime = actualTime - firstDemand.getTrackTime();

                int vehicleToHandle = firstDemand.getVehicleCount();

                if (vehicleToHandle <= CAPACITY - actualCapacity) {
                    actualDemands.removeFirst();
                    newEvents.add(new HandleRequestEvent(limitTime, actualTime + 1 + roadTime + 1, vehicleToHandle, startVehicleTime,firstDemand.getWarehouseLocation(),firstDemand.getCoastLocation(),firstDemand.getTrackTime(), firstDemand.getDemander()));
                    actualCapacity += vehicleToHandle;
                } else {
                    firstDemand.setVehicleCount(vehicleToHandle - (CAPACITY - actualCapacity));
                    newEvents.add(new HandleRequestEvent(limitTime, actualTime + 1 + roadTime + 1, CAPACITY - actualCapacity, startVehicleTime,firstDemand.getWarehouseLocation(),firstDemand.getCoastLocation(),firstDemand.getTrackTime(), firstDemand.getDemander()));
                    actualCapacity = CAPACITY;
                }
            } else {
                //scenario 1" we start immediately
                ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, events, newEvents);

                //scenario 2: we wait for next transport vehicles
                int delayTime = firstDemand.getTrackTime() - actualTime;
                for (HandleRequestEvent event : newEvents) {
                    event.StartTime += delayTime;
                    event.HandleTime += delayTime;
                }
                actualTime += delayTime;
            }
        }
        ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, events, newEvents);
    }

    private void ExtendScenario(int actualTime, int shoreNr, LinkedList<WarehouseRequest> leftCoast, LinkedList<WarehouseRequest> rightCoast, LinkedList<WarehouseRequest> actualDemands, LinkedList<Event> events, LinkedList<HandleRequestEvent> newEvents) {
        LinkedList<Event> extendedEvents = new LinkedList<Event>(events);
        extendedEvents.addAll(newEvents);
        extendedEvents.add(new StartEvent(shoreNr, actualTime + 1));
        if (shoreNr == 1) {
            RealizeScenario(actualTime + 1 + roadTime + 1, 2, CreateDeepCopy(actualDemands), CreateDeepCopy(rightCoast), extendedEvents);
        } else {
            RealizeScenario(actualTime + 1 + roadTime + 1, 1, CreateDeepCopy(leftCoast), CreateDeepCopy(actualDemands), extendedEvents);
        }
    }

    private void SummarizeScenario(LinkedList<Event> events) {
        int value = 0;
        for (Event event : events) {
            if (event instanceof HandleRequestEvent) {
                HandleRequestEvent handleRequestEvent = ((HandleRequestEvent) event);
                value += Integer.max(handleRequestEvent.HandleTime - handleRequestEvent.LimitTime, 0) * handleRequestEvent.VehicleCount;
            }
        }
        if (value < bestValue) {
            bestValue = value;
            bestScenario = new LinkedList<>(events);
        }
    }

    private LinkedList<WarehouseRequest> CreateDeepCopy(LinkedList<WarehouseRequest> requests) {
        LinkedList<WarehouseRequest> result = new LinkedList<>();
        for (WarehouseRequest e : requests) {
            result.add(new WarehouseRequest(e.getVehicleCount(), e.getLimitTime(), e.getTrackTime(),e.getWarehouseLocation(),e.getCoastLocation(), e.getDemander()));
        }
        return result;
    }

    //endregion

    //region Old code

    //		LinkedList<Helpers.WarehouseRequest> rightCoast=new LinkedList<Helpers.WarehouseRequest>(Arrays.asList(
//		        new Helpers.WarehouseRequest(10,23,9),
//                new Helpers.WarehouseRequest(12,15,2),
//                new Helpers.WarehouseRequest(7,47,12)));
//
//        LinkedList<Helpers.WarehouseRequest> leftCoast=new LinkedList<Helpers.WarehouseRequest>(Arrays.asList(
//                new Helpers.WarehouseRequest(3,14,7),
//                new Helpers.WarehouseRequest(8,50,6),
//                new Helpers.WarehouseRequest(11,11,18)));

//    private SupplyRequest CalculateFerryPossibility(SupplyRequest request){
//        SupplyRequest response=new SupplyRequest(request.ShoreNr,0,0);
//        if(request.ShoreNr==1){
//            if(state== FerryState.SHORE_1){
//                response.RequestedTime=0;
//            }
//            else if(state== FerryState.TRIP_FROM_2_TO_1){
//                response.RequestedTime=java.lang.Math.max(0,positionIndex-request.RequestedTime);
//            }
//            else {
//                return response;
//            }
//
//            if(freePlacesFrom1To2>request.VehicleCount){
//                response.VehicleCount=request.VehicleCount;
//                freePlacesFrom1To2-=request.VehicleCount;
//            }
//            else{
//                response.VehicleCount=freePlacesFrom1To2;
//                freePlacesFrom1To2=0;
//            }
//        }
//        else{  //request.ShoreNr==2
//            if(state== FerryState.SHORE_2){
//                response.RequestedTime=0;
//            }
//            else if(state== FerryState.TRIP_FROM_1_TO_2){
//                response.RequestedTime=java.lang.Math.max(0,roadTime-1- positionIndex-request.RequestedTime);
//            }
//            else {
//                return response;
//            }
//
//            if(freePlacesFrom2To1>request.VehicleCount){
//                response.VehicleCount=request.VehicleCount;
//                freePlacesFrom2To1-=request.VehicleCount;
//            }
//            else{
//                response.VehicleCount=freePlacesFrom2To1;
//                freePlacesFrom2To1=0;
//            }
//        }
//        return response;
//    }


//    private void HandleVehicleInform(ACLMessage msg){
//        String[] description=msg.getContent().split("\n");
//
//        double latitude=Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
//        double longitude=Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());
//        int shoreNr=2;
//        if(coast1Location.Latitude==latitude && coast1Location.Longitude==longitude){
//            shoreNr=1;
//        }
//
//        if(shoreNr==1){
//            handlePlacesFrom1To2++;
//        }
//        else{
//            handlePlacesFrom2To1++;
//        }
//
//        System.out.println(getAID().getName() +": Handle Vehicle Inform from "+msg.getSender().getLocalName());
//    }
    //endregion
}
