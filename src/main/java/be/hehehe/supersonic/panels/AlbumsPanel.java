package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.subsonic.restapi.Child;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@SuppressWarnings("serial")
public class AlbumsPanel extends JPanel {

	@Inject
	SubsonicService subsonicService;

	@PostConstruct
	public void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		new AlbumLoader().execute();

	}

	private void addCover(final AlbumCover.Model model) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				add(new AlbumCover(model));
			}
		});
	}

	private class AlbumLoader extends SwingWorker<Object, AlbumCover.Model> {

		@Override
		protected Object doInBackground() throws Exception {
			Response response = subsonicService.invoke("getAlbumList",
					new Param("type", "newest"), new Param("size", "500"));
			for (Child child : response.getAlbumList().getAlbum()) {
				String coverArtId = child.getCoverArt();
				BufferedImage image = ImageIO.read(subsonicService
						.invokeBinary("getCoverArt", new Param(coverArtId),
								new Param("size", "100")));
				AlbumCover.Model model = new AlbumCover.Model(child.getTitle(),
						image);
				publish(model);
			}
			return null;
		}

		@Override
		protected void process(List<AlbumCover.Model> chunks) {
			for (AlbumCover.Model model : chunks) {
				addCover(model);
			}
		}
	}
}
