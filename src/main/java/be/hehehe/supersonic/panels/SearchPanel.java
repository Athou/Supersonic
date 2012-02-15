package be.hehehe.supersonic.panels;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import be.hehehe.supersonic.events.LibraryChangedEvent;
import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.SearchTableModel;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class SearchPanel extends JPanel {
	private JTextField searchField;
	private JXTable table;
	private SearchTableModel tableModel;

	@Inject
	Library library;

	@Inject
	Logger log;

	@Inject
	Event<SongEvent> event;

	public SearchPanel() {
		setLayout(new MigLayout("insets 0", "[grow]", "[][grow]"));

		add(new JLabel("Search:"), "cell 0 0");
		searchField = new JTextField();
		add(searchField, "cell 0 0,growx");
		searchField.setColumns(10);
		searchField.setCaretColor(getBackground().darker());
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshList();
			}
		});

		table = new JXTable();
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, "cell 0 1,grow");

		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(true);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		tableModel = new SearchTableModel();
		table.setModel(tableModel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							int selectedRow = table.getSelectedRow();
							if (selectedRow >= 0) {
								SongEvent songEvent = new SongEvent(
										Type.CHANGE_SELECTION);
								songEvent.setSong(getSelectedSong());
								event.fire(songEvent);
							}
						}
					}
				});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					SongEvent songEvent = new SongEvent(Type.PLAY);
					songEvent.setSong(getSelectedSong());
					event.fire(songEvent);
				}
			}
		});
	}

	private void refreshList() {
		tableModel.clear();
		String text = searchField.getText();
		if (StringUtils.isNotBlank(text)) {
			text = text.toUpperCase();
			String[] keywords = text.split(" ");
			for (SongModel song : library.getSongs()) {
				String songText = song.getArtist() + " " + song.getAlbum()
						+ " " + song.getTitle();
				songText = songText.toUpperCase();

				boolean show = true;
				for (String keyword : keywords) {
					if (songText.indexOf(keyword) == -1) {
						show = false;
						break;
					}
				}
				if (show) {
					tableModel.add(song);
				}
			}
		}
	}

	private SongModel getSelectedSong() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			selectedRow = 0;
		}
		int row = table.convertRowIndexToModel(selectedRow);
		return tableModel.get(row);
	}

	public void onLibraryRefresh(@Observes final LibraryChangedEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (e.isDone()) {
					refreshList();
				}
			}
		});
	}

}
