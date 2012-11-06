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

import static java.util.Collections.unmodifiableMap;
import groovy.lang.Closure;

import java.util.LinkedHashMap;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;

/**
 * 
 * Repräsentiert eine Tableiste.
 * 
 * @author siva.selvarajah
 */
public class ScriptTabs extends ScriptComponent {

	private final Tabs tabs;
	private ScriptPortlet scriptPortlet;

	private Closure<?> onChange;

	private Map<String, ScriptTab> elements = new LinkedHashMap<String, ScriptTab>();

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param tabs
	 *            Reiter
	 */
	ScriptTabs(Tabs tabs) {
		this.tabs = tabs;
		tabs.addTabChangeEventListener(new TabChangeEventHandler() {

			private static final long serialVersionUID = 42L;

			@Override
			public void onTabChange(TabChangeEvent event) {
				if (onChange != null) {
					onChange.call(ScriptTabs.this);
				}
			}
		});
	}

	/**
	 * Gibt Reiterliste zurück.
	 * 
	 * @return Tabsliste
	 */
	public Map<String, ScriptTab> getElements() {
		return unmodifiableMap(elements);
	}

	/**
	 * @return Das Portlet, sofern das Tabs-Element direkt am Portlet hängt
	 */
	public ScriptPortlet getPortlet() {
		return scriptPortlet;
	}

	void setPortlet(ScriptPortlet scriptPortlet) {
		this.scriptPortlet = scriptPortlet;
	}

	/**
	 * Nur im Build-Prozess genutzt.
	 * 
	 * @param scriptTab
	 *            ein neues Tab-Element
	 */
	void addElement(ScriptTab scriptTab) {
		this.elements.put(scriptTab.getId(), scriptTab);
		scriptTab.setTabs(this);
	}

	/**
	 * 
	 * @return Aktiver Tab
	 */
	public ScriptTab getActiveTab() {
		return elements.get(this.tabs.getActiveTabId());
	}

	/**
	 * Setzt den aktiven Tab über dessen ID.
	 * 
	 * @param tabId
	 *            ID des Tabs
	 */
	public void setActiveTab(String tabId) {
		tabs.setActiveTabById(tabId);
	}

	/**
	 * 
	 * @return Closure, das beim Wechsel des aktiven Tabs ausgeführt wird.
	 */
	public Closure<?> getOnChange() {
		return onChange;
	}

	/**
	 * 
	 * @param onChange
	 *            Closure, das beim beim Wechsel des aktiven Tabs ausgeführt
	 *            werden soll.
	 */
	public void setOnChange(Closure<?> onChange) {
		this.onChange = onChange;
	}

}
