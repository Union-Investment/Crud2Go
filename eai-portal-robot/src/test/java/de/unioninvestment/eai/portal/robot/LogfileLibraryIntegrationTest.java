package de.unioninvestment.eai.portal.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

/**
 * These are long-running tests, therefore they are excluded from normal test
 * runs.
 * 
 * @author carsten.mjartan
 */
public class LogfileLibraryIntegrationTest {

	private static final String TEST_LOGFILE = "target/test.log";
	private LogfileLibrary library = new LogfileLibrary();

	@Test
	public void shouldSucceedFindingFirstRowOfLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			library.monitorLogFile(TEST_LOGFILE);

			out.println("new line");
			out.flush();

			library.shouldHaveLogFileEntry(TEST_LOGFILE, "new line", 5);

		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailFindingFirstRowOfLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			library.monitorLogFile(TEST_LOGFILE);

			out.println("new line");
			out.flush();

			library.shouldNotHaveLogFileEntry(TEST_LOGFILE, "new line", 5);

		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailNotFindingAnyRowInLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			library.monitorLogFile(TEST_LOGFILE);
			out.println("new line");
			out.flush();
			library.shouldHaveLogFileEntry(TEST_LOGFILE, "other line", 5);
		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotFindOldRowsInLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			out.println("first line");
			out.flush();

			library.monitorLogFile(TEST_LOGFILE);

			out.println("second line");
			out.flush();

			library.shouldHaveLogFileEntry(TEST_LOGFILE, "first line", 5);
		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	@Test
	public void shouldSucceedFindingNewRowInLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			out.println("first line");
			out.flush();

			library.monitorLogFile(TEST_LOGFILE);

			out.println("second line");
			out.flush();

			library.shouldHaveLogFileEntry(TEST_LOGFILE, "second line", 5);
		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	@Test
	public void shouldSucceedFindingFurtherRowsInLogfile() throws IOException,
			InterruptedException {
		PrintWriter out = new PrintWriter(new FileWriter(TEST_LOGFILE));
		try {
			println(out, "first line");

			library.monitorLogFile(TEST_LOGFILE);

			println(out, "second line");
			println(out, "third line");

			library.shouldHaveLogFileEntry(TEST_LOGFILE, "third line", 5);
		} finally {
			library.stopLogFileMonitoring(TEST_LOGFILE);
			out.close();
			deleteLogFile();
		}
	}

	private void println(PrintWriter out, String string) {
		out.println(string);
		out.flush();
	}

	private void deleteLogFile() throws InterruptedException {
		File file = new File(TEST_LOGFILE);
		for (int i = 0; i < 100; i++) {
			if (file.delete()) {
				return;
			}
			Thread.sleep(100);
		}
		throw new RuntimeException("Cannot delete test file");
	}
}
