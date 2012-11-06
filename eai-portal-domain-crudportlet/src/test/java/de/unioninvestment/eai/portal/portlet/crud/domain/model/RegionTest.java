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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class RegionTest {

	private Region region;
	private EventBus mockBus;

	@Before
	public void setUp() {
		this.mockBus = Mockito.mock(EventBus.class);

		RegionConfig config = new RegionConfig();
		config.setId("ID");
		config.setTitle("Title");
		this.region = new Region(config);
		this.region.setEventBus(this.mockBus);
	}

	@Test
	public void shouldGetPropertiesFromConfig() {
		assertNotNull(this.region.getConfig());
		assertEquals("ID", this.region.getId());
		assertEquals("Title", this.region.getTitle());
		assertFalse(this.region.isCollapsible());
		assertFalse(this.region.isCollapsed());
	}

	@Test
	public void shouldNotFireEventsBecauseNotCollapsible() {
		this.region.addCollapseEventHandler(new CollapseEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onCollapse(CollapseEvent event) {
				fail();
			}
		});
		this.region.setCollapsed(true);
	}

	@Test
	public void shouldFireEvents() {
		this.region.getConfig().setCollapsible(true);

		CollapseEventHandler mockCollapseHandler = Mockito
				.mock(CollapseEventHandler.class);
		this.region.addCollapseEventHandler(mockCollapseHandler);

		ExpandEventHandler mockExpandHandler = Mockito
				.mock(ExpandEventHandler.class);
		this.region.addExpandEventHandler(mockExpandHandler);

		this.region.setCollapsed(true);
		verify(mockCollapseHandler, times(1)).onCollapse(
				any(CollapseEvent.class));
		verify(this.mockBus, times(1)).fireEvent(any(CollapseEvent.class));

		reset(this.mockBus);
		this.region.setCollapsed(false);

		verify(mockExpandHandler, times(1)).onExpand(any(ExpandEvent.class));
		verify(this.mockBus, times(1)).fireEvent(any(ExpandEvent.class));
	}
}
