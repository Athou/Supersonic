package be.hehehe.supersonic.model;

import java.awt.Dimension;

public class ApplicationStateModel {

	private int windowState;
	private Dimension windowSize;
	private Dimension searchPanel;
	private Dimension coverPanel;

	public int getWindowState() {
		return windowState;
	}

	public void setWindowState(int windowState) {
		this.windowState = windowState;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}

	public Dimension getSearchPanel() {
		return searchPanel;
	}

	public void setSearchPanel(Dimension searchPanel) {
		this.searchPanel = searchPanel;
	}

	public Dimension getCoverPanel() {
		return coverPanel;
	}

	public void setCoverPanel(Dimension coverPanel) {
		this.coverPanel = coverPanel;
	}

}
