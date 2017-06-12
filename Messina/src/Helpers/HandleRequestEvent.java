package Helpers;

import Helpers.Event;
import jade.core.AID;

/**
 * Created by Sławek on 2017-06-10.
 */
public class HandleRequestEvent extends Event {

    public HandleRequestEvent(int limitTime,int handleTime,int vehicleCount,int startVehicleTime,Point location,Point coastLocation,int roadTime,AID demander){
        super(startVehicleTime);
        this.LimitTime=limitTime;
        this.HandleTime=handleTime;
        this.VehicleCount=vehicleCount;
        this.Location=location;
        this.CoastLocation=coastLocation;
        this.RoadTime=roadTime;
        this.Demander=demander;
    }

    public int LimitTime;
    public int HandleTime;
    public int VehicleCount;
    public Point Location;
    public Point CoastLocation;
    public int RoadTime;
    public AID Demander;
}
