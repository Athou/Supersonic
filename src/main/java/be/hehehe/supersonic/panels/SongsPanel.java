package be.hehehe.supersonic.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.model.SongsTableModel;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class SongsPanel extends JScrollPane {

	@Inject
	Library library;

	private JXTable table;
	private SongsTableModel tableModel;

	@PostConstruct
	public void init() {
		buildFrame();
	}

	private void buildFrame() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		setViewportView(panel);

		table = new JXTable();
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(table, "cell 0 0,grow");
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
