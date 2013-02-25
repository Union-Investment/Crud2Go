/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package de.unioninvestment.eai.portal.junit.rules;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class LogMessage extends ArgumentMatcher<LoggingEvent> implements
		Serializable {

	private static final long serialVersionUID = -1909837398271763801L;
	private final String substring;
	private Level level;

	public LogMessage(Level level, String substring) {
		this.level = level;
		this.substring = substring;
	}

	@Override
	public boolean matches(Object actual) {
		LoggingEvent event = (LoggingEvent) actual;
		return (actual != null) && (event.getLevel() == level)
				&& event.getMessage().toString().contains(substring);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("logs message ").appendValue(substring)
				.appendText(" at level ").appendValue(level);
	}
}
