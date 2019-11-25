package comp1206.sushi.server;

import comp1206.sushi.common.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Dave Waddington -30091055
 * 
 * This class is just here for checking if anything is being used before deleting it. 
 * There are things that I think will need to be added to it in the second coursework
 * like not removing a drone who is in the middle of completing a task which wasnt asked for.
 * 
 * all methods in this class are my own. 
 */

public class CheckUsage {
    AutoDraw ad = new AutoDraw();

    /**
     * Checking the postcodes for uses
     * @param postcode
     * @param server
     * @return
     *
     * <<Watch out because I think this is where orders should also have a postcode but dont seem
     * to actually have one.>>
     */
    public boolean isPostcodeUsed(Postcode postcode, ServerInterface server){
        //Initialising list to check
        ArrayList<Postcode> used=new ArrayList<>();
        //Building lists
        for (Supplier sup: server.getSuppliers()){
            used.add(sup.getPostcode());
        }
        for (User user: server.getUsers()){
            used.add(user.getPostcode());
        }
        used.add(server.getRestaurantPostcode());

        //Checking lists
        if (ad.ifInList(used,postcode.getName())==null){
            return false;
        }else return true;
    }


    public boolean isSupplierUsed(Supplier supplier, ServerInterface server){
        //Initialising list to check
        ArrayList<Supplier> used=new ArrayList<>();
        //Building lists
        for (Ingredient element: server.getIngredients()){
            used.add(element.getSupplier());
        }
        //Checking lists
        if (ad.ifInList(used,supplier.getName())==null){
            return false;
        }else return true;
    }


    public boolean isIngredientUsed(Ingredient ingredient, ServerInterface server){
        //Initialising list to check
        ArrayList<Ingredient> used=new ArrayList<>();
        //Building lists
        for (Ingredient i : server.getIngredients()) {
            for (Dish element : server.getDishes()) {
                int freq;
                try{
                    freq = Integer.parseInt(element.getRecipe().get(i).toString());
                }catch(NullPointerException e){
                    freq = 0;
                }
                //Please ignore the above line its 4am and I'm really tired
                if (freq!=0){
                    used.add(i);
                }
            }
        }
        //Checking lists
        if (ad.ifInList(used,ingredient.getName())==null){
            return false;
        }else return true;
    }


//    public boolean is
}


