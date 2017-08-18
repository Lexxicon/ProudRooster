/**
 * 
 */
package input.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.platform.win32.WinUser.KEYBDINPUT;
import com.sun.jna.win32.W32APIOptions;

/**
 * Created: Aug 30, 2014
 * 
 * @author Brian Holman
 *
 */
public class InputSender {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(InputSender.class);
	private static final int KEY_PRESSED = 0x0100;
	private static final int KEY_RELEASED = 0x0101;

	public static void sendString(HookedApplication application, String data) {
		for (char c : data.toCharArray()) {
			INPUT in = new INPUT();
			in.input.setType(KEYBDINPUT.class);
			in.type = new DWORD(1);
			in.input.ki.wVk = new WORD(MyUser32.INSTANCE.VkKeyScan(c));
			in.input.ki.dwFlags = new DWORD();
			DWORD word = new DWORD(1);

			User32.INSTANCE.SetForegroundWindow(application.getHwnd());
			User32.INSTANCE.SendInput(word, new INPUT[] { in }, in.size());
			in.input.ki.dwFlags = new DWORD(KEYBDINPUT.KEYEVENTF_KEYUP);
			User32.INSTANCE.SendInput(word, new INPUT[] { in }, in.size());

		}
		// User32.INSTANCE.SetForegroundWindow(application.getHwnd());
		// User32.INSTANCE.SendInput(word, new INPUT[] { in }, in.size());
		// in.input.ki.dwFlags = new DWORD(KEYBDINPUT.KEYEVENTF_KEYUP);
		// User32.INSTANCE.SendInput(word, new INPUT[] { in }, in.size());
	}
}

interface MyUser32 extends User32 {
	MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class, W32APIOptions.DEFAULT_OPTIONS);

	/**
	 * Sends the specified message to a window or windows.
	 *
	 * @param hWnd
	 *            A handle to the window whose window procedure will receive the
	 *            message.
	 * @param Msg
	 *            The message to be sent.
	 * @param wParam
	 *            Additional message-specific information.
	 * @param lParam
	 *            Additional message-specific information.
	 * @return The result of the message processing; it depends on the message
	 *         sent.
	 */
	LRESULT SendMessage(HWND hWnd, int Msg, WPARAM wParam, String lParam);

	/**
	 * Translates (maps) a virtual-key code into a scan code or character value,
	 * or translates a scan code into a virtual-key code.
	 *
	 * @param uCode
	 *            The virtual key code or scan code for a key.
	 * @param uMapType
	 *            The translation to be performed.
	 * @return The return value is either a scan code, a virtual-key code, or a
	 *         character value, depending on the value of uCode and uMapType. If
	 *         there is no translation, the return value is zero.
	 */
	int MapVirtualKey(int uCode, int uMapType);

	/**
	 * Translates a character to the corresponding virtual-key code and shift
	 * state for the current keyboard.
	 *
	 * @param ch
	 *            The character to be translated into a virtual-key code.
	 * @return On success, the low-order byte contains the virtual-key code and
	 *         the high-order byte contains the shift state; -1 on failure.
	 */
	short VkKeyScan(char ch);
}