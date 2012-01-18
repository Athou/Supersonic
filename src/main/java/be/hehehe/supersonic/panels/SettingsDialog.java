package be.hehehe.supersonic.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.service.Prefs;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {

	@Inject
	private PreferencesService preferencesService;

	private JTextField addressTxt;
	private JTextField loginTxt;
	private JPasswordField passwordTxt;
	private JButton okButton;
	private JButton cancelButton;

	@PostConstruct
	public void init() {
		setPreferredSize(new Dimension(400, 250));
		SwingUtils.centerContainer(this);
		pack();
		buildFrame();
		attachBehavior();
		loadPrefs();
		setModal(true);
	}

	private void buildFrame() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][][]"));

		JPanel subsonicInfosPanel = new JPanel();
		subsonicInfosPanel.setBorder(BorderFactory
				.createTitledBorder("Subsonic"));
		getContentPane().add(subsonicInfosPanel, "cell 0 0,grow");
		subsonicInfosPanel.setLayout(new MigLayout("", "[][grow]", "[][][]"));

		JLabel addressLabel = new JLabel("Address");
		subsonicInfosPanel.add(addressLabel, "cell 0 0,alignx left");

		addressTxt = new JTextField();
		subsonicInfosPanel.add(addressTxt, "cell 1 0,growx");
		addressTxt.setColumns(10);

		JLabel loginLabel = new JLabel("Login");
		subsonicInfosPanel.add(loginLabel, "cell 0 1,alignx left");

		loginTxt = new JTextField();
		subsonicInfosPanel.add(loginTxt, "cell 1 1,growx");
		loginTxt.setColumns(10);

		JLabel passwordLabel = new JLabel("Password");
		subsonicInfosPanel.add(passwordLabel, "cell 0 2,alignx left");

		passwordTxt = new JPasswordField();
		subsonicInfosPanel.add(passwordTxt, "cell 1 2,growx");

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, "cell 0 1,grow");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 2,growx");
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		okButton = new JButton("OK");
		panel.add(okButton);

		cancelButton = new JButton("Cancel");
		panel.add(cancelButton);
	}

	private void attachBehavior() {
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
				close();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
	}

	private void save() {
		Preferences prefs = preferencesService.getPreferences();
		prefs.put(Prefs.SUBSONIC_ADDRESS, addressTxt.getText());
		prefs.put(Prefs.SUBSONIC_LOGIN, loginTxt.getText());
		prefs.put(Prefs.SUBSONIC_PASSWORD,
				new String(passwordTxt.getPassword()));
	}

	private void loadPrefs() {
		Preferences prefs = preferencesService.getPreferences();
		addressTxt.setText(prefs.get(Prefs.SUBSONIC_ADDRESS,
				"http://localhost/path/to/subsonic"));
		loginTxt.setText(prefs.get(Prefs.SUBSONIC_LOGIN, null));
		passwordTxt.setText(prefs.get(Prefs.SUBSONIC_PASSWORD, null));
	}

	private void close() {
		dispose();
	}

}
