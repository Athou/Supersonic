package be.hehehe.supersonic;

import javax.enterprise.inject.Instance;
import javax.swing.SwingUtilities;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import be.hehehe.supersonic.service.PreferencesService;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				WeldContainer weld = new Weld().initialize();
				Instance<Object> instance = weld.instance();
				instance.select(PreferencesService.class).get().applySettings();
				instance.select(Supersonic.class).get();

			}
		});
	}
}
