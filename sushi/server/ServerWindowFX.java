package comp1206.sushi.server;

import comp1206.sushi.common.*;
import comp1206.sushi.mock.MockServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author Dave Waddington -30091055
 * 
 * All methods are my own in this class <EXCEPT START TIMER are my own implementations (Some have the names from the original serverWindow but all 
 * logic updated by me(Author))> 
 * 
 * StartTimer source: https://stackoverflow.com/questions/31421212/javafx-scheduling-and-concurrency-with-tasks
 *
 * 
 * This class creates the main serverWindow, and is the users main interaction with the server, it allows things to be 
 * found, edited and added to the server. 
 */

public class ServerWindowFX extends Application implements UpdateListener {
    private static final long serialVersionUID = -4661566573959270000L;
    BorderPane pane = new BorderPane();
    public ServerInterface server;
    private Stage stage;
    ToggleGroup group = new ToggleGroup();
    RadioButton postcodeRadio;
    RadioButton dronesRadio;
    RadioButton staffRadio;
    RadioButton supplierRadio;
    RadioButton ingredientsRadio;
    RadioButton dishesRadio;
    RadioButton ordersRadio;
    RadioButton usersRadio;

    //My ListViews and text inputs on the main window have to be made accessible throughout the class so that I dont lose focusModel when I refresh
    public ListView listView;
    public TextField textInput;

    private AutoDraw autoDraw = new AutoDraw();
    private CheckUsage checkUsage = new CheckUsage();

    public static void main(String[] args) { launch(args); }

    public ServerWindowFX() { }


    /**
     * Because of the way that launch() from javaFX works I can't pass any parameters
     * through the constructor, I need a clear one and all construction needs to be
     * done in the start method
     * @param server
     */
    public ServerWindowFX(ServerInterface server,String[] argv){
        addServer(new MockServer());
        launch(this.getClass(),argv);
    }

    public void addServerAndLaunch(ServerInterface server, String[] argv) {
        this.server = server;
        server.addUpdateListener(this);
        launch(this.getClass(),argv);
    }

    public void addServer(ServerInterface server) {
        this.server = server;
        server.addUpdateListener(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        /**
         *Adding the server here instead of through the constructor like
         *the other ServerWindow because this is how I have to do it with FX
         * because it works differently to swing
         */

        addServer(new MockServer());
        stage.setScene(new Scene(pane));

        GridPane postPane = postPane();
        GridPane winSel = windowSelector();
        pane.setLeft(winSel);
        pane.setCenter(postPane);
        this.stage = stage;
        stage.setTitle(server.getRestaurantName());
        textInput.requestFocus();
        //Using my timelines refresh method
        //Start timed updates
        startTimer();
        stage.setResizable(false);
        stage.setX(80);
        stage.setY(150);
        stage.setAlwaysOnTop(true);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
//                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();
    }

    /**
     * This method simply creates and displays the radio buttons that I'm using instead of tabs which
     * forms the basis of the layout (Because I started coding and learning about JavaFX at the same
     * time so it wasn't until I was half way through the coursework that I discovered there is a tab
     * layout I could have used as default and decided I liked this too much to change it :) )
     * It's just a group of radio buttons with labels of the names of different pages.
     * @return
     */
    public GridPane windowSelector() {

        GridPane winSel = new GridPane();
        winSel.setPadding(new Insets(10, 10, 10, 10));
        winSel.setHgap(10);
        winSel.setVgap(10);


        Label title = new Label("Window Selector:");
        title.setStyle("-fx-font-weight: bold");
        winSel.add(title,0,0);

        postcodeRadio = new RadioButton("Postcodes");
        dronesRadio = new RadioButton("Drones");
        staffRadio = new RadioButton("Staff");
        supplierRadio = new RadioButton("Suppliers");
        ingredientsRadio = new RadioButton("Ingredients");
        dishesRadio = new RadioButton("Dishes");
        ordersRadio = new RadioButton("Orders");
        usersRadio = new RadioButton("Users");

        postcodeRadio.setSelected(true);
        postcodeRadio.setOnMouseClicked((new PageButtonListener()));
        postcodeRadio.setToggleGroup(group);
        dronesRadio.setOnMouseClicked((new PageButtonListener()));
        dronesRadio.setToggleGroup(group);
        staffRadio.setOnMouseClicked(new PageButtonListener());
        staffRadio.setToggleGroup(group);
        supplierRadio.setOnMouseClicked(new PageButtonListener());
        supplierRadio.setToggleGroup(group);
        ingredientsRadio.setOnMouseClicked(new PageButtonListener());
        ingredientsRadio.setToggleGroup(group);
        dishesRadio.setOnMouseClicked(new PageButtonListener());
        dishesRadio.setToggleGroup(group);
        ordersRadio.setOnMouseClicked(new PageButtonListener());
        ordersRadio.setToggleGroup(group);
        usersRadio.setOnMouseClicked(new PageButtonListener());
        usersRadio.setToggleGroup(group);


        winSel.add(postcodeRadio, 0, 2);
        winSel.add(dronesRadio, 0, 3);
        winSel.add(staffRadio, 0, 4);
        winSel.add(supplierRadio, 0, 5);
        winSel.add(ingredientsRadio, 0, 6);
        winSel.add(dishesRadio, 0, 7);
        winSel.add(ordersRadio, 0, 8);
        winSel.add(usersRadio, 0, 9);

        winSel.setStyle("-fx-background-color: lightGray;");

//        //This is the map Page opener button, I'm not sure where to put it really.
//        Button mapButton = new Button("Map");
//        winSel.add(mapButton, 0, 11);
//        mapButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) { MapPage mapPage = new MapPage(server); }});
        MapPage mapPage = new MapPage(server);

        return winSel;
    }

    /**
     * The following <something>Pane() methods are all used to create the various pages on the main
     * page of the program. They are mostly similar bar a few button and content differences which
     * bear the requirement for separate methods to return the pane. This is also very useful for
     * updating the pages because of my refreshAll() method
     * @return
     */
    public GridPane postPane() {

        GridPane postPane = autoDraw.contentPaneConstructor("Postcodes:");
        postPane.add(new Label("Existing:"), 0, 1);

        listView = new ListView();
        List<Postcode> postcodes = server.getPostcodes();
        for (Postcode postcode : postcodes){
            Map<String, Double> postcodeLatLong = postcode.getLatLong();
            Double lat = postcodeLatLong.get("lat");
            Double lon = postcodeLatLong.get("lon");
            listView.getItems().add(postcode.getName()+"     ~Lat: "+ lat + "     Long: "+ lon);
        }
        listView.getFocusModel().focus(-1);
        listView.setPrefSize(410,200);

        postPane.add(listView, 0, 2, 3, 1);
        postPane.add(new Label("Add Postcode:"), 0, 3);
        textInput = new TextField();
        postPane.add(textInput, 1, 3);

        //Button and handler for adding element to list in server
        Button addButton = new Button("Add");
        postPane.add(addButton, 2, 3);
        addButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String input = textInput.getText().toUpperCase();
                if(autoDraw.postcodeFailure(input, server.getPostcodes())){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Your postcode is not going to work");
                    alert.setContentText("The postcode you input is invalid, try again");
                    alert.showAndWait();
                }else {
                    server.addPostcode(input);
                }
                textInput.clear();
                refreshAll();
            }
        });
        //Button and handler for removing element from the server list
        Button removePostcode = new Button("Remove Postcode");
        postPane.add(removePostcode,2,1);
        removePostcode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Postcode> postcodeList = server.getPostcodes();
                    Postcode removing = postcodeList.get(selected);
                    if (checkUsage.isPostcodeUsed(removing,server)){
                        throw new ServerInterface.UnableToDeleteException("Postcode is being used");
                    }else{
                        server.removePostcode(removing);
                        refreshAll();
                    }
                }catch (IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove");
                    alert.showAndWait();
                }catch(ServerInterface.UnableToDeleteException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Looks like this postcode is being used by something else in the server!");
                    alert.showAndWait();
                }
            }
        });
        return postPane;
    }

    public GridPane dronesPane() {
        GridPane dronesPane = autoDraw.contentPaneConstructor("Drones:");

        listView = new ListView();
        List<Drone> drones = server.getDrones();
        for (Drone drone : drones){
            Number progress = server.getDroneProgress(drone);
            try {
                progress = progress.intValue();
                listView.getItems().add(drone.getName() + " ~ " + server.getDroneStatus(drone) + " ~ Progress: " + progress + "%");
            }catch(NullPointerException e){}
        }
        listView.getFocusModel().focus(-1);
        listView.setPrefHeight(200);

        dronesPane.add(new Label("Active List:"),0,1);
        dronesPane.add(listView, 0, 2, 3, 1);

        Button removeDrone= new Button("Remove");
        dronesPane.add(removeDrone,2,1);
        removeDrone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Drone> droneList = server.getDrones();
                    Drone removing = droneList.get(selected);
                    if (!removing.getTracker().isWorking()){
                        server.removeDrone(removing);
                        refreshAll();
                    }else{
                        throw new ServerInterface.UnableToDeleteException("Drone used");
                    }
                }catch (ServerInterface.UnableToDeleteException | IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove");
                    alert.showAndWait();
                }
            }
        });

        dronesPane.add(new Label("Input new drone speed:"), 0, 3);
        textInput = new TextField();
        textInput.setPromptText("Input the new drone's speed");
        dronesPane.add(textInput, 1, 3);


        Button addDrone = new Button("Add");
        dronesPane.add(addDrone, 2, 3);
        addDrone.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Integer speed = Integer.parseInt(textInput.getText());
                    server.addDrone(speed);
                    textInput.clear();
                    refreshAll();
//                }catch (NullPointerException e){
//                    System.out.println("Theres a null pointer on creation. And thats fine");
                }catch(Exception e){
//                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("THIS IS NOT AN INTEGER, THIS INPUT IS \nMEANT FOR THE NEW DRONE'S SPEED");
                    alert.setContentText("Please input an integer");
                    alert.showAndWait();
                    textInput.clear();
                }
            }
        });
        return dronesPane;
    }

    public GridPane staffPane(){
        GridPane staffPane = autoDraw.contentPaneConstructor("Staff:");

        listView = new ListView();
        List<Staff> staffList = server.getStaff();
        for (Staff staff : staffList){
            listView.getItems().add(staff.getName()+" ~ "+ server.getStaffStatus(staff)+" ~ "+staff.getFatigue());
        }

        addStuff(staffPane, listView);
        staffPane.add(textInput,1,3);
        //Button and handler for adding element to list in server
        Button addButton = new Button("Add");
        staffPane.add(new Label("Add staff member: "),0,3);
        staffPane.add(new Label("Active List: "),0,1);
        staffPane.add(addButton, 2, 3);
        addButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                server.addStaff(textInput.getText());
                textInput.clear();
                refreshAll();
            }
        });

        Button remove= new Button("Remove");
        staffPane.add(remove,2,1);
        remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Staff> staffList = server.getStaff();
                    Staff removing = staffList.get(selected);
                    server.removeStaff(removing);
                    refreshAll();
                }catch (ServerInterface.UnableToDeleteException | IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove");
                    alert.showAndWait();
                }
            }
        });
        return staffPane;
    }

    public GridPane supplierPane(){
        GridPane panel = autoDraw.contentPaneConstructor("Suppliers:");
        listView = new ListView();
        List<Supplier> suppliers = server.getSuppliers();
        for (Supplier element: suppliers){
            listView.getItems().add(element.getName()+" ~ "+element.getPostcode().getName());
        }
        listView.getFocusModel().focus(-1);
        listView.setPrefSize(200,200);
        panel.add(listView, 0, 2, 5, 1);
        textInput = new TextField();
        autoDraw.addAndEditButtons("Supplier", panel, listView, server);
        
        Button remove= new Button("Remove");
        panel.add(remove,3,3);
        remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Supplier> elementList = server.getSuppliers();
                    Supplier removing = elementList.get(selected);
                    if(checkUsage.isSupplierUsed(removing, server)){
                        throw new ServerInterface.UnableToDeleteException("This is being used");
                    }else{
                        server.removeSupplier(removing);
                        refreshAll();
                    }
                }catch (ServerInterface.UnableToDeleteException | IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove or it was being used somewhere else");
                    alert.showAndWait();
                }
            }
        });

        return panel;
    }

    public GridPane ingredientPane(){
        GridPane panel = autoDraw.contentPaneConstructor("Ingredient:");
        listView = new ListView();
        List<Ingredient> ing = server.getIngredients();
        Map<Ingredient,Number> ingredientNumberMap = server.getIngredientStockLevels();
        for (Ingredient element: ing){
            String freq = ingredientNumberMap.get(element).toString();
            listView.getItems().add(element.getName()+" ~ Stock: "+freq);
        }
        addStuff(panel, listView);
        autoDraw.addAndEditButtons("Ingredients", panel, listView, server);

        Button remove= new Button("Remove");
        panel.add(remove,3,3);
        remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Ingredient> elementList = server.getIngredients();
                    Ingredient removing = elementList.get(selected);
                    if(checkUsage.isIngredientUsed(removing, server)){
                        throw new ServerInterface.UnableToDeleteException("This is being used");
                    }else{
                        server.removeIngredient(removing);
                        refreshAll();
                    }
                }catch (ServerInterface.UnableToDeleteException | IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove or it was being used somewhere else");
                    alert.showAndWait();
                }
            }
        });

        return panel;
    }

    public GridPane dishPane(){
        GridPane panel = autoDraw.contentPaneConstructor("Dish:");
        listView = new ListView();
        List<Dish> list = server.getDishes();
        Map<Dish,Number> dishNumberMap = server.getDishStockLevels();
        for (Dish element: list){
            String freq = dishNumberMap.get(element).toString();
            listView.getItems().add(element.getName()+" ~ Stock: "+freq);
        }
        addStuff(panel, listView);
        autoDraw.addAndEditButtons("Dishes", panel, listView, server);

        Button remove= new Button("Remove");
        panel.add(remove,3,3);
        remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    int selected = listView.getFocusModel().getFocusedIndex();
                    List<Dish> elementList = server.getDishes();
                    Dish removing = elementList.get(selected);
                    server.removeDish(removing);
                    refreshAll();
                }catch (ServerInterface.UnableToDeleteException | IndexOutOfBoundsException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error alert");
                    alert.setHeaderText("Was unable to delete for some reason");
                    alert.setContentText("Most likely you had nothing selected to remove or it was being used somewhere else");
                    alert.showAndWait();
                }
            }
        });

        return panel;
    }

    public GridPane orderPane(){
        GridPane panel = autoDraw.contentPaneConstructor("Order:");
        listView = new ListView();
        List<Order> list = server.getOrders();
        for (Order element: list){
            listView.getItems().add(element.getName()+" ~ Status:"+ server.getOrderStatus(element)+" ~ Distance:"+ server.getOrderDistance(element)+" ~ Cost:"+server.getOrderCost(element));
        }
        addStuff(panel, listView);
        listView.setPrefWidth(400);
        return panel;
    }

    public GridPane userPane(){
        GridPane panel = autoDraw.contentPaneConstructor("User:");
        listView = new ListView();
        List<User> list = server.getUsers();
        for (User element: list){
            listView.getItems().add(element.getName()+" ~ Postcode:"+element.getPostcode()+" ~ Distance:"+element.getDistance());
        }
        addStuff(panel, listView);
        listView.setPrefWidth(400);
        return panel;
    }

    /**
     * This is a method I made to save on a bit of code duplication, after creating this one I began to put
     * these kinds of methods in my AutoDraw class for the sake of having them in a different place to try
     * to keep this page look a little tidier.
     */
    public GridPane addStuff(GridPane panel, ListView listView){

        listView.getFocusModel().focus(-1);
        listView.setPrefSize(200,200);
        panel.add(listView, 0, 2, 4, 1);
//        panel.add(new Label("Add "+name+":"),0,3);
        textInput = new TextField();
        return panel;
    }

    /**
     * Using Timeline instead of scheduler becuase it wasnt working before and I think it's a problem it has
     * with javafx
     * 
     * I found this method on the internet when trying to get scheduler to work with javafx
     * heres the source: https://stackoverflow.com/questions/31421212/javafx-scheduling-and-concurrency-with-tasks
     * the author is Tuxxy_Thang on stack overflow
     */
    private Timeline timelineBackground = new Timeline();

    public void startTimer() {
        // initialise the Timeline cycle to indefinite duration
        timelineBackground.setCycleCount(Timeline.INDEFINITE);
        // add KeyFrame with a duration of 30 seconds, executing onFinished when the time expires
        timelineBackground.getKeyFrames().add(new KeyFrame(Duration.millis(2500), (actionEvent) -> {
            refreshAll();
        }, null, null));

        // start the Timeline
        timelineBackground.play();
    }

    /**
     * Refresh all parts of the server application based on receiving new data,
     * calling the server afresh
     *
     * Mine redraws pages and keeps anything that the user has done on the page
     */
    public void refreshAll() {
        int selected = listView.getFocusModel().getFocusedIndex();
        String typed = textInput.getText();
        int caretPos = textInput.getCaretPosition();

        if (postcodeRadio.isSelected()) {
            pane.setCenter(postPane());
        }else if(dronesRadio.isSelected()){
            pane.setCenter(dronesPane());
        }else if(staffRadio.isSelected()){
            pane.setCenter(staffPane());
        }else if(supplierRadio.isSelected()){
            pane.setCenter(supplierPane());
        }else if(ingredientsRadio.isSelected()){
            pane.setCenter(ingredientPane());
        }else if(dishesRadio.isSelected()){
            pane.setCenter(dishPane());
        }else if(ordersRadio.isSelected()){
            pane.setCenter(orderPane());
        }else if(usersRadio.isSelected()){
            pane.setCenter(userPane());
        }
        listView.getFocusModel().focus(selected);
        listView.getSelectionModel().select(selected);
        listView.scrollTo(selected);
        textInput.setText(typed);
        if (!typed.equals("")){
            textInput.requestFocus();
        }
        textInput.positionCaret(caretPos);

    }
//    final Runnable runRefresh = new Runnable() {public void run() { refreshAll();}};

    /**
     * Respond to the model being updated by refreshing all data displays
     */
    public void updated(UpdateEvent updateEvent) {
        refreshAll();
    }

    //Nested class for handling which page I'm displaying
    class PageButtonListener implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            refreshAll();
        }

    }

}

/**     Bibliography
 *
 *  -Timeline (Scheduler wasn't working with javafx very nicely)
 *      -https://stackoverflow.com/questions/31421212/javafx-scheduling-and-concurrency-with-tasks
 *
 *  -FocusModels and keeping the current item selected during refresh
 *      -https://stackoverflow.com/questions/11088612/javafx-select-item-in-listview
 *
 *  -General Checking in lists
 *      -https://stackoverflow.com/questions/13056350/passing-in-a-sub-class-to-a-method-but-having-the-super-class-as-the-parameter
 *
 *  -
 **/





