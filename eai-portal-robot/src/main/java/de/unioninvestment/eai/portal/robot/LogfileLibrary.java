package de.unioninvestment.eai.portal.robot;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;

public class LogfileLibrary {

	private static final int MINIMUM_DELAY_TO_DETECT_CHANGES = 1000;

	private Map<String, List<String>> rows = new ConcurrentHashMap<String, List<String>>();
	private Map<String, Tailer> tailers = new ConcurrentHashMap<String, Tailer>();

	private long retryInterval = 500;

	public void monitorLogFile(final String filename)
			throws InterruptedException {
		TailerListener listener = new TailerListenerAdapter() {
			@Override
			public void fileNotFound() {
				throw new IllegalArgumentException("Logfile '" + filename
						+ "' not found");
			}

			@Override
			public void handle(String line) {
				addLogRow(filename, line);
				System.out.println(line);
			}

			@Override
			public void handle(Exception ex) {
				throw new RuntimeException("Error tailing file", ex);
			}

		};
		Tailer tailer = new Tailer(new File(filename), listener, 1000, true,
				true);
		rows.put(filename, new LinkedList<String>());
		tailers.put(filename, tailer);

		Thread thread = new Thread(tailer);
		thread.setDaemon(true);
		thread.start();
		// this is required, because the file modification date granularity may
		// be too low so that immediate changes are not detected
		Thread.sleep(MINIMUM_DELAY_TO_DETECT_CHANGES);
	}

	private void addLogRow(final String filename, String line) {
		List<String> rowList = rows.get(filename);
		if (rowList != null) {
			synchronized (rowList) {
				rowList.add(line);
			}
		}
	}

	public boolean hasLogFileEntry(String filename, String entry) {
		List<String> rowList = rows.get(filename);
		synchronized (rowList) {
			for (String row : rowList) {
				if (row.contains(entry)) {
					return true;
				}
			}
		}
		return false;
	}

	public void shouldNotHaveLogFileEntry(String filename, String entry,
			int seconds) throws InterruptedException {

		boolean found = waitForLogFileEntry(filename, entry, seconds);
		if (found) {
			throw new IllegalStateException("Entry '" + entry
					+ "' should not exist in log file '" + filename + "'");
		}
	}

	public void shouldHaveLogFileEntry(String filename, String entry,
			int seconds) throws InterruptedException {

		boolean found = waitForLogFileEntry(filename, entry, seconds);
		if (!found) {
			throw new IllegalStateException("Entry '" + entry
					+ "' not found in log file '" + filename + "'");
		}
	}

	private boolean waitForLogFileEntry(String filename, String entry,
			int seconds)
			throws InterruptedException {
		long start = System.currentTimeMillis();
		boolean found = hasLogFileEntry(filename, entry);
		while (!found) {
			if ((System.currentTimeMillis() - start) > (seconds * 1000)) {
				break;
			}
			Thread.sleep(retryInterval);
			found = hasLogFileEntry(filename, entry);
		}
		return found;
	}

	public void stopLogFileMonitoring(String filename) {
		tailers.get(filename).stop();
		tailers.remove(filename);
		rows.remove(filename);
	}

	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval;
	}
}
