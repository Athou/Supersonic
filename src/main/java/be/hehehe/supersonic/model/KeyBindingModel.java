package be.hehehe.supersonic.model;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import be.hehehe.supersonic.events.SongEvent.Type;

public class KeyBindingModel {

	private Type type;
	private int keyCode;
	private int modifiers;

	public KeyBindingModel() {

	}

	public KeyBindingModel(Type type, int keyCode, int modifiers) {
		super();
		this.type = type;
		this.keyCode = keyCode;
		this.modifiers = modifiers;
	}

	public KeyBindingModel(KeyStroke keyStroke) {
		this.keyCode = keyStroke.getKeyCode();
		this.modifiers = keyStroke.getModifiers();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
			sb.append("CTRL ");
		}
		if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
			sb.append("SHIFT ");
		}
		if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
			sb.append("ALT ");
		}
		if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
			sb.append("ALT GR ");
		}
		if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
			sb.append("META ");
		}
		String toString = KeyStroke.getKeyStroke(keyCode, modifiers, false)
				.toString();
		String pressed = "pressed ";
		sb.append(toString.substring(toString.lastIndexOf(pressed)
				+ pressed.length()));
		return sb.toString();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

}
