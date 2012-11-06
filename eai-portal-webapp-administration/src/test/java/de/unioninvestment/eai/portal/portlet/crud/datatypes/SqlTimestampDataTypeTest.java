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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedTextField;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampFormatter;

public class SqlTimestampDataTypeTest {
	private SqlTimestampDataType type;

	// @Captor
	// private ArgumentCaptor<Compare.Less> filterLessCaptor;

	private Timestamp timestampMidnight;

	private Timestamp timestampNanos;

	private Timestamp timestamp1;

	@Before
	public void setUp() {
		type = new SqlTimestampDataType();
		MockitoAnnotations.initMocks(this);

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
	public void shouldCreateField() {
		String inputPrompt = "promt";
		Field field = type.createField(Timestamp.class, null, false,
				inputPrompt, null);

		assertNotNull(field);
		assertTrue(FormattedTextField.class.isAssignableFrom(field.getClass()));
		assertEquals(inputPrompt, ((FormattedTextField) field).getInputPrompt());
	}

	@Test
	public void shouldSupportsTimestampsForDisplayFilterButNotEditing() {

		assertTrue(type.supportsDisplaying(Timestamp.class));
		assertTrue(type.supportsEditing(Timestamp.class));
	}

	@Test
	public void shouldFormatCorrectly() {

		assertThat(type.formatPropertyValue(new ObjectProperty<Timestamp>(null,
				Timestamp.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Timestamp>(
				timestampMidnight, Timestamp.class), null), is("01.01.2011"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Timestamp>(
				timestamp1, Timestamp.class), null), is("01.01.2011 15:23:22"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Timestamp>(
				timestampNanos, Timestamp.class), null),
				is("01.01.2011 15:23:22.123456789"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

	@Test
	public void shouldAcceptAndReturnTimestampsInField() {
		ObjectProperty<Timestamp> timestampProperty = new ObjectProperty<Timestamp>(
				timestamp1, Timestamp.class);
		Field field = type.createField(null, "TS", false, null, null);
		field.setPropertyDataSource(timestampProperty);

		assertEquals("01.01.2011 15:23:22", field.getValue());

		field.setValue("01.01.2011 15:23:22.123456789");
		assertEquals(timestampNanos, timestampProperty.getValue());
	}

	@Test
	public void shouldReturnADropDown() {
		Field field = type.createSelect(Timestamp.class, null, null);
		Property integerDs = new ObjectProperty<Integer>(5, Integer.class);
		field.setPropertyDataSource(integerDs);
		assertThat(field, instanceOf(FormattedSelect.class));
		assertThat(field.getValue(), is((Object) "5"));

		FormattedSelect select = (FormattedSelect) field;
		Property formatter = select.getPropertyDataSource();
		assertThat(formatter, instanceOf(TimestampFormatter.class));
	}

}
