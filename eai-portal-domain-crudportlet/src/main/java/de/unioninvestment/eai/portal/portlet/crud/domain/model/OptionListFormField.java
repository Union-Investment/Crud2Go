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

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;

/**
 * Formular-Feld für Auswahl-Boxen.
 * 
 * @author max.hartmann
 * 
 */
public class OptionListFormField extends FormField {

	private static final long serialVersionUID = 1L;

	private OptionList optionList;

	private int visibleRows = 0;

	/**
	 * Konstruktor mit Paramtern.
	 * 
	 * @param config
	 *            Formular-Konfiguration
	 * @param optionList
	 *            Auswahl-Box-Modell
	 */
	public OptionListFormField(FormFieldConfig config, OptionList optionList) {
		super(config);
		this.optionList = optionList;
		if (config.getSelect() != null) {
			visibleRows = config.getSelect().getVisibleRows();
		}
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param config
	 *            FormFieldConfig
	 * @param optionList
	 *            Selection
	 * @param registerValueChangeListener
	 *            Ob der ValueChangeListener registriet werden soll.
	 */
	public OptionListFormField(FormFieldConfig config, OptionList optionList,
			boolean registerValueChangeListener) {
		super(config, registerValueChangeListener);
		this.optionList = optionList;
		if (config.getSelect() != null) {
			visibleRows = config.getSelect().getVisibleRows();
		}
	}

	public OptionListFormField(String name, String title,
			OptionList optionList, boolean isEditable) {
		super(name, title, null, isEditable);
		this.optionList = optionList;
	}

	public OptionList getOptionList() {
		return optionList;
	}
	
	public void setOptionList(OptionList optionList) {
		this.optionList = optionList;
	}

	public int getVisibleRows() {
		return visibleRows;
	}
}
