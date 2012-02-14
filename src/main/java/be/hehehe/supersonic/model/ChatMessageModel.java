package be.hehehe.supersonic.model;

public class ChatMessageModel {
	private String userName;
	private long time;
	private String message;

	public ChatMessageModel(String userName, long time, String message) {
		this.userName = userName;
		this.time = time;
		this.message = message;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
