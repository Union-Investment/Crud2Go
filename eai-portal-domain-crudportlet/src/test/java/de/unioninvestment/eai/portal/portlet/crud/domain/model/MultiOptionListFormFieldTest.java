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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormSelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

public class MultiOptionListFormFieldTest {

	@Mock
	private FormSelectConfig formSelectConfig;

	private MultiOptionListFormField field;

	@Mock
	FormFieldConfig configMock;

	@Mock
	OptionList selectionMock;

	@Mock
	EventRouter<FormFieldChangeEventHandler, FormFieldChangeEvent> eventRouter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(formSelectConfig.getVisibleRows()).thenReturn(3);
		when(configMock.getSelect()).thenReturn(formSelectConfig);

		field = new MultiOptionListFormField(configMock, selectionMock);
	}

	@Test
	public void shoudReturnSelection() {
		assertThat(field.getOptionList(), is(selectionMock));
	}

	@Test
	public void shuoldGetValue() {
		field.getListProperty().getValue().add("Eintrag 1");

		assertThat(field.getValue(), is("Eintrag 1"));
	}

	@Test
	public void shuoldGetNullValue() {
		assertEquals(field.getValue(), null);
	}

	@Test
	public void shuoldGetNullValue2() {
		field.getListProperty().getValue().add("Eintrag 1");
		field.getListProperty().getValue().add("Eintrag 2");

		assertEquals(field.getValue(), null);
	}

	@Test
	public void shouldSetValue() {
		field.setValue("Eintrag x");

		assertThat(field.getValue(), is("Eintrag x"));
	}

	@Test
	public void shouldSetValue2() {
		field.setValue("Eintrag x");
		field.setValue("Eintrag y");

		assertThat(field.getValue(), is("Eintrag y"));
	}

	@Test
	public void shouldSetValues() {
		Set<String> values = new HashSet<String>();
		values.add("Eintrag 1");
		values.add("Eintrag 2");
		values.add("Eintrag 3");

		field.setValues(values);

		assertThat(field.getValues(), equalTo(values));
	}

	@Test
	public void shouldSetNullValues() {
		Set<String> values = new HashSet<String>();
		field.setValues(values);

		assertThat(field.getValues(), equalTo(values));

		assertThat(field.getValue(), equalTo(null));
	}

	@Test
	public void shouldReturnNullOnMultipleValues() {
		Set<String> values = new HashSet<String>();
		values.add("Eintrag 1");
		values.add("Eintrag 2");
		values.add("Eintrag 3");

		field.setValues(values);

		assertThat(field.getValue(), equalTo(null));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldFailOnGetProperty() {
		field.getProperty();
	}

	@Test
	public void shouldFireValueChangeEventOnSetValue() {
		field = new MultiOptionListFormField(configMock, selectionMock,
				eventRouter);

		field.setValue("ssss");

		verify(eventRouter, times(1))
				.fireEvent(any(FormFieldChangeEvent.class));
	}

	@Test
	public void shouldFireValueChangeEventOnSetValues() {
		field = new MultiOptionListFormField(configMock, selectionMock,
				eventRouter);

		HashSet<String> values = new HashSet<String>();
		values.add("dddd");
		field.setValues(values);

		verify(eventRouter, times(1))
				.fireEvent(any(FormFieldChangeEvent.class));
	}
}
