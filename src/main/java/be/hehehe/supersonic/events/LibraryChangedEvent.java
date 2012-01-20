package be.hehehe.supersonic.events;

public class LibraryChangedEvent {
	private int total;
	private int progress;
	private boolean done;

	public LibraryChangedEvent(int progress, int total, boolean done) {
		this.total = total;
		this.progress = progress;
		this.done = done;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}
