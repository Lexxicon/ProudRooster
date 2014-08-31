/**
 * 
 */
package input.robot;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

/**
 * Created: Aug 30, 2014
 * 
 * @author Brian Holman
 *
 */
public class ImageGrabber {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(ImageGrabber.class);

	public static BufferedImage capture(HookedApplication application) {

		HDC hdcWindow = User32.INSTANCE.GetDC(application.getHwnd());
		HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

		RECT bounds = new RECT();
		User32Extra.INSTANCE.GetClientRect(application.getHwnd(), bounds);

		int width = bounds.right - bounds.left;
		int height = bounds.bottom - bounds.top;

		HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

		HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
		GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, WinGDIExtra.SRCCOPY);

		GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
		GDI32.INSTANCE.DeleteDC(hdcMemDC);

		BITMAPINFO bmi = new BITMAPINFO();
		bmi.bmiHeader.biWidth = width;
		bmi.bmiHeader.biHeight = -height;
		bmi.bmiHeader.biPlanes = 1;
		bmi.bmiHeader.biBitCount = 32;
		bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

		Memory buffer = new Memory(width * height * 4);
		GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

		GDI32.INSTANCE.DeleteObject(hBitmap);
		User32.INSTANCE.ReleaseDC(application.getHwnd(), hdcWindow);

		return image;
	}

	interface GDI32Extra extends GDI32 {

		GDI32Extra INSTANCE = (GDI32Extra) Native.loadLibrary("gdi32", GDI32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

		public boolean BitBlt(
				HDC hObject,
				int nXDest,
				int nYDest,
				int nWidth,
				int nHeight,
				HDC hObjectSource,
				int nXSrc,
				int nYSrc,
				DWORD dwRop);

	}

	interface WinGDIExtra extends WinGDI {

		public DWORD SRCCOPY = new DWORD(0x00CC0020);

	}

	interface User32Extra extends User32 {

		User32Extra INSTANCE = (User32Extra) Native.loadLibrary(
				"user32",
				User32Extra.class,
				W32APIOptions.DEFAULT_OPTIONS);

		public HDC GetWindowDC(HWND hWnd);

		public boolean GetClientRect(HWND hWnd, RECT rect);

	}
}
