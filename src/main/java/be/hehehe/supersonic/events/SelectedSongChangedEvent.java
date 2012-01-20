package be.hehehe.supersonic.events;

import be.hehehe.supersonic.model.SongModel;

public class SelectedSongChangedEvent {
	private SongModel song;

	public SelectedSongChangedEvent(SongModel song) {
		this.song = song;
	}

	public SongModel getSong() {
		return song;
	}

}
