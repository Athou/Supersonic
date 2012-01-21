package be.hehehe.supersonic.events;

/**
 * Fired when song is playing (song progress)
 * 
 */
public class PlayingSongProgressEvent {
	private long current;
	private long total;

	public PlayingSongProgressEvent(long current, long total) {
		this.current = current;
		this.total = total;
	}

	public long getCurrent() {
		return current;
	}

	public long getTotal() {
		return total;
	}

	public int getPercentage() {
		return (int) ((100 * current) / total);
	}

}
