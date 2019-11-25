package comp1206.sushi.server;

import comp1206.sushi.common.Model;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;


/**
 * @author Dave Waddington -30091055
 *
 * A bit like the GeneralPane, this saves a lot of code duplication but in an even cooler way
 * During this projecty I learned of generic classes and decided they were one of my new favourite things. 
 * Even though my implementation of generics is almost pointless. 
 * 
 * All the methods in this class are my own
 * 
 * reflection on this class and retrospectively the generalPane too, 
 * I kind of wish I'd made these abstract classes to ensure that I always remember to implement things when I come 
 * to extend it days later
 * 
 */

public class GeneralEditBox extends Stage{
//    protected TextField inputName;
//    private int index;
    protected AutoDraw ad = new AutoDraw();
    protected ServerInterface server;
    protected Button add;
    protected GridPane editPane;
    protected BorderPane mainPane;

    protected String type;
//    protected GridPane labelPane;



    public <T extends Model> GeneralEditBox(ServerInterface server, T object){
        this.server=server;
        this.type = object.getClass().getName().replaceAll("comp1206.sushi.common.","");
//        System.out.println(type);
        init(object);
//        inputName.setText(type.getName());
        setResizable(false);
        show();
    }

    public GeneralEditBox(ServerInterface server, String type){
        this.server=server;
        this.type=type;
        init(type);
        setResizable(false);
        show();
    }

    @Override
    public void close() {
        super.close();
    }

    public <T extends Model>void init(T type){
        setAlwaysOnTop(true);
        mainPane = new BorderPane();
        editPane = ad.contentPaneConstructor("");
        setScene(new Scene(mainPane));
        mainPane.setCenter(editPane);

        String className = type.getClass().getSimpleName();
        setTitle(className+" Editor");
        this.add = new Button("Add/Update");
        BorderPane.setAlignment(add, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(add,new Insets(10,10,10,10));
        mainPane.setBottom(add);

    }

    public void init(String type){
        setAlwaysOnTop(true);
        mainPane = new BorderPane();
        editPane = ad.contentPaneConstructor("");
        setScene(new Scene(mainPane));
        mainPane.setCenter(editPane);

        setTitle(type+" Editor");
        this.add = new Button("Add/Update");
        BorderPane.setAlignment(add, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(add,new Insets(10,10,10,10));
        mainPane.setBottom(add);


    }



    public void handleAddButton(EventHandler handler){
        add.setOnMouseClicked(handler);
    }

}
