package be.hehehe.supersonic.model;

public class SongModel {
	private String id;
	private String title;
	private String artist;
	private AlbumModel album;

	private int track;
	private long size;
	private int duration;

	@Override
	public String toString() {
		return "SongModel [id=" + id + ", title=" + title + ", artist="
				+ artist + ", album=" + album.getName() + ", track=" + track + ", size="
				+ size + ", duration=" + duration + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public AlbumModel getAlbum() {
		return album;
	}

	public void setAlbum(AlbumModel album) {
		this.album = album;
	}

}
