package be.hehehe.supersonic.panels;

import java.awt.Dimension;
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
				Dimension size = getSize();
				if (image != null && size.width > 0) {
					double ratio = (double) image.getHeight(null)
							/ image.getWidth(null);

					int effectiveWidth = 1;
					int effectiveHeight = (int) ratio;

					while (effectiveHeight < size.height
							&& effectiveWidth < size.width) {
						effectiveWidth++;
						effectiveHeight = (int) (ratio * effectiveWidth);
					}

					g.setColor(getBackground());
					g.fillRect(0, 0, size.width, size.height);

					int cornerx = Math.abs((size.width - effectiveWidth) / 2);
					int cornery = Math.abs((size.height - effectiveHeight) / 2);
					g.drawImage(image, cornerx, cornery, effectiveWidth
							+ cornerx, effectiveHeight + cornery, 0, 0,
							image.getWidth(null), image.getHeight(null), null);
				}
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
