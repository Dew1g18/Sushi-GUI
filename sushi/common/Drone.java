package comp1206.sushi.common;

import comp1206.sushi.common.Drone;
import comp1206.sushi.server.DroneTracker;

/**
 * NEED TO UPDATE!!!!
 * ADD A DRONE TRACKER
 * UPDATE MAP TO USE PREMADE DRONE TRACKERS
 */

public class Drone extends Model {

	private Number speed;
	private Number progress;
	
	private Number capacity;
	private Number battery;
	
	private String status;
	
	private Postcode source;
	private Postcode destination;

	private double totalDistance;

	private DroneTracker tracker;

	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
	}

	public Number getSpeed() {
		return speed;
	}

	public DroneTracker giveTracker(){
		this.tracker= new DroneTracker(this);
		return this.tracker;
	}

	public DroneTracker getTracker() {
		return tracker;
	}

	public Number getProgress() {
		try {
			double totalDistance = tracker.getTotalDistance() * 2;

			if (totalDistance == 0) {
				double y = 2*this.totalDistance-tracker.distanceFromRestaurant();
				this.progress =(y/this.totalDistance)*50;
			}else {
				this.progress = (tracker.distanceFromRestaurant() / (tracker.getTotalDistance())) * 50;
				this.totalDistance = totalDistance;
			}
			if (this.status.equals("Idle")){
					this.progress=0;
			}
			return progress;
		}catch (NullPointerException e){
			return null;
		}
	}
	
	public void setProgress(Number progress) {
		this.progress = progress;
	}
	
	public void setSpeed(Number speed) {
		this.speed = speed;
	}
	
	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
//		System.out.println(destination.getName());
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}
	
}
