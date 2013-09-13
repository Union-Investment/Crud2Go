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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.DialogConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;

public class DialogTest {

	@Mock
	private DialogConfig configMock;

	private Dialog dialog;

	@Mock
	private ShowEventHandler<Dialog> showEventListenerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		dialog = new Dialog(configMock);
	}

	@Test
	public void shouldGetId() {
		when(configMock.getId()).thenReturn("1");
		assertThat(dialog.getId(), is("1"));
	}

	@Test
	public void shouldGetBackLabel() {
		when(configMock.getBackButtonCaption()).thenReturn("Zurück");
		assertThat(dialog.getBackButtonCaption(), is("Zurück"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldFireShowEvent() {
		dialog.addShowEventListener(showEventListenerMock);
		dialog.fireShowEvent();
		verify(showEventListenerMock).onShow(any(ShowEvent.class));
	}

	@Test
	public void shouldNotUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(false);
		assertThat(dialog.isHorizontalLayout(), is(false));
	}

	@Test
	public void shoutUseHorizontalLayout() {
		when(configMock.isHorizontalLayout()).thenReturn(true);
		assertThat(dialog.isHorizontalLayout(), is(true));
	}

	@Test
	public void shouldReturnConstructorProperties() {
		dialog = new Dialog("2", "Zurück2");
		assertThat(dialog.getId(), is("2"));
		assertThat(dialog.getBackButtonCaption(), is("Zurück2"));
	}
}
