package be.hehehe.supersonic.model;

import java.util.List;

import com.google.common.collect.Lists;

public class AlbumModel {
	private String id;
	private String name;
	private String artist;
	private String coverId;
	private List<SongModel> songs = Lists.newArrayList();

	@Override
	public String toString() {
		return "AlbumModel [id=" + id + ", name=" + name + ", artist=" + artist
				+ ", coverId=" + coverId + ", songs=" + songs + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getCoverId() {
		return coverId;
	}

	public void setCoverId(String coverId) {
		this.coverId = coverId;
	}

	public List<SongModel> getSongs() {
		return songs;
	}

	public void setSongs(List<SongModel> songs) {
		this.songs = songs;
	}

}
