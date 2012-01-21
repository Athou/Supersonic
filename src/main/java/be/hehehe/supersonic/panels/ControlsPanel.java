package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.Player.State;
import be.hehehe.supersonic.events.PlayingSongChangedEvent;
import be.hehehe.supersonic.events.VolumeChangedEvent;
import be.hehehe.supersonic.service.IconService;

@SuppressWarnings("serial")
@Singleton
public class ControlsPanel extends JPanel {

	@Inject
	SongsPanel songsPanel;

	@Inject
	IconService iconService;

	@Inject
	Event<PlayingSongChangedEvent> songChangedEvent;

	@Inject
	Event<VolumeChangedEvent> volumeEvent;

	private boolean pause = false;

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("", "[][][][][]", "[grow][grow]"));

		JButton btnPrev = new JButton("Prev");
		add(btnPrev, "cell 0 0");

		JButton btnPlaypause = new JButton();
		btnPlaypause.setIcon(iconService.getIcon("play"));
		add(btnPlaypause, "cell 1 0");
		btnPlaypause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pause = !pause;
				State state = pause ? State.PAUSE : State.PLAY;
				songChangedEvent.fire(new PlayingSongChangedEvent(null, state));
			}
		});

		JButton btnStop = new JButton();
		btnStop.setIcon(iconService.getIcon("stop"));
		btnStop.setFocusable(false);
		add(btnStop, "cell 2 0");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				songChangedEvent.fire(new PlayingSongChangedEvent(null,
						State.STOP));
			}
		});

		JButton btnNext = new JButton("Next");
		add(btnNext, "cell 3 0");
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				songChangedEvent.fire(new PlayingSongChangedEvent(songsPanel
						.getNextSong(), State.PLAY));
			}
		});

		JSlider volumeSlider = new JSlider();
		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		volumeSlider.setValue(50);
		add(volumeSlider, "cell 4 0");
		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					float volume = source.getValue() / 100f;
					volumeEvent.fire(new VolumeChangedEvent(volume));
				}
			}
		});

		JSlider seekBar = new JSlider();
		add(seekBar, "cell 0 1 5 1,growx");
	}

}
