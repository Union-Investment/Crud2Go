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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;

public class TabTest {

	@Mock
	private Tabs tabsMock;

	@Mock
	TabConfig configMock;

	@Mock
	TabConfig configMock2;

	public Tab tab;

	public Tab tab2;

	@Mock
	private ShowEventHandler<Tab> showEventListenerMock;

	@Mock
	private HideEventHandler<Tab> hideEventListenerMock;

	@Captor
	private ArgumentCaptor<ShowEvent<Tab>> showEventCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		tab = new Tab(configMock);
		tab2 = new Tab(configMock2);
	}

	@Test
	public void shouldReturnTabTitle() {
		when(configMock.getTitle()).thenReturn("Testtitle");

		assertThat(tab.getTitle(), is("Testtitle"));
	}

	@Test
	public void shouldReturnTabsContainer() {
		tab.setTabs(tabsMock);

		assertThat(tab.getTabs(), is(tabsMock));
	}

	@Test
	public void shouldRegisterAsTabChangeEventHandler() {
		tab.setTabs(tabsMock);

		ArgumentCaptor<Tab.TabChangeEventHandler> captor = ArgumentCaptor
				.forClass(Tab.TabChangeEventHandler.class);

		verify(tabsMock).addTabChangeEventListener(captor.capture());
	}

	@Test
	public void shouldFireShowEvent() {
		when(configMock.getId()).thenReturn("tab1");
		tab.setTabs(tabsMock);

		ArgumentCaptor<Tab.TabChangeEventHandler> listenerCaptor = ArgumentCaptor
				.forClass(Tab.TabChangeEventHandler.class);
		verify(tabsMock).addTabChangeEventListener(listenerCaptor.capture());

		when(tabsMock.getActiveTab()).thenReturn(tab);
		when(tabsMock.getActiveTabId()).thenReturn("tab1");

		tab.addShowEventListener(showEventListenerMock);

		// act
		listenerCaptor.getValue().onTabChange(new TabChangeEvent(tabsMock));

		verify(showEventListenerMock).onShow(showEventCaptor.capture());

		assertThat(showEventCaptor.getValue().getSource(), is((Object) tab));
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shouldFireHideEvent() {

		when(configMock.getId()).thenReturn("tab1");
		tab.setTabs(tabsMock);

		ArgumentCaptor<Tab.TabChangeEventHandler> listenerCaptor = ArgumentCaptor
				.forClass(Tab.TabChangeEventHandler.class);

		verify(tabsMock).addTabChangeEventListener(listenerCaptor.capture());

		when(tabsMock.getActiveTab()).thenReturn(tab, tab2);
		when(tabsMock.getActiveTabId()).thenReturn("tab1", "tab2");

		tab.addHideEventListener(hideEventListenerMock);

		listenerCaptor.getValue().onTabChange(new TabChangeEvent(tabsMock));
		listenerCaptor.getValue().onTabChange(new TabChangeEvent(tabsMock));

		ArgumentCaptor<HideEvent> eventCaptor = ArgumentCaptor
				.forClass(HideEvent.class);
		verify(hideEventListenerMock).onHide(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getSource(), is((Object) tab));
	}

	@Test
	public void shouldNotUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(false);
		assertThat(tab.isHorizontalLayout(), is(false));
	}

	@Test
	public void shoutUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(true);

		assertThat(tab.isHorizontalLayout(), is(true));
	}

}
