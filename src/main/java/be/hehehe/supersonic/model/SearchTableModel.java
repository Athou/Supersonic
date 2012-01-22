package be.hehehe.supersonic.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class SearchTableModel extends AbstractTableModel {

	private static final String[] NAMES = new String[] { "Artist", "Album",
			"Title" };
	private static final Class<?>[] CLASSES = new Class<?>[] { String.class,
			String.class, String.class };

	private List<SongModel> songs = Lists.newArrayList();

	@Override
	public int getColumnCount() {
		return NAMES.length;
	}

	@Override
	public int getRowCount() {
		return songs.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SongModel song = songs.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return song.getArtist();
		case 1:
			return song.getAlbum();
		case 2:
			return song.getTitle();
		}
		return null;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return NAMES[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return CLASSES[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

	}

	public void clear() {
		songs.clear();
		fireTableDataChanged();
	}

	public void addAll(List<SongModel> list) {
		if (list != null) {
			songs.addAll(list);
		}
		fireTableDataChanged();
	}

	public void add(SongModel song) {
		if (song != null) {
			songs.add(song);
		}
		fireTableDataChanged();
	}

	public SongModel get(int i) {
		return songs.get(i);
	}

	public int indexOf(SongModel model) {
		return songs.indexOf(model);
	}
}
