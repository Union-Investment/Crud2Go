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

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.google.common.base.Strings;
import com.vaadin.data.util.converter.Converter.ConversionException;

/**
 * 
 * Hilft beim Umgang mit Timestamps.
 * 
 * @author max.hartmann
 * 
 */
public abstract class TimestampUtils {
	private static final String FALSCHES_FORMAT_MELDUNG = "Bitte geben Sie einen Zeitpunkt im Format 'DD.MM.YYYY[ HH:MM:SS[.NNNNNNNNN]]' ein.";

	public static final String YEAR_FORMAT = "yyyy";
	public static final String MONTH_FORMAT = "MM.yyyy";
	public static final String DATE_FORMAT = "dd.MM.yyyy";
	public static final String HOUR_FORMAT = "dd.MM.yyyy HH";
	public static final String MINUTE_FORMAT = "dd.MM.yyyy HH:mm";
	public static final String DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

	/**
	 * Formatiert den java.sql.Timestamp in das angegebene Format.
	 * 
	 * @param value
	 *            TimeStamp-Instanz
	 * @param format
	 *            Datumsformat für die Anzeige
	 * 
	 * @return Datums-String.
	 */
	public static String formatTimestamp(Timestamp value, String format) {
		if (format == null) {
			if (value.getNanos() != 0) {
				return reformatToGerman(value.toString());

			} else if (hasTimePart(value)) {
				return new SimpleDateFormat(DATETIME_FORMAT).format(value);

			} else {
				return new SimpleDateFormat(DATE_FORMAT).format(value);
			}
		}
		return new SimpleDateFormat(format).format(value);
	}

	/**
	 * Formatiert den java.sql.Timestamp in ein deutsches.
	 * 
	 * @param value
	 *            TimeStamp-Instanz
	 * @return Datums-String
	 */
	public static String formatTimestamp(Timestamp value) {
		return formatTimestamp(value, null);
	}

	/**
	 * @param value
	 *            Parse den String in ein java.sql.Timestamp untestützte Format
	 *            dd.MM.yyyy HH:mm:ss.fffffffff wobei der Zeit und Millisekunden
	 *            Anteil optional sind.
	 * @return java.sql.Timestamp mit einem Millisekunden Anteil.
	 */
	@SuppressWarnings("all")
	public static Timestamp parseTimestamp(String value) {
		Timestamp result;
		try {
			if (value == null) {
				result = null;

			} else if (value.length() == "yyyy-mm-dd hh:mm:ss.fffffffff"
					.length()) {
				String internalValue = reformatToInternal(value);
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() >= DATETIME_FORMAT.length() + 2) {
				// fill up nanoseconds
				String fullTimestamp = Strings.padEnd(value,
						DATETIME_FORMAT.length() + 10, '0');
				String internalValue = reformatToInternal(fullTimestamp);
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == DATETIME_FORMAT.length()) {
				String internalValue = reformatToInternal(value + ".000000000");
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == MINUTE_FORMAT.length()) {
				String internalValue = reformatToInternal(value
						+ ":00.000000000");
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == HOUR_FORMAT.length()) {
				String internalValue = reformatToInternal(value
						+ ":00:00.000000000");
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == DATE_FORMAT.length()) {
				String internalValue = reformatToInternal(value
						+ " 00:00:00.000000000");
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == MONTH_FORMAT.length()) {
				String internalValue = reformatToInternal("01-" + value
						+ " 00:00:00.000000000");
				result = Timestamp.valueOf(internalValue);

			} else if (value.length() == YEAR_FORMAT.length()) {
				String internalValue = reformatToInternal("01-01-" + value
						+ " 00:00:00.000000000");
				result = Timestamp.valueOf(internalValue);

			} else {
				throw new ConversionException(FALSCHES_FORMAT_MELDUNG);
			}
			return result;

		} catch (IllegalArgumentException e) {
			throw new ConversionException(FALSCHES_FORMAT_MELDUNG);
		}
	}

	/**
	 * @param internal
	 *            Setzt den Timestamp.toString Wert in das deutsche Format um.
	 * @return Datum im deutschen Datumsformat
	 */
	static String reformatToGerman(String internal) {
		String yearPart = internal.substring(0, 4);
		String monthPart = internal.substring(5, 7);
		String dayPart = internal.substring(8, 10);
		String timePart = internal.substring(11);
		return dayPart + "." + monthPart + "." + yearPart + " " + timePart;
	}

	/**
	 * 
	 * @param german
	 *            Formatiert das Datum im deutschen Format in das interne
	 *            Timestamp Datumsformat um.
	 * @return Datumsstring im internen Timestampformat
	 */
	static String reformatToInternal(String german) {
		String yearPart = german.substring(6, 10);
		String monthPart = german.substring(3, 5);
		String dayPart = german.substring(0, 2);
		String timePart = german.substring(11);
		return yearPart + "-" + monthPart + "-" + dayPart + " " + timePart;
	}

	/**
	 * 
	 * @param value
	 *            Prüft ob der Timestamp einen Zeitanteil besitzt.
	 * @return true falls Zeitanteil vorhanden.
	 */
	static boolean hasTimePart(Timestamp value) {
		if (value.getNanos() != 0) {
			return true;
		}

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(value.getTime());
		return calendar.get(SECOND) != 0 || calendar.get(MINUTE) != 0
				|| calendar.get(HOUR_OF_DAY) != 0;
	}
}
