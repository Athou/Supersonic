package be.hehehe.supersonic.events;

public class DownloadingEvent {
	private int percentage;

	public DownloadingEvent(int percentage) {
		this.percentage = percentage;
	}

	public int getPercentage() {
		return percentage;
	}

}
