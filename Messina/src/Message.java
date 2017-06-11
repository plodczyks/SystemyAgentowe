//import com.google.gson.Gson;

import Helpers.Point;

public class Message {
    private Point start;
    private Point end;
    public Message(double lattitude1, double longtitude1, double lattitude2, double longtitude2){
        start = new Point(lattitude1,longtitude1);
        end = new Point(lattitude2, longtitude2);
    }
//    @Override
//    public String toString(){
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }
}
