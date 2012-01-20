package be.hehehe.supersonic.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.model.SongsTableModel;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
public class SongsPanel extends JPanel {

	@Inject
	Library library;

	private JXTable table;
	private SongsTableModel tableModel;

	@PostConstruct
	public void init() {
		buildFrame();
	}

	private void buildFrame() {
		setLayout(new MigLayout("", "[grow]", "[grow]"));

		table = new JXTable();
		add(table, "cell 0 0,grow");
		tableModel = new SongsTableModel(library.getSongs());
		table.setModel(tableModel);
	}

	public void onLibraryRefresh(@Observes LibraryChangedEvent e) {
		if (e.isDone()) {
			tableModel.clear();
			tableModel.addAll(library.getSongs());
		}
	}
}
