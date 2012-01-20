package be.hehehe.supersonic.events;

import be.hehehe.supersonic.model.SongModel;

public class PlayingSongChangedEvent {
	private SongModel song;

	public PlayingSongChangedEvent(SongModel song) {
		this.song = song;
	}

	public SongModel getSong() {
		return song;
	}
}
