package comp1206.sushi;
import javax.swing.*;

import comp1206.sushi.mock.MockServer;
import comp1206.sushi.server.ServerWindow;
import comp1206.sushi.server.ServerWindowFX;
import static javafx.application.Application.launch;

public class ServerApplication {

	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());

			ServerWindowFX ui =new ServerWindowFX();
//			ui.addServerAndLaunch(new MockServer(),argv);  //THIS CURRENTLY DOESN'T WORK SO I WILL ADD THE MOCK SERVER IN THE START METHOD IN ui
//			System.out.println("Got here");
			ui.launch(ServerWindowFX.class,argv);

		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		//SwingUtilities.invokeLater(() -> new ServerWindow(new MockServer()));
	}
}
/**
 * Was told in a lab by Seb that I am allowed to make the changes that I have to the serverApplication
 * in order to get my program to work as an FX application
 */

