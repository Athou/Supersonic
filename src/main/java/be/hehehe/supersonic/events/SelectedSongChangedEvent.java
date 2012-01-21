package be.hehehe.supersonic.events;

import be.hehehe.supersonic.model.SongModel;

/**
 * Fired when the selected song in the playlist changes
 * 
 * 
 */
public class SelectedSongChangedEvent {
	private SongModel song;

	public SelectedSongChangedEvent(SongModel song) {
		this.song = song;
	}

	public SongModel getSong() {
		return song;
	}

}
