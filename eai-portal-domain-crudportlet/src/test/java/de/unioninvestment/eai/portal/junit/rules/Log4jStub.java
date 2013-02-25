package de.unioninvestment.eai.portal.junit.rules;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit Rule for Logging Checks.
 * 
 */
public class Log4jStub extends TestWatchman {

	private Level level = Level.INFO;
	private String category;

	private final Appender appenderMock = mock(Appender.class);

	public Log4jStub(String category) {
		this.category = category;
	}

	public Log4jStub(Class<?> classUnderTest) {
		this.category = classUnderTest.getName();
	}

	public Log4jStub(String category, Level level) {
		this.category = category;
		this.level = level;
	}

	public Log4jStub(Class<?> classUnderTest, Level level) {
		this.category = classUnderTest.getName();
		this.level = level;
	}

	@Override
	public void starting(FrameworkMethod method) {
		record(level);
		addAppenderToCategory(category);
	}

	@Override
	public void finished(FrameworkMethod method) {
		removeAppenderFromCategory(category);
		reset(appenderMock);
	}

	private void addAppenderToCategory(String category) {
		Logger logger = Logger.getLogger(category);
		logger.addAppender(appenderMock);
	}

	private void removeAppenderFromCategory(String category) {
		Logger logger = Logger.getLogger(category);
		logger.removeAppender(appenderMock);
	}

	public void record(Level level) {
		Logger.getLogger(category).setLevel(level);
	}

	public void assertInfo(String loggingStatement) {
		verify(appenderMock).doAppend(
				argThat(new LogMessage(Level.INFO, loggingStatement)));
	}

	public void assertWarning(String loggingStatement) {
		verify(appenderMock).doAppend(
				argThat(new LogMessage(Level.WARN, loggingStatement)));
	}

	public void assertError(String loggingStatement) {
		verify(appenderMock).doAppend(
				argThat(new LogMessage(Level.ERROR, loggingStatement)));
	}

}