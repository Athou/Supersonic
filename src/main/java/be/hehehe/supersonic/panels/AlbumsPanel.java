package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;

import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class AlbumsPanel extends JPanel {

	@Inject
	Library library;

	@PostConstruct
	public void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

	}
}
