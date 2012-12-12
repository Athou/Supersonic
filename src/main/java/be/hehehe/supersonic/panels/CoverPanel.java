package be.hehehe.supersonic.panels;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.CoverArtService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class CoverPanel extends JPanel {

	@Inject
	CoverArtService coverArtService;

	@Inject
	CoverDialog coverDialog;

	private BufferedImage image;
	private SongModel songModel;

	@PostConstruct
	public void init() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (image != null) {
					coverDialog.open(image, songModel);
				}
			}
		});
	}

	public void loadCover(@Observes final SongEvent e) {
		songModel = e.getSong();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (e.getType() == Type.SELECTION_CHANGED) {
			new SwingWorker<Object, Void>() {
				@Override
				protected Object doInBackground() throws Exception {
					SongModel song = e.getSong();
					image = coverArtService.getCoverImage(song.getCoverId());
					return null;
				}

				@Override
				protected void done() {
					setVisible(false);
					setVisible(true);
				};
			}.execute();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		SwingUtils.drawImage(image, g, this);
	}
}
