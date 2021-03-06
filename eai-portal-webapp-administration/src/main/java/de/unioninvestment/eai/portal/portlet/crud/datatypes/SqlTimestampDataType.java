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
import java.text.DateFormat;
import java.text.Format;
import java.util.Date;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.PopupDateField;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.DatePickerSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.SelectSupport;
import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;
import de.unioninvestment.eai.portal.support.vaadin.support.DateToTimestampConverter;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampFormatter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Enthält die Anzeige-, Editier- und Filterlogik die spezifisch für
 * {@link Timestamp} Datentypen ist.
 * 
 * @author carsten.mjartan
 * 
 */
@org.springframework.stereotype.Component("sqlTimestampDataType")
public class SqlTimestampDataType extends AbstractDataType implements
		DisplaySupport, EditorSupport, SelectSupport, DatePickerSupport {

	@Override
	public boolean supportsDisplaying(Class<?> clazz) {
		return Timestamp.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean supportsEditing(Class<?> clazz) {
		return Timestamp.class.isAssignableFrom(clazz);
	}

	@Override
	public AbstractSelect createSelect(Class<?> type, Object propertyId,
			Format format) {
		return super.createSelect(type, propertyId, format);
	}

	@Override
	public PopupDateField createDatePicker(Class<?> type, Object propertyId,
			String inputPrompt, String format) {
		return super.createDatePicker(type, propertyId, inputPrompt, format);
	}

	public Converter<Date, ?> createDateConverter(Class<?> type,
			String simpleDateFormat) {
		int resolution = DateUtils.getResolution(simpleDateFormat);
		DateToTimestampConverter converter = new DateToTimestampConverter(
				resolution);
		return converter;
	}

	@Override
	public Converter<String, ?> createFormatter(Class<?> type, Format format) {
		return new TimestampFormatter((DateFormat) format);
	}

	@Override
	boolean isReadonly() {
		return false;
	}

}
