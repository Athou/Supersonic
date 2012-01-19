package be.hehehe.supersonic;

import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import be.hehehe.supersonic.panels.AlbumsPanel;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class Supersonic extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	@Inject
	SupersonicMenu supersonicMenu;

	@Inject
	AlbumsPanel albumsPanel;

	@Inject
	Logger log;

	@PostConstruct
	public void init() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		SwingUtils.centerContainer(this);
		pack();
		setVisible(true);

		setJMenuBar(supersonicMenu);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "cell 0 0,grow");

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Albums", scrollPane);
		scrollPane.setViewportView(albumsPanel);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_2, null);

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 1,grow");

	}

}
