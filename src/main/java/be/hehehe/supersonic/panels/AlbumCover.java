package be.hehehe.supersonic.panels;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class AlbumCover extends JPanel {
	public AlbumCover(String name, BufferedImage image) {

		setLayout(new MigLayout("", "[]", "[][]"));

		JLabel imageLabel = new JLabel("New label");
		imageLabel.setIcon(new ImageIcon(image));
		add(imageLabel, "cell 0 0");

		JLabel nameLabel = new JLabel(name);
		add(nameLabel, "cell 0 1");

	}

}
