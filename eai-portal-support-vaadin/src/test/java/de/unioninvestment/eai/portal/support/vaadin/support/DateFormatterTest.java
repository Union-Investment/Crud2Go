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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class DateFormatterTest {

	private Date date1;

	@Before
	public void setUp() {
		long time = new GregorianCalendar(2011, 0, 1).getTime().getTime();
		date1 = new Date(time);
	}

	@Test
	public void shouldFormatCorrectly() {
		DateFormatter type = new DateFormatter(null);

		assertThat(
				type.convertToPresentation(null, String.class, Locale.GERMANY),
				is(nullValue()));
		assertThat(
				type.convertToPresentation(date1, String.class, Locale.GERMANY),
				is("01.01.2011 00:00:00"));
	}
}
