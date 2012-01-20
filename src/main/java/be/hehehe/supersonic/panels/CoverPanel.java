package be.hehehe.supersonic.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.enterprise.event.Observes;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import be.hehehe.supersonic.events.SelectedSongChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.CoverArtService;

@SuppressWarnings("serial")
@Singleton
public class CoverPanel extends JPanel {

	@Inject
	CoverArtService coverArtService;

	private BufferedImage image;

	public void loadCover(@Observes final SelectedSongChangedEvent e) {
		new SwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				SongModel song = e.getSong();
				image = ImageIO
						.read(coverArtService.getCover(song.getCoverId()));
				repaint();
				return null;
			}
		}.execute();
	}

	@Override
	protected void paintComponent(Graphics g) {
		//TODO maintain aspect ratio
		Dimension size = getSize();
		if (image != null && size.width > 0) {
			g.drawImage(image, 0, 0, size.width, size.height, 0, 0,
					image.getWidth(null), image.getHeight(null), null);
		}
	}
}
