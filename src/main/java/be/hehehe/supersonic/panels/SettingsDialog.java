package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.KeyBindingService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class SettingsDialog extends JDialog {

	@Inject
	KeyBindingPanel keyBindingPanel;

	@Inject
	private PreferencesService preferencesService;

	@Inject
	KeyBindingService keyBindingService;

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
	private JComboBox proxyTypeComboBox;

	private JComboBox lafCombo;

	private JCheckBox systrayCheckbox;
	private JCheckBox notifCheckbox;

	private JTabbedPane tabbedPane;

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
		getContentPane().setLayout(new MigLayout("fillx"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "grow, wrap");

		JPanel generalTab = new JPanel();
		generalTab.setLayout(new MigLayout("insets 0, fill"));
		tabbedPane.add(generalTab, "General");
		tabbedPane.add(keyBindingPanel, "Key Bindings");

		JPanel subsonicInfosPanel = new JPanel();
		subsonicInfosPanel.setBorder(BorderFactory
				.createTitledBorder("Subsonic"));
		generalTab.add(subsonicInfosPanel, "growx, push, wrap");
		subsonicInfosPanel.setLayout(new MigLayout("insets 0, fillx"));

		subsonicInfosPanel.add(new JLabel("Address"));
		subsonicInfosPanel.add(addressTxt = new JTextField(),
				"growx, push,  wrap");

		subsonicInfosPanel.add(new JLabel("Login"));
		subsonicInfosPanel
				.add(loginTxt = new JTextField(), "growx, push, wrap");

		subsonicInfosPanel.add(new JLabel("Password"));
		subsonicInfosPanel.add(passwordTxt = new JPasswordField(),
				"growx, wrap");

		subsonicInfosPanel
				.add(testButton = new JButton("Test"), "span, center");
		testButton.setFocusPainted(false);

		JPanel proxyPanel = new JPanel();
		proxyPanel.setLayout(new MigLayout("insets 0, fillx"));
		proxyPanel.setBorder(BorderFactory.createTitledBorder("Proxy"));
		generalTab.add(proxyPanel, "growx, wrap");

		proxyPanel.add(proxyEnabledCheckBox = new JCheckBox("Enable proxy"),
				"wrap");

		proxyPanel.add(new JLabel("Proxy Host"));
		proxyPanel.add(proxyHostTxt = new JTextField(), "growx, push, wrap");

		proxyPanel.add(new JLabel("Proxy Port"));
		proxyPanel.add(proxyPortTxt = new JTextField(), "growx, push, wrap");

		proxyPanel.add(new JLabel("Proxy Type"));
		proxyPanel
				.add(proxyTypeComboBox = new JComboBox(), "growx, push, wrap");

		proxyPanel.add(proxyAuthRequiredCheckbox = new JCheckBox(
				"Proxy uses authentication"), "wrap");

		proxyPanel.add(new JLabel("Login"));
		proxyPanel.add(proxyLoginTxt = new JTextField(), "growx, push, wrap");

		proxyPanel.add(new JLabel("Password"));
		proxyPanel.add(proxyPasswordTxt = new JPasswordField(),
				"growx, push, wrap");

		JPanel lafPanel = new JPanel();
		lafPanel.setLayout(new MigLayout("insets 0, fillx"));
		lafPanel.setBorder(BorderFactory.createTitledBorder("Look And Feel"));
		generalTab.add(lafPanel, "growx, wrap");

		lafPanel.add(lafCombo = new JComboBox(), "grow, wrap");

		lafPanel.add(
				systrayCheckbox = new JCheckBox("Minimize to system tray"),
				"wrap");

		lafPanel.add(notifCheckbox = new JCheckBox(
				"Display notifications when song changes"), "wrap");

		getContentPane().add(okButton = new JButton("OK"),
				"span, split 2, right");
		getContentPane().add(cancelButton = new JButton("Cancel"));
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
				preferencesService.applySkin(preferencesService
						.getLookAndFeel());
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

		proxyTypeComboBox.setModel(new DefaultComboBoxModel(new Proxy.Type[] {
				Proxy.Type.HTTP, Proxy.Type.SOCKS }));

		DisableControlsListener controlListener = new DisableControlsListener();
		proxyEnabledCheckBox.addActionListener(controlListener);
		proxyAuthRequiredCheckbox.addActionListener(controlListener);

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			lafCombo.addItem(new SkinWrapper(info));
		}

		for (SkinInfo info : SubstanceLookAndFeel.getAllSkins().values()) {
			lafCombo.addItem(new SkinWrapper(info));
		}

		lafCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SkinWrapper wrap = (SkinWrapper) lafCombo.getSelectedItem();
				preferencesService.applySkin(wrap.getClassName());
				SwingUtilities.updateComponentTreeUI(SettingsDialog.this);
				SettingsDialog.this.pack();
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
		preferencesService.setProxyType((Proxy.Type) proxyTypeComboBox
				.getSelectedItem());
		preferencesService.setProxyAuthRequired(proxyAuthRequiredCheckbox
				.isSelected());
		preferencesService.setProxyLogin(proxyLoginTxt.getText());
		preferencesService.setProxyPassword(new String(proxyPasswordTxt
				.getPassword()));
		preferencesService.setLookAndFeel(((SkinWrapper) lafCombo
				.getSelectedItem()).getClassName());
		preferencesService.setMinimizeToTray(systrayCheckbox.isSelected());
		preferencesService.setDisplayNotifications(notifCheckbox.isSelected());
		preferencesService.setKeyBindings(keyBindingPanel.getBindings());
		preferencesService.setMediaKeyActive(keyBindingPanel
				.isMediaKeyBindingActive());

		preferencesService.flush();
		preferencesService.applySettings();
		keyBindingService.applyBindings();
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
		notifCheckbox.setSelected(preferencesService.isDisplayNotifications());
		keyBindingPanel.setBindings(preferencesService.getKeyBindings());
		keyBindingPanel.setMediaKeyBindingActive(preferencesService
				.isMediaKeyBindingActive());
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			loadPrefs();
			setControlStates();
			tabbedPane.setSelectedIndex(0);
		}
		super.setVisible(b);
	}

	private void close() {
		dispose();
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

		private String name;
		private String className;

		public SkinWrapper(String name, String className) {
			this.name = name;
			this.className = className;
		}

		public SkinWrapper(LookAndFeelInfo info) {
			this.name = "Java - " + info.getName();
			this.className = info.getClassName();
		}

		public SkinWrapper(SkinInfo info) {
			this.name = "Substance - " + info.getDisplayName();
			this.className = info.getClassName();
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof SkinWrapper)) {
				return false;
			}
			SkinWrapper w = (SkinWrapper) obj;
			return StringUtils.equals(w.className, className);
		}

		public String getClassName() {
			return className;
		}

	}

}
