package be.hehehe.supersonic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingWorker;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.subsonic.restapi.Child;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.model.AlbumModel;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService.Param;
import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.SwingUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Singleton
public class Library {
	public enum RefreshMode {
		INCREMENTAL("Incremental Refresh"), FULL("Full Refresh");

		private String name;

		private RefreshMode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final String LIBRARY_FILEPATH = "cache/library.zip";

	private List<AlbumModel> albums = Collections
			.synchronizedList(new ArrayList<AlbumModel>());
	@Inject
	SubsonicService subsonicService;

	@Inject
	Event<LibraryChangedEvent> libraryEvent;

	@PostConstruct
	public void loadFromFile() {
		File libraryFile = new File(LIBRARY_FILEPATH);
		if (libraryFile.exists()) {
			try {
				ZipInputStream zip = new ZipInputStream(new FileInputStream(
						libraryFile));
				zip.getNextEntry();
				String content = IOUtils.toString(zip);
				albums = new JSONDeserializer<List<AlbumModel>>()
						.deserialize(content);
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	private void saveToFile() throws SupersonicException {
		ZipOutputStream zip = null;
		File libraryFile = new File(LIBRARY_FILEPATH);
		String json = new JSONSerializer().deepSerialize(albums);
		try {
			zip = new ZipOutputStream(new FileOutputStream(libraryFile));
			zip.setLevel(Deflater.BEST_COMPRESSION);
			zip.putNextEntry(new ZipEntry("library.json"));
			IOUtils.write(json, zip);
			zip.closeEntry();
		} catch (IOException e) {
			throw new SupersonicException(e);
		} finally {
			IOUtils.closeQuietly(zip);
		}
	}

	public void refresh(final RefreshMode refreshMode)
			throws SupersonicException {
		new SwingWorker<Object, Integer>() {

			private int total = 0;

			@Override
			protected Object doInBackground() throws Exception {
				try {
					List<Child> list = fetchAlbumList();
					if (refreshMode == RefreshMode.FULL) {
						albums.clear();
					} else {
						list = incrementalFilter(list);
					}

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
										"getMusicDirectory",
										new Param(album.getId()));
								for (Child song : response2.getDirectory()
										.getChild()) {
									// TODO handle subdirectories
									if (!song.isIsDir() && !song.isIsVideo()) {
										SongModel songModel = new SongModel();
										songModel.setId(song.getId());
										songModel.setArtist(song.getArtist());
										songModel.setTitle(song.getTitle());
										songModel.setAlbum(song.getAlbum());
										songModel.setCoverId(albumModel
												.getCoverId());
										songModel.setTrack(song.getTrack());
										songModel.setSize(song.getSize());
										songModel.setDuration(song
												.getDuration());

										albumModel.getSongs().add(songModel);
										albumModel.setName(song.getAlbum());
										albumModel.setArtist(song.getArtist());
									}
								}
								return albumModel;
							}

						};
						threads.add(service.submit(callable));
					}
					total = list.size();
					publish(0);
					List<AlbumModel> albums = Lists.newArrayList();
					for (Future<AlbumModel> future : threads) {
						try {
							AlbumModel albumModel = future.get();
							albums.add(albumModel);
							publish(albums.size());
						} catch (Exception e) {
							SwingUtils.handleError(e);
						}
					}
					getAlbums().addAll(albums);
					Collections.sort(getAlbums());
					saveToFile();
				} catch (Exception e) {
					Exception se = new SupersonicException(
							"Could not refresh library.", e);
					SwingUtils.handleError(se);
				}

				return null;
			}

			@Override
			protected void process(List<Integer> progress) {
				Integer last = Iterables.getLast(progress);
				libraryEvent.fire(new LibraryChangedEvent(last, total, false));
			}

			@Override
			protected void done() {
				libraryEvent.fire(new LibraryChangedEvent(total, total, true));
			};
		}.execute();

	}

	private List<Child> incrementalFilter(List<Child> list) {
		Iterator<AlbumModel> it = albums.iterator();
		List<Child> processList = Lists.newArrayList();
		while (it.hasNext()) {
			AlbumModel model = it.next();
			boolean remove = true;
			for (Child child : list) {
				if (StringUtils.equals(child.getId(), model.getId())) {
					remove = false;
					break;
				}
			}
			if (remove) {
				it.remove();
			}
		}

		for (Child child : list) {
			boolean insert = true;
			for (AlbumModel model : albums) {
				if (StringUtils.equals(child.getId(), model.getId())) {
					insert = false;
					break;
				}
			}
			if (insert) {
				processList.add(child);
			}
		}

		return processList;
	}

	private List<Child> fetchAlbumList() throws SupersonicException {
		int step = 500;
		int count = -1;
		int offset = 0;
		List<Child> albumList = Lists.newArrayList();
		while (count != 0) {
			Response response = subsonicService.invoke("getAlbumList",
					new Param("type", "newest"), new Param("size", step),
					new Param("offset", offset));
			List<Child> list = response.getAlbumList().getAlbum();
			count = list.size();
			albumList.addAll(list);
			offset += step;
		}

		return albumList;
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
