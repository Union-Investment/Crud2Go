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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Formatierung und Parsing von SQL Timestamps. Wird als Wrapper um eine
 * entsprechende PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
@org.springframework.stereotype.Component
public class TimestampFormatter implements Converter<String, Timestamp> {

	private static final long serialVersionUID = 1L;
	private DateFormat format;

	/**
	 * Leerer Konstruktor.
	 * 
	 * @param format
	 */
	public TimestampFormatter(DateFormat format) {
		this.format = format;
	}

	@Override
	public Timestamp convertToModel(String value,
			Class<? extends Timestamp> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (value == null || value.trim().length() == 0) {
			return null;
		} else if (format != null) {
			try {
				return new Timestamp(format.parse(value).getTime());

			} catch (ParseException e) {
				throw new ConversionException(
						"Error converting String to Timestamp", e);
			}
		} else {
			return TimestampUtils.parseTimestamp(value);
		}
	}

	/**
	 * Formatiert Timestamp Werte in deutscher Lokalisierung, für andere
	 * Datentypen wird toString() aufgerufen.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String convertToPresentation(Timestamp value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (value != null) {
			if (format != null) {
				return format.format(value);
			} else {
				return TimestampUtils.formatTimestamp((Timestamp) value);
			}
		} else {
			return null;
		}
	}

	@Override
	public Class<Timestamp> getModelType() {
		return Timestamp.class;
	}

	public java.lang.Class<String> getPresentationType() {
		return String.class;
	}

}
