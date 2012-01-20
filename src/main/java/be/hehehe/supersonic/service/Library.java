package be.hehehe.supersonic.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingWorker;

import org.subsonic.restapi.Child;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.model.AlbumModel;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService.Param;
import be.hehehe.supersonic.utils.SupersonicException;

import com.google.common.collect.Lists;

@Singleton
public class Library {

	private List<AlbumModel> albums;

	@Inject
	SubsonicService subsonicService;

	@Inject
	Event<LibraryChangedEvent> libraryEvent;

	public void refresh() throws SupersonicException {
		try {
			List<Child> list = subsonicService
					.invoke("getAlbumList", new Param("type", "newest"),
							new Param("size", "500")).getAlbumList().getAlbum();

			final List<AlbumModel> albums = Collections
					.synchronizedList(new ArrayList<AlbumModel>());
			for (final Child album : list) {
				new SwingWorker<Object, AlbumModel>() {
					@Override
					protected Object doInBackground() throws Exception {
						AlbumModel albumModel = new AlbumModel();
						albumModel.setId(album.getId());
						albumModel.setCoverId(album.getCoverArt());
						Response response2 = subsonicService.invoke(
								"getMusicDirectory", new Param(album.getId()));
						for (Child song : response2.getDirectory().getChild()) {
							SongModel songModel = new SongModel();
							songModel.setId(song.getId());
							songModel.setArtist(song.getArtist());
							songModel.setTitle(song.getTitle());
							songModel.setAlbum(song.getAlbum());
							songModel.setTrack(song.getTrack());
							songModel.setSize(song.getSize());
							songModel.setDuration(song.getDuration());

							albumModel.getSongs().add(songModel);
							albumModel.setName(song.getAlbum());
							albumModel.setArtist(song.getArtist());
						}
						publish(albumModel);
						return null;
					}

					@Override
					protected void process(List<AlbumModel> chunks) {
						for (AlbumModel model : chunks) {
							albums.add(model);
							libraryEvent.fire(new LibraryChangedEvent(albums
									.size(), albums.size(), false));
						}
					}
				}.execute();
			}
			this.albums = albums;

			libraryEvent.fire(new LibraryChangedEvent(albums.size(), albums
					.size(), true));
		} catch (Exception e) {
			throw new SupersonicException("Could not refresh library.", e);
		}
	}

	public List<AlbumModel> getAlbums() {
		return albums;
	}

	public List<SongModel> getSongs() {
		if (albums == null) {
			return null;
		}
		List<SongModel> songs = Lists.newArrayList();
		for (AlbumModel album : albums) {
			songs.addAll(album.getSongs());
		}
		return songs;
	}
}
