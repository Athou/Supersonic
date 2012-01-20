package be.hehehe.supersonic.events;

public class VolumeChangedEvent {
	private float volume;

	public VolumeChangedEvent(float volume) {
		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}

}
