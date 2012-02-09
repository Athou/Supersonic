package be.hehehe.supersonic;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.jboss.weld.environment.se.Weld;

public class Main {

	@Inject
	Supersonic supersonic;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Weld().initialize().instance().select(Main.class).get();
			}
		});
	}
}
