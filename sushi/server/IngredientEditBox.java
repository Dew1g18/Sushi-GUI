package comp1206.sushi.server;

import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Supplier;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dave Waddington -30091055
 * This is another edit box that just adds/edits ingredients
 * All these methods are my own
 */

public class IngredientEditBox extends GeneralEditBox {
    ArrayList<TextField> textFieldArrayList;
    Ingredient ingredient=null;

    TextField inputName ;
    TextField inputUnit ;
    TextField inputThreshold ;
    TextField inputAmount ;
    ComboBox inputSupplier ;

    List<TextField> textFields = Arrays.asList(
    inputName,inputUnit,inputThreshold,inputAmount);

    public IngredientEditBox(ServerInterface server, Ingredient ingredient) {
        super(server, ingredient);
        textFieldArrayList = new ArrayList<>();
        this.ingredient=ingredient;
        initSub();
        handleAddButton(new AddOrUpdate(this));
        show();
    }

    public IngredientEditBox(ServerInterface server, String type){
        super(server, type);
        textFieldArrayList = new ArrayList<>();
        initSub();
        handleAddButton(new AddOrUpdate(this));
        show();
    }
    

    /**
     * this init essentially just gets all the information that is going to be needed to edit an ingredient set up and populates the 
     * new stage. Its really boring really, the magic is in the handler (and even the handler isnt that magic)
     */
    <T extends Control>void initSub(){
        ArrayList<Label> labelArrayList = new ArrayList<Label>(
            Arrays.asList(
            new Label("Name: "),
            new Label("Unit: "),
            new Label("Restock Threshold: "),
            new Label("Restock Amount: "),
            new Label("Supplier: ")
            ));


        String name =new String("");
        String unit =new String("");
        String threshold =new String("");
        String amount=new String("");
        String supplier=new String("");
        if (ingredient!=null){
            name = ingredient.getName();
            unit = ingredient.getUnit();
            threshold = Integer.toString(ingredient.getRestockThreshold().intValue());
            amount = Integer.toString(ingredient.getRestockAmount().intValue());
            supplier = ingredient.getSupplier().getName();
        }

        inputName = new TextField(name);
        inputUnit = new TextField(unit);
        inputThreshold = new TextField(threshold);
        inputAmount = new TextField(amount);
        inputSupplier = ad.makeComboBox(server.getSuppliers());
        inputSupplier.getSelectionModel().select(supplier);


        this.textFields = Arrays.asList(
                inputName,inputUnit,inputThreshold,inputAmount);


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
        editPane.add(inputSupplier,2,i);
        this.sizeToScene();
    }


    /**
     * This is an innerClass that I use for my handler, like I do in all my editBoxes, it has a couple 
     * if statements to see if it needs to create a new object to add to the server or just edit, then
     *  catches problems like having empty text feilds and refuses to act with them
     */
    class AddOrUpdate implements EventHandler {
        GeneralEditBox box;

        public AddOrUpdate(GeneralEditBox box){ this.box=box; }

        public void handle(Event event) {
            try {
                String nameInput = inputName.getText();
                String unitInput = inputUnit.getText();
                String thresholdInput = inputThreshold.getText();
                String amountInput = inputAmount.getText();
                String supplierInput = inputSupplier.getSelectionModel().getSelectedItem().toString();

                if (ad.areTextfieldsEmpty(textFields) || inputSupplier.getSelectionModel().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("You are missing some information");
                    alert.setContentText("If you wish to add or update this you must input their details");
                    alert.showAndWait();
                } else {
                    Supplier supplier = ad.ifInList(server.getSuppliers(), supplierInput);
                    int restockThreshold = Integer.parseInt(thresholdInput);
                    int restockAmount = Integer.parseInt(amountInput);
                    if (ingredient == null) {
                        server.addIngredient(nameInput, unitInput, supplier, restockThreshold, restockAmount);
                        box.close();
                    }else{
                        ingredient.setName(nameInput);
                        ingredient.setUnit(unitInput);
                        ingredient.setSupplier(supplier);
                        ingredient.setRestockAmount(restockAmount);
                        ingredient.setRestockThreshold(restockThreshold);
                        box.close();
                    }

                }
            }catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Alert!!!");
                alert.setHeaderText("Looks like you're trying to give me a string instead of a number!");
                alert.setContentText("Please give reasonable inputs mydude");
                alert.showAndWait();
            }
        }
    }
}

