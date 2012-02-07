package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy.Type;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class SettingsDialog extends JDialog {

	@Inject
	private PreferencesService preferencesService;

	@Inject
	SubsonicService subsonicService;

	@Inject
	IconService iconService;

	@Inject
	Logger log;

	private JTextField addressTxt;
	private JTextField loginTxt;
	private JPasswordField passwordTxt;
	private JButton testButton;

	private JButton okButton;
	private JButton cancelButton;
	private JTextField proxyHostTxt;
	private JTextField proxyPortTxt;
	private JTextField proxyLoginTxt;
	private JPasswordField proxyPasswordTxt;

	private JCheckBox proxyEnabledCheckBox;

	private JCheckBox proxyAuthRequiredCheckbox;
	private JLabel lblProxyType;
	private JComboBox proxyTypeComboBox;

	private JComboBox lafCombo;

	private JCheckBox systrayCheckbox;

	@PostConstruct
	public void init() {
		setTitle("Supersonic Settings");
		setModal(true);
		buildFrame();
		attachBehavior();
		pack();
		setSize(400, getHeight());
		SwingUtils.centerContainer(this);
		setIconImage(iconService.getIcon("cog").getImage());
	}

	private void buildFrame() {
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][]"));

		JPanel subsonicInfosPanel = new JPanel();
		subsonicInfosPanel.setBorder(BorderFactory
				.createTitledBorder("Subsonic"));
		getContentPane().add(subsonicInfosPanel, "cell 0 0,grow");
		subsonicInfosPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));

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

		JPanel testPanel = new JPanel();
		testPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		subsonicInfosPanel.add(testPanel, "cell 0 3 2 1, grow");

		testButton = new JButton("Test");
		testButton.setFocusable(false);
		testPanel.add(testButton);

		JPanel proxyPanel = new JPanel();
		proxyPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][]"));
		proxyPanel.setBorder(BorderFactory.createTitledBorder("Proxy"));
		getContentPane().add(proxyPanel, "cell 0 1,grow");

		proxyEnabledCheckBox = new JCheckBox("Enable proxy");
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

		lblProxyType = new JLabel("Proxy Type");
		proxyPanel.add(lblProxyType, "cell 0 3,alignx left");

		proxyTypeComboBox = new JComboBox();
		proxyPanel.add(proxyTypeComboBox, "cell 1 3,growx");

		proxyAuthRequiredCheckbox = new JCheckBox("Proxy uses authentication");
		proxyPanel.add(proxyAuthRequiredCheckbox, "cell 0 4");

		JLabel lblLogin = new JLabel("Login");
		proxyPanel.add(lblLogin, "cell 0 5,alignx left");

		proxyLoginTxt = new JTextField();
		proxyPanel.add(proxyLoginTxt, "cell 1 5,growx");
		proxyLoginTxt.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		proxyPanel.add(lblPassword, "cell 0 6,alignx left");

		proxyPasswordTxt = new JPasswordField();
		proxyPanel.add(proxyPasswordTxt, "cell 1 6,growx");

		JPanel lafPanel = new JPanel();
		lafPanel.setBorder(BorderFactory.createTitledBorder("Look And Feel"));
		getContentPane().add(lafPanel, "cell 0 2,growx");
		lafPanel.setLayout(new MigLayout("", "[grow]", "[][]"));
		lafCombo = new JComboBox();
		lafPanel.add(lafCombo, "cell 0 0, grow");

		systrayCheckbox = new JCheckBox("Minimize to system tray");
		lafPanel.add(systrayCheckbox, "cell 0 1");

		JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 3,growx");
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
		getRootPane().setDefaultButton(okButton);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetLookAndFeel();
				close();
			}
		});

		final SettingsDialog that = this;
		testButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String host = addressTxt.getText();
				final String userName = loginTxt.getText();
				final String password = new String(passwordTxt.getPassword());
				new SwingWorker<Object, Void>() {
					private Exception exception;

					@Override
					protected Object doInBackground() throws Exception {
						try {
							Response response = subsonicService.invoke("ping",
									host, userName, password);
							if (response.getError() != null) {
								exception = new SupersonicException(response
										.getError().getMessage());
							}
						} catch (Exception ex) {
							exception = ex;
						}
						return null;
					}

					protected void done() {
						if (exception == null) {
							JOptionPane.showMessageDialog(that,
									"Connection successful.");
						} else {
							SwingUtils.handleError(exception);
						}
					};
				}.execute();
			}
		});

		proxyTypeComboBox.setModel(new DefaultComboBoxModel(new Type[] {
				Type.HTTP, Type.SOCKS }));

		DisableControlsListener controlListener = new DisableControlsListener();
		proxyEnabledCheckBox.addActionListener(controlListener);
		proxyAuthRequiredCheckbox.addActionListener(controlListener);

		for (SkinInfo info : SubstanceLookAndFeel.getAllSkins().values()) {
			lafCombo.addItem(new SkinWrapper(info));
		}

		lafCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SkinWrapper wrap = (SkinWrapper) lafCombo.getSelectedItem();
				SubstanceLookAndFeel.setSkin(wrap.getInfo().getClassName());
			}
		});
	}

	private void save() {
		preferencesService.setSubsonicHostname(addressTxt.getText());
		preferencesService.setSubsonicLogin(loginTxt.getText());
		preferencesService.setSubsonicPassword(new String(passwordTxt
				.getPassword()));
		preferencesService.setProxyEnabled(proxyEnabledCheckBox.isSelected());
		preferencesService.setProxyHostname(proxyHostTxt.getText());
		preferencesService.setProxyPort(proxyPortTxt.getText());
		preferencesService.setProxyType((Type) proxyTypeComboBox
				.getSelectedItem());
		preferencesService.setProxyAuthRequired(proxyAuthRequiredCheckbox
				.isSelected());
		preferencesService.setProxyLogin(proxyLoginTxt.getText());
		preferencesService.setProxyPassword(new String(proxyPasswordTxt
				.getPassword()));
		preferencesService.setLookAndFeel(((SkinWrapper) lafCombo
				.getSelectedItem()).getInfo().getClassName());
		preferencesService.setMinimizeToTray(systrayCheckbox.isSelected());

		preferencesService.flush();
		preferencesService.applySettings();
	}

	private void loadPrefs() {
		addressTxt.setText(preferencesService.getSubsonicHostname());
		loginTxt.setText(preferencesService.getSubsonicLogin());
		passwordTxt.setText(preferencesService.getSubsonicPassword());
		proxyEnabledCheckBox.setSelected(preferencesService.isProxyEnabled());
		proxyHostTxt.setText(preferencesService.getProxyHostname());
		proxyPortTxt.setText(preferencesService.getProxyPort());
		proxyTypeComboBox.setSelectedItem(preferencesService.getProxyType());

		proxyAuthRequiredCheckbox.setSelected(preferencesService
				.isProxyAuthRequired());
		proxyLoginTxt.setText(preferencesService.getProxyLogin());
		proxyPasswordTxt.setText(preferencesService.getProxyPassword());

		lafCombo.setSelectedItem(new SkinWrapper("", preferencesService
				.getLookAndFeel()));
		systrayCheckbox.setSelected(preferencesService.isMinimizeToTray());
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			loadPrefs();
			setControlStates();
		}
		super.setVisible(b);
	}

	private void close() {
		dispose();
	}

	private void resetLookAndFeel() {
		SubstanceLookAndFeel.setSkin(preferencesService.getLookAndFeel());
	}

	private class DisableControlsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setControlStates();
		}
	}

	public void setControlStates() {

		boolean proxyEnabled = proxyEnabledCheckBox.isSelected();
		proxyHostTxt.setEnabled(proxyEnabled);
		proxyPortTxt.setEnabled(proxyEnabled);
		proxyTypeComboBox.setEnabled(proxyEnabled);
		proxyAuthRequiredCheckbox.setEnabled(proxyEnabled);

		boolean authRequired = proxyEnabled
				&& proxyAuthRequiredCheckbox.isSelected();
		proxyLoginTxt.setEnabled(authRequired);
		proxyPasswordTxt.setEnabled(authRequired);

	}

	private class SkinWrapper {

		private SkinInfo info;

		public SkinWrapper(SkinInfo info) {
			this.info = info;
		}

		public SkinWrapper(String name, String className) {
			this.info = new SkinInfo(name, className);
		}

		@Override
		public String toString() {
			return info.getDisplayName();
		}

		@Override
		public boolean equals(Object obj) {
			SkinWrapper w = (SkinWrapper) obj;
			return StringUtils.equals(w.getInfo().getClassName(), getInfo()
					.getClassName());
		}

		public SkinInfo getInfo() {
			return info;
		}

	}

}
