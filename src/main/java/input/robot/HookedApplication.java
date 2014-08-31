/**
 * 
 */
package input.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Created: Aug 30, 2014
 * 
 * @author Brian Holman
 *
 */
public class HookedApplication {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(HookedApplication.class);
	private String applicationName;
	private IntByReference applicationProcessPointer;
	private HANDLE applicationHandle;
	private HWND hwnd;

	/**
	 * 
	 */
	public HookedApplication() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName
	 *            the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the applicationProcessPointer
	 */
	public IntByReference getApplicationProcessPointer() {
		return applicationProcessPointer;
	}

	/**
	 * @param applicationProcessPointer
	 *            the applicationProcessPointer to set
	 */
	public void setApplicationProcessPointer(IntByReference applicationProcessPointer) {
		this.applicationProcessPointer = applicationProcessPointer;
	}

	/**
	 * @return the applicationHandle
	 */
	public HANDLE getApplicationHandle() {
		return applicationHandle;
	}

	/**
	 * @param applicationHandle
	 *            the applicationHandle to set
	 */
	public void setApplicationHandle(HANDLE applicationHandle) {
		this.applicationHandle = applicationHandle;
	}

	public HWND getHwnd() {
		return hwnd;
	}

	public void setHwnd(HWND hwnd) {
		this.hwnd = hwnd;
	}

}
