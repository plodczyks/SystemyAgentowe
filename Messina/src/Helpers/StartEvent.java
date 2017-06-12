package Helpers;

import Helpers.Event;

/**
 * Created by Sławek on 2017-06-10.
 */
public class StartEvent extends Event {

    public StartEvent(int shoreNr,int startTime){
        super(startTime);
        this.ShoreNr=shoreNr;
    }
    public int ShoreNr;

}
