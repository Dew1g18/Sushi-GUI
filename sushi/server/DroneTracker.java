package comp1206.sushi.server;

import com.lynden.gmapsfx.javascript.object.LatLong;
import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Postcode;

import java.util.Map;


/**
 * @author Dave Waddington -30091055
 * this class is for keeping track of drones on the map, a new one is added to every drone on creation and it is used by my 
 * map to gettheir latLong positions and to get them to make progress from each second. It also helps the drones own progress 
 * variable stay properly updated
 */

public class DroneTracker {
    AutoDraw ad = new AutoDraw();
    Drone drone;
    LatLong latLong;
    final LatLong restaurant;
    final Postcode restaurantPostcode;
    Postcode destination;
    double totalDistance;

    public DroneTracker(Drone drone){
        this.drone = drone;
        this.restaurantPostcode=drone.getSource();
//        Map<String, Double> sourceLatLong = drone.getSource().getLatLong();
//        this.latLong = new LatLong(sourceLatLong.get("lat"), sourceLatLong.get("lon"));
        this.latLong = ad.toLatLong(drone.getSource());
        this.restaurant = latLong;
        this.destination=drone.getDestination();

    }


    /**
     * This will check to see if the drone has made it past the destination
     * It allows me to check on a given refresh if I need to tell the server to set the destination to the source
     * @return
     */
    public boolean reachedDestination(){
//        System.out.println("here");
        LatLong destination = ad.toLatLong(this.drone.getDestination());
        if(latLong.distanceFrom(destination)<=Double.parseDouble(drone.getSpeed().toString())*5){
            return true;
        }else return false;
    }

//    public void setDestination(Postcode destination){
//        drone.setDestination(destination);
//    }

public boolean isWorking(){
    //        if (reachedDestination()){
    //            System.out.println();
            if(atRestaurant()) {
                if (drone.getStatus().equals("Flying")) {
                    drone.setStatus("Idle");
                }
                return false;
            }else if (drone.getStatus().equals("Idle")){
                drone.setStatus("Flying");
            }return true;
        }

    public boolean atRestaurant(){
        if (latLong.distanceFrom(restaurant)<=Double.parseDouble(drone.getSpeed().toString())*5){
            return true;
        }else return false;
    }


    public double distanceFromRestaurant(){
        Map<String, Double> destLatLon = drone.getDestination().getLatLong();
        double desLat = destLatLon.get("lat");
        double desLon = destLatLon.get("lon");
        LatLong destination = new LatLong(desLat,desLon);
//        if (latLong.getBearing(destination)==latLong.getBearing(restaurant)){
//            return 2*getTotalDistance()-latLong.distanceFrom(restaurant);
//        }else {
        return latLong.distanceFrom(restaurant);
//        }
    }

    public double getTotalDistance(){
        Map<String, Double> resLatLon = this.restaurantPostcode.getLatLong();
        double resLat = resLatLon.get("lat");
        double resLon = resLatLon.get("lon");
        LatLong rest = new LatLong(resLat,resLon);
        Map<String, Double> destLatLon = drone.getDestination().getLatLong();
        double desLat = destLatLon.get("lat");
        double desLon = destLatLon.get("lon");
        return rest.distanceFrom( new LatLong(desLat,desLon));
    }

    /**
     *  I AM SO HAPPY
     *
     *  If the drone is at the destination it will turn around and head back to the restaurant
     *  if its at the restaurant it will return null and will be assigned a new destination
     *
     * @return
     */
    public LatLong progress(){
        LatLong whereAmI = this.latLong;
        if (reachedDestination()){
            drone.setDestination(restaurantPostcode);
            return whereAmI;
        }
//        System.out.println(drone.getDestination());
        LatLong destination = ad.toLatLong(drone.getDestination());
        double speed = Double.parseDouble(drone.getSpeed().toString());
        double bearing = whereAmI.getBearing(destination);
//        System.out.println(drone.getDestination());
        if (bearing==0||(drone.getDestination()==restaurantPostcode)&&atRestaurant()){
            isWorking();
            return whereAmI;
        }

        this.latLong = whereAmI.getDestinationPoint(bearing,speed*10);
//        System.out.println(whereAmI);
//        System.out.println(this.latLong);

//        System.out.println("Progressing");
        isWorking();
        this.destination=drone.getDestination();
        return this.latLong;
    }
}
