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

import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.support.vaadin.support.FormattedTextField;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;
import de.unioninvestment.eai.portal.support.vaadin.table.ReadonlyFormatter;

/**
 * Basisklasse für Datentypen, die vom Portal dargestellt werden.
 */
public abstract class AbstractDataType implements DisplaySupport {

	public AbstractDataType() {
		//
	}

	@Override
	public Field createField(Class<?> type, Object propertyId,
			boolean multiline, String inputPrompt, Format format) {

		if (multiline) {
			TextArea textField = new TextArea();
			textField.setNullRepresentation("");
			textField.setRows(1);
			textField.setNullSettingAllowed(true);
			if (inputPrompt != null) {
				textField.setInputPrompt(inputPrompt);
			}
			return textField;
		} else {
			TextField textField;
			PropertyFormatter formatter = createFormatter(type, format);
			if (formatter == null) {
				textField = new TextField();
			} else {
				textField = new FormattedTextField(formatter);
			}
			textField.setNullRepresentation("");
			textField.setNullSettingAllowed(true);
			if (inputPrompt != null) {
				textField.setInputPrompt(inputPrompt);
			}
			return textField;
		}
	}

	@Override
	public PropertyFormatter createFormatter(Class<?> type, Format format) {
		return new ReadonlyFormatter(this, format);
	}

}