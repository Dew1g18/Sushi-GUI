package comp1206.sushi.server;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author Dave Waddington -30091055
 * This class just saves me from setting all of this stuff every time I create a new pane (as I do this a lot)
 * Basically just here to prevent some code duplication and save some time. 
 * 
 * This is part of what allows me to keep my panes looking so consistent and in my opinion pretty profesional. 
 * this is where I would implement global textType changes etc but decided not to as I thought it looked fine
 * 
 * All the methods in this class are my own. 
 */
public class GeneralPane extends GridPane {

    public GeneralPane(String whatPane, ListView listView, ServerInterface serverInterface, TextField textInput) {
        GridPane postPane = contentPaneConstructor(whatPane + ":");
        listView = new ListView();
    }

    public GridPane contentPaneConstructor(String paneName) {
        GridPane contPane = new GridPane();
        contPane.setPadding(new Insets(10, 10, 10, 10));
        contPane.setHgap(10);
        contPane.setVgap(10);
        Label top = new Label(paneName);
        contPane.add(top, 0, 0, 2, 1);
        return contPane;
    }
}
