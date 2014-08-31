/**
 * 
 */
package input.robot;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created: Aug 29, 2014
 * 
 * @author Brian Holman
 *
 */
public class MainDisplay extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1840632605350660208L;
	private static transient final Logger LOGGER = LoggerFactory.getLogger(MainDisplay.class);

	TextField outputField;
	Button prepHook;
	TextField hookedWindow;
	TextField hookedPID;

	HookedApplication application;
	BufferedImage image;

	private int thisID;

	/**
	 * @throws Exception
	 * 
	 */
	public MainDisplay() {
		super("Test");
		LOGGER.info("Creaeting main display");
		setup();
		Panel p = new Panel();

		p.setLayout(new FlowLayout(FlowLayout.LEADING));
		p.add(buildSendBox());
		p.add(buildHookStatus());
		p.add(buildPrepHookButton());
		p.add(buildImagePane());
		add(p);
	}

	private Panel buildImagePane() {
		Panel p = new Panel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6330092215673015468L;

			@Override
			public void paint(java.awt.Graphics g) {
				if (image != null) {
					g.drawImage(image, 0, 0, null);
				}
			}
		};
		return p;
	}

	private Panel buildSendBox() {
		Label textToSend = new Label("Text to send");
		outputField = new TextField(50);
		outputField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				LOGGER.info("{}", e);
				InputSender.sendString(application, outputField.getText());
				outputField.setText("");
			}
		});
		Panel f = new Panel();
		f.add(textToSend);
		f.add(outputField);
		return f;
	}

	private Panel buildPrepHookButton() {
		prepHook = new Button("Prepare Hook");
		prepHook.setEnabled(true);
		prepHook.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				LOGGER.debug("Prep hook request");
				final ApplicationGrabber grabber = new ApplicationGrabber();
				prepHook.setEnabled(false);
				prepHook.setLabel("Hooking...");
				grabber.setOnHook(new SimpleAction() {

					@Override
					public void execute() {
						if (grabber.getApplication() != null
								&& grabber.getApplication().getApplicationProcessPointer().getValue() != thisID) {
							application = grabber.getApplication();
							image = ImageGrabber.capture(application);
							hookedWindow.setText(application.getApplicationName());
							hookedPID.setText(String.valueOf(application.getApplicationProcessPointer().getValue()));
							Toolkit.getDefaultToolkit().removeAWTEventListener(grabber);
							prepHook.setEnabled(true);
							prepHook.setLabel("Prepare Hook");
						}
					}
				});
				Toolkit.getDefaultToolkit().addAWTEventListener(grabber, AWTEvent.FOCUS_EVENT_MASK);
				LOGGER.info("Application Grabber Hooked");
			}
		});
		Panel f = new Panel();
		f.add(prepHook);
		return f;
	}

	private Panel buildHookStatus() {

		Panel panel = new Panel();
		Label hookedWindowLabel = new Label("Hooked Window");
		hookedWindow = new TextField(20);
		hookedWindow.setEditable(false);
		Label hookedIDLabel = new Label("Hooked PID");
		hookedPID = new TextField(8);
		hookedPID.setEditable(false);
		panel.setLayout(new GridLayout(2, 2));
		panel.add(hookedWindowLabel);
		panel.add(hookedWindow);
		panel.add(hookedIDLabel);
		panel.add(hookedPID);

		return panel;
	}

	private void setup() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				LOGGER.info("Closing window");
				dispose();
			}
		});
	}

	public int getThisID() {
		return thisID;
	}

	public void setThisID(int thisID) {
		this.thisID = thisID;
	}
}
