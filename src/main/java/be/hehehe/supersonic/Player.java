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
import org.apache.commons.lang.ObjectUtils;

import be.hehehe.supersonic.events.PlayingSongChangedEvent;
import be.hehehe.supersonic.events.PlayingSongProgressEvent;
import be.hehehe.supersonic.events.VolumeChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@Singleton
public class Player {

	@Inject
	SubsonicService subsonicService;

	@Inject
	Event<PlayingSongProgressEvent> progressEvent;

	public enum State {
		PLAY, PAUSE, STOP, SKIP;
	}

	private State state = State.STOP;
	private SongModel currentSong;
	private int skipToPercentage = -1;

	private SourceDataLine line;
	private AudioInputStream din;

	private float volume = 0.5f;

	public void stateChanged(@Observes PlayingSongChangedEvent e) {

		State state = e.getState();
		if (state == State.STOP) {
			stop();
		} else if (state == State.PAUSE) {
			pause();
		} else if (state == State.PLAY) {
			boolean sameSong = ObjectUtils.equals(e.getSong(), currentSong);
			if (sameSong) {
				unpause();
			} else {
				play(e.getSong());
			}
		} else if (state == State.SKIP) {
			skipTo(e.getSkipTo());
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

		if (line != null) {
			line.stop();
			line.drain();
			line.close();
		}
		IOUtils.closeQuietly(din);
		line = null;
		din = null;
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
			stop();
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

				read = din.read(data, 0, data.length);
				if (read != -1) {
					line.write(data, 0, read);

					PlayingSongProgressEvent event = new PlayingSongProgressEvent(
							line.getMicrosecondPosition() / 1000000,
							currentSong.getDuration());
					progressEvent.fire(event);
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
