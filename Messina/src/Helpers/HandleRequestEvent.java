package Helpers;

import Helpers.Event;
import jade.core.AID;

/**
 * Created by Sławek on 2017-06-10.
 */
public class HandleRequestEvent extends Event {

    public HandleRequestEvent(int limitTime,int handleTime,int vehicleCount,int startVehicleTime,AID demander){
        this.LimitTime=limitTime;
        this.HandleTime=handleTime;
        this.StartVehicleTime=startVehicleTime;
        this.VehicleCount=vehicleCount;
        this.Demander=demander;
    }

    public int LimitTime;
    public int HandleTime;
    public int StartVehicleTime;
    public int VehicleCount;
    public AID Demander;
}