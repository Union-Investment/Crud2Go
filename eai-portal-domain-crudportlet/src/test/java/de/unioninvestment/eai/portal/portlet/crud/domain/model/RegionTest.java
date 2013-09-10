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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class RegionTest {

	private Region region;

	@Mock
	private RegionConfig configMock;

	@Mock
	private EventBus busMock;

	@Mock
	private CollapseEventHandler collapseEventHandlerMock;

	@Mock
	private ExpandEventHandler expandHandlerMock;

	@Mock
	private Panel panelMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		this.region = new Region(configMock);
		this.region.setEventBus(this.busMock);
		this.region.addCollapseEventHandler(collapseEventHandlerMock);
		this.region.addExpandEventHandler(expandHandlerMock);
	}

	@Test
	public void shouldGetPropertiesFromConfig() {
		when(configMock.getId()).thenReturn("ID");
		when(configMock.getTitle()).thenReturn("Title");
		when(configMock.isCollapsible()).thenReturn(true);
		when(configMock.isCollapsed()).thenReturn(true);
		when(configMock.isHorizontalLayout()).thenReturn(true);

		assertThat(this.region.getConfig(), is(configMock));
		assertThat(this.region.getId(), is("ID"));
		assertThat(this.region.getTitle(), is("Title"));
		assertThat(this.region.isCollapsible(), is(true));
		assertThat(this.region.isCollapsed(), is(false)); // !!!
		assertThat(this.region.isHorizontalLayout(), is(true));

		reset(this.configMock);
		when(configMock.isCollapsible()).thenReturn(false);
		when(configMock.isCollapsed()).thenReturn(false);
		when(configMock.isHorizontalLayout()).thenReturn(false);
		assertThat(this.region.isCollapsible(), is(false));
		assertThat(this.region.isCollapsed(), is(false));
		assertThat(this.region.isHorizontalLayout(), is(false));
	}

	@Test
	public void shouldNotFireEventsBecauseNotCollapsible() {
		when(configMock.isCollapsible()).thenReturn(false);

		// this should not fire an event, because the region is configured to be
		// not collapsible
		this.region.setCollapsed(true);

		verifyZeroInteractions(collapseEventHandlerMock);
	}

	@Test
	public void shouldFireEvents() {
		when(configMock.isCollapsible()).thenReturn(true);

		this.region.setCollapsed(true);
		verify(collapseEventHandlerMock, times(1)).onCollapse(
				any(CollapseEvent.class));
		verify(this.busMock, times(1)).fireEvent(any(CollapseEvent.class));

		reset(this.busMock);
		this.region.setCollapsed(false);

		verify(expandHandlerMock, times(1)).onExpand(any(ExpandEvent.class));
		verify(this.busMock, times(1)).fireEvent(any(ExpandEvent.class));
	}
	
	@Test
	public void shouldLetParentPanelAttachDialogs() {
		region.setPanel(panelMock);
		region.attachDialog("4711");
		verify(panelMock).attachDialog("4711");
	}
	
	@Test
	public void shouldLetParentPanelDetachDialogs() {
		region.setPanel(panelMock);
		region.detachDialog();
		verify(panelMock).detachDialog();
	}
}
