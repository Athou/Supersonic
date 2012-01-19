package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXErrorPane;

import be.hehehe.supersonic.model.AlbumModel;
import be.hehehe.supersonic.service.Library;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
public class AlbumsPanel extends JPanel {

	@Inject
	Library library;

	@PostConstruct
	public void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

	}
}
