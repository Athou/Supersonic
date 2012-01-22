package be.hehehe.supersonic;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import be.hehehe.supersonic.panels.ControlsPanel;
import be.hehehe.supersonic.panels.CoverPanel;
import be.hehehe.supersonic.panels.SearchPanel;
import be.hehehe.supersonic.panels.SettingsDialog;
import be.hehehe.supersonic.panels.SongsPanel;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class Supersonic extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private static final String TITLE = "Supersonic";

	@Inject
	PreferencesService preferencesService;

	@Inject
	IconService iconService;

	@Inject
	SupersonicMenu supersonicMenu;

	@Inject
	SupersonicTray supersonicTray;

	@Inject
	SearchPanel searchPanel;

	@Inject
	CoverPanel coverPanel;

	@Inject
	SongsPanel songsPanel;

	@Inject
	ControlsPanel controlsPanel;

	@Inject
	SettingsDialog settingsDialog;

	@Inject
	Logger log;

	@PostConstruct
	public void init() {

		setTitle(TITLE);
		setIconImage(iconService.getIcon("supersonic-big").getImage());
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
		leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPane);

		leftSplitPane.setBottomComponent(coverPanel);
		leftSplitPane.setTopComponent(searchPanel);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new MigLayout("insets 0", "[grow]", "[][grow]"));
		mainSplitPane.setRightComponent(rightPanel);

		rightPanel.add(controlsPanel, "cell 0 0,grow");
		rightPanel.add(songsPanel, "cell 0 1,grow");

		boolean trayAdded = supersonicTray.addTray(this);
		if (trayAdded) {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowIconified(WindowEvent e) {
					hideSupersonic();
				}
			});
		}

		mainSplitPane.setResizeWeight(0.5);
		leftSplitPane.setResizeWeight(0.5);

		if (StringUtils.isBlank(preferencesService.getSubsonicHostname())) {
			settingsDialog.setVisible(true);
		}
	}

	public void hideSupersonic() {
		setVisible(false);
	}

	public void showSupersonic() {
		setState(Frame.NORMAL);
		setVisible(true);
		toFront();
		repaint();
	}

}
