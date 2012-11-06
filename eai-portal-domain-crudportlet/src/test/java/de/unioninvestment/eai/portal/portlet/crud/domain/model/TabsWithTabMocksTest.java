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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class TabsWithTabMocksTest {

	private Tabs tabs = new Tabs();

	@Mock
	private EventBus eventBusMock;

	@Mock
	private Tab tabMock1;
	@Mock
	private Tab tabMock2;
	@Mock
	private Tab tabMock3;

//	@Mock
//	private Panel panelMock;

	@Mock
	private TabChangeEventHandler listenerMock;

	@Captor
	private ArgumentCaptor<TabChangeEvent> eventCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		tabs.setEventBus(eventBusMock);

		when(tabMock1.getId()).thenReturn("tab1");
		when(tabMock2.getId()).thenReturn("tab2");
		when(tabMock3.getId()).thenReturn("tab3");
		tabs.addElement(tabMock1);
		tabs.addElement(tabMock2);
		tabs.addElement(tabMock3);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldReturnAnUnmodifiableListOfTabs() {
		List<Tab> result = tabs.getElements();
		result.remove(0);
	}

	@Test
	public void shouldSendTabChangeEventToListenersOnValueChange() {
		tabs.addTabChangeEventListener(listenerMock);

		tabs.setActiveTab(tabMock2);

		verify(listenerMock).onTabChange(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getSource(), is(tabs));
	}

	@Test
	public void shouldSendTabChangeEventToEventBusOnValueChange() {
		tabs.setActiveTab(tabMock2);

		verify(eventBusMock).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue().getSource(), is(tabs));
	}
}
