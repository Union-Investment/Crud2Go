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

import java.sql.Timestamp;
import java.text.Format;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.SelectSupport;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;
import de.unioninvestment.eai.portal.support.vaadin.support.FormattedTextField;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampFormatter;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampUtils;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Enthält die Anzeige-, Editier- und Filterlogik die spezifisch für
 * {@link Timestamp} Datentypen ist.
 * 
 * @author carsten.mjartan
 * 
 */
@org.springframework.stereotype.Component("sqlTimestampDataType")
public class SqlTimestampDataType implements DisplaySupport, EditorSupport,
		SelectSupport {

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsDisplaying(Class<?> clazz) {
		return Timestamp.class.isAssignableFrom(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public String formatPropertyValue(Property property, Format format) {
		if (property == null || property.getValue() == null) {
			return "";
		}
		Timestamp value = (Timestamp) property.getValue();

		return TimestampUtils.formatTimestamp(value);
	}

	@Override
	public boolean supportsEditing(Class<?> clazz) {
		return Timestamp.class.isAssignableFrom(clazz);
	}

	@Override
	public Field createField(Class<?> type, Object propertyId,
			boolean multiline, String inputPrompt, Format format) {

		TextField textField = new FormattedTextField(createFormatter(null,
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
		return new FormattedSelect(createFormatter(null, format));
	}

	@Override
	public PropertyFormatter createFormatter(Class<?> type, Format format) {
		return new TimestampFormatter();
	}

}
