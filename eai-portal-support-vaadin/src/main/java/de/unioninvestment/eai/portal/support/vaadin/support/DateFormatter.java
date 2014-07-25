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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * PropertyFormatter für Date Datentypen.
 * 
 * @author markus.bonsch
 * 
 */
public class DateFormatter implements Converter<String, Date> {

	public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	private static final long serialVersionUID = 1L;
	private DateFormat format;

	/**
	 * Leerer Konstruktor.
	 * 
	 * @param format
	 */
	public DateFormatter(DateFormat format) {
		this.format = format;
	}

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType,
			Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (value == null || value.trim().length() == 0) {
			return null;
		} else {
			try {
				if (format != null) {
					return format.parse(value);
				} else {
					return new SimpleDateFormat(DEFAULT_DATE_FORMAT)
							.parse(value);
				}

			} catch (ParseException e) {
				throw new ConversionException(
						"Error converting String to Timestamp", e);
			}
		}
	}

	/**
	 * Formatiert Timestamp Werte in deutscher Lokalisierung, für andere
	 * Datentypen wird toString() aufgerufen.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String convertToPresentation(Date value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		if (value != null) {
			if (format != null) {
				return format.format(value);
			} else {
				return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(value);
			}
		} else {
			return null;
		}
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	public java.lang.Class<String> getPresentationType() {
		return String.class;
	}

}
