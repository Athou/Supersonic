package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	private JTextField proxyHostTxt;
	private JTextField proxyPortTxt;
	private JTextField proxyLoginTxt;
	private JPasswordField proxyPasswordTxt;

	@PostConstruct
	public void init() {
		setModal(true);
		buildFrame();
		attachBehavior();
		loadPrefs();
		pack();
		setSize(400, getHeight());
		SwingUtils.centerContainer(this);
	}

	private void buildFrame() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][]"));

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

		JPanel proxyPanel = new JPanel();
		proxyPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][][]"));
		proxyPanel.setBorder(BorderFactory.createTitledBorder("Proxy"));
		getContentPane().add(proxyPanel, "cell 0 1,grow");

		JCheckBox proxyEnabledCheckBox = new JCheckBox("Enable proxy");
		proxyPanel.add(proxyEnabledCheckBox, "cell 0 0");

		JLabel lblNewLabel = new JLabel("Proxy Host");
		proxyPanel.add(lblNewLabel, "cell 0 1,alignx left");

		proxyHostTxt = new JTextField();
		proxyPanel.add(proxyHostTxt, "cell 1 1,growx");
		proxyHostTxt.setColumns(10);

		JLabel lblProxyPort = new JLabel("Proxy Port");
		proxyPanel.add(lblProxyPort, "cell 0 2,alignx left");

		proxyPortTxt = new JTextField();
		proxyPanel.add(proxyPortTxt, "cell 1 2,growx");
		proxyPortTxt.setColumns(10);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Proxy uses authentication");
		proxyPanel.add(chckbxNewCheckBox, "cell 0 3");

		JLabel lblLogin = new JLabel("Login");
		proxyPanel.add(lblLogin, "cell 0 4,alignx left");

		proxyLoginTxt = new JTextField();
		proxyPanel.add(proxyLoginTxt, "cell 1 4,growx");
		proxyLoginTxt.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		proxyPanel.add(lblPassword, "cell 0 5,alignx left");

		proxyPasswordTxt = new JPasswordField();
		proxyPanel.add(proxyPasswordTxt, "cell 1 5,growx");

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
