package Helpers;

public class Point {

	public Point(double latitude, double longitude){
		Latitude=latitude;
		Longitude=longitude;
	}
	public double Latitude;
	public double Longitude;
	
	@Override
	public boolean equals(Object o){
		Point p=(Point)o;
		return this.Latitude==p.Latitude && this.Longitude==p.Longitude;
	}
	
	@Override
	public String toString(){
		return "("+Latitude+","+Longitude+")";
	}
}
