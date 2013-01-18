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

import groovy.lang.Closure;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.HideEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;

/**
 * 
 * Repräsentiert ein Tab.
 * 
 * @author siva.selvarajah
 */
public class ScriptTab extends ScriptPanel {

	private final Tab tab;
	private ScriptTabs scriptTabs;

	private Closure<?> onShow;
	private Closure<?> onHide;

	/**
	 * @param tab
	 *            der Tab aus dem Domain-Modell
	 */
	ScriptTab(Tab tab) {
		super(tab);
		this.tab = tab;
		registerEventListeners();
	}

	private void registerEventListeners() {
		tab.addShowEventListener(new ShowEventHandler<Tab>() {

			private static final long serialVersionUID = 42L;

			@Override
			public void onShow(ShowEvent<Tab> event) {
				if (onShow != null) {
					onShow.call(ScriptTab.this);
				}
			}

		});
		tab.addHideEventListener(new HideEventHandler<Tab>() {

			private static final long serialVersionUID = 42L;

			@Override
			public void onHide(HideEvent<Tab> event) {
				if (onHide != null) {
					onHide.call(ScriptTab.this);
				}
			}

		});
	}

	/**
	 * 
	 * @return Id des Tabs
	 */
	public String getId() {
		return tab.getId();
	}

	/**
	 * 
	 * @return Angezeigter Titel
	 */
	public String getTitle() {
		return tab.getTitle();
	}

	/**
	 * 
	 * @return Übergeordnete Tabs-Liste, in der sich der Tab befindet.
	 */
	public ScriptTabs getTabs() {
		return scriptTabs;
	}

	/**
	 * Diese Methode wird beim Build aufgerufen.
	 * 
	 * @param scriptTabs
	 *            Übergeordnete Tabs-Liste
	 */
	void setTabs(ScriptTabs scriptTabs) {
		this.scriptTabs = scriptTabs;
	}

	/**
	 * 
	 * @return Closure, das beim Einblenden des Tabs ausgeführt wird.
	 */
	public Closure<?> getOnShow() {
		return onShow;
	}

	/**
	 * 
	 * @param onShow
	 *            Closure, das beim Einblenden des Tabs ausgeführt werden soll.
	 *            Im Parameter 'it' wird die aktuelle {@link ScriptTab}-Instanz
	 *            übergeben
	 */
	public void setOnShow(Closure<?> onShow) {
		this.onShow = onShow;
	}

	/**
	 * 
	 * @return Closure, das beim Ausblenden des Tabs ausgeführt wird.
	 */
	public Closure<?> getOnHide() {
		return onHide;
	}

	/**
	 * 
	 * @param onHide
	 *            Closure, das beim Ausblenden des Tabs ausgeführt werden soll.
	 *            Im Parameter 'it' wird die aktuelle {@link ScriptTab}-Instanz
	 *            übergeben
	 */
	public void setOnHide(Closure<?> onHide) {
		this.onHide = onHide;
	}
}
