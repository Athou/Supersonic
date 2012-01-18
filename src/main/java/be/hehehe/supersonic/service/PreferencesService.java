package be.hehehe.supersonic.service;

import java.net.Proxy.Type;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

@Singleton
public class PreferencesService {

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

	private Preferences prefs;

	@PostConstruct
	public void init() {
		prefs = Preferences.userNodeForPackage(PreferencesService.class);
	}

	@Inject
	Logger log;

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

	public void flush() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			log.error("Could not flush preferences", e);
		}
	}

}
