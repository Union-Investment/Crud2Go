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

import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.config.CheckboxConfig;
import de.unioninvestment.eai.portal.support.vaadin.support.CheckboxPropertyConverter;

/**
 * 
 * Checkbox Modelobjekt für die Tabelleneditierung.
 * 
 * @author markus.bonsch
 * 
 */
public class CheckBoxTableColumn {

	private CheckboxConfig checkboxConfig;

	private ObjectProperty<String> property = new ObjectProperty<String>("");

	private CheckboxPropertyConverter checkboxProperty;

	/**
	 * Konstruktor.
	 * 
	 * @param checkboxConfig
	 *            CheckboxConfig-Model
	 */
	public CheckBoxTableColumn(CheckboxConfig checkboxConfig) {
		this.checkboxConfig = checkboxConfig;
		checkboxProperty = new CheckboxPropertyConverter(property,
				checkboxConfig.getCheckedValue(),
				checkboxConfig.getUncheckedValue());
	}

	/**
	 * @return die Property zur Befüllung der Checkbox
	 */
	public CheckboxPropertyConverter getCheckboxProperty() {
		return checkboxProperty;
	}

	/**
	 * @return den Wert für den Checked-Zustand
	 */
	public String getCheckedValue() {
		return checkboxConfig.getCheckedValue();
	}

	/**
	 * @return den Wert für den Unchecked-Zustand
	 */
	public String getUncheckedValue() {
		return checkboxConfig.getUncheckedValue();
	}

}
