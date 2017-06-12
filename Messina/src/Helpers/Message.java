package Helpers;

import com.google.gson.Gson;

public class Message {
    private Point start;
    private Point end;
    private IconType type;
    public Message(double lattitude1, double longtitude1, double lattitude2, double longtitude2, IconType type){
        start = new Point(lattitude1,longtitude1);
        end = new Point(lattitude2, longtitude2);
        this.type = type;
    }
    public Message(Point p1, Point p2, IconType type){
        start = p1;
        end = p2;
        this.type = type;
    }
    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
