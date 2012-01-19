package be.hehehe.supersonic.panels;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class AlbumCover extends JPanel {
	public AlbumCover(Model model) {

		setLayout(new MigLayout("", "[]", "[][]"));

		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(model.getImage()));
		add(imageLabel, "cell 0 0,alignx center");

		JLabel nameLabel = new JLabel(model.getName());
		add(nameLabel, "cell 0 1,alignx center");

	}

	public static class Model {
		private String name;
		private BufferedImage image;

		public Model(String name, BufferedImage image) {
			this.name = name;
			this.image = image;
		}

		public String getName() {
			return name;
		}

		public BufferedImage getImage() {
			return image;
		}

	}
}
