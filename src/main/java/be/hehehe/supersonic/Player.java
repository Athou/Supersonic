package be.hehehe.supersonic;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingWorker;

import org.apache.commons.io.IOUtils;

import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.events.VolumeChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@Singleton
public class Player {

	@Inject
	SubsonicService subsonicService;

	@Inject
	Event<SongEvent> event;

	private enum State {
		PLAY, PAUSE, STOP, SKIP;
	}

	private State state = State.STOP;
	private SongModel currentSong;
	private int skipToPercentage = -1;

	private SourceDataLine line;
	private AudioInputStream din;

	private Object mutex = new Object();

	private float volume = 0.5f;

	public void stateChanged(@Observes SongEvent e) {

		Type type = e.getType();
		if (type == Type.STOP) {
			stop();
		} else if (type == Type.PAUSE) {
			if (state == State.PAUSE) {
				unpause();
			} else {
				pause();
			}
		} else if (type == Type.PLAY) {
			if (state == State.PAUSE) {
				SongModel song = e.getSong();
				if (song != null && !song.equals(currentSong)) {
					play(song);
				} else {
					unpause();
				}
			} else {
				play(e.getSong());
			}
		} else if (type == Type.SKIP_TO) {
			skipTo(e.getSkipToPercentage());
		}
	}

	public void volumeChanged(@Observes VolumeChangedEvent e) {
		volume = e.getVolume();
		setGain();
	}

	private void setGain() {
		if (line != null) {
			FloatControl gainControl = (FloatControl) line
					.getControl(FloatControl.Type.MASTER_GAIN);
			double minGainDB = gainControl.getMinimum();
			double maxGainDB = gainControl.getMaximum();
			double ampGainDB = 0.5f * maxGainDB - minGainDB;
			double cste = Math.log(10.0) / 20;
			double valueDB = minGainDB + (1 / cste)
					* Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * volume);

			valueDB = Math.min(valueDB, maxGainDB);
			valueDB = Math.max(valueDB, minGainDB);

			gainControl.setValue((float) valueDB);
		}
	}

	public void play(final SongModel song) {
		stop();
		currentSong = song;
		new SwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				InputStream stream = subsonicService.invokeBinary("stream",
						new Param(song.getId()));
				start(stream);
				return null;
			}
		}.execute();
	}

	public void stop() {
		state = State.STOP;

		synchronized (mutex) {
			if (line != null) {
				line.stop();
				line.drain();
				line.close();
			}
			IOUtils.closeQuietly(din);
			line = null;
			din = null;
		}
	}

	public void pause() {
		if (line != null) {
			line.stop();
			line.flush();
		}
		state = State.PAUSE;
	}

	public void unpause() {
		if (line != null) {
			line.start();
		}
		state = State.PLAY;
	}

	private void requestNextSong() {
		event.fire(new SongEvent(Type.FINISHED));
	}

	public void skipTo(int skipToPercentage) {
		state = State.SKIP;
		this.skipToPercentage = skipToPercentage;
	}

	private void start(InputStream inputStream) {
		AudioInputStream in = null;
		state = State.PLAY;
		try {
			in = AudioSystem.getAudioInputStream(new BufferedInputStream(
					inputStream));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			rawplay(decodedFormat, din);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (state == State.STOP) {
				stop();
			} else {
				requestNextSong();
			}
		}

	}

	private void rawplay(AudioFormat targetFormat, AudioInputStream din)
			throws Exception {
		byte[] data = new byte[4096];
		line = getLine(targetFormat);
		setGain();
		if (line != null) {
			line.start();
			int read = 0;

			din.mark(Integer.MAX_VALUE);
			while (read != -1 && state != State.STOP) {

				if (state == State.PAUSE) {
					Thread.sleep(300);
				} else if (state == State.SKIP) {
					din.reset();
					din.skip((currentSong.getSize() / 100) * skipToPercentage);
					state = State.PLAY;
				}

				long lastEvent = 0;
				read = din.read(data, 0, data.length);
				if (read != -1) {
					synchronized (mutex) {
						if (line != null) {
							line.write(data, 0, read);
							long currentPosition = line
									.getMicrosecondPosition();
							if (currentPosition - lastEvent > 500000) {
								SongEvent songEvent = new SongEvent(
										Type.PROGRESS);
								songEvent
										.setCurrentPosition(currentPosition / 1000000);
								songEvent.setTotal(currentSong.getDuration());
								songEvent.setSong(currentSong);
								event.fire(songEvent);
								lastEvent = currentPosition;
							}
						}
					}
				}
			}
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
}
