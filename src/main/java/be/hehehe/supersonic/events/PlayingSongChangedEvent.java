package be.hehehe.supersonic.events;

import be.hehehe.supersonic.Player.State;
import be.hehehe.supersonic.model.SongModel;

/**
 * Fired when the status of the current song changes
 * 
 */
public class PlayingSongChangedEvent {
	private SongModel song;
	private State state;
	private int skipToPercentage;

	public PlayingSongChangedEvent(SongModel song, State state) {
		this.song = song;
		this.state = state;
	}

	public PlayingSongChangedEvent(SongModel song, State state,
			int skipToPercentage) {
		this.song = song;
		this.state = state;
		this.skipToPercentage = skipToPercentage;
	}

	public int getSkipTo() {
		return skipToPercentage;
	}

	public SongModel getSong() {
		return song;
	}

	public State getState() {
		return state;
	}

}
