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

import static org.mockito.Mockito.verify;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;

public class ScriptTabTest {

	@Mock
	private Tab tabMock;

	private ScriptTab stab;

	@Mock
	private Closure<?> onShowMock;

	@Mock
	private Closure<?> onHideMock;

	@Captor
	private ArgumentCaptor<ShowEventHandler<Tab>> showEventCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		stab = new ScriptTab(tabMock);
	}

	@Test
	public void shouldFireShowEvent() {
		verify(tabMock).addShowEventListener(showEventCaptor.capture());

		stab.setOnShow(onShowMock);

		ShowEvent<Tab> showEvent = new ShowEvent<Tab>(tabMock);
		showEventCaptor.getValue().onShow(showEvent);

		verify(onShowMock).call(stab);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void shouldFireHideEvent() {
		ArgumentCaptor<HideEventHandler> showEventCaptor = ArgumentCaptor
				.forClass(HideEventHandler.class);
		verify(tabMock).addHideEventListener(showEventCaptor.capture());

		stab.setOnHide(onHideMock);

		HideEvent<Tab> hideEvent = new HideEvent<Tab>(tabMock);
		showEventCaptor.getValue().onHide(hideEvent);

		verify(onHideMock).call(stab);
	}

}
