package be.hehehe.supersonic.action;

import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.AbstractAction;

import be.hehehe.supersonic.service.KeyBindingService;

@SuppressWarnings("serial")
@Singleton
public class ExitAction extends AbstractAction {

	@Inject
	KeyBindingService keyBindingService;

	@Override
	public void actionPerformed(ActionEvent e) {
		keyBindingService.stop();
		System.exit(0);
	}

}
