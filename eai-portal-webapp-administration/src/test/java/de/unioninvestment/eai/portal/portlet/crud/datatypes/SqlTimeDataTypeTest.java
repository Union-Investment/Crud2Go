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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Field;

public class SqlTimeDataTypeTest {
	private SqlTimeDataType type;

	private Time time1;

	@Before
	public void setUp() {
		type = new SqlTimeDataType();
		MockitoAnnotations.initMocks(this);

		long time = new GregorianCalendar(1970, 0, 1, 10, 12, 32).getTime()
				.getTime();
		time1 = new Time(time);

	}

	@Test
	public void shouldSupportsTimestampsForDisplayFilterButNotEditing() {

		assertTrue(type.supportsDisplaying(Time.class));
		assertTrue(type.supportsEditing(Time.class));
	}

	@Test
	public void shouldFormatCorrectly() {

		assertThat(type.formatPropertyValue(new ObjectProperty<Time>(null,
				Time.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Time>(time1,
				Time.class), null), is("10:12:32"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

	@Test
	public void shouldAcceptEmptyLineAsNull() {
		Field field = type.createField(null, null, false, null, null);

		Property dateDs = new ObjectProperty<Time>(time1, Time.class);
		field.setPropertyDataSource(dateDs);

		field.changeVariables(this,
				Collections.singletonMap("text", (Object) ""));
		assertThat(field.getValue(), nullValue());
	}

}
