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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.support.vaadin.support.ChainedConverter;

public abstract class AbstractDataTypeTest<T extends AbstractDataType> {

	protected T type;

	protected abstract T createDataType();

	@Mock
	private Converter<String, Integer> formatterMock;

	private AbstractDataType simpleType = new AbstractDataType() {

		@Override
		public boolean supportsDisplaying(Class<?> clazz) {
			return true;
		}

		@Override
		boolean isReadonly() {
			return true;
		}

		@Override
		public Converter<String, ?> createFormatter(Class<?> type, Format format) {
			return formatterMock;
		}
	};

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		type = createDataType();
	}

	@Test
	public void shouldProvideADefaultTextField() {
		Field<?> field = simpleType.createField(Integer.class, "property",
				false, "prompt", null);

		assertThat(field, instanceOf(TextField.class));
		TextField textField = (TextField) field;

		assertThat(textField.getInputPrompt(), is("prompt"));
		assertThat(textField.getNullRepresentation(), is(""));
		assertThat(textField.isReadOnly(), is(true));
		assertThat(textField.isNullSettingAllowed(), is(true));
		assertEquals(formatterMock, textField.getConverter());
	}

	@Test
	public void shouldProvideADefaultSelect() {
		Field<?> field = simpleType.createSelect(Integer.class, null, null);

		assertThat(field, instanceOf(ComboBox.class));
		ComboBox select = (ComboBox) field;
		assertThat(select.isReadOnly(), is(true));
		assertEquals(formatterMock, select.getConverter());
	}

	@Test
	public void shouldProvideADefaultCheckBox() {
		Field<?> field = simpleType.createCheckBox(Integer.class, "1", "0",
				new DecimalFormat("#"));

		assertThat(field, instanceOf(CheckBox.class));
		CheckBox checkbox = (CheckBox) field;
		assertThat(checkbox.isReadOnly(), is(true));
		assertThat((Converter) checkbox.getConverter(),
				instanceOf(ChainedConverter.class));

		Converter<Boolean, Object> converter = checkbox.getConverter();

		when(formatterMock.convertToModel("1", Integer.class, Locale.GERMANY))
				.thenReturn(1);
		when(formatterMock.convertToModel("0", Integer.class, Locale.GERMANY))
				.thenReturn(0);
		assertThat(
				converter.convertToModel(true, Integer.class, Locale.GERMANY),
				is((Object) 1));
		assertThat(
				converter.convertToModel(false, Integer.class, Locale.GERMANY),
				is((Object) 0));
	}

	@Test
	public void shouldProvideADefaultDatePicker() {
		PopupDateField field = type.createDatePicker(Date.class, "property",
				"prompt", "dd.MM.yyyy HH:mm:ss");
		assertThat(field, instanceOf(PopupDateField.class));
		DateField dateField = (DateField) field;
		assertThat(dateField.getDateFormat(), is("dd.MM.yyyy HH:mm:ss"));
		assertThat(dateField.getResolution(), is(Resolution.SECOND));
	}

}
