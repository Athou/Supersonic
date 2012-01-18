package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JPanel;

import org.subsonic.restapi.Artist;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.Index;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.Player;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@SuppressWarnings("serial")
public class AlbumsPanel extends JPanel {
	@Inject
	SubsonicService subsonicService;

	@PostConstruct
	public void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		addAlbums();

	}

	private void addAlbums() {
		try {
			Response response = subsonicService.invoke("getIndexes");
			for (Index index : response.getIndexes().getIndex()) {
				for (Artist artist : index.getArtist()) {
					Response response2 = subsonicService.invoke(
							"getMusicDirectory", new Param(artist.getId()));
					for (Child child : response2.getDirectory().getChild()) {
						System.out.println(child.getId() + " "
								+ child.getTitle());
						break;
					}
					break;
				}
				break;
			}

			Player player = new Player();
			player.start(subsonicService.invokeBinary("stream", new Param("2f686f6d652f616e6f2f737562736f6d757369632f41205065726665637420436972636c65202d20546869727465656e74682053746570202d20323030332f30392d415f506572666563745f436972636c652d5468655f4e757273655f57686f5f4c6f7665645f4d652e6d7033")));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
