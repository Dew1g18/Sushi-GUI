package comp1206.sushi.server;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Supplier;
import comp1206.sushi.server.AutoDraw;
import comp1206.sushi.server.ServerInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;


import static com.lynden.gmapsfx.javascript.object.MapTypeIdEnum.HYBRID;
import static com.lynden.gmapsfx.javascript.object.MapTypeIdEnum.ROADMAP;
import static com.lynden.gmapsfx.javascript.object.MapTypeIdEnum.TERRAIN;

/**
 * @author Dave Waddington -30091055
 * 
 * This class is what draws my GMAPSFX map!! 
 * 
 * the methods mapInitialized() and startTimer() are based off of methods I found from the internet
 * although they excecute some of my own logic now as well/instead
 * The startTimer method is used and referenced in ServerWindowFX
 * The mapInitialized method is from robs guide online, rob is the autor, I'd leave his twitter handle but 
 * it says his account is banned.. 
 * source:https://rterp.wordpress.com/2014/04/25/gmapsfx-add-google-maps-to-your-javafx-application/
 * 
 * All other methods in this class are my own
 */


public class MapPage extends Stage implements MapComponentInitializedListener {
    private ServerInterface server;
    private AutoDraw ad;
    private final Double zeroLatitude = 50.913193;
    private final Double zeroLongitude = -1.476856;
    private final Double endLatitude = 50.891097;
    private final Double endLongitude =  -1.337896;
    MarkerOptions mo;
    double centerLat;
    double centerLon;
    public boolean trueForInfo;

    MarkerImageFactory markerImageFactory = new MarkerImageFactory();

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<DroneTracker> trackers = new ArrayList<>();
    private ArrayList<Marker> droneMarkers = new ArrayList<>();

    GoogleMapView mapView;
    GoogleMap map;

    /**
     * This is a pretty standard constructor, creates the map, sets the center and then sets the map to be the scene
     * of the stage that this class is in creation. 
     */
    public MapPage(ServerInterface server){
        this.server = server;
        this.trueForInfo=true;
        centerLat = server.getRestaurantPostcode().getLatLong().get("lat");
        centerLon = server.getRestaurantPostcode().getLatLong().get("lon");
        mapView = new GoogleMapView("en-US", "AIzaSyByxtLlS212X-09ONsVt93LOtFC-8nGuKY");
        mapView.addMapInializedListener(this);

        this.setScene(new Scene(mapView));
        setHeight(900);
        setWidth(1600);
        setX(50);
        setY(50);
        MapConfig mapConfig = new MapConfig(this);
        show();
    }


    //This was from the gmapsFX help guide online
    //https://rterp.wordpress.com/2014/04/25/gmapsfx-add-google-maps-to-your-javafx-application/ author @RobTerp on twitter
    public void mapInitialized() {
        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();
//        LatLong tmp = new LatLong(1.0,1.0);
        mapOptions.center(new LatLong(centerLat,centerLon))
                .mapType(ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(true)
                .zoomControl(false)
                .zoom(16);
        map = mapView.createMap(mapOptions);
//        populateSuppliers();
        restaurantPlace();
        startTimer();
//        refreshAll();
    }

    private Timeline timelineBackground = new Timeline();
    /**
     * this is another use of the startTimer method that I got from 
     * Tuxxy_thang on stack overflow, referenced in serverWindow FX
     */
    private void startTimer() {
        initiateDrones();
        // initialise the Timeline cycle to indefinite duration
        timelineBackground.setCycleCount(Timeline.INDEFINITE);
        // add KeyFrame with a duration of 30 seconds, executing onFinished when the time expires
        timelineBackground.getKeyFrames().add(new KeyFrame(Duration.millis(1000), (actionEvent) -> {
            refreshAll();
        }, null, null));
        // start the Timeline
        timelineBackground.play();
    }

    /**
     * using another method called refresh all which essentually here works as a time progression 
     * method, which is why I made it private as I done want anything calling it that isn't my timer
     */
    private void refreshAll(){
        map.removeMarkers(markers);
        markers.clear();
        populateSuppliers();
        initiateDrones();
    }

    /**
     * Not implemented yet but there's some ral potential here for implementing the suppliers postcodes and shit.
     */
    public Marker markPostcode(Postcode postcode, String icon){
        //Add a marker to the map
        double lat = postcode.getLatLong().get("lat");
        double lon = postcode.getLatLong().get("lon");
        LatLong latLong = new LatLong(lat,lon);

//        System.out.println(lat);
        mo = new MarkerOptions();
        mo.position(latLong)
                .visible(true)
                .icon(null)
                .title(postcode.getName());
        if(!trueForInfo) {
            mo.label(icon);
//            mo.icon("file:///src/main/assets/restaurante.png");
        }

//        String imgpath = "file:///src/main/assets/restaurante.png";
//        mo.icon(imgpath);
        Marker marker = new Marker(mo);

        map.addMarker(marker);
        markers.add(marker);
        return marker;
    }


    /**
     * This gets all the suppliers in the server and plots them all on the map
     */
    public void populateSuppliers(){
        List<Supplier> suppliers = server.getSuppliers();
//        System.out.println("Got here");
        for (Supplier element : suppliers){
            Marker mark = markPostcode(element.getPostcode(), element.getName());
            if (trueForInfo){
                supplyInfo(element,mark);
            }
        }
    }


    /**
     * This changes a boolean switch I have for whether or not to show labels or infoWindows for the suppliers in the 
     * map, I added this switch because sometimes its easier to see one than the other.
     */
    public void switchInfo(){
        this.trueForInfo=!this.trueForInfo;
    }

    /**
     * This takes in a given supplier during the creation process, after another method having checked
     *  the boolean to see if it should have an infowindow or not and then adds the infowindow 
     */
    public void supplyInfo(Supplier element, Marker mark){
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content(element.getName()+"~  "+"Postcode: "+element.getPostcode());
        infoWindowOptions.disableAutoPan(true);
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map,mark);
    }


    /**
     * Just places the restaurantes marker on creation of the map (the only static marker on the map) 
     */
    public void restaurantPlace(){
        Marker mark = markPostcode(server.getRestaurantPostcode(), "Restaurant");
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.disableAutoPan(true)
                .content("RESTAURANT");
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);
        infoWindow.open(map,mark);
//        mark.setOptions(mo.icon(getIcons().get(1).toString()));
        markers.remove(0);
    }


    /**
     * I never actually used this as the spec did not say to check if things were in southampton only that you might need to
     * if you're doing a static map, but seeing as I aint no scrub my drones are not held by this. :)
     */
    public boolean isInBounds(Postcode postcode){
        Map<String, Double> latLong = postcode.getLatLong();
        Double lat = latLong.get("lat");
        Double lon = latLong.get("lon");
        if (lat>zeroLatitude||lat<endLatitude){
            return false;
        }else if(lon>zeroLongitude||lon<endLongitude){
            return false;
        }else return true;
    }

    /**
     * This gets all the drones in the server when it is called, checks if any of them arent on the map and if it finds one that 
     * isnt it will plot it wherever it needs to be. it then updates the list of trackers that this class keeps
     */
    private void initiateDrones(){
        ArrayList<DroneTracker> trackersLocal = new ArrayList<>();
        for (Drone drone : server.getDrones()){
            DroneTracker dt =isDroneOnMap(drone);
            if (dt!=null){
                trackersLocal.add(dt);
            }else {
                trackersLocal.add(drone.giveTracker());
//                System.out.println("Created droneTracker");
            }
        }
        this.trackers=trackersLocal;
        drawDrones();
    }

    /**
     * This is just some simple logic to see if a given drone is on the map and then returns its tracker so whoever uses the method can
     * use it as a boolean (if != null) or to get the tracker of a given drone to do stuff with it.
     */
    public DroneTracker isDroneOnMap(Drone drone){
        for (DroneTracker droneTracker : this.trackers){
            if (droneTracker.drone.equals(drone)){
                return droneTracker;
            }
        }return null;
    }

    /**
     * This method is just for marking drones, it uses the drones' droneTracker and places it with no icon and a label with the drones' name
     */
    private Marker markDrone(DroneTracker dt){
        mo = new MarkerOptions();
        mo.position(dt.progress())
                .visible(true)
                .icon(null)
                .label(dt.drone.getName());
        Marker mark = new Marker(mo);
        droneMarkers.add(mark);
        return mark;
    }

    /**
     * removes the drones from the map by calling the ones that are contained on the drone list, then pulls them all from the server and draws again.
     */
    private void drawDrones(){
        map.removeMarkers(droneMarkers);
        droneMarkers.clear();
        for (DroneTracker element: this.trackers){
            map.addMarker(markDrone(element));
//            System.out.println("Looking at trackers");
        }
    }

    public ServerInterface getServer() {
        return server;
    }
}

/**Bibliography ->> for the extension
 *
 * I have evidently used Googles very own GMapsFX api for this map drawing and stuff, would like to give that entire
 * api site a mention as I think I must have been to at least half of its pages! namely
 * -https://rterp.github.io/GMapsFX/
 * -https://github.com/rterp/GMapsFX/issues/130
 *
 * Image references
 *
 *  -Sushi icon
 *      -https://www.google.co.uk/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiav8PZgojhAhWQA2MBHUcxABIQjRx6BAgBEAU&url=https%3A%2F%2Fwww.iconfinder.com%2Ficons%2F396957%2Fchopstick_food_sushi_icon&psig=AOvVaw2WBq9FQX4h0DvVbewKCuEN&ust=1552872408112806
 *
 *  -Drone icon
 *      -https://www.google.co.uk/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwjFnLOeg4jhAhWGHxQKHT79DtMQjRx6BAgBEAU&url=https%3A%2F%2Fthenounproject.com%2Fterm%2Fdrone%2F14866%2F&psig=AOvVaw2gqROS9J3EF8u7Be7ua8nq&ust=1552872282234981
 *
 *  -Van icon
 *      -https://www.google.co.uk/url?sa=i&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiu--Ktg4jhAhUKEBQKHeX1CxkQjRx6BAgBEAU&url=%2Furl%3Fsa%3Di%26source%3Dimages%26cd%3D%26ved%3D%26url%3Dhttp%253A%252F%252Fchittagongit.com%252Ficon%252Fmoving-van-icon-25.html%26psig%3DAOvVaw26i_4hbko_LX1AuLQ73R5B%26ust%3D1552872592246922&psig=AOvVaw26i_4hbko_LX1AuLQ73R5B&ust=1552872592246922
 *
 *
 *
 */

