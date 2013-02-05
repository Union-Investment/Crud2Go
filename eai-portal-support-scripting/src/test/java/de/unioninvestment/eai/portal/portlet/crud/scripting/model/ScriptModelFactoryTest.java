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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class ScriptModelFactoryTest {
	private ScriptModelFactory scriptModelFactory;

	@Mock
	private ConnectionPoolFactory connectionPoolFactoryMock;

	@Mock
	private UserFactory userFactoryMock;

	@Mock
	private FormFieldConfig configMock;

	@Mock
	private OptionList optionListMock;

	@Mock
	private EventBus eventBusMock;

	@Mock
	private Portal portalMock;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		scriptModelFactory = new ScriptModelFactory(connectionPoolFactoryMock,
				userFactoryMock, portalMock);
	}

	@Test
	public void shouldCreateFormField() {
		FormField field = new FormField(configMock);
		ScriptFormField formField = scriptModelFactory.getFormField(field);

		assertThat(formField instanceof ScriptOptionListFormField, is(false));
		assertThat(formField instanceof ScriptMultiOptionListFormField,
				is(false));
	}

	@Test
	public void shouldCreateOptionListFormField() {
		OptionListFormField field = new OptionListFormField(configMock,
				optionListMock);
		ScriptFormField formField = scriptModelFactory.getFormField(field);

		assertThat(formField instanceof ScriptOptionListFormField, is(true));
	}

	@Test
	public void shouldCreateMultiOptionListFormField() {
		MultiOptionListFormField field = new MultiOptionListFormField(
				configMock, optionListMock);
		ScriptFormField formField = scriptModelFactory.getFormField(field);

		assertThat(formField instanceof ScriptMultiOptionListFormField,
				is(true));
	}
}
