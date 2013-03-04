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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;

/**
 * Converts for ReST-Format attributes to match the configured datatypes.
 * 
 * @author carsten.mjartan
 */
public class ValueConverter {

	/**
	 * @param targetClass
	 *            the class to convert the value into
	 * @param format
	 *            an optional format
	 * @param locale
	 *            the locale
	 * @param value
	 *            the source value
	 * @return the converted value as instance of the targetClass or
	 *         <code>null</code>
	 */
	public Object convertValue(Class<?> targetClass, String format,
			Locale locale, Object value) {
		if (value == null) {
			return null;

		} else if (isCompatibleType(targetClass, value)) {
			return value;

		} else if (targetClass == String.class) {
			return value.toString();

		} else if (targetClass == Date.class) {
			return convertValueToDate(format, locale, value);

		} else {
			return value;
		}
	}

	private boolean isCompatibleType(Class<?> targetClass, Object value) {
		return targetClass.isAssignableFrom(value.getClass());
	}

	private Object convertValueToDate(String format, Locale locale, Object value) {
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());

		} else if (value instanceof String) {
			return convertStringToDate(format, locale, (String) value);

		} else {
			throw new IllegalArgumentException("Cannot convert to date: "
					+ value);
		}
	}

	private Object convertStringToDate(String format, Locale locale,
			String value) {
		try {
			if (StringUtils.isBlank(value)) {
				return null;
			} else if (StringUtils.equalsIgnoreCase(format, "iso8601")) {
				Calendar calendar = DatatypeConverter.parseDateTime(value);
				return calendar.getTime();
			} else {
				// SimpleDateFormat
				return new SimpleDateFormat(format, locale).parse(value);
			}

		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"Cannot convert to date: "
							+ value, e);
		}
	}
}
