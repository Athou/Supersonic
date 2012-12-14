package be.hehehe.supersonic.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import be.hehehe.supersonic.utils.SupersonicException;

@Singleton
public class UpdateService {

	private static final String UPDATE_URL = "http://lolz.hehehe.be/supersonic/version.txt";

	@Inject
	VersionService versionService;

	@Inject
	ConnectionService connectionService;

	@Inject
	Logger log;

	public boolean checkForUpdate() throws SupersonicException {
		boolean update = false;

		InputStream is = null;
		try {
			is = connectionService.getConnection(new URL(UPDATE_URL));
			String newVersion = IOUtils.toString(is);
			String oldVersion = versionService.getVersion();

			log.info("Old version: " + oldVersion);
			log.info("New version: " + newVersion);

			update = parseVersion(newVersion) > parseVersion(oldVersion);

			log.info("Update needed: " + update);

		} catch (Exception e) {
			throw new SupersonicException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(is);
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
