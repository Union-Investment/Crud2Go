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
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.config.DateConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.support.vaadin.support.DateToStringConverter;

public class DateFormFieldTest {

	private DateFormField dateFormField;

	@Mock
	private FormFieldConfig config;

	@Mock
	private DateConfig dateConfig;

	@Mock
	private UI uiMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		UI.setCurrent(uiMock);
		when(uiMock.getLocale()).thenReturn(Locale.GERMANY);
	}

	@After
	public void tearDown() {
		UI.setCurrent(null);
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

		assertEquals(DateToStringConverter.class, dateFormField.getConverter()
				.getClass());
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
	public void shouldReturnEndDateForHourResolution() {
		verifyReturnedEndDate("HH dd.MM.yyyy", "10 20.05.2011",
				new GregorianCalendar(2011, 4, 20, 11, 0, 0));
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

		dateConfig.setFormat("dd.MM.yyyy hh:mm");
		assertEquals(Calendar.MINUTE, dateFormField.getResolution());
	}
}
