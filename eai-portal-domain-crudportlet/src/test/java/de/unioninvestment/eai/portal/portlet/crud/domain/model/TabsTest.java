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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class TabsTest {

	@Mock
	private TabsConfig configMock;

	private Tabs tabs;

	@Mock
	private Tab tab;

	@Mock
	private TabChangeEventHandler handlerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		tabs = new Tabs(configMock);
	}

	@Test
	public void shouldHandleOnChangeEvents() {
		ArgumentCaptor<TabChangeEvent> eventCaptor = ArgumentCaptor
				.forClass(TabChangeEvent.class);

		tabs.setEventBus(new EventBus());
		tabs.addElement(tab);
		tabs.addTabChangeEventListener(handlerMock);

		tabs.setActiveTab(tab);

		verify(handlerMock).onTabChange(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getSource(), is((Object) tabs));
	}

	@Test
	public void shouldAddTabs() {
		Tab tabMock1 = mock(Tab.class);
		Tab tabMock2 = mock(Tab.class);
		Tab tabMock3 = mock(Tab.class);

		tabs.addElement(tabMock1);
		tabs.addElement(tabMock2);
		tabs.addElement(tabMock3);

		List<Tab> result = tabs.getElements();
		assertThat(result.size(), is(3));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldReturnAnUnmodifiableListOfTabs() {
		Tab tabMock1 = mock(Tab.class);
		Tab tabMock2 = mock(Tab.class);
		Tab tabMock3 = mock(Tab.class);

		tabs.addElement(tabMock1);
		tabs.addElement(tabMock2);
		tabs.addElement(tabMock3);

		List<Tab> result = tabs.getElements();
		result.remove(0);
	}

}
