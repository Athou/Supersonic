package be.hehehe.supersonic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import be.hehehe.supersonic.action.ExitAction;
import be.hehehe.supersonic.action.LibraryRefreshAction;
import be.hehehe.supersonic.panels.AboutDialog;
import be.hehehe.supersonic.panels.SettingsDialog;
import be.hehehe.supersonic.service.IconService;

@SuppressWarnings("serial")
@Singleton
public class SupersonicMenu extends JMenuBar {

	@Inject
	SettingsDialog settingsDialog;

	@Inject
	AboutDialog aboutDialog;

	@Inject
	IconService iconService;

	@Inject
	LibraryRefreshAction refreshAction;

	@Inject
	ExitAction exitAction;

	@PostConstruct
	public void init() {

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		add(helpMenu);

		JMenuItem settingsMenu = new JMenuItem("Settings...");
		settingsMenu.setMnemonic(KeyEvent.VK_S);
		settingsMenu.setIcon(iconService.getIcon("cog"));
		fileMenu.add(settingsMenu);
		settingsMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsDialog.setVisible(true);
			}
		});

		JMenuItem refreshMenu = new JMenuItem("Refresh Library");
		refreshMenu.setMnemonic(KeyEvent.VK_R);
		refreshMenu.setIcon(iconService.getIcon("arrow_rotate_clockwise"));
		fileMenu.add(refreshMenu);
		refreshMenu.addActionListener(refreshAction);

		fileMenu.add(new JSeparator());

		JMenuItem quitMenu = new JMenuItem("Quit");
		quitMenu.setMnemonic(KeyEvent.VK_Q);
		fileMenu.add(quitMenu);
		quitMenu.addActionListener(exitAction);

		JMenuItem aboutMenu = new JMenuItem("About...");
		aboutMenu.setMnemonic(KeyEvent.VK_A);
		aboutMenu.setIcon(iconService.getIcon("help"));
		helpMenu.add(aboutMenu);
		aboutMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutDialog.setVisible(true);
			}
		});

	}
}
