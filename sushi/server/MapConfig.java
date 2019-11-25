package comp1206.sushi.server;

import comp1206.sushi.common.Drone;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author Dave Waddington -30091055
 * 
 * This is a class that I made to just add a few extra options to the map when you launch it 
 * as at default the drones cant get given destinations or anything and i figured you might want a 
 * button to let you choose if you want to see the infoWindows above the suppliers as when you're 
 * not zoomed in they can get in the way quite a lot 
 * 
 * All the code in this class is my own
 */
public class MapConfig extends Stage {
    MapPage map;
    AutoDraw ad = new AutoDraw();
    GridPane editPane;

    /**
     * This simple constructor just gets the placing set up correctly. 
     */
    public MapConfig(MapPage map){
        this.map=map;
        this.setAlwaysOnTop(true);
        this.setMinWidth(150);
        this.setMinHeight(70);
        this.setX(80);
        this.setY(520);
        this.setTitle("Map configurator!");
        init();
        show();
    }

    //really basic page setup
    void init(){
        BorderPane mainPane = new BorderPane();
        editPane = ad.contentPaneConstructor("");
        setScene(new Scene(mainPane));
        mainPane.setCenter(editPane);
        buttons();
    }

    //Adds the buttons
    /**
     * These buttons have some really simple logic behind them, theres really not much to say about them
     */
    void buttons(){
        ToggleButton info = new ToggleButton("Toggle Info Windows");
        editPane.add(info,0,0);
        info.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                map.switchInfo();
            }
        });


        Button destinationTest = new Button("Send Drones to random Suppliers");
        editPane.add(destinationTest,1,0);
        destinationTest.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Drone drone: map.getServer().getDrones()){
                    if(!drone.getTracker().isWorking()){
                        drone.setDestination(ad.randomSupplierPostcode(map.getServer().getSuppliers()));
                    }
                }
            }
        });

        Button destinationTestPost = new Button("Send Drones to random Postcodes");
        editPane.add(destinationTestPost,1,2);
        destinationTestPost.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Drone drone: map.getServer().getDrones()){
                    if(!drone.getTracker().isWorking()){
                        drone.setDestination(ad.randomPostcode(map.getServer().getPostcodes()));
                    }
                }
            }
        });

    }
}
