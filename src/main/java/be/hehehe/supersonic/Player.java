package be.hehehe.supersonic;

import java.io.BufferedInputStream;
import java.io.InputStream;

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
import be.hehehe.supersonic.events.VolumeChangedEvent;
import be.hehehe.supersonic.model.SongModel;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;

@Singleton
public class Player {

	@Inject
	SubsonicService subsonicService;

	public enum State {
		PLAY, PAUSE, STOP;
	}

	private State state = State.STOP;
	private SongModel currentSong;

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
				play(e.getSong().getId());
			}
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

	public void play(final String songId) {
		stop();
		new SwingWorker<Object, Void>() {
			@Override
			protected Object doInBackground() throws Exception {
				InputStream stream = subsonicService.invokeBinary("stream",
						new Param(songId));
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
			// Start
			line.start();
			int nBytesRead = 0;
			int nBytesWritten = 0;
			while (nBytesRead != -1 && state != State.STOP) {

				if (state == State.PAUSE) {
					Thread.sleep(300);
				}
				nBytesRead = din.read(data, 0, data.length);
				if (nBytesRead != -1)
					nBytesWritten += line.write(data, 0, nBytesRead);
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
