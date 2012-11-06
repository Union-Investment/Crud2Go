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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.DateConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampConverter;

public class DateFormFieldTest {

	private DateFormField dateFormField;

	@Mock
	private FormFieldConfig config;

	@Mock
	private DateConfig dateConfig;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void shouldGetDateFormat() {
		String format = "dd.MM.yyyy";
		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		DateConfig dateConfig = new DateConfig();
		dateConfig.setFormat(format);
		when(config.getDate()).thenReturn(dateConfig);

		assertEquals(format, dateFormField.getDateFormat());
	}

	@Test
	public void shouldDefaultValue() {
		String format = "dd.MM.yyyy";
		String defaultValue = "01.01.2010";
		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		DateConfig dateConfig = new DateConfig();
		dateConfig.setDefault(defaultValue);
		when(config.getDate()).thenReturn(dateConfig);

		assertEquals(defaultValue, dateFormField.getDefaultValue());
	}

	@Test
	public void shouldReturnTimestampProperty() {
		String format = "dd.MM.yyyy";
		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		assertEquals(TimestampConverter.class, dateFormField
				.getTimestampProperty().getClass());
	}

	@Test
	public void shouldReturnBeginDate() {
		String format = "dd.MM.yyyy";
		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		dateFormField.setValue("20.05.2011");
		assertThat(dateFormField.getBeginDate(), is(new GregorianCalendar(2011,
				4, 20).getTime()));
	}

	@Test
	public void shouldReturnEndDateForSecondResolution() {
		verifyReturnedEndDate("HH:mm:ss dd.MM.yyyy", "11:12:13 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 11, 12, 14));
	}

	@Test
	public void shouldReturnEndDateForMinuteResolution() {
		verifyReturnedEndDate("mm:ss dd.MM.yyyy", "12:13 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 0, 12, 14));
	}

	@Test
	public void shouldReturnEndDateForHourResolution() {
		verifyReturnedEndDate("HH dd.MM.yyyy", "10 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 11, 0, 0));
	}

	@Test
	public void shouldReturnEndDateForDayResolution() {
		verifyReturnedEndDate("dd.MM.yyyy", "20.05.2011",
				new GregorianCalendar(2011, 4, 21));
	}

	@Test
	public void shouldReturnEndDateForMonthResolution() {
		verifyReturnedEndDate("MM.yyyy", "05.2011", new GregorianCalendar(2011,
				5, 1));
	}

	@Test
	public void shouldReturnEndDateForYearResolution() {
		verifyReturnedEndDate("yyyy", "2011", new GregorianCalendar(2012, 0, 1));
	}

	private void verifyReturnedEndDate(String format, String value, Calendar cal) {
		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		dateFormField.setValue(value);
		assertThat(dateFormField.getEndDate(), is(cal.getTime()));
	}

	@Test
	public void shouldGetResolution() {
		String format = "";

		when(config.getDate()).thenReturn(dateConfig);
		when(dateConfig.getFormat()).thenReturn(format);
		dateFormField = new DateFormField(config);

		DateConfig dateConfig = new DateConfig();
		dateConfig.setFormat(format);
		when(config.getDate()).thenReturn(dateConfig);

		dateConfig.setFormat("dd.MM.yyyy hh:mm:ss");
		assertEquals(Calendar.SECOND, dateFormField.getResolution());

		dateConfig.setFormat("dd.MM.yyyy hh:mm");
		assertEquals(Calendar.MINUTE, dateFormField.getResolution());

		dateConfig.setFormat("dd.MM.yyyy HH");
		assertEquals(Calendar.HOUR_OF_DAY, dateFormField.getResolution());

		dateConfig.setFormat("dd.MM.yyyy");
		assertEquals(Calendar.DAY_OF_MONTH, dateFormField.getResolution());

		dateConfig.setFormat("MM.yyyy");
		assertEquals(Calendar.MONTH, dateFormField.getResolution());

		dateConfig.setFormat("yyyy");
		assertEquals(Calendar.YEAR, dateFormField.getResolution());
	}
}
