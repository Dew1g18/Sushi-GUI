package comp1206.sushi.server;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.*;

/**
 * @author Dave Waddington -30091055
 * 
 * This class was one of the most difficult for me to work out how to implement (bar the map)
 * It is essentially just another add/edit box but this time for dishes. For a while I had the ingretients 
 * not actually in a scroll box and then realised there would probably eventually be a lot more than 3 or 4 of them
 * 
 * fixed now though its fine
 * 
 * all methods in this class are my own.
 */

public class DishEditBox extends GeneralEditBox {
    ArrayList<TextField> textFieldArrayList;
    Dish dish=null;

    Map<Ingredient,Number> map;


    Button update;
    ComboBox comboBox;
    TextField number;

    TextField inputName ;
    TextField inputDescription ;
    TextField inputPrice;
    TextField inputThreshold ;
    TextField inputAmount ;


    List<TextField> textFields = Arrays.asList(
            inputName,inputDescription,inputPrice,inputThreshold,inputAmount);

    public DishEditBox(ServerInterface server, Dish dish) {
        super(server, dish);
        textFieldArrayList = new ArrayList<>();
        this.dish=dish;
        this.map=dish.getRecipe();
        initSub();
        handleAddButton(new AddOrUpdate(this));
//        setResizable(true);
//        this.sizeToScene();
        show();
    }

    public DishEditBox(ServerInterface server, String type){
        super(server, type);
        textFieldArrayList = new ArrayList<>();
        this.map = new HashMap<Ingredient,Number>();
//        this.map =
        initSub();
        handleAddButton(new AddOrUpdate(this));
//        setResizable(true);
        show();
    }

    void initSub(){
        this.setMaxHeight(400);
        ArrayList<Label> labelArrayList = new ArrayList<Label>(
                Arrays.asList(
                        new Label("Name: "),
                        new Label("Description: "),
                        new Label("Price: "),
                        new Label("Restock Threshold: "),
                        new Label("Restock Amount: ")
                ));

        String name= new String();
        String description =new String();
        String price = new String();
        String threshold =new String();
        String amount=new String();
        if (dish!=null){
            name = dish.getName();
            description = dish.getDescription();
            price = Double.toString(dish.getPrice().doubleValue());
            threshold = Integer.toString(dish.getRestockThreshold().intValue());
            amount = Integer.toString(dish.getRestockAmount().intValue());
        }

        inputName = new TextField(name);
        inputDescription = new TextField(description);
        inputPrice = new TextField(price);
        inputThreshold = new TextField(threshold);
        inputAmount = new TextField(amount);


        this.textFields = Arrays.asList(
                inputName,inputDescription,inputPrice,inputThreshold,inputAmount);


        int i=0;
        for (Label l : labelArrayList){
            editPane.add(l,0,i);
            i++;
        }
        i=0;
        for (TextField t : textFields){
            editPane.add(t,2,i);
            i++;
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(recipeMaker(map));
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        mainPane.setRight(scrollPane);
//        editPane.add(,2,i);
//        mainPane.setCenter(editPane);
//        mainPane.setRight(recipeListView(map));
//        update.setOnMouseClicked(new MapUpdater(number,comboBox.getSelectionModel().getSelectedItem().toString()));
        this.sizeToScene();
        this.setMaxHeight(400);
    }

    GridPane recipeMaker(Map<Ingredient,Number> map){
        GridPane rm = new GridPane();
        rm.setPadding(new Insets(10, 10, 10, 10));
        rm.setHgap(10);
        rm.setVgap(10);
        List<Ingredient> ingredientList = server.getIngredients();
        rm.add(new Label("You must update ingredients individually before updating the" +
                "\nwhole dish for them to be saved!"),0,0,2,2);
        int iter = 2;
        for (Ingredient i : ingredientList){
            rm.add(new Label(i.getName()+": "),0,iter);
            Button button = new Button("Update");
            String freq;
            try{
                freq =Integer.toString(map.get(i).intValue());
            }catch(Exception e){
                freq = "0";
            }
            TextField textField = new TextField(freq);
            button.setOnMouseClicked(new MapUpdater(textField,i));
            rm.add(textField,1,iter);
            rm.add(button,2,iter);
            iter++;

        }
        return rm;
    }

    GridPane comboxAndButton(Map<Ingredient,Number> map){
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);

        ArrayList<String> list = ad.stringsFromArray(server.getIngredients());
        ObservableList<String> comboList = FXCollections.observableArrayList(
                list);
        comboBox = new ComboBox(comboList);
        pane.add(comboBox,0,0);

        update = new Button("Add ingredient");
        number = new TextField();

        pane.add(number,0,1);
        pane.add(update,0,2);
        return pane;
    }

    GridPane recipeListView(Map<Ingredient,Number> map){
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(10);
        pane.setVgap(10);

        ListView listView = new ListView();

        for (Ingredient element : server.getIngredients()){
            try {
                listView.getItems().add(element.getName() + " ~ " + map.get(element));
            }catch(Exception e){
                System.out.println("Probably not in the map..");
            }
        }
        pane.add(listView,0,0,2,4);
        pane.add(comboxAndButton(map),3,0,2,3);
        this.sizeToScene();
        return pane;
    }




    class MapUpdater implements EventHandler{
        TextField text;
        Ingredient ingredient;
        public MapUpdater(TextField text, Ingredient ingredient){
            this.text=text;
            this.ingredient=ingredient;
        }

        public MapUpdater(TextField text, String ingredient){
            this.text = text;
            this.ingredient = ad.ifInList(server.getIngredients(),ingredient);
            mainPane.setRight(recipeListView(map));
        }

        @Override
        public void handle(Event event) {
            try {
                int number = Integer.parseInt(text.getText());
                map.put(ingredient, number);
            }catch(NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You are missing some information");
                alert.setContentText("If you wish to add or update this you must input their details");
                alert.showAndWait();
            }catch(NullPointerException e){
                map.put(ingredient, Integer.parseInt(text.getText()));
            }
        }
    }

    class AddOrUpdate implements EventHandler {
        GeneralEditBox box;

        public AddOrUpdate(GeneralEditBox box){ this.box=box; }

        public void handle(Event event) {
            try {
                String nameInput = inputName.getText();
                String descriptionInput = inputDescription.getText();
                String priceInput = inputPrice.getText();
                String thresholdInput = inputThreshold.getText();
                String amountInput = inputAmount.getText();

                if (ad.areTextfieldsEmpty(textFields)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("You are missing some information");
                    alert.setContentText("If you wish to add or update this you must input their details");
                    alert.showAndWait();
                } else {
                    double price = Double.parseDouble(priceInput);
                    int restockThreshold = Integer.parseInt(thresholdInput);
                    int restockAmount = Integer.parseInt(amountInput);

                    if (dish == null) {
                        Dish newDish = server.addDish(nameInput, descriptionInput, price, restockThreshold, restockAmount);
                        for (Ingredient in : server.getIngredients()){
                            server.addIngredientToDish(newDish, in, map.get(in));
                        }
                        // newDish.setRecipe(map);
                        box.close();
                    }else{
                        dish.setName(nameInput);
                        dish.setDescription(descriptionInput);
                        dish.setPrice(price);
                        dish.setRestockAmount(restockAmount);
                        dish.setRestockThreshold(restockThreshold);
                        for (Ingredient in : server.getIngredients()){
                            server.addIngredientToDish(dish, in, map.get(in));
                        }
                        // dish.setRecipe(map);
                        box.close();
                    }

                }
            }catch(NullPointerException e){
                System.out.println("Null");
            } catch (Exception e) {
//                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Alert!!!");
                alert.setHeaderText("Looks like you're trying to give me a string instead of a number!");
                alert.setContentText("Please give reasonable inputs mydude");
                alert.showAndWait();
            }
        }
    }
}


