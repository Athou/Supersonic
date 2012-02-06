package be.hehehe.supersonic.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

public class DownloadingStream extends InputStream implements Runnable {

	private InputStream stream;
	private int pos = 0;
	private boolean open = true;

	private List<Integer> bytes = Lists.newArrayList();

	public DownloadingStream(InputStream stream) {
		this.stream = stream;
		Executors.newCachedThreadPool().execute(this);
	}

	@Override
	public void run() {
		try {
			int read = -1;
			while ((read = stream.read()) != -1) {
				bytes.add(read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
			open = false;
		}

	}

	@Override
	public int read() throws IOException {

		while (open && available() == 0) {
			SwingUtils.sleep(200);
		}
		if (!open && available() == 0) {
			return -1;
		}
		int result = bytes.get(pos);
		pos++;
		return result;
	}

	@Override
	public int available() throws IOException {
		return bytes.size() - pos;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public long skip(long n) throws IOException {
		int skip = (int) n;
		int available = available();
		if (available < n) {
			skip = available;
		}
		pos += skip;
		return skip;
	}

}
