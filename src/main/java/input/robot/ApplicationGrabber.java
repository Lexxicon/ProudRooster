/**
 * 
 */
package input.robot;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Created: Aug 29, 2014
 * 
 * @author Brian Holman
 *
 */
public class ApplicationGrabber implements AWTEventListener {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ApplicationGrabber.class);

	private SimpleAction listener;

	HookedApplication application;

	/**
	 * 
	 */
	public ApplicationGrabber() {
	}

	public void setOnHook(SimpleAction listener) {
		this.listener = listener;
	}

	public void eventDispatched(AWTEvent event) {
		LOGGER.debug("Event {}", event);
		if ((FocusEvent.FOCUS_LOST & event.getID()) == FocusEvent.FOCUS_LOST) {
			LOGGER.debug("Focus lost");
			try {
				application = new HookedApplication();
				locateActiveWindow(application);
			} catch (Exception e) {
				LOGGER.error("Failed to locate window. {}", e);
			}
			listener.execute();
		}
	}

	/**
	 * @return the application
	 */
	public HookedApplication getApplication() {
		return application;
	}

	/**
	 * DRAGONS LIVE HERE. don't fuck with it.
	 */

	private static final int MAX_TITLE_LENGTH = 1024;
	// found at
	// http://msdn.microsoft.com/en-us/library/windows/desktop/ms684880%28v=vs.85%29.aspx
	private static final int PROCESS_QUERY_INFORMATION = 0x0400;
	private static final int PROCESS_VM_READ = 0x0010;

	public static void locateActiveWindow(HookedApplication application) throws Exception {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();

		User32.INSTANCE.GetWindowText(foregroundWindow, buffer, MAX_TITLE_LENGTH);
		LOGGER.debug("Active window title: {}", Native.toString(buffer));

		IntByReference pointer = new IntByReference();
		User32.INSTANCE.GetWindowThreadProcessId(foregroundWindow, pointer);
		LOGGER.debug("Window Process ptr: {} value: {}", pointer, pointer.getValue());
		HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ,
				false,
				pointer.getValue());
		LOGGER.debug("Active window process: {}", process);
		application.setApplicationProcessPointer(pointer);
		application.setApplicationName(Native.toString(buffer));
		application.setApplicationHandle(process);
		application.setHwnd(foregroundWindow);
	}
}
