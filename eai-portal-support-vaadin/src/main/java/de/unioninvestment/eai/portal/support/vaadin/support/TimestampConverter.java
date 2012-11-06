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
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

/**
 * Frontend-TO-Backendkonverter für Timestamps.
 * 
 * @author max.hartmann
 * 
 */
public class TimestampConverter extends PropertyConverter<Date> {

	private static final long serialVersionUID = 42L;
	private final int dateResolution;
	private SimpleDateFormat dateFormat;
	private String format;

	/**
	 * 
	 * Konstruktor mit Parametern.
	 * 
	 * @param backingProperty
	 *            Property des Backends
	 * @param dateResolution
	 *            Genauigkeit des Datums
	 * @param format
	 *            Formatierung des Datums
	 */
	public TimestampConverter(ObjectProperty<String> backingProperty,
			int dateResolution, String format) {
		super(backingProperty);
		this.dateResolution = dateResolution;
		this.format = format;
		this.dateFormat = new SimpleDateFormat(format);
	}

	@Override
	public Object convertToBackend(Date input) {
		if (input == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(input);
		configureTimestamp(cal);
		return this.dateFormat.format(new Timestamp(cal.getTimeInMillis()));
	}

	/**
	 * Nullt je nach dateResolution Zeit-/Datumswerte.
	 * 
	 * @param cal
	 *            Calendar
	 */
	void configureTimestamp(Calendar cal) {
		if (dateResolution <= Calendar.YEAR) {
			cal.set(Calendar.MONTH, 0);
		}
		if (dateResolution <= Calendar.MONTH) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}
		if (dateResolution <= Calendar.HOUR_OF_DAY) {
			cal.set(Calendar.MINUTE, 0);
		}
		if (dateResolution <= Calendar.MINUTE) {
			cal.set(Calendar.SECOND, 0);
		}
		if (dateResolution <= Calendar.SECOND) {
			cal.set(Calendar.MILLISECOND, 0);
		}
	}

	@Override
	public Object convertToFrontend(String input) {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		try {
			return this.dateFormat.parse(input);
		} catch (ParseException e) {
			throw new Property.ConversionException(MessageFormat.format(
					"Bitte geben Sie einen Zeitpunkt im Format ''{0}'' ein.",
					this.format));
		}
	}

}
