package be.hehehe.supersonic.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class ChatMessageTableModel extends AbstractTableModel {

	private static final String[] NAMES = new String[] { "Time", "User",
			"Message" };
	private static final Class<?>[] CLASSES = new Class<?>[] { String.class,
			String.class, String.class };

	private List<ChatMessageModel> messages = Lists.newArrayList();

	@Override
	public int getColumnCount() {
		return NAMES.length;
	}

	@Override
	public int getRowCount() {
		return messages.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ChatMessageModel message = messages.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return DateFormat.getTimeInstance().format(
					new Date(message.getTime()));
		case 1:
			return message.getUserName();
		case 2:
			return message.getMessage();
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
		messages.clear();
		fireTableDataChanged();
	}

	public void addAll(List<ChatMessageModel> list) {
		if (list != null) {
			messages.addAll(list);
		}
		fireTableDataChanged();
	}

	public void add(ChatMessageModel message) {
		if (message != null) {
			messages.add(message);
		}
		fireTableDataChanged();
	}
}
