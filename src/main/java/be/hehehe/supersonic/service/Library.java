package be.hehehe.supersonic.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang.ObjectUtils;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.Response;

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

	public void refresh() throws SupersonicException {
		try {
			Response response = subsonicService.invoke("getAlbumList",
					new Param("type", "newest"), new Param("size", "500"));
			List<Child> list = response.getAlbumList().getAlbum();
			Collections.sort(list, new ChildComparator());

			ExecutorService service = Executors.newFixedThreadPool(5);
			List<Future<AlbumModel>> threads = Lists.newArrayList();
			for (final Child album : list) {
				Callable<AlbumModel> callable = new Callable<AlbumModel>() {
					@Override
					public AlbumModel call() throws Exception {

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
							songModel.setTrack(song.getTrack());
							songModel.setSize(song.getSize());

							albumModel.getSongs().add(songModel);
							albumModel.setName(song.getAlbum());
							albumModel.setArtist(song.getArtist());
						}
						return albumModel;
					}

				};
				threads.add(service.submit(callable));

				List<AlbumModel> albums = Lists.newArrayList();
				for (Future<AlbumModel> future : threads) {
					AlbumModel albumModel = future.get();
					albums.add(albumModel);
				}
				this.albums = albums;

			}
		} catch (Exception e) {
			throw new SupersonicException("Could not refresh library.", e);
		}
	}

	public List<AlbumModel> getAlbums() {
		return albums;
	}

	private class ChildComparator implements Comparator<Child> {
		@Override
		public int compare(Child o1, Child o2) {
			return ObjectUtils.compare(o1.getTitle(), o2.getTitle());
		}
	}

}
