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
import be.hehehe.supersonic.service.Library;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
@Named
public class SupersonicMenu extends JMenuBar {

	@Inject
	SettingsDialog settingsDialog;

	@Inject
	IconService iconService;
	
	@Inject
	Library library;

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

		
		JMenuItem refreshMenu = new JMenuItem("Refresh Library");
		refreshMenu.setIcon(iconService.getIcon("arrow_rotate_clockwise"));
		fileMenu.add(refreshMenu);
		refreshMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					library.refresh();
				} catch (SupersonicException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
