package be.hehehe.supersonic.service;

import java.io.InputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.URLBuilder;

@Named
public class SubsonicService {

	@Inject
	PreferencesService preferencesService;
	
	@Inject Logger log;

	@SuppressWarnings("unchecked")
	public Response invoke(String method, Param... params)
			throws SupersonicException {
		Response response = null;
		try {
			String responseString = IOUtils.toString(invokeBinary(method,
					params));
			JAXBContext context = JAXBContext.newInstance(Response.class
					.getPackage().getName());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement<Response> jaxbResponse = (JAXBElement<Response>) unmarshaller
					.unmarshal(new StringReader(responseString));
			response = jaxbResponse.getValue();
		} catch (Exception e) {
			throw new SupersonicException(e);
		}
		return response;
	}

	public InputStream invokeBinary(String method, Param... params)
			throws SupersonicException {
		InputStream is = null;
		log.debug("Invoking: " + method);
		try {
			URLBuilder builder = new URLBuilder(
					preferencesService.getSubsonicHostname() + "/rest/"
							+ method + ".view");
			builder.addParam("u", preferencesService.getSubsonicLogin());
			builder.addParam("p", preferencesService.getSubsonicPassword());
			builder.addParam("v", "1.7.0");
			builder.addParam("c", "supersonic");
			for (Param param : params) {
				builder.addParam(param.getName(), param.getValue());
			}

			URL url = new URL(builder.build());
			System.out.println(url.toString());
			URLConnection connection = url.openConnection();

			clearProxy();
			if (preferencesService.isProxyEnabled()) {
				setProxy(connection);

			}

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			is = url.openStream();
		} catch (Exception e) {
			throw new SupersonicException(e);
		}
		return is;

	}

	private void clearProxy() {
		System.setProperty("http.proxyHost", "");
		System.setProperty("http.proxyPort", "");
		System.setProperty("https.proxyHost", "");
		System.setProperty("https.proxyPort", "");

		System.setProperty("socksProxyHost", "");
		System.setProperty("socksProxyPort", "");

	}

	private void setProxy(URLConnection connection) {
		if (preferencesService.getProxyType() == Type.HTTP) {
			System.setProperty("http.proxyHost",
					preferencesService.getProxyHostname());
			System.setProperty("http.proxyPort",
					preferencesService.getProxyPort());
			System.setProperty("https.proxyHost",
					preferencesService.getProxyHostname());
			System.setProperty("https.proxyPort",
					preferencesService.getProxyPort());
			if (preferencesService.isProxyAuthRequired()) {
				String password = preferencesService.getProxyLogin() + ":"
						+ preferencesService.getProxyPassword();
				String encodedPassword = Base64.encodeBase64String(password
						.getBytes());
				connection.setRequestProperty("Proxy-Authorization", "Basic "
						+ encodedPassword);
				Authenticator
						.setDefault(new ProxyAuth(preferencesService
								.getProxyLogin(), preferencesService
								.getProxyPassword()));

			}
		} else {
			System.setProperty("socksProxyHost",
					preferencesService.getProxyHostname());
			System.setProperty("socksProxyPort",
					preferencesService.getProxyPort());
			if (preferencesService.isProxyAuthRequired()) {
				System.setProperty("java.net.socks.username",
						preferencesService.getProxyLogin());
				System.setProperty("java.net.socks.password",
						preferencesService.getProxyPassword());
				Authenticator
						.setDefault(new ProxyAuth(preferencesService
								.getProxyLogin(), preferencesService
								.getProxyPassword()));
			}
		}
	}

	public static class Param {
		private String name;
		private String value;

		public Param(String value) {
			this("id", value);
		}

		public Param(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
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
