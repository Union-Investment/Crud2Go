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
import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;
import de.unioninvestment.eai.portal.support.vaadin.support.ChainedConverter;
import de.unioninvestment.eai.portal.support.vaadin.support.CheckboxPropertyConverter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Basisklasse für Datentypen, die vom Portal dargestellt werden.
 */
public abstract class AbstractDataType implements DisplaySupport {

	public AbstractDataType() {
		//
	}

	@Override
	public Field<?> createField(Class<?> type, Object propertyId,
			boolean multiline, String inputPrompt, Format format) {

		AbstractTextField textField;
		if (multiline) {
			TextArea textArea = new TextArea();
			textArea.setRows(1);
			textField = textArea;
		} else {
			textField = new TextField();
		}
		textField.setConverter(createFormatter(type, format));
		textField.setReadOnly(isReadonly());
		textField.setNullRepresentation("");
		textField.setNullSettingAllowed(true);
		if (inputPrompt != null) {
			textField.setInputPrompt(inputPrompt);
		}
		return textField;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected AbstractSelect createSelect(Class<?> type, Object propertyId,
			Format format) {
		AbstractSelect select = new ComboBox();
		select.setConverter((Converter) createFormatter(type, format));
		select.setReadOnly(isReadonly());
		return select;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected CheckBox createCheckBox(Class<?> type, String checkedValue,
			String uncheckedValue, Format format) {
		CheckBox checkBox = new CheckBox();
		checkBox.setReadOnly(isReadonly());

		CheckboxPropertyConverter checkboxConverter = new CheckboxPropertyConverter(
				checkedValue, uncheckedValue);
		Converter<String, ?> formatter = createFormatter(type, format);
		if (formatter != null) {
			ChainedConverter<Boolean, String, ?> converter = new ChainedConverter(
					checkboxConverter, formatter);
			checkBox.setConverter(converter);
		} else {
			checkBox.setConverter(checkboxConverter);
		}

		return checkBox;
	}

	protected PopupDateField createDatePicker(Class<?> type, Object propertyId,
			String inputPrompt, String format) {
		int resolution = DateUtils.getResolution(format);
		PopupDateField field = new PopupDateField();

		Converter<Date, ?> converter = createDateConverter(type, format);
		field.setConverter(converter);

		field.setInputPrompt(inputPrompt);
		if (format != null) {
			field.setDateFormat(format);
			field.setResolution(DateUtils.getVaadinResolution(resolution));
		}
		return field;
	}

	@Override
	@Deprecated
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String formatPropertyValue(Property property, Format format) {
		if (property == null || property.getValue() == null) {
			return "";
		}
		Converter<String, Object> nf = (Converter<String, Object>) createFormatter(
				property.getType(), format);
		if (nf == null) {
			return property.getValue().toString();
		} else {
			return nf.convertToPresentation(property.getValue(), String.class,
					Context.getLocale());
		}
	}

	abstract boolean isReadonly();

	@Override
	public Converter<String, ?> createFormatter(Class<?> type, Format format) {
		return null;
	}

	public Converter<Date, ?> createDateConverter(Class<?> type,
			String simpleDateFormat) {
		return null;
	}

}