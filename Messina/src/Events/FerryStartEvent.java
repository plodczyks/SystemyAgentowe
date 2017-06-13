package Events;

/**
 * Created by Sławek on 2017-06-10.
 */
public class FerryStartEvent extends StartEvent {

    public FerryStartEvent(int shoreNr, int startTime){
        super(startTime);
        this.ShoreNr=shoreNr;
    }
    public int ShoreNr;

}
