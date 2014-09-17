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

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.PreferencesRepository;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * Repräsentation das Portlets im Modell.
 * 
 * @author carsten.mjartan
 */
public class Portlet implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(Portlet.class);

    /**
	 * Permission Actions.
	 */
	public enum Permission {
		DISPLAY_GENERATED_CONTENT
	}

	private Page page;
	private Tabs tabs;
	private Map<String, Object> elementsById = new HashMap<String, Object>();
	private Map<String, Tab> tabsById = new HashMap<String, Tab>();
	private Map<String, Dialog> dialogsById = new HashMap<String, Dialog>();
	private Map<String, Region> regionsById = new HashMap<String, Region>();
	private String title;
	private final PortletConfig config;

	private EventRouter<PortletLoadedEventHandler, PortletLoadedEvent> loadEventRouter = new EventRouter<PortletLoadedEventHandler, PortletLoadedEvent>();
	private EventRouter<PortletReloadedEventHandler, PortletReloadedEvent> reloadEventRouter = new EventRouter<PortletReloadedEventHandler, PortletReloadedEvent>();

	private Set<Role> portletRoles = new HashSet<Role>();

	private EventBus eventBus;

	private Map<String, Realm> realms = new HashMap<String, Realm>();

	private PortletContext context;

	private HashMap<String, Preference> preferences;

	/**
	 * @param config
	 *            PortletConfig
	 * @param context
	 */
	public Portlet(EventBus eventBus, PortletConfig config,
			PortletContext context) {
		this.eventBus = eventBus;
		this.config = config;
		this.context = context;
		this.title = config.getTitle();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Page kann nur durch den Builder gesetzt werden.
	 * 
	 * @param page
	 *            Page
	 */
	void setPage(Page page) {
		this.page = page;
		page.setPortlet(this);
	}

	public Page getPage() {
		return page;
	}

	/**
	 * Setzt die Tabs.
	 * 
	 * @param tabs
	 *            Tabs
	 */
	void setTabs(Tabs tabs) {
		this.tabs = tabs;
		tabs.setPortlet(this);
	}

	/**
	 * Liefert das Tab-Objekt, wenn das Portlet nur aus einer
	 * &lt;tabs&gt;-Konfiguration besteht, sonst null.
	 * 
	 * @return die Tabs, oder null
	 */
	public Tabs getTabs() {
		return tabs;
	}

	public Map<String, Tab> getTabsById() {
		return tabsById;
	}

	public Map<String, Dialog> getDialogsById() {
		return dialogsById;
	}

	/**
	 * @return eine unveränderbare Map aller Bereichs-Instanzen.
	 */
	public Map<String, Region> getRegionsById() {
		return Collections.unmodifiableMap(this.regionsById);
	}

	/**
	 * @param id
	 *            die ID der angefragten Komponente
	 * @return die Komponente aus dem Model
	 * @throws IllegalArgumentException
	 *             wenn die ID unbekannt ist
	 */
	public Object getElementById(String id) {
		Object result = elementsById.get(id);
		if (result == null) {
			throw new IllegalArgumentException("Element der ID '" + id
					+ "' ist nicht bekannt!");
		}
		return result;
	}

	/**
	 * Wird während des Build-Prozesses aufgerufen.
	 * 
	 * @param id
	 *            die Konfigurationsweite ID der Komponente
	 * @param element
	 *            die Referenz auf die Model-Instanz
	 */
	void addElementById(String id, Object element) {
		if (id != null) {
			if (elementsById.containsKey(id)) {
				throw new IllegalStateException("Element der ID '" + id
						+ "' ist bereits registriert");
			} else {
				elementsById.put(id, element);
				if (element instanceof Tab) {
					tabsById.put(id, (Tab) element);
				} else if (element instanceof Dialog) {
					dialogsById.put(id, (Dialog) element);
				} else if (element instanceof Region) {
					regionsById.put(id, (Region) element);
				}
			}
		}
	}

	Map<String, Object> getElementsById() {
		return elementsById;
	}

	/**
	 * add a new role.
	 * 
	 * @param portletRole
	 *            Rolle
	 */
	void addRole(Role portletRole) {
		this.portletRoles.add(portletRole);
	}

	/**
	 * @return die am Portlet konfigurierten Benutzerrollen
	 */
	public Set<Role> getRoles() {
		return unmodifiableSet(this.portletRoles);
	}

	/**
	 * @param handler
	 *            a new handler for Refreshing of portlet components
	 */
	public void addRefreshHandler(PortletRefreshedEventHandler handler) {
		eventBus.addHandler(PortletRefreshedEvent.class, handler);
	}

	/**
	 * Triggers a refresh of the data of all portlet components
	 */
	public void refresh() {
		LOG.info("Start refreshing portlet components");
		eventBus.fireEvent(new PortletRefreshedEvent(this));
	}

	/**
	 * Informs the portlet that a page reload is happening. It's up to the
	 * Portlet configuration if this triggers a refresh.
	 */
	public void handleLoad() {
		LOG.info("Portlet is loaded (GUI is initialized)");
		loadEventRouter.fireEvent(new PortletLoadedEvent(this));
	}

	/**
	 * Informs the portlet that a page reload is happening. It's up to the
	 * Portlet configuration if this triggers a refresh.
	 */
	public void handleReload() {
		LOG.info("Portlet is reloaded");
		reloadEventRouter.fireEvent(new PortletReloadedEvent(this));
		if (config.isRefreshOnPageReload()) {
			refresh();
		}
	}

	/**
	 * @param handler
	 *            a new handler for custom handling of page reloads
	 */
	public void addLoadHandler(PortletLoadedEventHandler handler) {
		loadEventRouter.addHandler(handler);
	}

	/**
	 * @param handler
	 *            a new handler for custom handling of page reloads
	 */
	public void addReloadHandler(PortletReloadedEventHandler handler) {
		reloadEventRouter.addHandler(handler);
	}

	public Map<String, Realm> getAuthenticationRealms() {
		return unmodifiableMap(realms);
	}

	void addRealm(String name, Realm realm) {
		realms.put(name, realm);
	}

	/**
	 * @return <code>true</code>, falls der User generierten Content sehen darf.
	 */
	public boolean allowsDisplayGeneratedContent() {
		return context.getCurrentUser().hasPermission(config,
				Permission.DISPLAY_GENERATED_CONTENT, true);
	}

	public PortletContext getContext() {
		return context;
	}

	public synchronized Map<String, Preference> getPreferences() {
		if (this.preferences == null) {
			this.preferences = new LinkedHashMap<String, Preference>();
			if (config.getPreferences() != null) {
                PreferencesRepository repository = Context.getBean(PreferencesRepository.class);
				for (PreferenceConfig pref : config.getPreferences()
						.getPreference()) {
					preferences.put(pref.getKey(), repository.getPreference(this, pref));
				}
			}
		}
		return preferences;
	}
	
	public PortletCaching getCaching() {
		return new PortletCaching();
	}
}
