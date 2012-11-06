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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;

public class FormActionsTest {

	private FormAction action1;
	private FormAction action2;
	private FormActions actions;

	@Mock
	private Form form;

	@Mock
	private ActionHandler actionHandlerMock;

	@Mock
	private Portlet portletMock;
	@Mock
	private SearchFormAction searchActionHandlerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		FormActionConfig formActionConfig = new FormActionConfig();
		formActionConfig.setId("id01");
		formActionConfig.setTitle("title1");
		action1 = new FormAction(portletMock, formActionConfig,
				actionHandlerMock, null);

		FormActionConfig formActionConfig2 = new FormActionConfig();
		formActionConfig2.setId("id02");
		formActionConfig2.setTitle("title1");
		action2 = new FormAction(portletMock, formActionConfig2,
				searchActionHandlerMock, null);

		actions = new FormActions(asList(action1, action2));
	}

	@Test
	public void shouldAllowIteration() {
		assertThat(actions.iterator().next(), is(action1));
	}

	@Test
	public void shouldReturnSearchAction() {
		assertThat(actions.getSearchAction(), is(action2));
	}

	@Test
	public void shouldReturnNullOnMissingSearchAction() {
		actions = new FormActions(asList(action1));
		assertThat(actions.getSearchAction(), nullValue());
	}

	@Test
	public void shouldReturnActionById() {
		assertThat(actions.getById("id01"), is(action1));
	}

	@Test
	public void shouldReturnNullOnNonExistentAction() {
		assertThat(actions.getById("doesNotExist"), nullValue());
	}
}
