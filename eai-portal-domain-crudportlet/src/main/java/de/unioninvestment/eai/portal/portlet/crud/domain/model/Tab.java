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

import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Modell-Klassen für eine Portlet-Seite.
 * 
 * @author max.hartmann
 * 
 */
public class Tab extends Panel implements LazyInitializable<Tab> {

	/**
	 * 
	 * Implementierung des Interfaces TabChangeEventHandler.
	 * 
	 * @author siva.selvarajah
	 */
	public class TabChangeEventHandler
			implements
			de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler {

		private static final long serialVersionUID = 1L;

		private boolean active;

		@Override
		public void onTabChange(TabChangeEvent event) {
			if (Tab.this.equals(event.getSource().getActiveTab())) {
				fireShowEvent();
				active = true;
			} else if (active) {
				fireHideEvent();
				active = false;
			}
		}
	}

	private static final long serialVersionUID = 1L;
	private Tabs tabs;
	private final EventRouter<ShowEventHandler<Tab>, ShowEvent<Tab>> showEventRouter = new EventRouter<ShowEventHandler<Tab>, ShowEvent<Tab>>();
	private final EventRouter<HideEventHandler<Tab>, HideEvent<Tab>> hideEventRouter = new EventRouter<HideEventHandler<Tab>, HideEvent<Tab>>();

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param config
	 *            Konfiguration der Portlet-Seite
	 */
	public Tab(TabConfig config) {
		super(config);
	}

	public String getId() {
		return this.getConfig().getId();
	}

	/**
	 * 
	 * Setzt die Tabs und fügt einen TabChangeEventListener hinzu. Das
	 * Neu-Setzen Tabs ist nicht vorgesehen.
	 * 
	 * @param tabs
	 *            Tabs
	 */
	void setTabs(Tabs tabs) {
		this.tabs = tabs;
		tabs.addTabChangeEventListener(new TabChangeEventHandler());
	}

	/**
	 * @return der Anzeigetitel des Tab
	 */
	public String getTitle() {
		return this.getConfig().getTitle();
	}

	public Tabs getTabs() {
		return tabs;
	}

	/**
	 * 
	 * Fügt ein ShowEventListener hinzu.
	 * 
	 * @param showEventListener
	 *            ShowEventHandler
	 */
	public void addShowEventListener(ShowEventHandler<Tab> showEventListener) {
		this.showEventRouter.addHandler(showEventListener);
	}

	/**
	 * 
	 * Fügt ein HideEventListener hinzu.
	 * 
	 * @param hideEventListener
	 *            ShowEventHandler
	 */
	public void addHideEventListener(HideEventHandler<Tab> hideEventListener) {
		this.hideEventRouter.addHandler(hideEventListener);
	}

	/**
	 * Feuert ein {@link ShowEvent}.
	 */
	void fireShowEvent() {
		ShowEvent<Tab> event = new ShowEvent<Tab>(this);
		showEventRouter.fireEvent(event);
	}

	/**
	 * Feuert einen {@link HideEvent}.
	 */
	void fireHideEvent() {
		HideEvent<Tab> event = new HideEvent<Tab>(this);
		hideEventRouter.fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void attachDialog(String dialogId) {
		getPresenter().attachDialog(dialogId, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TabConfig getConfig() {
		return (TabConfig) super.getConfig();
	}
}
