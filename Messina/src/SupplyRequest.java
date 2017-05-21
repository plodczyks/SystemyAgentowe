
public class SupplyRequest {

	public SupplyRequest(int shoreNr, int vehicleCount, int requestedTime){
		ShoreNr=shoreNr;
		VehicleCount=vehicleCount;
		RequestedTime=requestedTime;
	}
	public SupplyRequest() {
	}
	public int ShoreNr;
	public int VehicleCount;
	public int RequestedTime;
	
	@Override
	public boolean equals(Object o){
		SupplyRequest sr=(SupplyRequest)o;
		return this.ShoreNr==sr.ShoreNr && this.VehicleCount==sr.VehicleCount && this.RequestedTime==sr.RequestedTime;
	}
}
