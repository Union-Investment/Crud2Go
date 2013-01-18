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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletReloadedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletReloadedEventHandler;

public class PortletTest {

	private Portlet portlet;
	private PortletConfig config;

	@Mock
	private Page pageMock;

	@Mock
	private Tab tabMock;

	@Mock
	private PortletRefreshedEventHandler portletRefreshHandlerMock;

	@Mock
	private PortletReloadedEventHandler portletReloadHandlerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		config = new PortletConfig();
		config.setTitle("MyTitle");

		portlet = new Portlet(config);
	}

	@Test
	public void shouldAddPage() {
		portlet.setPage(pageMock);
		assertThat(portlet.getPage(), is(pageMock));
	}

	@Test
	public void shouldReturnConfiguredTitle() {
		assertThat(portlet.getTitle(), is("MyTitle"));
	}

	@Test
	public void shouldReturnElementById() {
		portlet.addElementById("test", this);
		assertThat(portlet.getElementById("test"), is((Object) this));
	}

	@Test
	public void shouldRegisterTabsById() {
		portlet.addElementById("test", tabMock);
		assertThat(portlet.getElementById("test"), is((Object) tabMock));
		assertThat(portlet.getTabsById().get("test"), is(tabMock));
	}

	@Test
	public void shouldRegisterRegionsById() {
		Region regionMock = Mockito.mock(Region.class);
		String id = "region";
		portlet.addElementById(id, regionMock);

		assertSame(portlet.getElementById(id), regionMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnUnknownElementId() {
		portlet.getElementById("UNKNOWN");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldRejectTwoElementsOfSameId() {
		portlet.addElementById("test", this);
		portlet.addElementById("test", this);
	}

	@Test
	public void shouldIgnoreNullId() {
		portlet.addElementById(null, this);
		assertThat(portlet.getElementsById().size(), is(0));
	}

	@Test
	public void shouldFireRefreshEventOnRefresh() {
		portlet.addRefreshHandler(portletRefreshHandlerMock);

		portlet.refresh();

		verify(portletRefreshHandlerMock).onPortletRefresh(
				new PortletRefreshedEvent(portlet));
	}

	@Test
	public void shouldRefreshOnPageReloadIfConfigured() {
		config.setRefreshOnPageReload(true);
		portlet = new Portlet(config);
		portlet.addRefreshHandler(portletRefreshHandlerMock);

		portlet.handleReload();

		verify(portletRefreshHandlerMock).onPortletRefresh(
				new PortletRefreshedEvent(portlet));
	}

	@Test
	public void shouldFireReloadEventOnReload() {
		portlet.addReloadHandler(portletReloadHandlerMock);

		portlet.handleReload();

		verify(portletReloadHandlerMock).onPortletReload(
				new PortletReloadedEvent(portlet));
	}

	@Test
	public void shouldNotRefreshOnPageReloadByDefault() {
		portlet.addRefreshHandler(portletRefreshHandlerMock);

		portlet.handleReload();

		verifyZeroInteractions(portletRefreshHandlerMock);
	}
}
