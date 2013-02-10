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
import javax.swing.JComponent;
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

		panel.setLayout(new MigLayout("fillx"));

		panel.add(new JLabel("Supersonic"), "growx");
		panel.add(new JLabel("v" + versionService.getVersion()), "wrap");

		panel.add(new JLabel("Author"), "growx");
		panel.add(new JLabel("Athou"), "wrap");

		panel.add(new JLabel("Sources"), "growx");
		panel.add(new Hyperlink("GitHub",
				"https://github.com/Athou/Supersonic/"), "wrap");

		panel.add(new JLabel("Download"), "growx");
		panel.add(new Hyperlink("Here",
				"http://lolz.hehehe.be/supersonic/version.txt"), "wrap");

		panel.add(new JLabel("Discussions"), "growx");

		String url = "http://forum.subsonic.org/forum/viewtopic.php?f=4&t=8823";
		JComponent hyperlink = null;
		try {
			hyperlink = new Hyperlink("Subsonic Forum", url);
		} catch (Exception e) {
			hyperlink = new JLabel(url);
		}
		panel.add(hyperlink, "wrap");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		okButton.setFocusable(false);
		panel.add(okButton, "span, center");

		pack();
		setSize(260, getHeight());
		setResizable(false);
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
