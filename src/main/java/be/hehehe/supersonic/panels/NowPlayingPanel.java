package be.hehehe.supersonic.panels;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
@Singleton
public class NowPlayingPanel extends JPanel {
	
	@PostConstruct
	public void init() {
		add(new JLabel("cc"));
	}
	
}
