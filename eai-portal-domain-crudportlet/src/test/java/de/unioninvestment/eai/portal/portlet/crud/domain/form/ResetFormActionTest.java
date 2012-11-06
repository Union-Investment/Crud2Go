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
package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DeferredExecutionActionProxy;

public class ResetFormActionTest {

	private ResetFormAction resetAction = new ResetFormAction();

	private FormFields formFields;

	@Mock
	private Form formMock;

	@Mock
	private Page pageMock;

	@Mock
	private Table tableMock;

	@Mock
	private DataContainer dbContainerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(dbContainerMock);
		when(pageMock.findNextElement(Table.class, formMock)).thenReturn(
				tableMock);
		when(formMock.getPanel()).thenReturn(pageMock);
	}

	@Test
	public void shouldResetInputValuesToEmptyStrings() {
		FormField formField1 = createFormField("field1", "title1", "prompt1",
				null, "filterValue1");
		FormField formField2 = createFormField("field2", "title2", "prompt2",
				null, "filterValue2");
		formFields = new FormFields(formField1, formField2);
		when(formMock.getFields()).thenReturn(formFields);

		resetAction.execute(formMock);

		assertThat(formField1.getValue(), nullValue());
		assertThat(formField2.getValue(), nullValue());

	}

	@Test
	public void shouldResetInputValuesToDefaultValue() {
		FormField formField1 = createFormField("field1", "title1", "prompt1",
				"default1", "filterValue1");
		FormField formField2 = createFormField("field2", "title2", "prompt2",
				"default2", "filterValue2");
		formFields = new FormFields(formField1, formField2);
		when(formMock.getFields()).thenReturn(formFields);

		resetAction.execute(formMock);

		assertThat(formField1.getValue(), is("default1"));
		assertThat(formField2.getValue(), is("default2"));

	}

	@Test
	public void shouldDeactivateValueChangeTriggerActionWhileReseting() {
		FormField formField1 = createFormField("field1", "title1", "prompt1",
				"default1", "filterValue1");
		FormField formField2 = createFormField("field2", "title2", "prompt2",
				"default2", "filterValue2");
		formFields = new FormFields(formField1, formField2);
		DeferredExecutionActionProxy proxyMock = mock(DeferredExecutionActionProxy.class);
		when(formMock.getFields()).thenReturn(formFields);
		when(formMock.getValueChangeTriggerAction()).thenReturn(proxyMock);

		resetAction.execute(formMock);

		verify(proxyMock).setActivated(false);
		verify(proxyMock, times(2)).setActivated(true);
		verify(proxyMock).execute();

		assertThat(formField1.getValue(), is("default1"));
		assertThat(formField2.getValue(), is("default2"));
	}

	private FormField createFormField(String name, String title,
			String inputPrompt, String defaultValue, String value) {
		FormFieldConfig config = new FormFieldConfig();
		config.setName(name);
		config.setTitle(title);
		config.setInputPrompt(inputPrompt);
		config.setDefault(defaultValue);
		FormField field = new FormField(config);
		field.setValue(value);
		return field;
	}

}
