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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;

/**
 * 
 * Repräsentiert ein Panel.
 * 
 * @author siva.selvarajah
 */
public class ScriptPanel extends ScriptComponent {

	private final Panel panel;

	@SuppressWarnings("unused")
	private ScriptPortlet scriptPortlet;
	private List<ScriptComponent> elements = new ArrayList<ScriptComponent>();

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param panel
	 *            Interne Panelmodel
	 */
	ScriptPanel(Panel panel) {
		this.panel = panel;
	}

	/**
	 * @return die Seitenelemente
	 */
	public List<ScriptComponent> getElements() {
		return unmodifiableList(elements);
	}

	void setPortlet(ScriptPortlet scriptPortlet) {
		this.scriptPortlet = scriptPortlet;
	}

	/**
	 * Fügt eine Komponente hinzu.
	 * 
	 * @param scriptComponent
	 *            Komponente
	 */
	void addElement(ScriptComponent scriptComponent) {
		elements.add(scriptComponent);
		scriptComponent.setPanel(this);
	}

	/**
	 * Ersetzt die Hauptseite oder eine beliebige Unterseite durch die
	 * Unterseite ({@code dialog}) mit der übergebenen ID. Falls diese
	 * Unterseite bereits angezeigt wird, hat der Aufruf keine Auswirkungen.
	 * 
	 * @param dialogId
	 *            die ID des dialog-Tags im XML
	 * @throws IllegalArgumentException
	 *             falls kein Dialog mit der gegebenen {@code dialogId}
	 *             existiert
	 */
	public void attachDialog(String dialogId) {
		panel.attachDialog(dialogId);
	}

	/**
	 * Ersetzt die Unterseite ({@code dialog}) durch die Hauptseite.
	 */
	public void detachDialog() {
		panel.detachDialog();
	}
}