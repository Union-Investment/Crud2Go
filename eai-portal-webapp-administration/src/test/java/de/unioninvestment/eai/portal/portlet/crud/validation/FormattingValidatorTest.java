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

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.UI;

public class FormattingValidatorTest {

	@Mock
	private AbstractField<?> field;
	@Mock
	private Converter<String, Object> converter;

	@Mock
	private UI uiMock;

	private FormattingValidator validator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		validator = new FormattingValidator(field);
	}

	private void addConverterMock(Class<Object> modelClass) {
		when(field.getConverter()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return converter;
			}
		});
		when(converter.getModelType()).thenReturn(modelClass);
	}

	@Test(expected = InvalidValueException.class)
	public void shouldReturnWrappedParseException() throws Exception {
		addConverterMock(Object.class);
		when(converter.convertToModel("z", Object.class, null)).thenThrow(
				new NumberFormatException("z"));

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
		addConverterMock(Object.class);

		validator.validate("z");
	}

	@Test
	public void shouldNotCallFormatterForNonStrings() throws Exception {
		addConverterMock(Object.class);

		validator.validate(1);

		verifyZeroInteractions(converter);
	}

}
