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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.FormPresenter;

public class DefaultFormViewTest {

	private DefaultFormView view;

	@Mock
	private FormPresenter presenterMock;

	private Form form;

	private FormActions actions;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		FormConfig config = new FormConfig();
		config.setTriggerOnChanges("dummy");

		FormFieldConfig field1Config = new FormFieldConfig();
		field1Config.setName("field1");
		config.getField().add(field1Config);

		FormActionConfig actionConfig = new FormActionConfig();
		actionConfig.setId("dummy");
		config.getAction().add(actionConfig);

		FormField field1 = new FormField(field1Config);
		FormFields fields = new FormFields(field1);

		FormAction dummyAction = new FormAction(mock(Portlet.class),
				actionConfig, null, null);
		actions = new FormActions(
				Arrays.asList(new FormAction[] { dummyAction }));
		form = new Form(config, fields, actions);

		this.view = new DefaultFormView();
		view.initialize(presenterMock, form);
	}

	@Test
	public void shouldCorrectlyCalculateRowCount() {
		assertThat(DefaultFormView.calculateRowCount(2, 0), is(1));
		assertThat(DefaultFormView.calculateRowCount(2, 1), is(1));
		assertThat(DefaultFormView.calculateRowCount(3, 3), is(1));
		assertThat(DefaultFormView.calculateRowCount(3, 4), is(2));
		assertThat(DefaultFormView.calculateRowCount(3, 12), is(4));
	}
}
