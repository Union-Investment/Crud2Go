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

import de.unioninvestment.eai.portal.portlet.crud.domain.events.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;
import groovy.lang.Closure;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.bind.JAXBException;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class ScriptPortletTest {
	private ScriptPortlet scriptPortlet;

	@Mock
	private Portlet portletMock;

	@Mock
	private User userMock;

	@Mock
	private Closure<?> closureMock;

	@Captor
	private ArgumentCaptor<PortletRefreshedEventHandler> refreshedHandlerCaptor;

	@Captor
	private ArgumentCaptor<PortletReloadedEventHandler> reloadedHandlerCaptor;

	@Captor
	private ArgumentCaptor<PortletLoadedEventHandler> loadedHandlerCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		scriptPortlet = new ScriptPortlet(portletMock, false);
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowNoSuchElementExceptionOnUnknownId()
			throws JAXBException {

		ScriptPortlet scriptPortlet = new ScriptPortlet(portletMock, false);
		scriptPortlet.getElementById("unknownId");
	}

	@Test
	public void shouldCallOnRefreshClosureOnRefresh() {
		scriptPortlet.setOnRefresh(closureMock);
		verify(portletMock).addRefreshHandler(refreshedHandlerCaptor.capture());

		refreshedHandlerCaptor.getValue().onPortletRefresh(
				new PortletRefreshedEvent(portletMock));

		verify(closureMock).call(scriptPortlet);
	}

	@Test
	public void shouldCallOnReloadClosureOnReload() {
		scriptPortlet.setOnReload(closureMock);
		verify(portletMock).addReloadHandler(reloadedHandlerCaptor.capture());

		reloadedHandlerCaptor.getValue().onPortletReload(
				new PortletReloadedEvent(portletMock));

		verify(closureMock).call(scriptPortlet);
	}

	@Test
	public void shouldCallOnLoadClosureOnLoad() {
		scriptPortlet.setOnLoad(closureMock);
		verify(portletMock).addLoadHandler(loadedHandlerCaptor.capture());

		loadedHandlerCaptor.getValue().onPortletLoad(
				new PortletLoadedEvent(portletMock));

		verify(closureMock).call(scriptPortlet);
	}

	@Test
	public void shouldDelegateRefresh() {
		scriptPortlet.refresh();
		verify(portletMock).refresh();
	}

	@Test
	public void shouldProvideElementsAsMap() {
		Object myElement = new Object();
		scriptPortlet.addElementById("myId", myElement);

		Map<String, Object> result = scriptPortlet.getElements();

		assertThat(result.get("myId"), sameInstance(myElement));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotAllowChangesToElementsMap() {
		Map<String, Object> elements = scriptPortlet.getElements();

		elements.put("not", "allowed");
	}
	
}
