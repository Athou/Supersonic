package be.hehehe.supersonic;

import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import be.hehehe.supersonic.panels.CoverPanel;
import be.hehehe.supersonic.panels.SongsPanel;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class Supersonic extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	@Inject
	SupersonicMenu supersonicMenu;

	@Inject
	CoverPanel coverPanel;

	@Inject
	SongsPanel songsPanel;

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
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));

		JSplitPane mainSplitPane = new JSplitPane();
		getContentPane().add(mainSplitPane, "cell 0 0,grow");

		JSplitPane leftSplitPane = new JSplitPane();
		leftSplitPane.setResizeWeight(0.5);
		leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPane);

		leftSplitPane.setBottomComponent(coverPanel);

		JSplitPane rightSplitPane = new JSplitPane();
		rightSplitPane.setResizeWeight(0.2);
		rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setRightComponent(rightSplitPane);

		rightSplitPane.setBottomComponent(songsPanel);

	}

}
