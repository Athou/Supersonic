package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.KeyBindingModel;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Singleton
public class KeyBindingPanel extends JPanel {

	private BindingComponent play = new BindingComponent(Type.PLAY);
	private BindingComponent pause = new BindingComponent(Type.PAUSE);
	private BindingComponent stop = new BindingComponent(Type.STOP);
	private BindingComponent next = new BindingComponent(Type.FINISHED);
	private BindingComponent[] components;

	@PostConstruct
	public void init() {
		components = new BindingComponent[] { play, pause, stop, next };
		setLayout(new MigLayout("", "[][grow]", "[][][][]"));

		add(new JLabel("Play"), "cell 0 0");
		add(new JLabel("Pause"), "cell 0 1");
		add(new JLabel("Stop"), "cell 0 2");
		add(new JLabel("Next song"), "cell 0 3");

		add(play, "cell 1 0,grow");
		add(pause, "cell 1 1,grow");
		add(stop, "cell 1 2,grow");
		add(next, "cell 1 3,grow");

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

	private class BindingComponent extends JPanel {

		private Type type;

		private JTextField field = new JTextField();
		private JButton clear = new JButton("Clear");

		private KeyStroke keyStroke;

		public BindingComponent(Type type) {
			this.type = type;
			setLayout(new MigLayout("", "[grow][]", "[]"));
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
