package be.hehehe.supersonic.panels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class CoverDialog extends JFrame {

	private BufferedImage image;

	@Inject
	IconService iconService;

	@PostConstruct
	public void init() {
		setTitle("Cover");
		setIconImage(iconService.getIcon("supersonic-big").getImage());
		add(new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				SwingUtils.drawImage(image, g, this);
			}
		});
	}

	public void open(BufferedImage image, SongModel songModel) {
		this.image = image;
		setSize(image.getWidth(null), image.getHeight(null));
		setTitle(songModel.getArtist() + " - " + songModel.getAlbum());
		setVisible(false);
		setVisible(true);
		SwingUtils.centerContainer(this);
	}
}
