package be.hehehe.supersonic;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {

				}

				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

				Supersonic supersonic = new Supersonic();
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
