package be.hehehe.supersonic.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
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
		SwingUtils.centerContainer(this);

		Container panel = getContentPane();

		panel.setLayout(new MigLayout("", "[]", "[]"));
		panel.add(
				new JLabel("Supersonic version " + versionService.getVersion()),
				"cell 0 0");
		panel.add(new JLabel("by Athou"), "cell 0 1");

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(okButton, "cell 0 3, center");

		pack();
	}
}
