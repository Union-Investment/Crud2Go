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
import java.text.NumberFormat;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.CheckBoxSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.SelectSupport;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedConvertedCheckBox;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedTextField;
import de.unioninvestment.eai.portal.support.vaadin.support.NumberFormatter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Enthält die Anzeige-, Editor- und Filterlogik die spezifisch für Number
 * Datentypen ist.
 * 
 * @author carsten.mjartan
 * 
 */
@org.springframework.stereotype.Component("numberDataType")
public class NumberDataType implements DisplaySupport, EditorSupport,
		SelectSupport, CheckBoxSupport {

	@Override
	public boolean supportsDisplaying(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

	@Override
	public String formatPropertyValue(Property property, Format format) {
		if (property == null || property.getValue() == null) {
			return "";
		}
		NumberFormatter nf = createFormatter(property.getType(),
				(NumberFormat) format);
		nf.setPropertyDataSource(property);

		return nf.toString();
	}

	@Override
	public boolean supportsEditing(Class<?> clazz) {
		return Number.class.isAssignableFrom(clazz);
	}

	@Override
	public Field createField(Class<?> type, Object propertyId,
			boolean multiline, String inputPrompt, Format format) {

		TextField textField = new FormattedTextField(createFormatter(type,
				format));
		textField.setNullRepresentation("");
		textField.setNullSettingAllowed(true);
		if (inputPrompt != null) {
			textField.setInputPrompt(inputPrompt);
		}
		return textField;
	}

	@Override
	public AbstractSelect createSelect(Class<?> type, Object propertyId,
			Format format) {
		AbstractSelect textField = new FormattedSelect(createFormatter(type,
				format));
		return textField;
	}

	@Override
	public NumberFormatter createFormatter(Class<?> type, Format format) {
		@SuppressWarnings("unchecked")
		Class<? extends Number> propType = (Class<? extends Number>) type;

		return new NumberFormatter(propType, (NumberFormat) format);
	}

	@Override
	public CheckBox createCheckBox(Class<?> type, String checkedValue,
			String uncheckedValue, Format format) {
		@SuppressWarnings("unchecked")
		Class<? extends Number> propType = (Class<? extends Number>) type;
		return new FormattedConvertedCheckBox(new NumberFormatter(propType,
				(NumberFormat) format),
				checkedValue, uncheckedValue);
	}

}
