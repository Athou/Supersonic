package be.hehehe.supersonic.service;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy.Type;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.GraphiteSkin;

import be.hehehe.supersonic.model.ApplicationStateModel;
import be.hehehe.supersonic.model.KeyBindingModel;

import com.google.common.collect.Lists;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Singleton
public class PreferencesService {

	private static final String LOOKANDFEEL = "lookandfeel";
	private static final String MINIMIZE_TO_TRAY = "minimize-to-tray";
	private static final String SUBSONIC_ADDRESS = "subsonic-address";
	private static final String SUBSONIC_LOGIN = "subsonic-login";
	private static final String SUBSONIC_PASSWORD = "subsonic-password";
	private static final String PROXY_ENABLED = "proxy-enabled";
	private static final String PROXY_HOSTNAME = "proxy-hostname";
	private static final String PROXY_PORT = "proxy-port";
	private static final String PROXY_TYPE = "proxy-type";
	private static final String PROXY_AUTHREQUIRED = "proxy-authrequired";
	private static final String PROXY_LOGIN = "proxy-login";
	private static final String PROXY_PASSWORD = "proxy-password";
	private static final String KEYBINDINGS = "keybindings";
	private static final String KEYBINDINGS_MEDIAKEYS = "keybindings-media";
	private static final String APPLICATION_STATE = "appstate";

	private static final String VOLUME = "volume";

	private Preferences prefs;

	@Inject
	Logger log;

	@PostConstruct
	public void init() {
		prefs = Preferences.userNodeForPackage(PreferencesService.class);
		applySettings();
	}

	public String getSubsonicHostname() {
		return prefs.get(SUBSONIC_ADDRESS, "");
	}

	public void setSubsonicHostname(String hostname) {
		prefs.put(SUBSONIC_ADDRESS, hostname);
	}

	public String getSubsonicLogin() {
		return prefs.get(SUBSONIC_LOGIN, "");
	}

	public void setSubsonicLogin(String login) {
		prefs.put(SUBSONIC_LOGIN, login);
	}

	public String getSubsonicPassword() {
		return prefs.get(SUBSONIC_PASSWORD, "");
	}

	public void setSubsonicPassword(String passwd) {
		prefs.put(SUBSONIC_PASSWORD, passwd);
	}

	public boolean isProxyEnabled() {
		return prefs.getBoolean(PROXY_ENABLED, false);
	}

	public void setProxyEnabled(boolean enabled) {
		prefs.putBoolean(PROXY_ENABLED, enabled);
	}

	public String getProxyHostname() {
		return prefs.get(PROXY_HOSTNAME, "");
	}

	public void setProxyHostname(String hostname) {
		prefs.put(PROXY_HOSTNAME, hostname);
	}

	public String getProxyPort() {
		return prefs.get(PROXY_PORT, "");
	}

	public void setProxyPort(String port) {
		prefs.put(PROXY_PORT, port);
	}

	public Type getProxyType() {
		String proxyString = prefs.get(PROXY_TYPE, Type.HTTP.name());
		return Type.valueOf(proxyString);
	}

	public void setProxyType(Type type) {
		prefs.put(PROXY_TYPE, type.name());
	}

	public boolean isProxyAuthRequired() {
		return prefs.getBoolean(PROXY_AUTHREQUIRED, false);
	}

	public void setProxyAuthRequired(boolean required) {
		prefs.putBoolean(PROXY_AUTHREQUIRED, required);
	}

	public String getProxyLogin() {
		return prefs.get(PROXY_LOGIN, "");
	}

	public void setProxyLogin(String login) {
		prefs.put(PROXY_LOGIN, login);
	}

	public String getProxyPassword() {
		return prefs.get(PROXY_PASSWORD, "");
	}

	public void setProxyPassword(String passwd) {
		prefs.put(PROXY_PASSWORD, passwd);
	}

	public String getLookAndFeel() {
		return prefs.get(LOOKANDFEEL, GraphiteSkin.class.getName());
	}

	public void setLookAndFeel(String className) {
		prefs.put(LOOKANDFEEL, className);
	}

	public int getVolume() {
		return prefs.getInt(VOLUME, 50);
	}

	public void setVolume(int volume) {
		prefs.putInt(VOLUME, volume);
	}

	public boolean isMinimizeToTray() {
		return prefs.getBoolean(MINIMIZE_TO_TRAY, false);
	}

	public void setMinimizeToTray(boolean minimize) {
		prefs.putBoolean(MINIMIZE_TO_TRAY, minimize);
	}

	public List<KeyBindingModel> getKeyBindings() {
		List<KeyBindingModel> list = Lists.newArrayList();
		String json = prefs.get(KEYBINDINGS, null);
		if (json != null) {
			list = new JSONDeserializer<List<KeyBindingModel>>()
					.deserialize(json);
		}
		return list;
	}

	public void setKeyBindings(List<KeyBindingModel> keyBindings) {
		prefs.put(KEYBINDINGS, new JSONSerializer().deepSerialize(keyBindings));
	}

	public boolean isMediaKeyBindingActive() {
		return prefs.getBoolean(KEYBINDINGS_MEDIAKEYS, true);
	}

	public void setMediaKeyActive(boolean active) {
		prefs.putBoolean(KEYBINDINGS_MEDIAKEYS, active);
	}

	public ApplicationStateModel getApplicationState() {
		ApplicationStateModel model = null;
		String json = prefs.get(APPLICATION_STATE, null);
		if (json != null) {
			model = new JSONDeserializer<ApplicationStateModel>()
					.deserialize(json);
		}
		return model;
	}

	public void setApplicationState(ApplicationStateModel model) {
		prefs.put(APPLICATION_STATE, new JSONSerializer().deepSerialize(model));
	}

	public void flush() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			log.error("Could not flush preferences", e);
		}
	}

	public void applySettings() {
		String plafClassName = getLookAndFeel();
		try {
			SubstanceLookAndFeel.setSkin(plafClassName);
		} catch (Exception e) {
			log.info("Could not set the look and feel " + plafClassName + ".");
		}

		if (getProxyType() == Type.HTTP) {
			System.setProperty("http.proxyHost", getProxyHostname());
			System.setProperty("http.proxyPort", getProxyPort());
			System.setProperty("https.proxyHost", getProxyHostname());
			System.setProperty("https.proxyPort", getProxyPort());
			if (isProxyAuthRequired()) {
				Authenticator.setDefault(new ProxyAuth(getProxyLogin(),
						getProxyPassword()));

			}
		} else {
			System.setProperty("socksProxyHost", getProxyHostname());
			System.setProperty("socksProxyPort", getProxyPort());
			if (isProxyAuthRequired()) {
				System.setProperty("java.net.socks.username", getProxyLogin());
				System.setProperty("java.net.socks.password",
						getProxyPassword());
				Authenticator.setDefault(new ProxyAuth(getProxyLogin(),
						getProxyPassword()));
			}
		}
	}

	public class ProxyAuth extends Authenticator {
		private PasswordAuthentication auth;

		private ProxyAuth(String user, String password) {
			auth = new PasswordAuthentication(user,
					password == null ? new char[] {} : password.toCharArray());
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return auth;
		}
	}
}
