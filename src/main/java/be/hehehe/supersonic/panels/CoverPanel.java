package be.hehehe.supersonic.panels;

import java.awt.image.BufferedImage;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.events.SelectedSongChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@SuppressWarnings("serial")
@Singleton
public class CoverPanel extends JPanel {

	@Inject
	SubsonicService subsonicService;

	private JLabel label;

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("", "[]", "[]"));
		label = new JLabel();
		add(label, "cell 0 0,grow");
	}

	public void loadCover(@Observes final SelectedSongChangedEvent e) {
		new SwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				SongModel song = e.getSong();
				BufferedImage image = ImageIO.read(subsonicService
						.invokeBinary("getCoverArt", new Param(song.getAlbum()
								.getCoverId())));
				label.setIcon(new ImageIcon(image));
				return null;
			}
		}.execute();
	}
}
