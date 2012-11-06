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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

public class StringDataTypeTest {
	private StringDataType type;

	@Before
	public void setUp() {
		type = new StringDataType();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSupportStringRight() {
		assertTrue(type.supportsDisplaying(String.class));
		assertTrue(type.supportsEditing(String.class));
	}

	@Test
	public void shouldFormatCorrectly() {
		assertThat(type.formatPropertyValue(new ObjectProperty<Object>(null,
				Object.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Integer>(4711),
				null),
				is("4711"));
		assertThat(type.formatPropertyValue(new ObjectProperty<String>("test"),
				null),
				is("test"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

	@Test
	public void shouldAcceptEmptyLineAsNull() {
		Field field = type.createField(null, null, true, null, null);
		assertThat(field, instanceOf(TextArea.class));
		TextArea textField = (TextArea) field;
		assertThat(textField.getNullRepresentation(), is(""));
		assertTrue(textField.isNullSettingAllowed());
	}
}
