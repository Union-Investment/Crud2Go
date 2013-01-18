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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Eine Containerkomponente für eine Tabgruppe.
 * 
 * 
 * @author markus.bonsch
 * 
 */
@Configurable
public class Tabs extends Component {

	@Autowired
	private EventBus eventBus;

	private final List<Tab> elements = new ArrayList<Tab>();

	private Portlet portlet;

	private ObjectProperty<Tab> activeTabProperty;

	private EventRouter<TabChangeEventHandler, TabChangeEvent> eventRouter = new EventRouter<TabChangeEventHandler, TabChangeEvent>();

	/**
	 * @param config
	 *            the component's configuration.
	 * @since 1.45
	 */
	private final TabsConfig config;

	/**
	 * Konstruktor.
	 * 
	 * @param config
	 *            the component's configuration.
	 * @since 1.45
	 */
	@SuppressWarnings("all")
	public Tabs(TabsConfig config) {
		this.config = config;
	}

	/**
	 * @since 1.45
	 */
	public String getWidth() {
		return this.config.getWidth();
	}

	/**
	 * @since 1.45
	 */
	public String getHeight() {
		return this.config.getHeight();
	}

	private void registerValueChangeListener() {
		activeTabProperty.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				fireTabChangeEvent();
			}
		});
	}

	/**
	 * 
	 * @param handler
	 *            TabChangeEventHandler
	 */
	public void addTabChangeEventListener(TabChangeEventHandler handler) {
		eventRouter.addHandler(handler);
	}

	private void fireTabChangeEvent() {
		TabChangeEvent event = new TabChangeEvent(this);
		eventRouter.fireEvent(event);
		eventBus.fireEvent(event);
	}

	public List<Tab> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * 
	 * @param element
	 *            Tab
	 * @return Position des Tabs
	 */
	public int indexOf(Tab element) {
		return elements.indexOf(element);
	}

	/**
	 * Fügt ein Tab hinzu.
	 * 
	 * @param tab
	 *            Tab
	 */
	void addElement(Tab tab) {
		if (elements.isEmpty()) {
			activeTabProperty = new ObjectProperty<Tab>(tab);
			registerValueChangeListener();
		}
		elements.add(tab);
		tab.setTabs(this);
	}

	void setPortlet(Portlet portlet) {
		this.portlet = portlet;
	}

	/**
	 * @return liefert das Portlet
	 */
	public Portlet getPortlet() {
		return getPanel() != null ? getPanel().getPortlet() : portlet;
	}

	public ObjectProperty<Tab> getActiveTabProperty() {
		return activeTabProperty;
	}

	/**
	 * Setzt den aktiven Tab über dessen ID.
	 * 
	 * @param id
	 *            ID des Tabs
	 */
	public void setActiveTabById(String name) {
		for (Tab tab : elements) {
			if (tab.getId().equals(name)) {
				activeTabProperty.setValue(tab);
				break;
			}
		}
	}

	/**
	 * @return ID des aktiven Tabs
	 */
	public String getActiveTabId() {
		return getActiveTab().getId();
	}

	/**
	 * @return den aktiven Tab
	 */
	public Tab getActiveTab() {
		return activeTabProperty.getValue();
	}

	/**
	 * Set den aktiven Tab.
	 * 
	 * @param tab
	 *            Tab
	 */
	public void setActiveTab(Tab tab) {
		if (elements.contains(tab)) {
			activeTabProperty.setValue(tab);
		}
	}

	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
