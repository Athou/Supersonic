package be.hehehe.supersonic;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager
							.setLookAndFeel(new SubstanceGraphiteLookAndFeel());

				} catch (Exception e) {
					Logger.getLogger(Main.class).info(
							"Could not set the look and feel.");
				}

				WeldContainer weld = new Weld().initialize();
				weld.instance().select(Supersonic.class).get();

			}
		});
	}
}
