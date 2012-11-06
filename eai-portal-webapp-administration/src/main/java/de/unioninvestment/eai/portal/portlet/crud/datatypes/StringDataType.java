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
package de.unioninvestment.eai.portal.portlet.crud.datatypes;

import java.text.Format;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Select;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.CheckBoxSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.SelectSupport;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedConvertedCheckBox;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Enthält die Anzeige-, Editier- und Filterlogik die spezifisch für String
 * Datentypen ist.
 * 
 * @author carsten.mjartan
 * 
 */
@org.springframework.stereotype.Component("stringDataType")
public class StringDataType extends AbstractDataType implements DisplaySupport,
		EditorSupport, SelectSupport, CheckBoxSupport {

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsDisplaying(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public String formatPropertyValue(Property property, Format format) {
		if (property == null || property.getValue() == null) {
			return "";
		}
		return property.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsEditing(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public AbstractSelect createSelect(Class<?> type, Object propertyId,
			Format format) {
		AbstractSelect textField = new Select();
		textField.setInvalidAllowed(false);
		return textField;
	}

	@Override
	public CheckBox createCheckBox(Class<?> type, String checkedValue,
			String uncheckedValue, Format format) {

		return new FormattedConvertedCheckBox(null, checkedValue,
				uncheckedValue);
	}

	@Override
	public PropertyFormatter createFormatter(Class<?> type, Format format) {
		return null;
	}

}
