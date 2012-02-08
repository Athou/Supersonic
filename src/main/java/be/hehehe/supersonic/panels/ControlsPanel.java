package be.hehehe.supersonic.panels;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

	private JLabel currentSongLabel;

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
		volumeSlider.setValue(preferencesService.getVolume());

		chckbxShuffle = new JCheckBox("Shuffle");
		chckbxShuffle.setFocusable(false);
		add(chckbxShuffle, "cell 5 0");

		chckbxRepeat = new JCheckBox("Repeat");
		chckbxRepeat.setFocusable(false);
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
					// event.fire(songEvent);
				}
			}
		});

		progressText = new JLabel(SwingUtils.formatDuration(0L));
		add(progressText, "cell 6 1,alignx center");

		JPanel currentSongPanel = new JPanel();
		currentSongPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		currentSongLabel = new JLabel(" ");
		currentSongPanel.add(currentSongLabel);
		currentSongLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(currentSongPanel, "cell 0 2 7 1,grow");
		currentSongLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SongEvent songEvent = new SongEvent(Type.CHANGE_SELECTION);
				songEvent.setSong(currentSong);
				event.fire(songEvent);
			}
		});

	}

	private void nextSong() {
		SongEvent songEvent = new SongEvent(Type.PLAY);
		songEvent.setSong(getNextSong());
		event.fire(songEvent);
	}

	public SongModel getNextSong() {
		SongModel nextSong = songsPanel.getNextSong(currentSong);
		if (chckbxRepeat.isSelected()) {
			nextSong = currentSong;
		} else if (chckbxShuffle.isSelected()) {
			nextSong = songsPanel.getNextRandomSong();
		}
		return nextSong;
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
					currentSongLabel.setText(currentSong.getArtist() + " - "
							+ currentSong.getAlbum() + " - "
							+ currentSong.getTitle());
				} else if (e.getType() == Type.FINISHED) {
					nextSong();
				}
			}
		});

	}
}
