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

import de.unioninvestment.eai.portal.portlet.crud.domain.events.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import groovy.lang.Closure;

import java.util.*;

import static java.util.Collections.unmodifiableMap;

/**
 * Repräsentation das Portlets.
 * 
 * @author siva.selvarajah
 */
public class ScriptPortlet {

	private Portlet portlet;
	private ScriptPanel scriptPage;
	private ScriptTabs scriptTabs;
	private List<ScriptDialog> scriptDialogs = new ArrayList<ScriptDialog>();
	@SuppressWarnings("unused")
	private ScriptUser currentUser;
	private Map<String, Object> elementsById = new HashMap<String, Object>();

	private Closure<?> onLoad;
	private Closure<?> onReload;
	private Closure<?> onRefresh;
    private boolean inTest = false;

    /**
	 * Konstruktor mit Parameter.
	 * 
	 * @param portlet
	 *            Portlet
	 */
	/**
	 * @param portlet
	 */
	ScriptPortlet(Portlet portlet, boolean inTest) {
		this.portlet = portlet;
		addEventHandlerForOnRefresh();
		addEventHandlerForOnLoad();
		addEventHandlerForOnReload();
        this.inTest = inTest;
	}

	private void addEventHandlerForOnLoad() {
		this.portlet.addLoadHandler(new PortletLoadedEventHandler() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onPortletLoad(PortletLoadedEvent event) {
                if (onLoad != null) {
                    onLoad.call(ScriptPortlet.this);
                }
            }
        });
	}

	private void addEventHandlerForOnReload() {
		this.portlet.addReloadHandler(new PortletReloadedEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPortletReload(PortletReloadedEvent event) {
				if (onReload != null) {
					onReload.call(ScriptPortlet.this);
				}
			}
		});
	}

	private void addEventHandlerForOnRefresh() {
		this.portlet.addRefreshHandler(new PortletRefreshedEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPortletRefresh(PortletRefreshedEvent event) {
				if (onRefresh != null) {
					onRefresh.call(ScriptPortlet.this);
				}
			}
		});
	}

	/**
	 * @return Seite
	 */
	public ScriptPanel getPage() {
		return scriptPage;
	}

	/**
	 * @return Alle dem Portlet direkt untergeordneten Tabs
	 */
	public ScriptTabs getTabs() {
		return scriptTabs;
	}

	List<ScriptDialog> getDialogs() {
		return scriptDialogs;
	}

	void addDialog(ScriptDialog scriptDialog) {
		scriptDialogs.add(scriptDialog);
	}

	/**
	 * @return Titel
	 */
	public String getTitle() {
		return portlet.getTitle();
	}

	/**
	 * Fügt eine Seite dem Portlet hinzu.
	 * 
	 * @param scriptPage
	 *            Seite
	 */
	void setPage(ScriptPage scriptPage) {
		this.scriptPage = scriptPage;
		scriptPage.setPortlet(this);
	}

	/**
	 * Fügt eine Liste von Reitern dem Portlet hinzu.
	 * 
	 * @param scriptTabs
	 *            Reiterliste
	 */
	void setTabs(ScriptTabs scriptTabs) {
		this.scriptTabs = scriptTabs;
		scriptTabs.setPortlet(this);
	}

	/**
	 * @param id
	 *            die ID der Komponente
	 * @return die entsprechende Script-Komponente
	 */
	public Object getElementById(String id) {
		Object result = elementsById.get(id);
		if (result == null) {
			throw new NoSuchElementException(
					"Unknown Component id '"
							+ id
							+ "'. The element does not exist or is excluded from build by permissions");
		}
		return result;
	}

	/**
	 * Fügt ein Element hinzu.
	 * 
	 * @param id
	 *            Id des Elements
	 * @param scriptElement
	 *            Element
	 */
	void addElementById(String id, Object scriptElement) {
		this.elementsById.put(id, scriptElement);
	}

	/**
	 * @param onReload
	 *            Closure mit dem Parameter { ScriptPortlet it -> ... }, die
	 *            nach dem Reload des Portlets aufgerufen wird
	 */
	public void setOnReload(Closure<?> onReload) {
		this.onReload = onReload;
	}
    /**
     * @param onLoad
     *            Closure mit dem Parameter { ScriptPortlet it -> ... }, die
     *            nach dem Laden des Portlets aufgerufen wird
     */
    public void setOnLoad(Closure<?> onLoad) {
        this.onLoad = onLoad;
    }

	/**
	 * @param onRefresh
	 *            Closure mit dem Parameter { ScriptPortlet it -> ... }, die
	 *            nach dem Refresh des Portlets aufgerufen wird
	 */
	public void setOnRefresh(Closure<?> onRefresh) {
		this.onRefresh = onRefresh;
	}

	/**
	 * @return Closure mit dem Parameter { ScriptPortlet it -> ... }, die nach
	 *         dem Reload des Portlets aufgerufen wird
	 */
	public Closure<?> getOnReload() {
		return onReload;
	}

	/**
	 * @return Closure mit dem Parameter { ScriptPortlet it -> ... }, die nach
	 *         dem Laden des Portlets aufgerufen wird
	 */
	public Closure<?> getOnLoad() {
		return onLoad;
	}

	/**
	 * @return Closure mit dem Parameter { ScriptPortlet it -> ... }, die nach
	 *         dem Refresh des Portlets aufgerufen wird
	 */
	public Closure<?> getOnRefresh() {
		return onRefresh;
	}

	/**
	 * Triggers a refresh of all components of the portlet
	 */
	public void refresh() {
		portlet.refresh();
	}

    public boolean isInTest() {
        return inTest;
    }

	public Map<String, Object> getElements() {
        return inTest ? elementsById : unmodifiableMap(elementsById);
	}

	/**
	 * @return Preferences as Map
	 */
	public Map<String, String> getPreferences() {
		return new ScriptPreferences(portlet.getPreferences());
	}
	
	/**
	 * @return Zugriff auf Caching-Mechanismen
	 */
	public ScriptPortletCaching getCaching() {
		return new ScriptPortletCaching(portlet.getCaching());
	}
}
