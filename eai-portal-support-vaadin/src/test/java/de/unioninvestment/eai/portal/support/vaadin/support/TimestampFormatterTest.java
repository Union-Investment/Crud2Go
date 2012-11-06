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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import de.unioninvestment.eai.portal.support.vaadin.support.TimestampFormatter;


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
	public void shouldFormatTimestampsAsGermanDates() {
		TimestampFormatter formatter = new TimestampFormatter();
		assertEquals("01.01.2011 15:23:22", formatter.format(timestamp1));
		assertEquals("true", formatter.format(true)); // needed by vaadin (why?)
		assertNull(formatter.format(null));
	}

	@Test
	public void shouldParseGermanTimestamps() throws Exception {
		TimestampFormatter formatter = new TimestampFormatter();
		assertEquals(timestamp1, formatter.parse("01.01.2011 15:23:22"));
		assertNull(formatter.parse(""));
		assertNull(formatter.parse(null));
	}
}
