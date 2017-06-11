package Helpers;

import jade.core.AID;

/**
 * Created by Sławek on 2017-06-10.
 */
public class WarehouseRequest {

    private int vehicleCount;
    private int limitTime;
    private int trackTime;
    private AID demander;

    public WarehouseRequest(int vehicleCount, int limitTime, int trackTime, AID demander) {
        this.vehicleCount = vehicleCount;
        this.limitTime = limitTime;
        this.trackTime = trackTime;
        this.demander=demander;
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

    public AID getDemander() {
        return demander;
    }

    public void setDemander(AID demander) {
        this.demander = demander;
    }
}
