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
package de.unioninvestment.eai.portal.support.vaadin;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Component;

/**
 * Abstrakte Oberklasse für Vaadin {@link Application} Klassen. Merkt sich in
 * einem {@link ThreadLocal} jeweils die aktuellen {@link PortletRequest} und
 * {@link PortletResponse} Instanzen, die dann über {@link #getCurrentRequest()}
 * und {@link #getCurrentResponse()} abgefragt werden können.
 * 
 * @author carsten.mjartan
 * 
 */
public abstract class PortletApplication extends Application implements
		PortletRequestListener {

	private static final long serialVersionUID = 1L;

	private static ThreadLocal<PortletRequest> currentRequest = new ThreadLocal<PortletRequest>();
	private static ThreadLocal<PortletResponse> currentResponse = new ThreadLocal<PortletResponse>();
	private static ThreadLocal<Application> currentApplication = new ThreadLocal<Application>();

	/**
	 * {@inheritDoc}
	 */
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		currentRequest.set(request);
		currentResponse.set(response);
		currentApplication.set(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onRequestEnd(PortletRequest request, PortletResponse response) {
		currentRequest.remove();
		currentResponse.remove();
		currentApplication.remove();
	}

	/**
	 * @return die {@link PortletRequest}-Instanz des aktuellen Requests
	 */
	public static PortletRequest getCurrentRequest() {
		return currentRequest.get();
	}

	/**
	 * @return die {@link PortletResponse}-Instanz des aktuellen Requests
	 */
	public static PortletResponse getCurrentResponse() {
		return currentResponse.get();
	}

	/**
	 * Fügt der Ansicht (dem ViewPanel) eine Komponente hinzu (z. B. Popup,
	 * ContextMenu)
	 * 
	 * @param component
	 *            eine Vaadin-Komponente
	 */
	public abstract void addToView(Component component);

	/**
	 * Entfernt alle über {@link #addToView(Component)} hinzugefügten
	 * Komponenten
	 */
	public abstract void removeAddedComponentsFromView();

	/**
	 * @return die {@link Application}-Instanz des Crudportlets
	 * @return
	 */
	public static Application getCurrentApplication() {
		return currentApplication.get();
	}

	/**
	 * Für Export-Threads...
	 * 
	 * @param application
	 *            das für den Thread aktuelle Application-Objekt
	 */
	public static void setCurrentApplication(Application application) {
		currentApplication.set(application);
	}
}
