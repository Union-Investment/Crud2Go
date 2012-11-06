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
package de.unioninvestment.eai.portal.portlet.crud.datatypes;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.support.DateFormatter;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;

public class SqlDateDataTypeTest {
	private SqlDateDataType type;

	private Date date1;

	@Before
	public void setUp() {
		type = new SqlDateDataType();
		MockitoAnnotations.initMocks(this);

		long time = new GregorianCalendar(2011, 0, 1).getTime().getTime();
		date1 = new Date(time);

	}

	@Test
	public void shouldSupportsTimestampsForDisplayFilterButNotEditing() {

		assertTrue(type.supportsDisplaying(Date.class));
		assertTrue(type.supportsEditing(Date.class));
	}

	@Test
	public void shouldFormatCorrectly() {

		assertThat(type.formatPropertyValue(new ObjectProperty<Date>(null,
				Date.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Date>(date1,
				Date.class), null), is("01.01.2011 00:00:00"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

	@Test
	public void shouldReturnADatePicker() {
		Field field = type.createField(null, null, false, null, null);
		assertThat(field, instanceOf(DateField.class));
		DateField dateField = (DateField) field;
		assertThat(dateField.getDateFormat(), is("dd.MM.yyyy HH:mm:ss"));
		assertThat(dateField.getResolution(), is(DateField.RESOLUTION_DAY));
	}

	@Test
	public void shouldReturnADropDown() {
		Field field = type.createSelect(DateField.class, null, null);
		Property integerDs = new ObjectProperty<Integer>(5, Integer.class);
		field.setPropertyDataSource(integerDs);
		assertThat(field, instanceOf(AbstractSelect.class));
		assertThat(field.getValue(), is((Object) "5"));

		FormattedSelect select = (FormattedSelect) field;
		Property formatter = select.getPropertyDataSource();
		assertThat(formatter, instanceOf(DateFormatter.class));
	}

	@Test
	public void shouldAcceptEmptyLineAsNull() {
		Field field = type.createField(null, null, false, null, null);

		Property dateDs = new ObjectProperty<Date>(new Date(
				System.currentTimeMillis()), Date.class);
		field.setPropertyDataSource(dateDs);

		field.changeVariables(this,
				Collections.singletonMap("dateString", (Object) ""));
		assertThat(field.getValue(), nullValue());
	}

}
