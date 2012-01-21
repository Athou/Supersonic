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

	public PlayingSongChangedEvent(SongModel song, State state) {
		this.song = song;
		this.state = state;
	}

	public SongModel getSong() {
		return song;
	}

	public State getState() {
		return state;
	}

}
