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
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;

/**
 * 
 * Repräsentiert ein Formularfeld im Formular.
 * 
 * @author siva.selvarajah
 */
public class ScriptFormField {

	protected FormField formField;
	protected ScriptForm scriptForm;

	protected Closure<?> onChange;

	/**
	 * Initialisierung der Event Handler.
	 * 
	 * @param formField
	 *            das zugehörige FormField
	 */
	public ScriptFormField(FormField formField) {
		this.formField = formField;
		formField
				.addFormFieldChangeEventListener(new FormFieldChangeEventHandler() {

					private static final long serialVersionUID = 42L;

					@Override
					public void onValueChange(FormFieldChangeEvent event) {
						if (onChange != null) {
							onChange.call(ScriptFormField.this);
						}
					}
				});
	}

	/**
	 * 
	 * @return Name
	 */
	public String getName() {
		return formField.getName();
	}

	/**
	 * 
	 * @return Titel
	 */
	public String getTitle() {
		return formField.getTitle();
	}

	/**
	 * 
	 * @return Eingabeaufforderung
	 */
	public String getInputPrompt() {
		return formField.getInputPrompt();
	}

	/**
	 * 
	 * @return Konfigurierter Standardwert
	 */
	public String getDefault() {
		return formField.getDefault();
	}

	/**
	 * Setzt den Eingabewert.
	 * 
	 * @param value
	 *            Neuer Wert
	 * 
	 */
	public void setValue(String value) {
		this.formField.setValue(value);
	}

	/**
	 * 
	 * @return Aktueller Wert des Formularfelds
	 */
	public String getValue() {
		return formField.getValue();
	}

	/**
	 * Schreibt in das Formularfeld den Standardwert.
	 */
	public void reset() {
		formField.reset();
	}

	void setForm(ScriptForm scriptForm) {
		this.scriptForm = scriptForm;
	}

	/**
	 * 
	 * @return Das Formular, in der sich das Formularfeld sich befindet.
	 */
	public ScriptForm getForm() {
		return scriptForm;
	}

	/**
	 * 
	 * @return Closure, das beim Ändern des Eingabewerts ausgeführt wird.
	 */
	public Closure<?> getOnChange() {
		return onChange;
	}

	/**
	 * 
	 * @param onChange
	 *            Closure, das beim Ändern des Eingabewerts ausgeführt werden
	 *            soll.
	 */
	public void setOnChange(Closure<?> onChange) {
		this.onChange = onChange;
	}
}
