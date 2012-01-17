package be.hehehe.supersonic.service;

import java.util.prefs.Preferences;

public class PreferencesService {

	private Preferences prefs = Preferences
			.userNodeForPackage(PreferencesService.class);

	public Preferences getPreferences() {
		return prefs;
	}

}
