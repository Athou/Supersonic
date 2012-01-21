package be.hehehe.supersonic.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import be.hehehe.supersonic.Player.State;
import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.events.PlayingSongChangedEvent;
import be.hehehe.supersonic.events.SelectedSongChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.model.SongsTableModel;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class SongsPanel extends JPanel {

	@Inject
	Library library;

	@Inject
	Event<SelectedSongChangedEvent> selectedSongEvent;

	@Inject
	Event<PlayingSongChangedEvent> playingSongEvent;

	private JXTable table;
	private SongsTableModel tableModel;

	@PostConstruct
	public void init() {
		buildFrame();
	}

	private void buildFrame() {

		setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));

		table = new JXTable();
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(true);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		tableModel = new SongsTableModel(library.getSongs());
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							int selectedRow = table.getSelectedRow();
							if (selectedRow >= 0) {
								selectedSongEvent
										.fire(new SelectedSongChangedEvent(
												getSelectedSong()));
							}
						}
					}
				});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					playingSongEvent.fire(new PlayingSongChangedEvent(
							getSelectedSong(), State.PLAY));
				}
			}

		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, "cell 0 0,grow");
		table.packAll();
	}

	public void onLibraryRefresh(@Observes LibraryChangedEvent e) {
		if (e.isDone()) {
			table.clearSelection();
			tableModel.clear();
			tableModel.addAll(library.getSongs());
			table.packAll();
		}
	}

	public void onSongChanged(@Observes PlayingSongChangedEvent e) {
		if (e.getSong() != null && !e.getSong().equals(getSelectedSong())) {
			int row = tableModel.indexOf(e.getSong());
			row = table.convertRowIndexToView(row);
			table.changeSelection(row, 0, false, false);
		}
	}

	public SongModel getSelectedSong() {
		int row = table.convertRowIndexToModel(table.getSelectedRow());
		return tableModel.get(row);
	}

	public SongModel getNextSong() {
		int row = table.convertRowIndexToModel(table.getSelectedRow());
		row++;
		if (tableModel.getRowCount() == row) {
			row = 0;
		}
		return tableModel.get(row);
	}
}
