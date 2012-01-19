package be.hehehe.supersonic.model;

public class SongModel {
	private String id;
	private String title;
	private String artist;
	private int track;
	private long size;

	@Override
	public String toString() {
		return "SongModel [id=" + id + ", title=" + title + ", artist="
				+ artist + ", track=" + track + ", size=" + size + "]";
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

}
