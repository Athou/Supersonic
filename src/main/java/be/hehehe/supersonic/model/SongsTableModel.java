package be.hehehe.supersonic.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import be.hehehe.supersonic.utils.SwingUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class SongsTableModel extends AbstractTableModel {

	private static final String[] NAMES = new String[] { "#", "Artist",
			"Album", "Title", "Duration" };
	private static final Class<?>[] CLASSES = new Class<?>[] { Integer.class,
			String.class, String.class, String.class, String.class };

	private List<SongModel> songs = Lists.newArrayList();

	public SongsTableModel(List<SongModel> list) {
		addAll(list);
	}

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
			return rowIndex + 1;
		case 1:
			return song.getArtist();
		case 2:
			return song.getAlbum();
		case 3:
			return song.getTitle();
		case 4:
			return SwingUtils.formatDuration(song.getDuration());
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

	public SongModel get(int i) {
		return songs.get(i);
	}

	public int indexOf(SongModel model) {
		return songs.indexOf(model);
	}
}
