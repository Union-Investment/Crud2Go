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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEventHandler;

public class TableActionTest {

	@Mock
	private Table tableMock;

	@Mock
	private TableActionConfig configMock;

	@Mock
	private ExecutionEventHandler handlerMock;

	@Mock
	private Portlet portletMock;

	@Mock
	private TableAction action1;
	
	@Mock
	private TableAction action2;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldHandleOnExecutionEvents() {
		ArgumentCaptor<ExecutionEvent> eventCaptor = ArgumentCaptor
				.forClass(ExecutionEvent.class);

		TableAction tableAction = new TableAction(portletMock, configMock,
				tableMock, null);
		tableAction.addExecutionEventListener(handlerMock);

		tableAction.execute();

		verify(handlerMock).onExecution(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getSource(), is((Object) tableAction));
	}
	
	@Test
	public void shouldReturnTitle() {
		when(configMock.getTitle()).thenReturn("title");
		TableAction tableAction = new TableAction(portletMock, configMock, tableMock, null);
		assertEquals("title", tableAction.getTitle());
	}
	
	@Test
	public void shouldCallTriggers() {
		Triggers triggers = new Triggers();
		triggers.addTrigger(new Trigger("action1"));
		triggers.addTrigger(new Trigger("action2"));
		
		when(portletMock.getElementById("action1")).thenReturn(action1);
		when(portletMock.getElementById("action2")).thenReturn(action2);
		
		TableAction action = new TableAction(portletMock, configMock, tableMock, triggers);
		action.execute();
		
		verify(action1, times(1)).execute();
		verify(action2, times(1)).execute();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldFailWithInvalidTriggerAction() {
		Triggers triggers = new Triggers();
		triggers.addTrigger(new Trigger("action1"));
		
		when(portletMock.getElementById("action1")).thenReturn(new Object());
		
		TableAction action = new TableAction(portletMock, configMock, tableMock, triggers);
		action.execute();
	}
}
