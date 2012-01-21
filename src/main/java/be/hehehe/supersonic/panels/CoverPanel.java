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

import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.CoverArtService;

@SuppressWarnings("serial")
@Singleton
public class CoverPanel extends JPanel {

	@Inject
	CoverArtService coverArtService;

	private BufferedImage image;

	public void loadCover(@Observes final SongEvent e) {
		if (e.getType() == Type.SELECTION_CHANGED) {
			new SwingWorker<Object, Void>() {
				@Override
				protected Object doInBackground() throws Exception {
					SongModel song = e.getSong();
					image = ImageIO.read(coverArtService.getCover(song
							.getCoverId()));
					// repaint();
					setVisible(false);
					setVisible(true);
					return null;
				}
			}.execute();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = getSize();
		if (image != null && size.width > 0) {
			double ratio = (double) image.getHeight(null)
					/ image.getWidth(null);

			int effectiveWidth = 1;
			int effectiveHeight = (int) ratio;

			while (effectiveHeight < size.height && effectiveWidth < size.width) {
				effectiveWidth++;
				effectiveHeight = (int) (ratio * effectiveWidth);
			}

			g.setColor(getBackground());
			g.fillRect(0, 0, size.width, size.height);

			int cornerx = Math.abs((size.width - effectiveWidth) / 2);
			int cornery = Math.abs((size.height - effectiveHeight) / 2);
			g.drawImage(image, cornerx, cornery, effectiveWidth + cornerx,
					effectiveHeight + cornery, 0, 0, image.getWidth(null),
					image.getHeight(null), null);
		}
	}
}
