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
package de.unioninvestment.eai.portal.support.vaadin.support;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.CheckBox;

/**
 * Erweiterung der Vaadin Checkbox die gleichzeitig eine Konvertierung gegebener
 * Checked-/Unchecked-Werte in true/false vornimmt.
 * 
 * @author bastian.spanneberg
 * @author markus.bonsch
 * 
 */
public class FormattedConvertedCheckBox extends CheckBox {

	private static final long serialVersionUID = 1L;

	private PropertyFormatter formatter;

	private final String checkedValue;

	private final String uncheckedValue;

	/**
	 * Konstruktor.
	 * 
	 * @param formatter
	 *            der Formatter der für die spätere DataSource verwendet werden
	 *            soll
	 * @param checkedValue
	 *            Wert für den Checked-Zustand
	 * @param uncheckedValue
	 *            Wert für den Unchecked-Zustand
	 */
	public FormattedConvertedCheckBox(PropertyFormatter formatter,
			String checkedValue, String uncheckedValue) {
		super();
		this.formatter = formatter;
		this.checkedValue = checkedValue;
		this.uncheckedValue = uncheckedValue;
		this.setInvalidAllowed(false);
	}

	/**
	 * Übergibt die DataSource an den Formatter.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void setPropertyDataSource(Property newDataSource) {

		PropertyConverter<Boolean> converter;
		if (formatter != null) {
			converter = new CheckboxPropertyConverter(formatter, checkedValue,
					uncheckedValue);
			formatter.setPropertyDataSource(newDataSource);
		} else {
			converter = new CheckboxPropertyConverter(newDataSource,
					checkedValue, uncheckedValue);

		}
		super.setPropertyDataSource(converter);
	}

}
