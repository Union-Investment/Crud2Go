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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;
import de.unioninvestment.eai.portal.support.vaadin.support.NumberFormatter;

public class NumberDataTypeTest {

	private NumberDataType type;

	@Mock
	private DataContainer containerMock;

	@Before
	public void setUp() {
		type = new NumberDataType();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSupportsNumbersForDisplayFilterAndEditing() {

		shouldSupportDataType(type, Byte.class);
		shouldSupportDataType(type, Short.class);
		shouldSupportDataType(type, Integer.class);
		shouldSupportDataType(type, Long.class);
		shouldSupportDataType(type, Double.class);
		shouldSupportDataType(type, Float.class);
		shouldSupportDataType(type, Byte.class);
		shouldSupportDataType(type, BigDecimal.class);
	}

	private void shouldSupportDataType(NumberDataType type,
			Class<? extends Number> clazz) {
		assertTrue(type.supportsDisplaying(clazz));
		assertTrue(type.supportsEditing(clazz));
	}

	@Test
	public void shouldFormatCorrectly() {

		assertThat(type.formatPropertyValue(new ObjectProperty<Integer>(null,
				Integer.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Integer>(4711),
				null),
				is("4,711"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Integer>(-4711),
				null),
				is("-4,711"));
		assertThat(
				type.formatPropertyValue(new ObjectProperty<Long>(4711L), null),
				is("4,711"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Short>(
				(short) 4711), null), is("4,711"));
		assertThat(type.formatPropertyValue(
				new ObjectProperty<Byte>((byte) 12), null),
				is("12"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Float>(
				(float) 123.12), null), is("123.12"));
		assertThat(type.formatPropertyValue(new ObjectProperty<Double>(123.12),
				null),
				is("123.12"));
		assertThat(type.formatPropertyValue(new ObjectProperty<BigDecimal>(
				new BigDecimal("1421.121")), null), is("1,421.121"));
		assertEquals("", type.formatPropertyValue(null, null));
	}

	@Test
	public void shouldFormatCorrectlyWithCustomPattern() {
		NumberFormat nf = new org.springframework.format.number.NumberFormatter(
				"#,##0.00").getNumberFormat(Locale.GERMAN);

		assertThat(type.formatPropertyValue(new ObjectProperty<BigDecimal>(
				new BigDecimal("421121")), nf), is("421.121,00"));

		assertThat(type.formatPropertyValue(new ObjectProperty<BigDecimal>(
				new BigDecimal("1114121.23")), nf), is("1.114.121,23"));

		nf = new org.springframework.format.number.NumberFormatter("#,##0.00")
				.getNumberFormat(Locale.ENGLISH);

		assertThat(type.formatPropertyValue(new ObjectProperty<BigDecimal>(
				new BigDecimal("1114121.23")), nf), is("1,114,121.23"));

		nf = new org.springframework.format.number.NumberFormatter("#,##0%")
				.getNumberFormat(Locale.GERMAN);
		assertThat(type.formatPropertyValue(new ObjectProperty<BigDecimal>(
				new BigDecimal("-1234")), nf), is("-123.400%"));

	}

	@Test
	public void shouldAcceptEmptyLineAsNull() {
		when(containerMock.getType("test")).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return Integer.class;
			}
		});

		Field field = type
				.createField(Integer.class, "test", false, null, null);
		assertThat(field, instanceOf(TextField.class));

		Property integerDs = new ObjectProperty<Integer>(5, Integer.class);
		field.setPropertyDataSource(integerDs);

		field.changeVariables(this,
				Collections.singletonMap("text", (Object) ""));
		assertThat(field.getValue(), nullValue());

		field.setValue("2");
		assertThat(integerDs.getValue(), is((Object) 2));
	}

	@Test
	public void shouldFormatWithNumberFormater() {
		NumberFormatter nf = new NumberFormatter(Integer.class, null);
		assertNull(nf.format(null));
		assertEquals("3", nf.format(3));
	}

	@Test
	public void shouldReturnADropDown() {
		Field field = type.createSelect(Integer.class, null, null);
		Property integerDs = new ObjectProperty<Integer>(5, Integer.class);
		field.setPropertyDataSource(integerDs);
		assertThat(field, instanceOf(FormattedSelect.class));
		assertThat(field.getValue(), is((Object) "5"));

		FormattedSelect select = (FormattedSelect) field;
		Property formatter = select.getPropertyDataSource();
		assertThat(formatter, instanceOf(NumberFormatter.class));
	}

}
