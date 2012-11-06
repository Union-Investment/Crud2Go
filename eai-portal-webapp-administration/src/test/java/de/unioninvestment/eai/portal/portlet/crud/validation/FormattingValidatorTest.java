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
package de.unioninvestment.eai.portal.portlet.crud.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Field;

public class FormattingValidatorTest {

	private Field field;
	private PropertyFormatter formatter;
	private FormattingValidator validator;

	@Before
	public void setUp() {
		field = mock(Field.class);
		formatter = mock(PropertyFormatter.class);
		validator = new FormattingValidator(field);
	}

	@Test(expected = InvalidValueException.class)
	public void shouldReturnWrappedParseException() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(formatter);
		when(formatter.parse("z")).thenThrow(new NumberFormatException("z"));

		validator.validate("z");
	}

	@Test
	public void shouldSucceedForNonFormattedProperties() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(
				new ObjectProperty<Integer>(1));
		validator.validate("z");
	}

	@Test
	public void shouldSucceedForCorrectFormat() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(formatter);

		validator.validate("z");
	}

	@Test
	public void shouldNotCallFormatterForNonStrings() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(formatter);

		validator.validate(1);

		verifyZeroInteractions(formatter);
	}

	@Test
	public void shouldReturnFalseIfInvalid() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(formatter);
		when(formatter.parse("z")).thenThrow(new NumberFormatException("z"));

		assertThat(validator.isValid("z"), is(false));
	}

	@Test
	public void shouldReturnTrueIfValid() throws Exception {
		when(field.getPropertyDataSource()).thenReturn(formatter);

		assertThat(validator.isValid("z"), is(true));
	}
}
