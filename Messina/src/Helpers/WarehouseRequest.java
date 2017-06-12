package Helpers;

import jade.core.AID;

/**
 * Created by Sławek on 2017-06-10.
 */
public class WarehouseRequest {

    private int vehicleCount;
    private int limitTime;
    private int trackTime;
    private Point warehouseLocation;
    private Point coastLocation;
    private AID demander;


    public WarehouseRequest(int vehicleCount, int limitTime, int trackTime,Point warehouseLocation,Point coastLocation, AID demander) {
        this.vehicleCount = vehicleCount;
        this.limitTime = limitTime;
        this.trackTime = trackTime;
        this.demander=demander;
        this.warehouseLocation=warehouseLocation;
        this.coastLocation=coastLocation;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public int getTrackTime() {
        return trackTime;
    }

    public void setTrackTime(int trackTime) {
        this.trackTime = trackTime;
    }

    public int getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }

    public Point getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(Point warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public Point getCoastLocation() {
        return coastLocation;
    }

    public void setCoastLocation(Point coastLocation) {
        this.coastLocation = coastLocation;
    }

    public AID getDemander() {
        return demander;
    }

    public void setDemander(AID demander) {
        this.demander = demander;
    }
}
