package comp1206.sushi.server;

import com.lynden.gmapsfx.javascript.object.LatLong;
import comp1206.sushi.common.Model;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

//import java.lang.reflect.Array;
//import org.reflections.ReflectionUtils.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Dave Waddington -30091055
 * -All methods here are mine
 * This class was made to automate some processes that I planned on repeating a few times 
 * putting them here cleaned up my code nicely and allowed me to save a few lines of messy
 * code here and there
 */

public class AutoDraw {

    /**
     * Here is where I'll be keeping a couple methods I've written to save some repetition of lines
     * when putting a page together
     *
     * @param paneName
     * @return
     */
    public GridPane contentPaneConstructor(String paneName) {
        GridPane contPane = new GridPane();
        contPane.setPadding(new Insets(10, 10, 10, 10));
        contPane.setHgap(10);
        contPane.setVgap(10);
        Label top = new Label(paneName);
        top.setStyle("-fx-font-weight: bold");
        contPane.add(top, 0, 0, 2, 1);
        return contPane;

    }

    /**
     * finds if something is in its list from the server with just the strings of names
     * Proud of this one.
     */
    public <T extends Model> T ifInList(List<T> list, String name) {
        for (T thing : list) {
            if (thing.getName().replaceAll(" ", "").equals(name.replaceAll(" ", ""))) {
                return thing;
            }
        }
        return null;
    }

    /**
     * Find the index of an object in the server list (needed for..... reasons)
     * (not needed anymore apparently but it's useful so I'm keeping it here in case for the next coursework)
     */
    public <T extends Model> Integer findIndex(String name, List<T> list){
        int i = 0;
        for (T current:list){
            if (current.getName().equals(name)){
                return i;
            }i++;
        }return null;
    }

    /**
     * Auto create a comboBox with all the items in a server list
     * Saved a bit of a hassle every time I wanted to make a combo
     * box using all the objects in a list from the server.
     */
    public <T extends Model> ComboBox makeComboBox(List<T> list){
        ArrayList<String> stringList = stringsFromArray(list);
        ObservableList<String> ComboList = FXCollections.observableArrayList(
                stringList);
        return new ComboBox(ComboList);
    }

    /**
     * Returns list of objects as strings of their names
     * Mainly just used for the combobox method but again, saving
     * about 6 lines of ugly looking code for every combo box
     */
    public <T extends Model> ArrayList<String> stringsFromArray(List<T> tArrayList){
        ArrayList<String> stringsList=new ArrayList<String>();
        for (T thing : tArrayList){
            stringsList.add(thing.getName());
        }
//        System.out.println("Here");
        return stringsList;
    }

    /**
     * Checks if any textfeilds in a list are empty
     * <<FALSE FOR EMPTY>>
     * realised at the end of the project that I made this early 
     * and didn't use it everywhere I should have..
     */
    public boolean areTextfieldsEmpty(List<TextField> list){
        try {
            for (TextField t : list) {
                if (t.getText().equals("")) {
                    return true;
                }
            }
            return false;
        }catch(NullPointerException e){
            return true;
        }
    }

    /**
     * creates the Add and edit buttons for a given page,
     * like the contentPaneConstructor , this was a bunch
     * of lines which I literally would have copied and
     * pasted for each of my panes.
     */
    public void addAndEditButtons(String page, GridPane panel, ListView listView, ServerInterface server){
        Button editButton = new Button("Edit");
        panel.add(new Label("Open edit/add page: "),0,1,2,1);
        panel.add(editButton,2,1);
        editButton.setOnMouseClicked(new addButtonHandler(page,"edit",listView,server));
        Button addButton = new Button("Add");
        panel.add(addButton,3,1);
        addButton.setOnMouseClicked(new addButtonHandler(page,"add",listView,server));

    }




    /**
     * I'm going to get LatLong from the internet from a method here because I want to be able to
     * throw an exception to the panel that will display postcodes in case the input is bad so that
     * I can pop up an error before calling and updating the server.
     *
     * This method will be called when you are trying to edit/add a new postcode to the server.
     */
    public String getStuffFromAPI(Postcode postcode){
        try {
            URL url = new URL("https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode=" + postcode.getName().replaceAll(" ", ""));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String info = reader.readLine();
            String[] infoArray = info.split(",");
            String latMap = infoArray[1].replaceAll("\"lat\":","");
            String longMap = infoArray[2].replaceAll("\"long\":","");
            info = latMap+"#"+longMap.replaceAll("}","");
//            System.out.println(info);
            return info.replaceAll("\"","");

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Postcode error");
            return null;
        }
    }


    /**
     * For Data validation I'm going to make a boolean method that actually calls the url stuff
     * to see whether the postcode is of the correct format before I actually try to create the
     * postcode so that I can send up an error box and not try to create postcodes that wont work!
     * @param postcode
     * @return
     * @throws PostcodeException
     */
    public boolean postcodeFailure(String postcode, List<Postcode> postcodes){
        try {
            URL url = new URL("https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode=" + postcode.replaceAll(" ", ""));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String info = reader.readLine();
            /**
             * Disclaimer, I know the following is messy but I am very tired and its functional
             * so just try to ignore it.
             */
            String[] infoArray = info.split(",");
            String latMap = infoArray[1].replaceAll("\"lat\":","");
            String longMap = infoArray[2].replaceAll("\"long\":","");
            info = latMap+"#"+longMap.replaceAll("}","");
//            System.out.println(info);

            /**
             *  When I am doing the drawing I can add validation to this method to check if the
             *  postcode is in the range.
             */
            if (ifInList(postcodes, postcode)==null){
                return false;
            }else{
                return true;
            }
            

        }catch (Exception e){
            return true;
        }
    }

    /**
     * This just gets the hashmap from a postcode and turns it to the form of a latlong object
     * which is easier for me to use in the gmapsfx package
     */
    public LatLong toLatLong(Postcode postcode){
        HashMap<String,Double> latLong = postcode.getLatLong();
//        System.out.println(latLong);
        double lat = latLong.get("lat");
        double lon = latLong.get("lon");
        LatLong it = new LatLong(lat, lon);
       return it;
    }


    /**
     * These final 2 methods are for the map so I can give the drones places to go without 
     * setting up the server to dish out postcodes on the reception of orders as that is coursework 2 stuff
     * 
     * So mine just shows that when given a postcode the drone travels where it's told to go and then comes back
     * displaying its progress percentage as it moves 
     */
    public Postcode randomSupplierPostcode(List<Supplier> suppliers){
        Random random = new Random();
        int randint = random.nextInt(suppliers.size());
        return suppliers.get(randint).getPostcode();
    }

    public Postcode randomPostcode(List<Postcode> postcodes){
        Random random = new Random();
        int randint = random.nextInt(postcodes.size());
        return postcodes.get(randint);
    }
}


