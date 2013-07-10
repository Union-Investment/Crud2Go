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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TabChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * 
 * Spezielle Implementierung von {@link UriFragmentUtility}, die für die
 * Backbuttonfunktionalität zuständig ist.
 * 
 * @author siva.selvarajah
 */
@Configurable(preConstruction = true)
public class PortletUriFragmentUtility implements UriFragmentChangedListener {

	private static final long serialVersionUID = 42L;

	@Autowired
	private EventBus eventBus;

	private final Portlet model;

	private final String portletId;

	private static final String DELIM = ",";

	/**
	 * Nur für JUnit-Test.
	 * 
	 * @param eventBus
	 *            EventBus
	 * @param model
	 *            Portlet
	 * @param portletId
	 *            die Portlet-ID des Portlet Containers
	 */
	public PortletUriFragmentUtility(EventBus eventBus, Portlet model,
			String portletId) {
		this.eventBus = eventBus;
		this.model = model;
		this.portletId = portletId;
	}

	/**
	 * Konstuktor.
	 * 
	 * @param model
	 *            Portlet
	 * @param portletId
	 *            die Portlet-ID des Portlet Containers
	 */
	public PortletUriFragmentUtility(Portlet model, String portletId) {
		this.model = model;
		this.portletId = portletId;
		initialize();
	}

	/**
	 * Initialisiert die Listener, die auf die TabChange events reagieren.
	 */
	final void initialize() {
		eventBus.addHandler(TabChangeEvent.class, new TabChangeEventHandler() {

			private static final long serialVersionUID = 42L;

			@Override
			public void onTabChange(TabChangeEvent event) {
				String fragmentID = buildTabStatus();
				Page.getCurrent().setUriFragment(fragmentID, false);
			}
		});

		Page.getCurrent().addUriFragmentChangedListener(this);
	}

	/**
	 * Erzeugt ein Fragment-String mit allen aktivierten Tabs.
	 * 
	 * @return Fragment
	 */
	String buildTabStatus() {
		Map<String, Tab> tabRefMap = model.getTabsById();

		StringBuilder fragment = new StringBuilder();

		fragment.append(portletId).append(";");

		for (Tab tab : tabRefMap.values()) {
			Tabs tabs = tab.getTabs();

			if (tabs.getActiveTab().equals(tab)) {
				fragment.append(tab.getId());
				fragment.append(DELIM);
			}
		}

		return fragment.toString();
	}

	/**
	 * Aktiviert alle im Fagment enthaltenen Tabs.
	 * 
	 * @param fragment
	 *            Fragment
	 */
	void activateTabs(String fragment) {
		String id = this.portletId + ";";

		if (fragment != null && fragment.startsWith(id)) {
			String fragmentTeil = fragment.substring(id.length());

			StringTokenizer stringTokenizer = new StringTokenizer(fragmentTeil,
					DELIM);
			activateTab(stringTokenizer);
		}
	}

	/**
	 * Aktiviert alle im Fagment enthaltenen Tabs.
	 * 
	 * @param tokenizer
	 *            StringTokenizer
	 */
	void activateTab(StringTokenizer tokenizer) {
		Map<String, Tab> tabRefMap = model.getTabsById();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (tabRefMap.containsKey(token)) {
				Tab tab = tabRefMap.get(token);
				tab.getTabs().setActiveTabById(tab.getId());
			}
		}
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		String fragment = event.getUriFragment();
		if (StringUtils.isNotEmpty(fragment)) {
			activateTabs(fragment);
		}
	}

	/**
	 * Setzt initial per Javascript das Fragment mit den aktuell aktiven Tabs.
	 */
	public void setInitialFragment() {
		Page.getCurrent().setUriFragment(buildTabStatus());
	}
}
