package be.hehehe.supersonic;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import be.hehehe.supersonic.action.CheckForUpdateAction;
import be.hehehe.supersonic.action.ExitAction;
import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.model.ApplicationStateModel;
import be.hehehe.supersonic.panels.ChatPanel;
import be.hehehe.supersonic.panels.ControlsPanel;
import be.hehehe.supersonic.panels.CoverPanel;
import be.hehehe.supersonic.panels.NowPlayingPanel;
import be.hehehe.supersonic.panels.SearchPanel;
import be.hehehe.supersonic.panels.SettingsDialog;
import be.hehehe.supersonic.panels.SongsPanel;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.service.VersionService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class Supersonic extends JFrame {

	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;

	private static final String TITLE = "Supersonic";

	@Inject
	ExitAction exitAction;

	@Inject
	PreferencesService preferencesService;

	@Inject
	VersionService versionService;

	@Inject
	CheckForUpdateAction checkForUpdateAction;

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
	NowPlayingPanel nowPlayingPanel;

	@Inject
	ChatPanel chatPanel;

	@Inject
	ControlsPanel controlsPanel;

	@Inject
	SettingsDialog settingsDialog;

	@Inject
	Logger log;

	private JTabbedPane tabs;

	@PostConstruct
	public void init() {

		setTitle(TITLE + " " + versionService.getVersion());
		setIconImage(iconService.getIcon("supersonic-big").getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(null);
			}
		});
		setVisible(true);
		applyPreviousState();
		checkForUpdateAction.actionPerformed(null);

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

		tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.add("Songs", songsPanel);
		tabs.add("Now Playing", nowPlayingPanel);
		tabs.add("Chat", chatPanel);

		rightPanel.add(controlsPanel, "cell 0 0,grow");
		rightPanel.add(tabs, "cell 0 1,grow");

		boolean trayAdded = supersonicTray.addTray(this);
		if (trayAdded) {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowIconified(WindowEvent e) {
					if (preferencesService.isMinimizeToTray()) {
						hideSupersonic();
					}
				}
			});
		}

		mainSplitPane.setResizeWeight(0.5);
		leftSplitPane.setResizeWeight(0);

		if (StringUtils.isBlank(preferencesService.getSubsonicHostname())) {
			settingsDialog.setVisible(true);
		}
	}

	private void applyPreviousState() {
		ApplicationStateModel model = preferencesService.getApplicationState();
		if (model == null) {
			setSize(new Dimension(WIDTH, HEIGHT));
			SwingUtils.centerContainer(this);
		} else {
			log.debug("Applying previous state settings");
			setSize(model.getWindowSize());
			coverPanel.setSize(model.getCoverPanel());
			searchPanel.setSize(model.getSearchPanel());
			SwingUtils.centerContainer(this);
			setExtendedState(model.getWindowState());
		}
	}

	public void hideSupersonic() {
		setVisible(false);
	}

	public void showSupersonic() {
		setVisible(true);
		setExtendedState(JFrame.NORMAL);
		toFront();
		repaint();
	}

	public void observes(@Observes SongEvent event) {
		if (event.getType() == SongEvent.Type.CHANGE_SELECTION) {
			tabs.setSelectedIndex(0);
		}
	}

}
