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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.ui.Window;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class PortletUriFragmentUtilityTest {

	@Mock
	private Portlet portletMock;

	@Mock
	private EventBus eventBus;

	@Mock
	Tabs tabsMock1;

	@Mock
	Tabs tabsMock2;

	@Mock
	Tab tabMock1;

	@Mock
	Tab tabMock2;

	String fragment;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	private PortletUriFragmentUtility portletUriFragmentUtility;

	public PortletUriFragmentUtilityTest() {
		MockitoAnnotations.initMocks(this);

		when(tabMock1.getId()).thenReturn("tab01");
		when(tabMock1.getTabs()).thenReturn(tabsMock1);
		when(tabsMock1.getActiveTab()).thenReturn(tabMock1);

		when(tabMock2.getId()).thenReturn("tab02");
		when(tabMock2.getTabs()).thenReturn(tabsMock2);
		when(tabsMock2.getActiveTab()).thenReturn(tabMock2);

		Map<String, Tab> tabRefMap = new HashMap<String, Tab>();
		tabRefMap.put("tab01", tabMock1);
		tabRefMap.put("tab02", tabMock2);

		when(portletMock.getTabsById()).thenReturn(tabRefMap);

		portletUriFragmentUtility = new PortletUriFragmentUtility(eventBus,
				portletMock, "PortletID");

		fragment = "PortletID;tab02,tab01,";
	}

	@Test
	public void ShouldBuildTabStatus() throws Exception {
		String buildTabStatus = portletUriFragmentUtility.buildTabStatus();

		assertThat(buildTabStatus, is(fragment));
	}

	@Test
	public void shouldInitialize() {
		portletUriFragmentUtility.initialize();
	}

	@Test
	public void shouldActivateTab() {
		when(tabMock1.getId()).thenReturn("tab01");
		when(tabMock1.getTabs()).thenReturn(tabsMock1);
		when(tabsMock1.getActiveTab()).thenReturn(tabMock1);

		when(tabMock2.getId()).thenReturn("tab02");
		when(tabMock2.getTabs()).thenReturn(tabsMock2);
		when(tabsMock2.getActiveTab()).thenReturn(tabMock2);

		Map<String, Tab> tabRefMap = new HashMap<String, Tab>();
		tabRefMap.put("tab01", tabMock1);
		tabRefMap.put("tab02", tabMock2);

		when(portletMock.getTabsById()).thenReturn(tabRefMap);

		portletUriFragmentUtility = new PortletUriFragmentUtility(eventBus,
				portletMock, "PortletID");

		// --------------------------

		portletUriFragmentUtility.activateTabs(fragment);

		verify(tabsMock1, times(1)).setActiveTabById("tab01");
		verify(tabsMock2, times(1)).setActiveTabById("tab02");
	}

	@Test
	public void shouldNotActivateTab() {
		when(tabMock1.getId()).thenReturn("tab01");
		when(tabMock1.getTabs()).thenReturn(tabsMock1);
		when(tabsMock1.getActiveTab()).thenReturn(tabMock1);

		when(tabMock2.getId()).thenReturn("tab02");
		when(tabMock2.getTabs()).thenReturn(tabsMock2);
		when(tabsMock2.getActiveTab()).thenReturn(tabMock2);

		Map<String, Tab> tabRefMap = new HashMap<String, Tab>();
		tabRefMap.put("tab01", tabMock1);
		tabRefMap.put("tab02", tabMock2);

		when(portletMock.getTabsById()).thenReturn(tabRefMap);

		portletUriFragmentUtility = new PortletUriFragmentUtility(eventBus,
				portletMock, "PortletID");

		// --------------------------

		portletUriFragmentUtility.activateTabs("AnderePortletID;tab02,tab01,");

		verify(tabsMock1, times(0)).setActiveTabById("tab01");
		verify(tabsMock2, times(0)).setActiveTabById("tab02");
	}

	@Test
	public void shouldSetInitialFragment() {
		Window mainWindow = mock(Window.class);
		portletUriFragmentUtility.setInitialFragment();

		verify(liferayContext.getPageMock(), times(1)).setUriFragment(fragment);
	}

	@Test
	public void shouldRunFragmentChangedEvent() {
		UriFragmentChangedEvent event = mock(UriFragmentChangedEvent.class);
		when(event.getUriFragment()).thenReturn(fragment);

		portletUriFragmentUtility.uriFragmentChanged(event);
	}
}
