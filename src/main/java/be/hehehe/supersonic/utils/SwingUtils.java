package be.hehehe.supersonic.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public class SwingUtils {
	public static void centerContainer(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = window.getSize();

		int newWidth = (screen.width - size.width) / 2;
		int newHeight = (screen.height - size.height) / 2;
		window.setLocation(newWidth, newHeight);
	}

	public static String formatDuration(long duration) {
		long minutes = TimeUnit.SECONDS.toMinutes(duration);
		long seconds = duration % 60;
		return minutes + ":" + StringUtils.leftPad("" + seconds, 2, "0");
	}

	public static void handleError(final Throwable e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ErrorInfo info = new ErrorInfo(null, e.getMessage(), null,
						null, e, null, null);
				JXErrorPane.showDialog(null, info);
			}
		});
	}

	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	public static void drawImage(BufferedImage image, Graphics g,
			Component parent) {
		Dimension size = parent.getSize();
		Color background = parent.getBackground();
		if (image != null && size.width > 0) {
			double ratio = (double) image.getHeight(null)
					/ image.getWidth(null);

			int effectiveWidth = 1;
			int effectiveHeight = (int) ratio;

			while (effectiveHeight < size.height && effectiveWidth < size.width) {
				effectiveWidth++;
				effectiveHeight = (int) (ratio * effectiveWidth);
			}

			g.setColor(background);
			g.fillRect(0, 0, size.width, size.height);

			int cornerx = Math.abs((size.width - effectiveWidth) / 2);
			int cornery = Math.abs((size.height - effectiveHeight) / 2);
			g.drawImage(image, cornerx, cornery, effectiveWidth + cornerx,
					effectiveHeight + cornery, 0, 0, image.getWidth(null),
					image.getHeight(null), null);
		}
	}
}
