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

import com.vaadin.data.util.PropertyFormatter;

/**
 * Formatierung und Parsing von SQL Timestamps. Wird als Wrapper um eine
 * entsprechende PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
@SuppressWarnings("unchecked")
@org.springframework.stereotype.Component
public class TimestampFormatter extends PropertyFormatter {

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

	/**
	 * Formatiert Timestamp Werte in deutscher Lokalisierung, für andere
	 * Datentypen wird toString() aufgerufen.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String format(Object value) {
		if (value instanceof Timestamp) {
			if (format != null) {
				return format.format(value);
			} else {
				return TimestampUtils.formatTimestamp((Timestamp) value);
			}
		} else if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

	/**
	 * Parst deutsch lokalisierte Timestamps und konvertiert in
	 * java.sql.Timestamp.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object parse(String formattedValue) throws Exception {
		if (formattedValue == null || formattedValue.trim().length() == 0) {
			return null;
		} else if (format != null) {
			return new Timestamp(format.parse(formattedValue).getTime());
		} else {
			return TimestampUtils.parseTimestamp(formattedValue);
		}
	}
}
