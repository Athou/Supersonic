package be.hehehe.supersonic;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.SystemUtils;

import be.hehehe.supersonic.action.ExitAction;
import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.utils.SwingUtils;

@Singleton
public class SupersonicTray {

	@Inject
	IconService iconService;

	@Inject
	ExitAction exitAction;

	@Inject
	PreferencesService preferencesService;

	private TrayIcon trayIcon;

	public boolean addTray(final Supersonic supersonic) {
		if (!SystemTray.isSupported()) {
			return false;
		}
		final JPopupMenu popup = new JPopupMenu();
		trayIcon = new TrayIcon(iconService.getIcon("supersonic").getImage());
		final SystemTray tray = SystemTray.getSystemTray();

		JMenuItem restoreItem = new JMenuItem("Restore");
		restoreItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				supersonic.showSupersonic();
			}
		});
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(exitAction);
		popup.add(restoreItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.setLocation(e.getX(), e.getY());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					supersonic.showSupersonic();
				} else if (e.getClickCount() == 1
						&& e.getButton() == MouseEvent.BUTTON2) {
					popup.setLocation(e.getX(), e.getY());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});
		trayIcon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				supersonic.showSupersonic();
			}
		});

		boolean trayAdded = false;
		try {
			tray.add(trayIcon);
			trayAdded = true;
		} catch (Exception e) {
			trayAdded = false;
		}
		return trayAdded;
	}

	public void onEvent(@Observes final SongEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (e.getType() == Type.PLAY) {
					if (preferencesService.isDisplayNotifications()) {
						SongModel song = e.getSong();
						String title = String.format("%s (%s)",
								song.getTitle(),
								SwingUtils.formatDuration(song.getDuration()));
						String desc = song.getArtist()
								+ SystemUtils.LINE_SEPARATOR
								+ song.getAlbum()
								+ (song.getYear() != 0 ? String.format(" (%d)",
										song.getYear()) : "");
						trayIcon.displayMessage(title, desc, MessageType.INFO);
					}
				}
			}
		});
	}
}
