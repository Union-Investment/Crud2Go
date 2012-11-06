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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;

public class FormTest {

	private FormActions actions;
	private FormFields fields;
	private FormConfig config;
	private Form form;

	@Mock
	private Panel panelMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		config = new FormConfig();
		fields = new FormFields();
		actions = new FormActions(new ArrayList<FormAction>());
		form = new Form(config, fields, actions);
	}

	@Test
	public void shouldProvidePageReference() {
		form.setPanel(panelMock);
		assertThat(form.getPanel(), is(panelMock));
	}

	@Test
	public void shouldAssignFields() {
		int columns = 23;
		config.setColumns(columns);
		Form f = new Form(config, fields, actions);

		assertThat(f.getActions(), is(actions));
		assertThat(f.getFields(), is(fields));
		assertThat(f.getColumns(), is(columns));
	}

	@Test
	public void shouldSetValueChangeTriggerAction() {

		assertNull(form.getValueChangeTriggerAction());

		FormActionConfig fac = new FormActionConfig();
		fac.setId("dummy");
		FormAction dummyAction = new FormAction(mock(Portlet.class), fac, null,
				null);
		config.setTriggerOnChanges("dummy");
		actions = new FormActions(
				Arrays.asList(new FormAction[] { dummyAction }));
		form = new Form(config, fields, actions);

		assertNotNull(form.getValueChangeTriggerAction());
	}

	@Test(expected = TechnicalCrudPortletException.class)
	public void shouldThrowExceptionOnNonExistingValueChangeTriggerAction() {
		FormActionConfig fac = new FormActionConfig();
		fac.setId("dummy");
		FormAction dummyAction = new FormAction(mock(Portlet.class), fac, null,
				null);
		config.setTriggerOnChanges("nonExistent");
		actions = new FormActions(
				Arrays.asList(new FormAction[] { dummyAction }));
		form = new Form(config, fields, actions);
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionOnResetActionAsTriggerAction() {

		assertNull(form.getValueChangeTriggerAction());

		FormActionConfig fac = new FormActionConfig();
		fac.setId("dummy");
		FormAction dummyAction = new FormAction(mock(Portlet.class), fac,
				new ResetFormAction(), null);
		config.setTriggerOnChanges("dummy");
		actions = new FormActions(
				Arrays.asList(new FormAction[] { dummyAction }));
		form = new Form(config, fields, actions);
	}
}
