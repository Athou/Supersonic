package be.hehehe.supersonic;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
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

				try {
					final Player p = new Player();
					p.start(new File("C:\\temp\\f.mp3").toURL().openStream());
					
					JButton button = new JButton("stop");
					supersonic.add(button);
					button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						p.stop();
							
						}
					});
					
				} catch (Exception e) {

					e.printStackTrace();
				}

			}

		});
	}
}
