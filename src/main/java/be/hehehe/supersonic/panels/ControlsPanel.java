package be.hehehe.supersonic.panels;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

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

import org.apache.commons.io.IOUtils;

import be.hehehe.supersonic.events.ControlsEvent;
import be.hehehe.supersonic.events.DownloadingEvent;
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

	private static final String CHAR_PLAY = "N";
	private static final String CHAR_PAUSE = "O";
	private static final String CHAR_STOP = "P";
	private static final String CHAR_NEXT = "Q";

	@Inject
	IconService iconService;

	@Inject
	PreferencesService preferencesService;

	@Inject
	Event<SongEvent> event;

	@Inject
	Event<VolumeChangedEvent> volumeEvent;

	@Inject
	Event<ControlsEvent> controlsEvent;

	private SongModel currentSong;

	private JSlider seekBar;
	private JLabel downloadProgressText;
	private JLabel progressText;
	private JCheckBox chckbxRepeat;
	private JCheckBox chckbxShuffle;

	private JLabel currentSongLabel;
	private SongModel selectedSong;

	private boolean mouseClicked = false;

	@PostConstruct
	public void init() {

		Font fontawesome = loadFont();
		setLayout(new MigLayout("insets 0"));

		final JButton btnPlay = new JButton(CHAR_PLAY);
		btnPlay.setFont(fontawesome);
		add(btnPlay);
		btnPlay.setFocusable(false);
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedSong != null) {
					SongEvent songEvent = new SongEvent(Type.PLAY);
					songEvent.setSong(selectedSong);
					event.fire(songEvent);
				}
			}
		});

		JButton btnPause = new JButton(CHAR_PAUSE);
		btnPause.setFont(fontawesome);
		btnPause.setFocusable(false);
		add(btnPause);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				event.fire(new SongEvent(Type.PAUSE));
			}
		});

		JButton btnStop = new JButton(CHAR_STOP);
		btnStop.setFont(fontawesome);
		btnStop.setFocusable(false);
		add(btnStop);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				event.fire(new SongEvent(Type.STOP));
			}
		});

		JButton btnNext = new JButton(CHAR_NEXT);
		btnNext.setFont(fontawesome);
		btnNext.setFocusable(false);
		add(btnNext);
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				event.fire(new SongEvent(Type.FINISHED));
			}
		});

		JSlider volumeSlider = new JSlider();
		volumeSlider.setFocusable(false);
		volumeSlider.setMinimum(0);
		volumeSlider.setMaximum(100);
		add(volumeSlider, "growx, pushx");
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

		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				controlsEvent.fire(new ControlsEvent(
						chckbxShuffle.isSelected(), chckbxRepeat.isSelected()));
			}
		};

		chckbxShuffle = new JCheckBox("Shuffle");
		chckbxShuffle.setFocusable(false);
		add(chckbxShuffle);
		chckbxShuffle.addChangeListener(changeListener);

		chckbxRepeat = new JCheckBox("Repeat");
		chckbxRepeat.setFocusable(false);
		add(chckbxRepeat, "wrap");
		chckbxRepeat.addChangeListener(changeListener);

		downloadProgressText = new JLabel();
		add(downloadProgressText, "alignx center");

		seekBar = new JSlider();
		seekBar.setFocusable(false);
		seekBar.setMinimum(0);
		seekBar.setMaximum(100);
		seekBar.setValue(0);
		add(seekBar, "span 5, growx");
		seekBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (mouseClicked && !source.getValueIsAdjusting()) {
					int percentage = source.getValue();
					SongEvent songEvent = new SongEvent(Type.SKIP_TO);
					songEvent.setSkipToPercentage(percentage);
					event.fire(songEvent);
				}
			}
		});
		seekBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mouseClicked(e);
				mouseClicked = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				mouseClicked = false;
			}
		});

		progressText = new JLabel(SwingUtils.formatDuration(0L));
		add(progressText, "alignx center, wrap");

		currentSongLabel = new JLabel(" ", JLabel.CENTER);
		currentSongLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(currentSongLabel, "dock south");
		currentSongLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SongEvent songEvent = new SongEvent(Type.CHANGE_SELECTION);
				songEvent.setSong(currentSong);
				event.fire(songEvent);
			}
		});

	}

	private Font loadFont() {
		InputStream is = null;
		Font font = null;
		try {
			is = getClass().getResourceAsStream("/META-INF/resources/sosa.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			font = font.deriveFont(24f);
		} catch (Exception e) {
			SwingUtils.handleError(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return font;
	}

	public void onProgress(@Observes final SongEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (e.getType() == Type.PROGRESS) {
					currentSong = e.getSong();

					int percentage = e.getPercentage();
					if (!mouseClicked) {
						seekBar.setValue(percentage);
					}
					progressText.setText(SwingUtils.formatDuration(e
							.getCurrentPosition())
							+ "/"
							+ SwingUtils.formatDuration(e.getTotal()));
					currentSongLabel.setText(currentSong.getArtist() + " - "
							+ currentSong.getAlbum() + " - "
							+ currentSong.getTitle());
				} else if (e.getType() == Type.SELECTION_CHANGED) {
					selectedSong = e.getSong();
				}
			}
		});
	}

	public void onDownloadProgress(@Observes final DownloadingEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int perc = Math.min(e.getPercentage(), 100);
				downloadProgressText.setText(perc + "%");
			}
		});

	}
}
