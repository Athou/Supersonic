package be.hehehe.supersonic.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.KeyBindingModel;
import be.hehehe.supersonic.panels.ControlsPanel;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

@Singleton
public class KeyBindingService implements HotKeyListener {

	@Inject
	Logger log;

	@Inject
	PreferencesService preferencesService;

	@Inject
	ControlsPanel controlsPanel;

	@Inject
	Event<SongEvent> event;

	private Provider provider;

	private List<KeyBindingModel> keyBindings;

	@PostConstruct
	public void init() {
		provider = Provider.getCurrentProvider(true);
		applyBindings();
	}

	public void applyBindings() {
		provider.reset();
		keyBindings = preferencesService.getKeyBindings();
		for (KeyBindingModel model : keyBindings) {
			provider.register(
					KeyStroke.getKeyStroke(model.getKeyCode(),
							model.getModifiers()), this);
			log.info("Registering Hotkey " + model.toString());
		}
	}

	public void stop() {
		provider.reset();
		provider.stop();
	}

	@Override
	public void onHotKey(HotKey hotKey) {
		log.debug("Received hotkey: " + hotKey.toString());
		if (hotKey.keyStroke != null) {
			KeyStroke keyStroke = hotKey.keyStroke;
			for (KeyBindingModel model : keyBindings) {
				if (keyStroke.getKeyCode() == model.getKeyCode()
						&& keyStroke.getModifiers() == model.getModifiers()) {
					SongEvent songEvent = new SongEvent(model.getType());
					if (model.getType() == Type.FINISHED) {
						songEvent.setSong(controlsPanel.getNextSong());
					}
					event.fire(songEvent);
				}
			}
		}
	}

}