package Animation;

import Helpers.IconType;
import Helpers.Point;
import com.google.gson.Gson;

public class Message {
    private Point start;
    private Point end;
    private int time;
	private IconType type;
    public Message(double latitude1, double longitude1, double latitude2, double longitude2, IconType type,int time){
        start = new Point(latitude1,longitude1);
        end = new Point(latitude2, longitude2);
        this.type = type;
        this.time=time;
    }
    public Message(Point p1, Point p2, IconType type,int time){
        start = p1;
        end = p2;
        this.type = type;
        this.time=time;
    }
    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
