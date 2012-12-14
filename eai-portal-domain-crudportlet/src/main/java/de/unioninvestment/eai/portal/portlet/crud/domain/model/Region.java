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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Modell für einen Bereich innerhalb einer Seite.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
@Configurable
public class Region extends Panel implements Component.ExpandableComponent {

	private static final long serialVersionUID = 1L;

	private final EventRouter<ExpandEventHandler, ExpandEvent> expandEventRouter = new EventRouter<ExpandEventHandler, ExpandEvent>();
	private final EventRouter<CollapseEventHandler, CollapseEvent> collapseEventRouter = new EventRouter<CollapseEventHandler, CollapseEvent>();

	private final RegionConfig config;

	@Autowired
	private EventBus eventBus;

	private boolean collapsed;

	/**
	 * Erzeugt eine neue Instanz eines Bereichs.
	 * 
	 * @param config
	 *            das entsprechende config-Element aus dem JAXB-Modell.
	 */
	public Region(RegionConfig config) {
		this.config = config;
		this.collapsed = config.isCollapsed();
	}

	/** @return das config element aus dem JAXB Modell. */
	public RegionConfig getConfig() {
		return this.config;
	}

	public String getId() {
		return this.config.getId();
	}

	public String getTitle() {
		return this.config.getTitle();
	}

	/**
	 * @return <code>true</code> if components within this region are to be
	 *         layed out horizontally, <code>false</code> if vertically.
	 * @since 1.45
	 */
	public boolean isHorizontalLayout() {
		return this.config.isHorizontalLayout();
	}

	public boolean isCollapsible() {
		return this.config.isCollapsible();
	}

	public boolean isCollapsed() {
		return this.collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		if (this.config.isCollapsible() && collapsed != this.collapsed) {
			this.collapsed = collapsed;
			SourceEvent<?, Region> event = null;
			if (this.collapsed) {
				event = new CollapseEvent(this);
				this.collapseEventRouter.fireEvent((CollapseEvent) event);
			} else {
				event = new ExpandEvent(this);
				this.expandEventRouter.fireEvent((ExpandEvent) event);
			}
			this.eventBus.fireEvent(event);
		}
	}

	/**
	 * @param handler
	 *            der Handler der hinzugefügt werden soll.
	 */
	public void addCollapseEventHandler(CollapseEventHandler handler) {
		this.collapseEventRouter.addHandler(handler);
	}

	/**
	 * @param handler
	 *            der Handler der hinzugefügt werden soll.
	 */
	public void addExpandEventHandler(ExpandEventHandler handler) {
		this.expandEventRouter.addHandler(handler);
	}

	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.45
	 */
	@Override
	public int getExpandRatio() {
		return config.getExpandRatio();
	}
}
