package be.hehehe.supersonic.events;

import be.hehehe.supersonic.model.SongModel;

public class SongEvent {
	public static enum Type {
		PAUSE, PLAY, STOP, PROGRESS, SKIP_TO, SELECTION_CHANGED;
	}

	private Type type;
	private SongModel song;

	private long currentPosition;
	private long total;
	private int skipToPercentage;

	public SongEvent(Type type) {
		this.type = type;
	}

	public int getPercentage() {
		return (int) ((100 * currentPosition) / total);
	}

	/* GETTERS AND SETTERS */

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public SongModel getSong() {
		return song;
	}

	public void setSong(SongModel song) {
		this.song = song;
	}

	public long getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(long currentPosition) {
		this.currentPosition = currentPosition;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getSkipToPercentage() {
		return skipToPercentage;
	}

	public void setSkipToPercentage(int skipToPercentage) {
		this.skipToPercentage = skipToPercentage;
	}

}
