package be.hehehe.supersonic.panels;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.KeyBindingModel;
import be.hehehe.supersonic.service.IconService;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Singleton
public class KeyBindingPanel extends JPanel {

	@Inject
	IconService iconService;

	private BindingComponent play;
	private BindingComponent pause;
	private BindingComponent stop;
	private BindingComponent next;
	private BindingComponent[] components;

	private JCheckBox mediaKeys;

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("insets 0", "[grow]", "[][]"));

		JPanel bindings = new JPanel();
		bindings.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
		bindings.setBorder(BorderFactory.createTitledBorder("Key Bindings"));
		add(bindings, "cell 0 0,growx");

		play = new BindingComponent(Type.PLAY);
		pause = new BindingComponent(Type.PAUSE);
		stop = new BindingComponent(Type.STOP);
		next = new BindingComponent(Type.FINISHED);
		components = new BindingComponent[] { play, pause, stop, next };

		bindings.add(new JLabel("Play"), "cell 0 0");
		bindings.add(new JLabel("Pause / unpause"), "cell 0 1");
		bindings.add(new JLabel("Stop"), "cell 0 2");
		bindings.add(new JLabel("Next song"), "cell 0 3");

		bindings.add(play, "cell 1 0,grow");
		bindings.add(pause, "cell 1 1,grow");
		bindings.add(stop, "cell 1 2,grow");
		bindings.add(next, "cell 1 3,grow");

		mediaKeys = new JCheckBox("Bind media keys");
		JPanel mediaKeysPanel = new JPanel();
		mediaKeysPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		mediaKeysPanel.setBorder(BorderFactory.createTitledBorder("Media Keys"));
		mediaKeysPanel.add(mediaKeys);
		add(mediaKeysPanel, "cell 0 1,growx");

	}

	public List<KeyBindingModel> getBindings() {
		List<KeyBindingModel> list = Lists.newArrayList();
		for (BindingComponent component : components) {
			if (component.getKeyBindingModel() != null) {
				list.add(component.getKeyBindingModel());
			}
		}
		return list;
	}

	public void setBindings(List<KeyBindingModel> bindings) {
		for (BindingComponent component : components) {
			component.setKeyBindingModel(null);
		}
		for (KeyBindingModel model : bindings) {
			for (BindingComponent component : components) {
				if (component.getType() == model.getType()) {
					component.setKeyBindingModel(model);
				}
			}
		}
	}

	public boolean isMediaKeyBindingActive() {
		return mediaKeys.isSelected();
	}

	public void setMediaKeyBindingActive(boolean mediaKeyBindingActive) {
		mediaKeys.setSelected(mediaKeyBindingActive);
	}

	private class BindingComponent extends JPanel {

		private Type type;

		private JTextField field;
		private JButton clear;

		private KeyStroke keyStroke;

		public BindingComponent(Type type) {
			this.type = type;
			clear = new JButton(iconService.getIcon("cross"));
			field = new JTextField();
			setLayout(new MigLayout("insets 0", "[grow][]", "[]"));
			add(field, "cell 0 0,grow");
			add(clear, "cell 1 0");

			field.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					int mod = e.getModifiersEx();
					setKeyStroke(KeyStroke.getKeyStroke(key, mod));
				}
			});
			field.setEditable(false);

			clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setKeyStroke(null);
				}
			});
		}

		private void setKeyStroke(KeyStroke keyStroke) {
			this.keyStroke = keyStroke;
			field.setText(keyStroke == null ? null : new KeyBindingModel(
					keyStroke).toString());
		}

		public Type getType() {
			return type;
		}

		public KeyBindingModel getKeyBindingModel() {
			return keyStroke == null ? null : new KeyBindingModel(type,
					keyStroke.getKeyCode(), keyStroke.getModifiers());
		}

		public void setKeyBindingModel(KeyBindingModel model) {
			setKeyStroke(model == null ? null : KeyStroke.getKeyStroke(
					model.getKeyCode(), model.getModifiers()));
		}
	}
}
