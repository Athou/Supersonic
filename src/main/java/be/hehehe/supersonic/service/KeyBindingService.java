package be.hehehe.supersonic.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import be.hehehe.supersonic.utils.SwingUtils;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

@Singleton
public class KeyBindingService implements HotKeyListener {

	@Inject
	Logger log;

	private Provider provider;

	@PostConstruct
	public void init() {
		provider = Provider.getCurrentProvider(true);
		provider.register(KeyStroke.getKeyStroke("ctrl A"), this);
	}

	public void stop() {
		provider.reset();
		provider.stop();
	}

	@Override
	public void onHotKey(HotKey hotKey) {
		log.info(hotKey.toString());
	}

}
