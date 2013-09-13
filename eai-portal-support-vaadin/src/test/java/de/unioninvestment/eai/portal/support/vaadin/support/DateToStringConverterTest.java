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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class DateToStringConverterTest {

	private DateToStringConverter dateToStringConverter;
	private Date date;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		date = new GregorianCalendar(2013, 6, 4, 14, 27, 13).getTime();
		int dateResolution = Calendar.HOUR_OF_DAY;
		String format = "dd.MM.yyyy";
		dateToStringConverter = new DateToStringConverter(dateResolution,
				format);
	}

	@Test
	public void shouldConvertToBackend() {

		assertThat(dateToStringConverter.convertToModel(date, String.class,
				Locale.GERMANY), is("04.07.2013"));
	}

	@Test
	public void shouldNotConvertToBackend() {
		assertThat(dateToStringConverter.convertToModel(null, String.class,
				Locale.GERMANY), is(nullValue()));
	}

	@Test
	public void shouldConvertToFrontend() {
		Date result = dateToStringConverter.convertToPresentation("04.07.2013",
				Date.class, Locale.GERMANY);
		assertThat(result, is(new GregorianCalendar(2013, 6, 4).getTime()));
	}

	@Test
	public void shouldNotConvertToFrontend() {
		assertThat(dateToStringConverter.convertToPresentation(null,
				Date.class, Locale.GERMANY), is(nullValue()));
	}

}
