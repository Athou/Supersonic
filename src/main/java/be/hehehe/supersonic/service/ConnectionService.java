package be.hehehe.supersonic.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;

import be.hehehe.supersonic.utils.SupersonicException;

@Singleton
public class ConnectionService {

	@Inject
	PreferencesService preferencesService;

	public InputStream getConnection(URL url) throws SupersonicException {
		InputStream is = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			if (connection instanceof HttpsURLConnection) {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0],
						new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
				SSLContext.setDefault(ctx);
				HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
				httpsConnection.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
			}

			if (preferencesService.isProxyEnabled()) {
				setProxy(connection);
			}

			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			connection.connect();
			int code = connection.getResponseCode();

			if (code != 200) {
				throw new SupersonicException("HTTP Error code: " + code);
			}

			is = connection.getInputStream();
		} catch (Exception e) {
			throw new SupersonicException(e.getMessage(), e);
		}
		return is;
	}

	private static class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private void setProxy(URLConnection connection) {
		if (preferencesService.getProxyType() == Type.HTTP) {
			if (preferencesService.isProxyAuthRequired()) {
				String password = preferencesService.getProxyLogin() + ":"
						+ preferencesService.getProxyPassword();
				String encodedPassword = Base64.encodeBase64String(password
						.getBytes());
				connection.setRequestProperty("Proxy-Authorization", "Basic "
						+ encodedPassword);
			}
		}
	}
}
