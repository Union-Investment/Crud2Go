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

import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;

/**
 * Datenmodell für einen Aktionsbutton im Formular.
 * 
 * @author carsten.mjartan
 */
public class FormAction extends AbstractAction {

	/**
	 * Interface die Logik, die bei bei Start einer Aktion ausgeführt werden
	 * soll.
	 * 
	 * @author carsten.mjartan
	 */
	public interface ActionHandler {

		/**
		 * @param form
		 *            das aktuelle Formular.
		 */
		void execute(Form form);
	}

	private static final long serialVersionUID = 1L;

	private boolean hidden = false;

	private final ActionHandler actionHandler;

	private Form form;

	/**
	 * 
	 * Konstruktor mit Parameter.
	 * 
	 * @param portlet
	 *            Portletmodel
	 * @param config
	 *            FormAction-Config-Model
	 * @param actionHandler
	 *            ActionHandler
	 * @param form
	 *            das Formular an dem die Action "hängt"
	 * @param triggers
	 *            Triggerliste
	 */
	public FormAction(Portlet portlet, FormActionConfig config,
			ActionHandler actionHandler, Triggers triggers) {
		super(portlet, config, triggers);

		this.actionHandler = actionHandler;
	}

	/**
	 * @return der Action Handler.
	 */
	public ActionHandler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void execute() {
		if (actionHandler != null) {
			actionHandler.execute(this.form);
		}
		fireExecutionEvent();

		super.execute();
	}

	/**
	 * @return das Form Objekt.
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * @return <code>true</code> wenn die Aktion nicht angezeigt werden soll.
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * Nur für die Build-Zeit relevant.
	 * 
	 * @param hidden
	 *            <code>true</code> wenn die Aktion nicht angezeigt werden soll.
	 */
	void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	void setForm(Form form) {
		this.form = form;

	}
}
