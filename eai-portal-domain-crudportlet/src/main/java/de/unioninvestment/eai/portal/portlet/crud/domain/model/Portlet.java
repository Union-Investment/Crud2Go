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

import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;

/**
 * Repräsentation das Portlets im Modell.
 * 
 * @author carsten.mjartan
 */
public class Portlet implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(Portlet.class);

	private Page page;
	private Tabs tabs;
	private Map<String, Object> elementsById = new HashMap<String, Object>();
	private Map<String, Tab> tabsById = new HashMap<String, Tab>();
	private Map<String, Dialog> dialogsById = new HashMap<String, Dialog>();
	private Map<String, Region> regionsById = new HashMap<String, Region>();
	private String title;
	@SuppressWarnings("unused")
	private final PortletConfig config;

	private Set<Role> roles = new HashSet<Role>();

	/**
	 * @param config
	 *            PortletConfig
	 */
	public Portlet(PortletConfig config) {
		this.config = config;
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
	 * @param role
	 *            Rolle
	 */
	void addRole(Role role) {
		this.roles.add(role);
	}

	public Set<Role> getRoles() {
		return unmodifiableSet(this.roles);
	}
}