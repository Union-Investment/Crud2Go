/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.unioninvestment.eai.portal.support.vaadin.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

public class TimestampUtilsTest {

	private Timestamp timestampMidnight;
	private Timestamp timestampNanos;

	private Timestamp timestamp1;

	@Before
	public void setUp() {
		long timeMidnight = new GregorianCalendar(2011, 0, 1, 0, 0, 0)
				.getTime().getTime();
		timestampMidnight = new Timestamp(timeMidnight);

		long time = new GregorianCalendar(2011, 0, 1, 15, 23, 22).getTime()
				.getTime();
		timestampNanos = new Timestamp(time);
		timestampNanos.setNanos(123456789);

		timestamp1 = new Timestamp(time);
	}

	@Test
	public void shouldFormatTimestamp() {
		Timestamp timestamp = (Timestamp) this.timestampMidnight.clone();
		timestamp.setNanos(15);
		assertEquals("01.01.2011 00:00:00.000000015",
				TimestampUtils.formatTimestamp(timestamp, null));

		timestamp = (Timestamp) this.timestampMidnight.clone();
		timestamp.setNanos(0);
		assertEquals("01.01.2011",
				TimestampUtils.formatTimestamp(timestamp, null));

		Calendar cal = new GregorianCalendar();
		long timeMidnight = new GregorianCalendar(2011, 0, 1, 1, 1, 1)
				.getTime().getTime();
		timestamp = new Timestamp(timeMidnight);
		assertEquals("01.01.2011 01:01:01",
				TimestampUtils.formatTimestamp(timestamp, null));

		String format = "dd.MM.yyyy HH:mm:ss";
		SimpleDateFormat df = new SimpleDateFormat(format);
		cal = new GregorianCalendar();
		timeMidnight = new GregorianCalendar().getTime().getTime();
		timestamp = new Timestamp(timeMidnight);
		assertEquals(df.format(new Date(cal.getTimeInMillis())),
				TimestampUtils.formatTimestamp(timestamp, format));

	}

	@Test
	public void shouldParseCorrectly() {
		assertEquals(null, TimestampUtils.parseTimestamp(null));

		assertEquals(timestampNanos,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22.123456789"));

		assertEquals(timestamp1,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22.000000000"));
		assertEquals(timestamp1,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22.000"));
		assertEquals(timestamp1,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22.00"));
		assertEquals(timestamp1,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22.0"));
		assertEquals(timestamp1,
				TimestampUtils.parseTimestamp("01.01.2011 15:23:22"));

		assertEquals(timestampMidnight,
				TimestampUtils.parseTimestamp("01.01.2011 00:00:00.000000000"));

		assertEquals(timestampMidnight,
				TimestampUtils.parseTimestamp("01.01.2011 00:00:00"));

		assertEquals(timestampMidnight,
				TimestampUtils.parseTimestamp("01.01.2011"));

		long timeMidnight = new GregorianCalendar(2011, 0, 1, 12, 12).getTime()
				.getTime();
		Timestamp timestamp = new Timestamp(timeMidnight);
		assertEquals(timestamp,
				TimestampUtils.parseTimestamp("01.01.2011 12:12"));

		timeMidnight = new GregorianCalendar(2011, 0, 1, 12, 0).getTime()
				.getTime();
		timestamp = new Timestamp(timeMidnight);
		assertEquals(timestamp, TimestampUtils.parseTimestamp("01.01.2011 12"));
	}

	@Test
	public void shouldFailParsing() {
		shouldFailParsing("1.1.99");
		shouldFailParsing("01.01.2011 15:23:22.0000s0000");
	}

	@Test
	public void shouldReturnIfTimeIsNotMidnight() {
		assertFalse(TimestampUtils.hasTimePart(timestampMidnight));
		assertTrue(TimestampUtils.hasTimePart(timestamp1));
		assertTrue(TimestampUtils.hasTimePart(timestampNanos));
	}

	private void shouldFailParsing(String value) {
		try {
			TimestampUtils.parseTimestamp(value);
			fail();
		} catch (com.vaadin.data.util.converter.Converter.ConversionException e) {
			assertThat(
					e.getMessage(),
					is("Bitte geben Sie einen Zeitpunkt im Format 'DD.MM.YYYY[ HH:MM:SS[.NNNNNNNNN]]' ein."));
		}
	}
}
