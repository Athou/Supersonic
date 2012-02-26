package be.hehehe.supersonic.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class DownloadingStream extends InputStream implements Runnable {

	private Logger log = Logger.getLogger(DownloadingStream.class);

	private BufferedInputStream source;
	private boolean closed;
	private int pos = 0;
	private int mark = -1;

	private int[] bytes;

	public DownloadingStream(InputStream source, int size) {
		this.source = new BufferedInputStream(source);
		bytes = new int[size];
		Arrays.fill(bytes, -1);
		Executors.newCachedThreadPool().execute(this);
	}

	@Override
	public int read() throws IOException {
		int read = -1;

		while (!hasData() && !closed) {
			log.debug("Buffering...");
			SwingUtils.sleep(100);
		}

		if (hasData()) {
			read = bytes[pos];
			pos++;
		}
		return read;
	}

	@Override
	public synchronized void mark(int readlimit) {
		mark = pos;
	}

	@Override
	public synchronized void reset() throws IOException {
		pos = mark;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void close() throws IOException {
		bytes = null;
	}

	@Override
	public long skip(long n) throws IOException {
		log.debug("skipping " + n + " bytes");
		pos += n;
		return n;
	}

	private boolean hasData() {
		return (bytes != null && pos < bytes.length && bytes[pos] != -1);
	}

	@Override
	public void run() {
		try {
			log.debug("Starting download of song.");
			int read = -1;
			int writePos = 0;
			while ((read = source.read()) != -1) {
 				try {
 					bytes[writePos] = read;
 					writePos++;
 				} catch (NullPointerException e) {
 					break;
 				}
			}
			log.debug("Song downloaded.");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(source);
			closed = true;
		}
	}

}
