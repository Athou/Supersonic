package be.hehehe.supersonic.panels;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.hyperlink.HyperlinkAction;

import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.VersionService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class AboutDialog extends JDialog {

	@Inject
	IconService iconService;

	@Inject
	VersionService versionService;

	@PostConstruct
	public void init() {
		setTitle("About");
		setModal(true);
		setIconImage(iconService.getIcon("supersonic-big").getImage());

		Container panel = getContentPane();

		panel.setLayout(new MigLayout("", "[][]", "[]"));

		panel.add(new JLabel("Supersonic"), "cell 0 0");
		panel.add(new JLabel("v" + versionService.getVersion()), "cell 1 0");

		panel.add(new JLabel("Author"), "cell 0 1");
		panel.add(new JLabel("Athou"), "cell 1 1");

		panel.add(new JLabel("Download and Sources"), "cell 0 2");
		panel.add(new Hyperlink("GitHub",
				"https://github.com/Athou/Supersonic/downloads"), "cell 1 2");

		panel.add(new JLabel("Discussion"), "cell 0 3");
		panel.add(new Hyperlink("Subsonic Forum",
				"http://forum.subsonic.org/forum/viewtopic.php?f=4&t=8823"),
				"cell 1 3");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(okButton, "cell 0 4 2 1, center");

		pack();
		SwingUtils.centerContainer(this);
	}

	private class Hyperlink extends JButton {
		public Hyperlink(String label, String uri) {
			super();
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Action action = HyperlinkAction.createHyperlinkAction(URI
					.create(uri));
			action.putValue(Action.NAME, "<html><u>" + label + "</u></html>");
			setAction(action);
			setHorizontalAlignment(SwingConstants.LEFT);
			setBorder(BorderFactory.createEmptyBorder());
			setBorderPainted(false);
			setContentAreaFilled(false);
			setFocusable(false);
		}
	}
}
