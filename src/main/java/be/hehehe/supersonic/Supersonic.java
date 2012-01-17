package be.hehehe.supersonic;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Supersonic extends JFrame {
	public Supersonic() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][grow]"));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "cell 0 0,grow");
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 1,grow");

	}

}
