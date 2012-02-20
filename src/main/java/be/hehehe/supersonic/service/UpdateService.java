package be.hehehe.supersonic.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;

import be.hehehe.supersonic.utils.SupersonicException;
import flexjson.JSONDeserializer;

@Singleton
public class UpdateService {

	private static final String UPDATE_URL = "https://api.github.com/repos/Athou/Supersonic/downloads";

	@Inject
	VersionService versionService;

	@Inject
	ConnectionService connectionService;

	public boolean checkForUpdate() throws SupersonicException {
		boolean update = false;

		try {
			InputStream inputStream = connectionService.getConnection(new URL(
					UPDATE_URL));
			String json = IOUtils.toString(inputStream);
			List<Map<String, String>> list = new JSONDeserializer<List<Map<String, String>>>()
					.deserialize(json);
			Map<String, String> info = list.get(0);
			String name = info.get("name");
			String newVersion = name.substring("supersonic-".length())
					.substring(0, ".zip".length());
			String oldVersion = versionService.getVersion();

			update = parseVersion(newVersion) > parseVersion(oldVersion);

		} catch (Exception e) {
			throw new SupersonicException(e.getMessage(), e);
		}
		return update;
	}

	private int parseVersion(String version) {
		return Integer.parseInt(version.replace(".", ""));
	}

	public URI getDownloadPageURI() {
		URI uri = null;
		try {
			uri = new URI("https://github.com/Athou/Supersonic/downloads");
		} catch (URISyntaxException e) {
			// never happens
		}
		return uri;
	}
}
