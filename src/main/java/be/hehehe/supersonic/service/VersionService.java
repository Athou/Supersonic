package be.hehehe.supersonic.service;

import java.util.ResourceBundle;

import javax.inject.Singleton;

@Singleton
public class VersionService {

	public String getVersion() {
		return ResourceBundle.getBundle("version").getString("version");
	}
}
