package Agents;

import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.LinkedList;

import Animation.Sender;
import Animation.Utilities;
import Events.FerryStartEvent;
import Events.StartEvent;
import Events.WarehouseStartEvent;
import Helpers.*;
import Requests.WarehouseRequest;
import Requests.WarehouseRequestComparator;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;


public class FerryAgent extends Agent {

    //starting params
    private static final int CAPACITY = 20;
    private FerryState state = FerryState.SHORE_2;

    //road parameters
    private Point coast1Location;
    private Point coast2Location;
    private int roadTime;
    private int positionIndex;

    //scenario details
    private LinkedList<FerryStartEvent> agentScenario = new LinkedList<>();
    private LinkedList<StartEvent> bestScenario = new LinkedList<>();
    private int bestValue;

    //database of actual requests
    private LinkedList<WarehouseRequest> coast1Requests = new LinkedList<>();
    private LinkedList<WarehouseRequest> coast2Requests = new LinkedList<>();

    //actual simulation params
    private int time = 0;
    private int coast1OccupiedCapacity=0;
    private int coast2OccupiedCapacity=0;

    //for animation
    Sender animationSender;

    protected void setup() {
        try {
            animationSender= Utilities.getConnectedSender();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Object[] args = getArguments();
        coast1Location = new Point(Double.parseDouble((String) args[0]), Double.parseDouble((String) args[1]));
        coast2Location = new Point(Double.parseDouble((String) args[2]), Double.parseDouble((String) args[3]));
        roadTime = Integer.parseInt((String) args[4]);
        positionIndex = roadTime;

        addHandleVehiclesOrderBehaviour();
    }

    private void addHandleMessagesBehaviour() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action()  {
                ACLMessage rcv = receive();
                if (rcv != null) {
                    if (rcv.getConversationId().contains("Strait Order")) HandleStraitOrder(rcv);
                    else if (rcv.getConversationId().contains("Vehicles Start")) HandleVehiclesStart(rcv);
                } else block();
            }
        });
    }

    //region Vehicles Order Request
    private void addHandleVehiclesOrderBehaviour(){
        //add to database vehicle orders
        addBehaviour(new WakerBehaviour(this, 1000) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onWake() {
                while (true) {
                    ACLMessage rcv = receive();
                    if (rcv != null) {
                        if (rcv.getConversationId().contains("Vehicles Order")) HandleVehiclesOrder(rcv);
                    } else break;
                }
                CalculateScenario(0,2,0);
                PropagateScenario();

                System.out.println(getAID().getName() + ": Actual time is " + time);
                System.out.println(getAID().getName() + ": My location index is " + positionIndex);
                System.out.println(getAID().getName() + ": My state is " + state);
                HandleTickTime();  // for 0 time
                addTickerBehaviour();
                addHandleMessagesBehaviour();
            }
        });
    }

    private void HandleVehiclesOrder(ACLMessage msg) {
        String[] description = msg.getContent().split("\n");

        double warehouseLatitude = Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
        double warehouseLongitude = Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());
        Point warehouseLocation = new Point(warehouseLatitude, warehouseLongitude);


        double latitude = Double.parseDouble((description[1].split(":")[1]).split(",")[0].trim());
        double longitude = Double.parseDouble((description[1].split(":")[1]).split(",")[1].trim());
        Point coastLocation = new Point(latitude, longitude);
        int shoreNr = 2;
        if (coast1Location.equals(coastLocation)) shoreNr = 1;


        int roadTime = Integer.parseInt((description[2].split(":")[1]).trim());

        int limitTime = Integer.parseInt((description[3].split(":")[1]).trim());

        int vehicleCount = Integer.parseInt((description[4].split(":")[1]).trim());

        WarehouseRequest request = new WarehouseRequest(vehicleCount, limitTime, roadTime, warehouseLocation, coastLocation, msg.getSender());
        if (shoreNr == 1) coast1Requests.add(request);
        else coast2Requests.add(request);

        System.out.println(getAID().getName() + ": Handle Vehicles Order Request from " + msg.getSender().getLocalName());
    }

    //endregion

    //region Vehicles Order Response
    private void PropagateScenario() {
        //add to own scenario
        agentScenario.clear();
        for (StartEvent e : bestScenario) {
            if (e instanceof FerryStartEvent) {
                FerryStartEvent ferryStartEvent = ((FerryStartEvent) e);
                agentScenario.add(ferryStartEvent);
            }
        }
        //send responses for vehiclesOrder
        for (StartEvent e : bestScenario) {
            if (e instanceof WarehouseStartEvent) ResponseForVehiclesOrder((WarehouseStartEvent) e);
        }
    }

    private void ResponseForVehiclesOrder(WarehouseStartEvent event) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId("Vehicles Order");
        String content = "Vehicle count: " + event.VehicleCount + "\n" +
                "Start time: " + event.StartTime;
        msg.setContent(content);
        msg.addReceiver(event.Demander);
        System.out.println(getAID().getName() + ": Send Vehicles Order Response to " + event.Demander.getLocalName());
        send(msg);
    }

    //endregion

    //region Vehicles Start Inform
    private void HandleVehiclesStart(ACLMessage msg) {

        String[] description = msg.getContent().split("\n");
        double warehouseLatitude = Double.parseDouble((description[0].split(":")[1]).split(",")[0].trim());
        double warehouseLongitude = Double.parseDouble((description[0].split(":")[1]).split(",")[1].trim());
        Point warehouseLocation = new Point(warehouseLatitude, warehouseLongitude);


        double latitude = Double.parseDouble((description[1].split(":")[1]).split(",")[0].trim());
        double longitude = Double.parseDouble((description[1].split(":")[1]).split(",")[1].trim());
        Point coastLocation = new Point(latitude, longitude);
        int shoreNr = 2;
        if (coast1Location.equals(coastLocation)) shoreNr = 1;

        int vehicleCount = Integer.parseInt((description[2].split(":")[1]).trim());

        //remove started action from bestScenario
        for(int i=0;i<bestScenario.size();i++){
            if(bestScenario.get(i) instanceof WarehouseStartEvent){
               WarehouseStartEvent actualEvent=((WarehouseStartEvent)(bestScenario.get(i)));
               if(actualEvent.VehicleCount==vehicleCount && actualEvent.Location.equals(actualEvent.Location) && actualEvent.CoastLocation.equals(coastLocation)){
                   bestScenario.remove(i);
                   break;
               }
            }
        }

        //add occupied values
        if(shoreNr==1) coast1OccupiedCapacity+=vehicleCount;
        else coast2OccupiedCapacity+=vehicleCount;
        System.out.println(getAID().getName() + ": Handle Vehicles Start Inform from " + msg.getSender().getLocalName() + " is handling");
    }
    //endregion

    //region Vehicles Cancel Inform

    private void SendCancelationToWarehouse(WarehouseStartEvent event) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId("Vehicles Cancel");
        String content = "Vehicle count: " + event.VehicleCount + "\n" +
                "Cancel start event";
        msg.setContent(content);
        msg.addReceiver(event.Demander);
        System.out.println(getAID().getName() + ": Send Vehicles Cancel Inform to " + event.Demander.getLocalName());
        send(msg);
    }
    //endregion

    //region Strait Order Request

    private void HandleStraitOrder(ACLMessage msg) {
        String[] description = msg.getContent().split("\n");

        //hardcoded values
        int beforeTime = Integer.parseInt(description[0].split(":")[1].trim());
        int reservationTime = Integer.parseInt(description[1].split(":")[1].trim());

        int boundaryLeftTime = time + 2;
        int boundaryRightTime = time + 12;
        //end hardcoded values
        for (int i = 0; i < agentScenario.size(); i++) {
            if (boundaryLeftTime <= agentScenario.get(i).StartTime && agentScenario.get(i).StartTime <= boundaryRightTime) {
                coast1Requests.clear();
                coast2Requests.clear();
                for (StartEvent startEvent : bestScenario) {
                    if (startEvent instanceof WarehouseStartEvent) {
                        WarehouseStartEvent warehouseStartEvent = (WarehouseStartEvent) startEvent;
                        SendCancelationToWarehouse(warehouseStartEvent);
                        WarehouseRequest request = new WarehouseRequest(warehouseStartEvent.VehicleCount, warehouseStartEvent.LimitTime,
                                warehouseStartEvent.RoadTime, warehouseStartEvent.Location, warehouseStartEvent.CoastLocation, warehouseStartEvent.Demander);
                        if (coast1Location.equals(warehouseStartEvent.CoastLocation)) coast1Requests.add(request);
                        else coast2Requests.add(request);
                    }
                }
                int startShoreNr = agentScenario.get(i).ShoreNr;
                agentScenario.clear();
                CalculateScenario(boundaryRightTime, startShoreNr,startShoreNr==1?coast1OccupiedCapacity:coast2OccupiedCapacity);
                PropagateScenario();
                break;
            }
        }
        System.out.println(getAID().getName() + ": Handle Strait Order Request from " + msg.getSender().getLocalName());
    }

    //endregion

    //region ticker handling

    private void addTickerBehaviour() {
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            public void onTick() {
                time++;
                HandleTickTime();
            }
        });
    }

    private void HandleTickTime() {
        switch (state) {
            case SHORE_1:
                if (agentScenario.size() > 0) {
                    if (agentScenario.getFirst().ShoreNr == 1 && agentScenario.getFirst().StartTime == time) {
                        FerryStartEvent ferryEvent = agentScenario.removeFirst();
                        state = FerryState.TRIP_FROM_1_TO_2;
                        coast1OccupiedCapacity = 0;
                        addBehaviour(new WakerBehaviour(this, 1000) {
                            @Override
                            protected void onWake() {
                                StartFerryAnimation(ferryEvent.ShoreNr);
                            }
                        });
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
                        FerryStartEvent ferryEvent = agentScenario.removeFirst();
                        state = FerryState.TRIP_FROM_2_TO_1;
                        coast2OccupiedCapacity = 0;
                        addBehaviour(new WakerBehaviour(this, 1000) {
                            @Override
                            protected void onWake() {
                                StartFerryAnimation(ferryEvent.ShoreNr);
                            }
                        });
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
    }

    private void StartFerryAnimation(int shoreNr) {
        try {
            Utilities.startSimulationFerry(animationSender, shoreNr, roadTime);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //endregion


    //region Scenario calculations

    private void CalculateScenario(int startTime,int startShoreNr,int occupiedCapacity) {

        bestScenario.clear();
        bestValue= Integer.MAX_VALUE;
        LinkedList<StartEvent> startEvents = new LinkedList<>();
        RealizeScenario(startTime, startShoreNr,occupiedCapacity, coast1Requests, coast2Requests, startEvents);

        //here we have in bestValue and bestScenario the best solution
        System.out.println(getAID().getName() + ": Scenario description:");
    }

    private void RealizeScenario(int actualTime, int shoreNr,int occupiedCapacity, LinkedList<WarehouseRequest> leftCoast, LinkedList<WarehouseRequest> rightCoast, LinkedList<StartEvent> startEvents) {

        if (leftCoast.size() == 0 && rightCoast.size() == 0) {
            SummarizeScenario(startEvents);
            return;
        }

        LinkedList<WarehouseRequest> actualDemands;
        if (shoreNr == 1) actualDemands = CreateDeepCopy(leftCoast);
        else actualDemands = CreateDeepCopy(rightCoast);

        actualDemands.sort(new WarehouseRequestComparator(actualTime));

        LinkedList<WarehouseStartEvent> newEvents = new LinkedList<WarehouseStartEvent>();

        while (actualDemands.size() > 0) {
            if (occupiedCapacity == CAPACITY) {
                ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, startEvents, newEvents);
                return;
            }
            WarehouseRequest firstDemand = actualDemands.getFirst();
            if (actualTime - firstDemand.TrackTime >= 0) {
                int limitTime = firstDemand.LimitTime;
                int startVehicleTime = actualTime - firstDemand.TrackTime;

                int vehicleToHandle = firstDemand.VehicleCount;

                if (vehicleToHandle <= CAPACITY - occupiedCapacity) {
                    actualDemands.removeFirst();
                    newEvents.add(new WarehouseStartEvent(limitTime, actualTime + 1 + roadTime + 1, vehicleToHandle, startVehicleTime, firstDemand.WarehouseLocation, firstDemand.CoastLocation, firstDemand.TrackTime, firstDemand.Demander));
                    occupiedCapacity += vehicleToHandle;
                } else {
                    firstDemand.VehicleCount = vehicleToHandle - (CAPACITY - occupiedCapacity);
                    newEvents.add(new WarehouseStartEvent(limitTime, actualTime + 1 + roadTime + 1, CAPACITY - occupiedCapacity, startVehicleTime, firstDemand.WarehouseLocation, firstDemand.CoastLocation, firstDemand.TrackTime, firstDemand.Demander));
                    occupiedCapacity = CAPACITY;
                }
            } else {
                //scenario 1" we start immediately
                ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, startEvents, newEvents);

                //scenario 2: we wait for next transport vehicles
                int delayTime = firstDemand.TrackTime - actualTime;
                for (WarehouseStartEvent event : newEvents) {
                    event.StartTime += delayTime;
                    event.HandleTime += delayTime;
                }
                actualTime += delayTime;
            }
        }
        ExtendScenario(actualTime, shoreNr, leftCoast, rightCoast, actualDemands, startEvents, newEvents);
    }

    private void ExtendScenario(int actualTime, int shoreNr, LinkedList<WarehouseRequest> leftCoast, LinkedList<WarehouseRequest> rightCoast, LinkedList<WarehouseRequest> actualDemands, LinkedList<StartEvent> startEvents, LinkedList<WarehouseStartEvent> newEvents) {
        LinkedList<StartEvent> extendedStartEvents = new LinkedList<StartEvent>(startEvents);
        extendedStartEvents.addAll(newEvents);
        extendedStartEvents.add(new FerryStartEvent(shoreNr, actualTime + 1));
        if (shoreNr == 1)
            RealizeScenario(actualTime + 1 + roadTime + 1, 2, 0, CreateDeepCopy(actualDemands), CreateDeepCopy(rightCoast), extendedStartEvents);
        else
            RealizeScenario(actualTime + 1 + roadTime + 1, 1, 0, CreateDeepCopy(leftCoast), CreateDeepCopy(actualDemands), extendedStartEvents);
    }

    private void SummarizeScenario(LinkedList<StartEvent> startEvents) {
        int value = 0;
        for (StartEvent startEvent : startEvents) {
            if (startEvent instanceof WarehouseStartEvent) {
                WarehouseStartEvent warehouseStartEvent = ((WarehouseStartEvent) startEvent);
                value += Integer.max(warehouseStartEvent.HandleTime - warehouseStartEvent.LimitTime, 0) * warehouseStartEvent.VehicleCount;
            }
        }
        if (value < bestValue) {
            bestValue = value;
            bestScenario = new LinkedList<>(startEvents);
            bestScenario.sort(new Comparator<StartEvent>() {
                public int compare(StartEvent o1, StartEvent o2) {
                    return o1.StartTime - o2.StartTime;
                }
            });
        }
    }

    private LinkedList<WarehouseRequest> CreateDeepCopy(LinkedList<WarehouseRequest> requests) {
        LinkedList<WarehouseRequest> result = new LinkedList<>();
        for (WarehouseRequest e : requests) {
            result.add(new WarehouseRequest(e.VehicleCount, e.LimitTime, e.TrackTime, e.WarehouseLocation, e.CoastLocation, e.Demander));
        }
        return result;
    }

    //endregion
}