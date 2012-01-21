package be.hehehe.supersonic.panels;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.service.Library;

@SuppressWarnings("serial")
@Singleton
public class SearchPanel extends JPanel {
	private JTextField searchField;
	private JTable table;

	@Inject
	Library library;

	public SearchPanel() {
		setLayout(new MigLayout("insets 0", "[grow]", "[][grow]"));

		searchField = new JTextField();
		add(searchField, "cell 0 0,growx");
		searchField.setColumns(10);

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, "cell 0 1,grow");
	}

}
