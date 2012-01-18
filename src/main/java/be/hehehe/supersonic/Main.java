package be.hehehe.supersonic;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Main {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

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

				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

				supersonic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				supersonic.setPreferredSize(new Dimension(WIDTH, HEIGHT));
				supersonic.setLocation((screen.width - WIDTH) / 2,
						(screen.height - HEIGHT) / 2);
				supersonic.pack();
				supersonic.setVisible(true);

			}

		});
	}
}
