package be.hehehe.supersonic.events;

public class ControlsEvent {

	private boolean shuffle;
	private boolean repeat;

	public ControlsEvent(boolean shuffle, boolean repeat) {
		super();
		this.shuffle = shuffle;
		this.repeat = repeat;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

}
