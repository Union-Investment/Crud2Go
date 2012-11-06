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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction.ActionHandler;

public class FormActionTest {

	@Mock
	private Portlet portletMock;

	@Mock
	private FormAction formActionMock1;

	@Mock
	private FormAction formActionMock2;

	private FormActionConfig formActionConfig;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		formActionConfig = new FormActionConfig();
		formActionConfig.setId("id01");
		formActionConfig.setTitle("titel1");

		when(portletMock.getElementById("action1")).thenReturn(formActionMock1);
		when(portletMock.getElementById("action2")).thenReturn(formActionMock2);
	}

	@Test
	public void shouldReturnTitle() {
		FormAction action = new FormAction(portletMock, formActionConfig,
				mock(ActionHandler.class), null);

		assertThat(action.getTitle(), is("titel1"));
	}

	@Test
	public void shouldExecuteTriggers() {
		Triggers triggers = new Triggers();
		triggers.addTrigger(new Trigger("action1"));
		triggers.addTrigger(new Trigger("action2"));

		FormAction action = new FormAction(portletMock, formActionConfig,
				mock(ActionHandler.class), triggers);

		action.execute();

		verify(portletMock, times(1)).getElementById("action1");
		verify(portletMock, times(1)).getElementById("action2");

		verify(formActionMock1, times(1)).execute();
		verify(formActionMock2, times(1)).execute();

	}

	@Test
	public void shouldNotBeInitiallyHiddenAndCanBeSetToHidden() {
		FormAction action = new FormAction(portletMock, formActionConfig,
				mock(ActionHandler.class), null);

		assertFalse(action.isHidden());

		action.setHidden(true);
		assertTrue(action.isHidden());
	}
}
