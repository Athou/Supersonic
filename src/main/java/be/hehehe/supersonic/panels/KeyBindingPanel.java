package be.hehehe.supersonic.panels;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class KeyBindingPanel extends JPanel {

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("", "[][]", "[][][]"));
	}
}
