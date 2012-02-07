package be.hehehe.supersonic.service;

import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.URLBuilder;

import com.google.common.collect.Lists;

@Singleton
public class SubsonicService {

	@Inject
	PreferencesService preferencesService;

	@Inject
	Logger log;

	public Response invoke(String method, Param... params)
			throws SupersonicException {
		String subsonicHost = preferencesService.getSubsonicHostname();
		String userName = preferencesService.getSubsonicLogin();
		String password = preferencesService.getSubsonicPassword();
		return invoke(method, subsonicHost, userName, password, params);
	}

	@SuppressWarnings("unchecked")
	public Response invoke(String method, String subsonicHost, String userName,
			String password, Param... params) throws SupersonicException {
		Response response = null;
		try {
			String responseString = IOUtils.toString(invokeBinary(method,
					subsonicHost, userName, password, params));
			if (StringUtils.isBlank(responseString)
					|| !responseString.startsWith("<?xml")) {
				throw new SupersonicException(
						"Response is not valid xml. Wrong address?");
			}
			log.debug("Response: " + responseString);
			JAXBContext context = JAXBContext.newInstance(Response.class
					.getPackage().getName());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement<Response> jaxbResponse = (JAXBElement<Response>) unmarshaller
					.unmarshal(new StringReader(responseString));
			response = jaxbResponse.getValue();
		} catch (SupersonicException e) {
			throw e;
		} catch (Exception e) {
			throw new SupersonicException(e);
		}
		return response;
	}

	public InputStream invokeBinary(String method, Param... params)
			throws SupersonicException {
		String subsonicHost = preferencesService.getSubsonicHostname();
		String userName = preferencesService.getSubsonicLogin();
		String password = preferencesService.getSubsonicPassword();
		return invokeBinary(method, subsonicHost, userName, password, params);
	}

	public InputStream invokeBinary(String method, String subsonicHost,
			String userName, String password, Param... params)
			throws SupersonicException {
		InputStream is = null;
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Invoking ");
			sb.append(method);
			sb.append(" with ");
			sb.append(params.length);
			sb.append(" params [");

			List<String> paramsString = Lists.newArrayList();
			for (Param param : params) {
				paramsString.add(param.name + "=" + param.value);
			}
			sb.append(StringUtils.join(paramsString, ","));
			sb.append("]");
			log.debug(sb.toString());
		}

		try {

			if (StringUtils.isNotBlank(subsonicHost)
					&& subsonicHost.endsWith(".view")) {
				subsonicHost = subsonicHost.substring(subsonicHost
						.lastIndexOf("/"));
			}
			if (!subsonicHost.endsWith("/")) {
				subsonicHost += "/";
			}
			URLBuilder builder = new URLBuilder(subsonicHost + "rest/" + method
					+ ".view");
			builder.addParam("u", userName);
			builder.addParam("p", password);
			builder.addParam("v", "1.7.0");
			builder.addParam("c", "supersonic");
			for (Param param : params) {
				builder.addParam(param.getName(), param.getValue());
			}

			URL url = new URL(builder.build());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

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
		} catch (SupersonicException e) {
			throw e;
		} catch (Exception e) {
			throw new SupersonicException(e);
		}
		return is;

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

		public Param(String name, int value) {
			this.name = name;
			this.value = "" + value;
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

}
