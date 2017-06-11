package Helpers;

import Helpers.Event;

/**
 * Created by Sławek on 2017-06-10.
 */
public class StartEvent extends Event {

    public StartEvent(int shoreNr,int startTime){
        this.ShoreNr=shoreNr;
        this.StartTime=startTime;
    }

    public int ShoreNr;
    public int StartTime;
}
