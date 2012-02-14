package be.hehehe.supersonic.model;

import java.awt.image.BufferedImage;

public class NowPlayingModel extends SongModel {
	private String userName;
	private BufferedImage image;
	private int minutesAgo;

	public NowPlayingModel(String songId, String userName, String artist, String title,
			int minutesAgo, BufferedImage image) {
		setId(songId);
		setTitle(title);
		setArtist(artist);
		this.userName = userName;
		this.minutesAgo = minutesAgo;
		this.image = image;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getMinutesAgo() {
		return minutesAgo;
	}

	public void setMinutesAgo(int minutesAgo) {
		this.minutesAgo = minutesAgo;
	}

}
