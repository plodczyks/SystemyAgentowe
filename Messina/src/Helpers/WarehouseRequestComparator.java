package Helpers;

import Helpers.WarehouseRequest;

import java.util.Comparator;

/**
 * Created by Sławek on 2017-06-10.
 */
public class WarehouseRequestComparator implements Comparator<WarehouseRequest> {

    private int actualTime;

    public WarehouseRequestComparator(int actualTime){
        this.actualTime=actualTime;
    }

    @Override
    public int compare(WarehouseRequest o1, WarehouseRequest o2) {
        if(o1.getTrackTime()>actualTime){
            //o1 nie zdazy na czas dojechac
            if(o2.getTrackTime()>actualTime){
                //o2 nie zdazy na czas dojechac
                return o1.getTrackTime()-o2.getTrackTime();
            }
            else{
                //o2 zdazy na czas dojechac
                return 1;
            }
        }
        else{
            //o1 zdazy na czas dojechac
            if(o2.getTrackTime()>actualTime){
                //o2 nie zdazy na czas dojechac
                return -1;
            }
            else{
                //o2 zdazy na czas dojechac
                return o1.getLimitTime()-o2.getLimitTime();
            }
        }
    }
}
