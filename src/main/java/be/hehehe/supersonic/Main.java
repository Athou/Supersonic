package be.hehehe.supersonic;

import javax.enterprise.inject.Instance;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.GraphiteSkin;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;

import be.hehehe.supersonic.service.PreferencesService;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				WeldContainer weld = new Weld().initialize();
				Instance<Object> instance = weld.instance();
				String className = instance.select(PreferencesService.class)
						.get().getLookAndFeel();
				try {
					SubstanceLookAndFeel.setSkin(className);
				} catch (Exception e) {
					Logger.getLogger(Main.class).info(
							"Could not set the look and feel " + className
									+ ".");
				}

				instance.select(Supersonic.class).get();

			}
		});
	}
}
