package comp1206.sushi.server;

import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dave Waddington -30091055
 * -All methods here are my own
 * This class makes an edit box that allows you to add or change details of a supplier
 */

public class SupplierEditBox extends Stage{
    private TextField inputName;
    private TextField inputPostcode;
    private int index;
    private AutoDraw ad = new AutoDraw();
    private ServerInterface server;
    private ComboBox postcodeCombo;

    /**
     * I have 2 constructors for this editBox as there are 2 cases in which it will be opened, 
     * the first is there is a supplier that needs to be edited and the second is the user wants
     * to create a new supplier, for this I set up the box slightly differently but the second 
     * half is more or less the same for both, adds or updates the selected supplier 
     */
    public SupplierEditBox(ServerInterface server, String type, int index){
        this.server=server;
        init(type);
        this.index = index;
        if (index!=-1){
            List<Supplier> suppliers = server.getSuppliers();
            Supplier supplier = suppliers.get(index);
            String postcodeName = supplier.getPostcode().getName();
            postcodeCombo.getSelectionModel().select(postcodeName);

            inputName.setText(supplier.getName());
        }
        setResizable(false);
        show();
    }

    public SupplierEditBox(ServerInterface server, String type){
        this.index = -1;
        this.server=server;
        init(type);
        show();
    }

    @Override
    public void close() {
        super.close();
    }

    public void init(String type){
        BorderPane mainPane = new BorderPane();
        GridPane editPane = ad.contentPaneConstructor("");
        GridPane labelPane = ad.contentPaneConstructor("");
        setScene(new Scene(mainPane));
        mainPane.setCenter(editPane);
        mainPane.setLeft(labelPane);

        setTitle(type+" Editor");
//        inputPostcode = new TextField();
        ArrayList<String> list = ad.stringsFromArray(server.getPostcodes());
        ObservableList<String> postComboList = FXCollections.observableArrayList(
                list);
        postcodeCombo = new ComboBox(postComboList);


        inputName=new TextField();
        Button add = new Button("Add/Update");
        add.setOnMouseClicked(new addOrUpdate(this));
        labelPane.add(new Label("Name: "),0,1);
        labelPane.add(new Label("Postcode: "),0,2);
        editPane.add(inputName,2,1,3,1);
        editPane.add(postcodeCombo,2,2,3,1);
        editPane.add(add,2,3);

    }

    class addOrUpdate implements EventHandler{
        SupplierEditBox box;

        public addOrUpdate(SupplierEditBox box){
            this.box=box;
        }

        public void handle(Event event) {
            String name = inputName.getText();


            if (postcodeCombo.getSelectionModel().isEmpty()||name.replaceAll(" ","").equals("")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You have not input any information");
                alert.setContentText("If you wish to add or update a supplier you must input their details");
                alert.showAndWait();
            }else {

                Postcode post = ad.ifInList(server.getPostcodes(), postcodeCombo.getSelectionModel().getSelectedItem().toString());
                if (index == -1) {
                    server.addSupplier(name, post);
                    box.close();
                } else {
                    Supplier editting = server.getSuppliers().get(index);
                    editting.setName(name);
                    editting.setPostcode(post);
                    box.close();
                }
            }
        }
    }
}
