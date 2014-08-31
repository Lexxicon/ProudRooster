/**
 * 
 */
package input.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created: Aug 29, 2014
 * 
 * @author Brian Holman
 *
 */
public class EntryPoint {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);

	public static void main(String[] args) {
		LOGGER.info("Starting");
		MainDisplay dis = new MainDisplay();
		dis.setSize(800, 600);
		dis.setVisible(true);
		Runtime.getRuntime().addShutdownHook(new Thread("Exit Hook") {
			/*
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				super.run();
				LOGGER.info("Shutting down");
			}
		});

		HookedApplication thisApp = new HookedApplication();
		try {
			ApplicationGrabber.locateActiveWindow(thisApp);
			dis.setThisID(thisApp.getApplicationProcessPointer().getValue());
		} catch (Exception e) {
			LOGGER.error("Failed to find this app. {}", e);
			System.exit(0);
		}

		LOGGER.info("Exiting");
	}
}
