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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.number.NumberFormatter;

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

		} else if (Number.class.isAssignableFrom(targetClass)) {
			return convertValueToNumber(targetClass, format, locale, value);

		} else {
			return value;
		}
	}

	private Object convertValueToNumber(Class<?> targetClass, String format,
			Locale locale, Object value) {
		if (value instanceof String) {
			return convertStringToValue(targetClass, format, locale,
					(String) value);
		} else if (value instanceof Number) {
			return convertNumberToNumber(targetClass, (Number) value);
		}
		throw new IllegalArgumentException("Cannot convert to "
				+ targetClass.getSimpleName() + ": "
				+ value + " of type " + value.getClass().getName());
	}

	private Object convertNumberToNumber(Class<?> targetClass, Number number) {
		if (targetClass.isAssignableFrom(number.getClass())) {
			return number;
		} else if (targetClass == Integer.class) {
			return number.intValue();
		} else if (targetClass == Long.class) {
			return number.longValue();
		} else if (targetClass == Double.class) {
			return number.doubleValue();
		} else if (targetClass == BigDecimal.class) {
			return new BigDecimal(number.toString());
		}
		throw new IllegalArgumentException("Cannot convert to "
				+ targetClass.getSimpleName() + ": "
				+ number + " of type " + number.getClass().getName());
	}

	private Object convertStringToValue(Class<?> targetClass, String format,
			Locale locale, String value) {

		try {
			Locale effectiveLocale = locale != null ? locale : Locale.US;
			NumberFormat nf = new NumberFormatter(format)
					.getNumberFormat(effectiveLocale);
			Number number = null;
			if (targetClass == BigDecimal.class) {
				((DecimalFormat) nf).setParseBigDecimal(true);
				number = nf.parse(value, new ParsePosition(0));
			} else {
				number = nf.parse(value);
			}
			return convertNumberToNumber(targetClass, number);

		} catch (ParseException e) {
			// continue to exception
		}

		throw new IllegalArgumentException("Cannot convert to "
				+ targetClass.getSimpleName() + ": "
				+ value);
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
