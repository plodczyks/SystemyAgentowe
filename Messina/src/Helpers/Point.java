package Helpers;

public class Point {

	public Point(double lat, double lng){
		this.lat=lat;
		this.lng=lng;
	}
	public double lat;
	public double lng;
	
	@Override
	public boolean equals(Object o){
		Point p=(Point)o;
		return this.lat==p.lat && this.lng==p.lng;
	}
	
	@Override
	public String toString(){
		return "("+lat+","+lng+")";
	}
}
