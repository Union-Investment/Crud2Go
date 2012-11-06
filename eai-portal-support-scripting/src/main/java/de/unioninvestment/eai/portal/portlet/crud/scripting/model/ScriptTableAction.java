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
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;

/**
 * 
 * Repräsentiert ein Tabellenbutton.
 * 
 * @author siva.selvarajah
 */
public class ScriptTableAction {

	private ScriptTable scriptTable;
	private final TableAction action;

	private Closure<?> onExecution;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param action
	 *            Button unterhalb der Tabelle
	 */
	ScriptTableAction(TableAction action) {
		this.action = action;
		action.addExecutionEventListener(new ExecutionEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onExecution(ExecutionEvent event) {
				if (onExecution != null) {
					onExecution.call(ScriptTableAction.this);
				}
			}
		});
	}

	public String getId() {
		return action.getId();
	}

	public String getTitle() {
		return action.getTitle();
	}

	/**
	 * Führt den Closure aus.
	 * 
	 * @see ScriptTableAction#onExecution
	 */
	public void execute() {
		action.execute();
	}

	/**
	 * Gibt dem Button zugeordnette die Tabelle zurück.
	 * 
	 * @return Übergeordnete Tabelle
	 */
	public ScriptTable getTable() {
		return scriptTable;
	}

	/**
	 * 
	 * @return Closure, das beim Betätigen des Tabellenbuttons ausgeführt wird.
	 */
	public Closure<?> getOnExecution() {
		return onExecution;
	}

	/**
	 * 
	 * @param onExecution
	 *            Closure, das beim Betätigen des Tabellenbuttons ausgeführt
	 *            werden soll.
	 */
	public void setOnExecution(Closure<?> onExecution) {
		this.onExecution = onExecution;
	}

	void setTable(ScriptTable scriptTable) {
		this.scriptTable = scriptTable;
	}

}
