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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class TimestampFormatterTest {

	private Timestamp timestampNanos;

	private Timestamp timestamp1;

	@Before
	public void setUp() {

		long time = new GregorianCalendar(2011, 0, 1, 15, 23, 22).getTime()
				.getTime();
		timestampNanos = new Timestamp(time);
		timestampNanos.setNanos(123456789);

		timestamp1 = new Timestamp(time);
	}

	@Test
	public void shouldFormatTimestampWithNanos() {
		TimestampFormatter formatter = new TimestampFormatter(null);

		assertEquals("01.01.2011 15:23:22.123456789",
				formatter.convertToPresentation(timestampNanos, String.class,
						Locale.GERMANY));
	}

	@Test
	public void shouldFormatAndReparseNow() {
		Date now = new Date();
		Timestamp nowTs = new Timestamp(now.getTime());
		TimestampFormatter formatter = new TimestampFormatter(null);
		String presentation = formatter.convertToPresentation(nowTs, String.class,
				Locale.GERMANY);
		Timestamp model = formatter.convertToModel(presentation, Timestamp.class, Locale.GERMANY);
		
		assertEquals(nowTs, model);
	}

	@Test
	public void shouldParseTimestampWithNanos() {
		TimestampFormatter formatter = new TimestampFormatter(null);

		assertEquals(timestampNanos, formatter.convertToModel(
				"01.01.2011 15:23:22.123456789", Timestamp.class,
				Locale.GERMANY));
	}

	@Test
	public void shouldFormatTimestampsAsGermanDates() {
		TimestampFormatter formatter = new TimestampFormatter(null);
		assertEquals("01.01.2011 15:23:22", formatter.convertToPresentation(
				timestamp1, String.class, Locale.GERMANY));
		// TODO needed by vaadin 6 (why?), what about Vaadin 7?
		// assertEquals("true", formatter.format(true));
		assertNull(formatter.convertToPresentation(null, String.class,
				Locale.GERMANY));
	}

	@Test
	public void shouldParseGermanTimestamps() throws Exception {
		TimestampFormatter formatter = new TimestampFormatter(null);
		assertEquals(timestamp1, formatter.convertToModel(
				"01.01.2011 15:23:22", Timestamp.class, Locale.GERMANY));
		assertNull(formatter
				.convertToModel("", Timestamp.class, Locale.GERMANY));
		assertNull(formatter.convertToModel(null, Timestamp.class,
				Locale.GERMANY));
	}

	@Test
	public void shouldParseUsingDateFormat() throws Exception {
		TimestampFormatter formatter = new TimestampFormatter(
				new SimpleDateFormat("dd.MM.yyyy HH:mm"));
		assertEquals(
				new Timestamp(
						new GregorianCalendar(2011, 0, 1, 15, 23)
								.getTimeInMillis()), formatter.convertToModel(
						"01.01.2011 15:23", Timestamp.class, Locale.GERMANY));
	}

	@Test
	public void shouldFormatUsingDateFormat() throws Exception {
		TimestampFormatter formatter = new TimestampFormatter(
				new SimpleDateFormat("dd.MM.yyyy HH:mm"));
		assertThat(formatter.convertToPresentation(timestamp1, String.class,
				Locale.GERMANY), is("01.01.2011 15:23"));
	}
}
