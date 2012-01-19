package be.hehehe.supersonic.panels;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@SuppressWarnings("serial")
public class AlbumCover extends JPanel {
	private JLabel imageLabel;

	public AlbumCover(SubsonicService service, Model model) {

		setLayout(new MigLayout("", "[]", "[][]"));

		imageLabel = new JLabel();
		imageLabel.setToolTipText(model.getName());
		add(imageLabel, "cell 0 0,alignx center");
		loadIcon(service, model);

	}

	public void loadIcon(final SubsonicService service, final Model model) {
		new SwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				BufferedImage image = ImageIO.read(service.invokeBinary(
						"getCoverArt", new Param(model.getCoverId()),
						new Param("size", "100")));
				imageLabel.setIcon(new ImageIcon(image));
				return null;
			}
		}.execute();

	}

	public static class Model {
		private String name;
		private String coverId;

		public Model(String name, String coverId) {
			this.name = name;
			this.coverId = coverId;
		}

		public String getName() {
			return name;
		}

		public String getCoverId() {
			return coverId;
		}

	}
}
