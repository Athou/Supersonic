package be.hehehe.supersonic.service;

import java.net.URL;

import javax.inject.Named;
import javax.swing.ImageIcon;

@Named
public class IconService {

	public ImageIcon getIcon(String iconName) {
		return getIcon(iconName, null);
	}

	public ImageIcon getIcon(String iconName, String description) {
		ImageIcon icon = null;
		URL imgURL = getClass().getResource("/icons/" + iconName + ".png");
		icon = new ImageIcon(imgURL, description);
		return icon;
	}
}
