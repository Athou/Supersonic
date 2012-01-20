package be.hehehe.supersonic.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.events.SelectedSongChangedEvent;
import be.hehehe.supersonic.model.SongsTableModel;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class SongsPanel extends JPanel {

	@Inject
	Library library;

	@Inject
	Event<SelectedSongChangedEvent> songEvent;

	private JXTable table;
	private SongsTableModel tableModel;

	@PostConstruct
	public void init() {
		buildFrame();
	}

	private void buildFrame() {

		setLayout(new MigLayout("", "[grow]", "[grow]"));

		table = new JXTable();
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(true);
		table.setFillsViewportHeight(true);
		tableModel = new SongsTableModel(library.getSongs());
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int row = table.convertRowIndexToModel(table
								.getSelectedRow());
						songEvent.fire(new SelectedSongChangedEvent(tableModel
								.get(row)));
					}
				});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, "cell 0 0,grow");
	}

	public void onLibraryRefresh(@Observes LibraryChangedEvent e) {
		if (e.isDone()) {
			tableModel.clear();
			tableModel.addAll(library.getSongs());
		}
	}
}
