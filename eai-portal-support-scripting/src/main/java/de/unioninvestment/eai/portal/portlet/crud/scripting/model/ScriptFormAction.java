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
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;

/**
 * 
 * Repräsenriert ein Formularbutton im Formular.
 * 
 * @author siva.selvarajah
 */
public class ScriptFormAction {
	private ScriptForm scriptForm;
	private Closure<?> onExecution;

	protected final FormAction action;

	/**
	 * @param action
	 *            die entsprechende Modell-Instanz
	 */
	ScriptFormAction(FormAction action) {
		this.action = action;
		action.addExecutionEventListener(new ExecutionEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onExecution(ExecutionEvent event) {
				if (onExecution != null) {
					onExecution.call(ScriptFormAction.this);
				}
			}
		});
	}

	public String getId() {
		return action.getId();
	}

	/**
	 * Führt das hinterlegte Closure aus.
	 * 
	 * @see ScriptFormAction#onExecution
	 */
	public void execute() {
		action.execute();
	}

	/**
	 * 
	 * @return Titel des Formularbuttons
	 */
	public String getTitle() {
		return action.getTitle();
	}

	/**
	 * @return Das Formular, in der sich das Formularbutton befindet.
	 */
	public ScriptForm getForm() {
		return scriptForm;
	}

	void setForm(ScriptForm scriptForm) {
		this.scriptForm = scriptForm;
	}

	/**
	 * 
	 * @return Closure, das beim Betätigen des Formularbuttons ausgeführt wird
	 */
	public Closure<?> getOnExecution() {
		return onExecution;
	}

	/**
	 * 
	 * @param onExecution
	 *            Closure, das beim Betätigen des Formularbuttons ausgeführt
	 *            werden soll
	 */
	public void setOnExecution(Closure<?> onExecution) {
		this.onExecution = onExecution;
	}

}
