package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.events.VolumeChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.IconService;
import be.hehehe.supersonic.service.PreferencesService;
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class ControlsPanel extends JPanel {

	@Inject
	SongsPanel songsPanel;

	@Inject
	IconService iconService;

	@Inject
	PreferencesService preferencesService;

	@Inject
	Event<SongEvent> event;

	@Inject
	Event<VolumeChangedEvent> volumeEvent;

	private int seekbarProgress = 0;
	private SongModel currentSong;

	private JSlider seekBar;
	private JLabel progressText;
	private JCheckBox chckbxRepeat;
	private JCheckBox chckbxShuffle;

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("insets 0", "[][][][][grow][][]", "[][]"));

		final JButton btnPlay = new JButton();
		btnPlay.setIcon(iconService.getIcon("play"));
		add(btnPlay, "cell 0 0");
		btnPlay.setFocusable(false);
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlay.setIcon(iconService.getIcon("play"));
				SongEvent songEvent = new SongEvent(Type.PLAY);
				songEvent.setSong(songsPanel.getSelectedSong());
				event.fire(songEvent);
			}
		});

		JButton btnPause = new JButton();
		btnPause.setIcon(iconService.getIcon("pause"));
		btnPause.setFocusable(false);
		add(btnPause, "cell 1 0");
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				event.fire(new SongEvent(Type.PAUSE));
			}
		});

		JButton btnStop = new JButton();
		btnStop.setIcon(iconService.getIcon("stop"));
		btnStop.setFocusable(false);
		add(btnStop, "cell 2 0");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				event.fire(new SongEvent(Type.STOP));
			}
		});

		JButton btnNext = new JButton();
		add(btnNext, "cell 3 0");
		btnNext.setIcon(iconService.getIcon("next"));
		btnNext.setFocusable(false);
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextSong();
			}
		});

		JSlider volumeSlider = new JSlider();
		volumeSlider.setFocusable(false);
		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		volumeSlider.setValue(preferencesService.getVolume());
		add(volumeSlider, "cell 4 0,growx");
		volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int volume = source.getValue();
					preferencesService.setVolume(volume);
					preferencesService.flush();
					float volumePercentage = volume / 100f;
					volumeEvent.fire(new VolumeChangedEvent(volumePercentage));
				}
			}
		});

		chckbxShuffle = new JCheckBox("Shuffle");
		add(chckbxShuffle, "cell 5 0");

		chckbxRepeat = new JCheckBox("Repeat");
		add(chckbxRepeat, "cell 6 0");

		seekBar = new JSlider();
		seekBar.setFocusable(false);
		seekBar.setMinimum(0);
		seekBar.setMaximum(100);
		seekBar.setValue(0);
		add(seekBar, "cell 0 1 6 1,growx");
		seekBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int percentage = source.getValue();
				if (!source.getValueIsAdjusting()
						&& percentage != seekbarProgress) {
					SongEvent songEvent = new SongEvent(Type.SKIP_TO);
					songEvent.setSkipToPercentage(percentage);
					event.fire(songEvent);
				}
			}
		});

		progressText = new JLabel(SwingUtils.formatDuration(0));
		add(progressText, "cell 6 1,alignx center");

	}

	private void nextSong() {
		SongEvent songEvent = new SongEvent(Type.PLAY);

		SongModel nextSong = songsPanel.getNextSong();
		if (chckbxRepeat.isSelected()) {
			nextSong = currentSong;
		} else if (chckbxShuffle.isSelected()) {
			nextSong = songsPanel.getNextRandomSong();
		}

		songEvent.setSong(nextSong);
		event.fire(songEvent);
	}

	public void onProgress(@Observes final SongEvent e) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (e.getType() == Type.PROGRESS) {
					currentSong = e.getSong();

					int percentage = e.getPercentage();
					seekbarProgress = percentage;
					seekBar.setValue(percentage);
					progressText.setText(SwingUtils.formatDuration(e
							.getCurrentPosition())
							+ "/"
							+ SwingUtils.formatDuration(e.getTotal()));
				} else if (e.getType() == Type.FINISHED) {
					nextSong();
				}
			}
		});

	}
}