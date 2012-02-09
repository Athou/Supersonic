package be.hehehe.supersonic.action;

import java.awt.event.ActionEvent;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import be.hehehe.supersonic.Supersonic;
import be.hehehe.supersonic.model.ApplicationStateModel;
import be.hehehe.supersonic.panels.CoverPanel;
import be.hehehe.supersonic.panels.SearchPanel;
import be.hehehe.supersonic.service.KeyBindingService;
import be.hehehe.supersonic.service.PreferencesService;

@SuppressWarnings("serial")
@Singleton
public class ExitAction extends AbstractAction {

	@Inject
	KeyBindingService keyBindingService;

	@Inject
	PreferencesService preferencesService;

	@Inject
	Instance<Object> cdi;

	@Inject
	Logger log;

	@Override
	public void actionPerformed(ActionEvent e) {

		Supersonic supersonic = cdi.select(Supersonic.class).get();
		CoverPanel coverPanel = cdi.select(CoverPanel.class).get();
		SearchPanel searchPanel = cdi.select(SearchPanel.class).get();

		ApplicationStateModel model = new ApplicationStateModel();
		model.setWindowState(supersonic.getExtendedState());
		model.setWindowSize(supersonic.getSize());
		model.setCoverPanel(coverPanel.getSize());
		model.setSearchPanel(searchPanel.getSize());
		log.debug(model.getWindowState());

		preferencesService.setApplicationState(model);
		preferencesService.flush();

		keyBindingService.stop();
		System.exit(0);
	}

}
