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
import be.hehehe.supersonic.utils.SwingUtils;

@SuppressWarnings("serial")
@Singleton
public class ControlsPanel extends JPanel {

	@Inject
	SongsPanel songsPanel;

	@Inject
	IconService iconService;

	@Inject
	Event<SongEvent> event;

	@Inject
	Event<VolumeChangedEvent> volumeEvent;

	private boolean play = false;
	private int seekbarProgress = 0;
	private SongModel currentSong;

	private JSlider seekBar;
	private JLabel progressText;
	private JCheckBox chckbxRepeat;
	private JCheckBox chckbxShuffle;

	@PostConstruct
	public void init() {
		setLayout(new MigLayout("insets 0", "[][][][][grow]", "[][]"));

		JButton btnPrev = new JButton();
		add(btnPrev, "cell 0 0");
		btnPrev.setIcon(iconService.getIcon("back"));
		// TODO back button
		btnPrev.setToolTipText("Does not work atm");
		btnPrev.setFocusable(false);

		final JButton btnPlaypause = new JButton();
		btnPlaypause.setIcon(iconService.getIcon("play"));
		add(btnPlaypause, "cell 1 0");
		btnPlaypause.setFocusable(false);
		btnPlaypause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				play = !play;
				Type type = play ? Type.PLAY : Type.PAUSE;
				String iconName = play ? "pause" : "play";
				btnPlaypause.setIcon(iconService.getIcon(iconName));
				SongEvent songEvent = new SongEvent(type);
				songEvent.setSong(songsPanel.getSelectedSong());
				event.fire(songEvent);
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
		volumeSlider.setValue(50);
		add(volumeSlider, "cell 4 0,growx");
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

		chckbxShuffle = new JCheckBox("Shuffle");
		add(chckbxShuffle, "flowx,cell 5 0");

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
