package be.hehehe.supersonic.utils;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class SupersonicException extends Exception {

	private String message;
	private Throwable t;

	public SupersonicException(String message) {
		this(message, null);
	}

	public SupersonicException(Throwable t) {
		this(t.getMessage(), t);
	}

	public SupersonicException(String message, Throwable t) {
		this.message = message;
		this.t = t;
		Logger.getLogger(SupersonicException.class).error(message, t);
	}

	public String getMessage() {
		return message;
	}

	public Throwable getT() {
		return t;
	}

}
