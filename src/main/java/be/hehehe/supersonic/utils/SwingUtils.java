package be.hehehe.supersonic.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public class SwingUtils {
	public static void centerContainer(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = window.getPreferredSize();
		if (size == null) {
			size = window.getSize();
		}
		int newWidth = (screen.width - size.width) / 2;
		int newHeight = (screen.height - size.height) / 2;
		window.setLocation(newWidth, newHeight);
	}

	public static String formatDuration(long duration) {
		long minutes = TimeUnit.SECONDS.toMinutes(duration);
		long seconds = duration % 60;
		return minutes + ":" + StringUtils.leftPad("" + seconds, 2, "0");
	}

	public static void handleError(Throwable e) {
		ErrorInfo info = new ErrorInfo(null, e.getMessage(), null, null, e,
				null, null);
		JXErrorPane.showDialog(null, info);
	}

	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
