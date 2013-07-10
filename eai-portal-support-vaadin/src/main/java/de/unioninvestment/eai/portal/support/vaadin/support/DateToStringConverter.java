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

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.converter.Converter;

import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;

/**
 * Frontend-TO-Backendkonverter für Timestamps.
 * 
 * @author max.hartmann
 * 
 */
public class DateToStringConverter implements Converter<Date, String> {

	private static final long serialVersionUID = 42L;
	private final int dateResolution;
	private SimpleDateFormat dateFormat;
	private String format;

	/**
	 * 
	 * Konstruktor mit Parametern.
	 * 
	 * @param dateResolution
	 *            Genauigkeit des Datums
	 * @param format
	 *            Formatierung des Datums
	 */
	public DateToStringConverter(int dateResolution, String format) {
		this.dateResolution = dateResolution;
		this.format = format;
		this.dateFormat = new SimpleDateFormat(format);
	}

	public String convertToModel(Date value,
			java.lang.Class<? extends String> targetType,
			java.util.Locale locale) throws Converter.ConversionException {
		if (value == null) {
			return null;
		}
		Date cleanedDate = DateUtils.cleanup(value, dateResolution);
		return this.dateFormat.format(cleanedDate);
	}

	public Date convertToPresentation(String value,
			java.lang.Class<? extends Date> targetType, java.util.Locale locale)
			throws Converter.ConversionException {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return this.dateFormat.parse(value);

		} catch (ParseException e) {
			throw new ConversionException(MessageFormat.format(
					"Bitte geben Sie einen Zeitpunkt im Format ''{0}'' ein.",
					this.format));
		}
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return Date.class;
	}
}
