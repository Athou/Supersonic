package be.hehehe.supersonic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import be.hehehe.supersonic.panels.SettingsDialog;
import be.hehehe.supersonic.service.IconService;

@SuppressWarnings("serial")
@Named
public class SupersonicMenu extends JMenuBar {

	@Inject
	SettingsDialog settingsDialog;

	@Inject
	IconService iconService;

	@PostConstruct
	public void init() {

		JMenu fileMenu = new JMenu("File");
		add(fileMenu);

		JMenuItem settingsMenu = new JMenuItem("Settings...");
		settingsMenu.setIcon(iconService.getIcon("cog"));
		fileMenu.add(settingsMenu);
		settingsMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsDialog.setVisible(true);
			}
		});

		fileMenu.add(new JSeparator());

		JMenuItem quitMenu = new JMenuItem("Quit");
		fileMenu.add(quitMenu);
		quitMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

	}
}
