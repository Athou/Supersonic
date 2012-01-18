package be.hehehe.supersonic;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Main {

	@Inject
	Supersonic supersonic;

	public static void main(String[] args) {

		try {
			UIManager
					.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		WeldContainer weld = new Weld().initialize();
		weld.instance().select(Main.class).get();
	}

	@PostConstruct
	public void init() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				supersonic.setVisible(true);
			}
		});
	}
}
