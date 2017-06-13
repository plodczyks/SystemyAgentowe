package Requests;

import Helpers.Point;
import jade.core.AID;

/**
 * Created by Sławek on 2017-06-10.
 */
public class WarehouseRequest {

    public int VehicleCount;
    public int LimitTime;
    public int TrackTime;
    public Point WarehouseLocation;
    public Point CoastLocation;
    public AID Demander;


    public WarehouseRequest(int vehicleCount, int limitTime, int trackTime,Point warehouseLocation,Point coastLocation, AID demander) {
        this.VehicleCount = vehicleCount;
        this.LimitTime = limitTime;
        this.TrackTime = trackTime;
        this.Demander=demander;
        this.WarehouseLocation=warehouseLocation;
        this.CoastLocation=coastLocation;
    }
}
