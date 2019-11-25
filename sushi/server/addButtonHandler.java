package comp1206.sushi.server;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

  /**
   * @author Dave Waddington -30091055
   * 
   * This class is just here to handle what to create when any of the add or edit buttons are 
   * clicked in the supplier, ingredient or dish panels.. 
   * I thought it would be more useful when I made it as I thought you'd need to edit a lot more than
   * just these 3 classes. 
   * 
   * THIS CLASS DEALS WITH ONLY THE ADD AND EDIT BUTTONS FROM THE MAIN PANES ON THE DEFAULT PAGE
   * WE ARE HANDLING THE LOGIC FOR THE EDITBOXES SEPARATELY!
   */

public class addButtonHandler implements EventHandler {
  

    String page;
    String button;
    ListView listView;
    ServerInterface server;
    public addButtonHandler(String page, String button, ListView listView,ServerInterface server){
        this.button=button;
        this.page=page;
        this.listView=listView;
        this.server=server;
    }
    @Override
    public void handle(Event event) {
        try {
            int selected = listView.getFocusModel().getFocusedIndex();

            if (page.equals("Supplier")) {
                if (button.equals("edit")) {
                    SupplierEditBox newWindow = new SupplierEditBox(server, "Supplier", selected);
                } else if (button.equals("add")) {
                    SupplierEditBox newWindow = new SupplierEditBox(server, "Supplier");
                }
            }else if(page.equals("Ingredients")){
                if (button.equals("edit")){
                    Ingredient ingredient = server.getIngredients().get(selected);
                    IngredientEditBox box = new IngredientEditBox(server,ingredient);
                    //GeneralEditBox ingWindow = new GeneralEditBox(server, ingredient);
                }else if(button.equals("add")){
                    IngredientEditBox box = new IngredientEditBox(server,"Ingredient");
                }
            }else if(page.equals("Dishes")){
                if (button.equals("edit")){
                    Dish dish=server.getDishes().get(selected);
                    DishEditBox box = new DishEditBox(server, dish);
                }else if (button.equals("add")){
                    DishEditBox box = new DishEditBox(server, "Dish");
                }

            }


        }catch (IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error alert");
            alert.setHeaderText("Was unable to complete action");
            alert.setContentText("Most likely you had nothing selected to edit or remove." +
                    "\nOr you are trying to delete something that is being used by" +
                    "\nsomething else. ");
            alert.showAndWait();
        }
    }

}
