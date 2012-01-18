package be.hehehe.supersonic.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class SwingUtils {
	public static void centerContainer(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension preferredSize = window.getPreferredSize();
		if (preferredSize != null) {
			int newWidth = (screen.width - preferredSize.width) / 2;
			int newHeight = (screen.height - preferredSize.height) / 2;
			window.setLocation(newWidth, newHeight);
		}
	}
}
