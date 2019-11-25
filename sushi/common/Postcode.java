package comp1206.sushi.common;

import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.server.AutoDraw;

public class Postcode extends Model {

	private String name;
	private HashMap<String,Double> latLong;
	private Number distance;

	AutoDraw ad = new AutoDraw();

	public Postcode(String code) {
		this.name = code;
		calculateLatLong();
		this.distance = Integer.valueOf(0);
	}
	
	public Postcode(String code, Restaurant restaurant) {
		this.name = code;
		calculateLatLong();
		calculateDistance(restaurant);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Number getDistance() {
		return this.distance;
	}

	public HashMap<String,Double> getLatLong() {
		return this.latLong;
	}
	
	protected void calculateDistance(Restaurant restaurant) {
		//This function needs implementing
		Postcode destination = restaurant.getLocation();
		double desLat = destination.latLong.get("lat");
		double desLong = destination.latLong.get("lon");
		double posLat = this.latLong.get("lat");
		double posLong = this.latLong.get("lon");
		double latDistance = desLat-posLat;
		double latDistanceSq = latDistance*latDistance;
		double lonDistance = desLong-posLong;
		double lonDistanceSq = lonDistance*lonDistance;
		this.distance = Math.sqrt(latDistanceSq+lonDistanceSq);
	}
	
	protected void calculateLatLong() {
		//This function needs implementing
//		String getting = https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode=

		String info = ad.getStuffFromAPI(this);
		String[] infoArray = info.split("#");
		double lat = Double.parseDouble(infoArray[0]);
		double longitude = Double.parseDouble(infoArray[1]);
        lat = (double)Math.round(lat * 1000000d) / 1000000d;
        longitude = (double)Math.round(longitude * 1000000d) / 1000000d;

//		System.out.println(longitude);
		this.latLong = new HashMap<>();
		latLong.put("lat", lat);
		latLong.put("lon", longitude);
		this.distance = new Integer(0);

	}
	
}
