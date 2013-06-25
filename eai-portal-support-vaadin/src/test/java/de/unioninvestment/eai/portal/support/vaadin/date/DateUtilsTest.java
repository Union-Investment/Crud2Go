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
package de.unioninvestment.eai.portal.support.vaadin.date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class DateUtilsTest {
	@Test
	public void shouldReturnEndDateForSecondResolution() throws ParseException {
		verifyReturnedEndDate("HH:mm:ss dd.MM.yyyy", "11:12:13 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 11, 12, 14));
	}

	@Test
	public void shouldReturnEndDateForMinuteResolution() throws ParseException {
		verifyReturnedEndDate("mm:ss dd.MM.yyyy", "12:13 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 0, 12, 14));
	}

	@Test
	public void shouldReturnEndDateForHourResolution() throws ParseException {
		verifyReturnedEndDate("HH dd.MM.yyyy", "10 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 11, 0, 0));
	}

	@Test
	public void shouldReturnEndDateForDayResolution() throws ParseException {
		verifyReturnedEndDate("dd.MM.yyyy", "20.05.2011",
				new GregorianCalendar(2011, 4, 21));
	}

	@Test
	public void shouldReturnEndDateForMonthResolution() throws ParseException {
		verifyReturnedEndDate("MM.yyyy", "05.2011", new GregorianCalendar(2011,
				5, 1));
	}

	@Test
	public void shouldReturnEndDateForYearResolution() throws ParseException {
		verifyReturnedEndDate("yyyy", "2011", new GregorianCalendar(2012, 0, 1));
	}

	private void verifyReturnedEndDate(String format, String value, Calendar cal)
			throws ParseException {
		int resolution = DateUtils.getResolution(format);
		Date beginDate = new SimpleDateFormat(format).parse(value);

		assertThat(DateUtils.getEndDate(beginDate, resolution),
				is(cal.getTime()));
	}

	@Test
	public void shouldGetResolution() {
		assertThat(DateUtils.getResolution("dd.MM.yyyy hh:mm:ss"),
				is(Calendar.SECOND));
		assertThat(DateUtils.getResolution("dd.MM.yyyy hh:mm"),
				is(Calendar.MINUTE));
		assertThat(DateUtils.getResolution("dd.MM.yyyy HH"),
				is(Calendar.HOUR_OF_DAY));
		assertThat(DateUtils.getResolution("dd.MM.yyyy"),
				is(Calendar.DAY_OF_MONTH));
		assertThat(DateUtils.getResolution("MM.yyyy"), is(Calendar.MONTH));
		assertThat(DateUtils.getResolution("yyyy"), is(Calendar.YEAR));
	}

}
