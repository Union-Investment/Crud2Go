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
import static java.util.Collections.unmodifiableMap;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;

/**
 * 
 * Repräsenriert ein Formular.
 * 
 * @author siva.selvarajah
 */
public class ScriptForm extends ScriptComponent {

	@SuppressWarnings("unused")
	private final Form form;
	private Map<String, ScriptFormField> fields = new LinkedHashMap<String, ScriptFormField>();
	private List<ScriptFormAction> actions = new LinkedList<ScriptFormAction>();

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param form
	 *            Formular
	 */
	ScriptForm(Form form) {
		this.form = form;
	}

	/**
	 * 
	 * @return Alle Formularfelder im Formular
	 */
	public Map<String, ScriptFormField> getFields() {
		return unmodifiableMap(fields);
	}

	/**
	 * 
	 * @return Alle Formularbuttons im Formular
	 */
	public List<ScriptFormAction> getActions() {
		return unmodifiableList(actions);
	}

	/**
	 * Fügt ein Formularfeld hinzu.
	 * 
	 * @param field
	 *            Formularfeld
	 */
	void addField(ScriptFormField field) {
		fields.put(field.getName(), field);
		field.setForm(this);
	}

	/**
	 * Fügt ein Formular-Button hinzu.
	 * 
	 * @param action
	 *            Formular-Button
	 */
	void addAction(ScriptFormAction action) {
		actions.add(action);
		action.setForm(this);
	}
}
