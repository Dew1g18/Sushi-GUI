package comp1206.sushi.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import comp1206.sushi.common.*;
import comp1206.sushi.server.ServerInterface.UnableToDeleteException;


/**
 * Provides the Sushi Server user interface
 *
 * <<DEPRECIATED FILE>>ServerWindowFX has taken its place<</DEPRECIATED>>
 */
public class ServerWindow extends JFrame implements UpdateListener {

	private static final long serialVersionUID = -4661566573959270000L;
	private ServerInterface server;
	
	/**
	 * Create a new server window
	 * @param server instance of server to interact with
	 */
	public ServerWindow(ServerInterface server) {
		super("Sushi Server");
		this.server = server;
		this.setTitle(server.getRestaurantName() + " Server");
		server.addUpdateListener(this);

            //My display window (using fx) not sure how to get this to run in here..
        //UserInterface ui = new UserInterface();


        //Display window
		setSize(800,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		//Start timed updates
		startTimer();
	}
	
	/**
	 * Start the timer which updates the user interface based on the given interval to update all panels
	 */
	public void startTimer() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);     
        int timeInterval = 5;
        
        scheduler.scheduleAtFixedRate(() -> refreshAll(), 0, timeInterval, TimeUnit.SECONDS);
	}
	
	/**
	 * Refresh all parts of the server application based on receiving new data, calling the server afresh
	 */
	public void refreshAll() {
		
	}
	
	//@Override
	/**
	 * Respond to the model being updated by refreshing all data displays
	 */
	public void updated(UpdateEvent updateEvent) {
		refreshAll();
	}
//
}
//
//
