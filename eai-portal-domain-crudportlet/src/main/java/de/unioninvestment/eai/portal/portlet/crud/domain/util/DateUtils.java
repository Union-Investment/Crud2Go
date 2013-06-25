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
package de.unioninvestment.eai.portal.portlet.crud.domain.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.ui.PopupDateField;

public class DateUtils {

	/**
	 * Liefert die Genauigkeit des Datums zurück. Dabei wird als Ergebnis eine
	 * Konstante der Klasse {@link Calendar} zurückgeliefert, die die
	 * Genauigkeit anzeigt.
	 * 
	 * Beispiel: {@link Calendar#MINUTE} für Minutengenauigkeit
	 * 
	 * @param dateFormat
	 *            eine Zeichenkette in {@link SimpleDateFormat} syntax
	 * @return Genauigkeit des Datums als {@link Calendar} Konstante oder -1,
	 *         falls nicht ermittelbar
	 */
	public static int getResolution(String dateFormat) {
		int resolution;

		if (dateFormat == null) {
			resolution = -1;

		} else if (dateFormat.contains("ss")) {
			resolution = Calendar.SECOND;

		} else if (dateFormat.contains("mm")) {
			resolution = Calendar.MINUTE;

		} else if (dateFormat.contains("HH")) {
			resolution = Calendar.HOUR_OF_DAY;

		} else if (dateFormat.contains("dd")) {
			resolution = Calendar.DAY_OF_MONTH;

		} else if (dateFormat.contains("MM")) {
			resolution = Calendar.MONTH;

		} else if (dateFormat.contains("yy")) {
			resolution = Calendar.YEAR;

		} else {
			resolution = -1;
		}

		return resolution;
	}

	public static int getVaadinResolution(int resolution) {
		switch (resolution) {
		case Calendar.SECOND:
			return PopupDateField.RESOLUTION_SEC;
		case Calendar.MINUTE:
			return PopupDateField.RESOLUTION_MIN;
		case Calendar.HOUR_OF_DAY:
			return PopupDateField.RESOLUTION_HOUR;
		case Calendar.DAY_OF_MONTH:
			return PopupDateField.RESOLUTION_DAY;
		case Calendar.MONTH:
			return PopupDateField.RESOLUTION_MONTH;
		case Calendar.YEAR:
			return PopupDateField.RESOLUTION_YEAR;
		default:
			return PopupDateField.RESOLUTION_MSEC;
		}
	}

	public static int getVaadinResolution(String dateFormat) {
		return getVaadinResolution(getResolution(dateFormat));
	}

	public static Date getEndDate(Date beginDate, int resolution) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(beginDate);

		switch (resolution) {
		case Calendar.SECOND:
			cal.add(Calendar.SECOND, 1);
			break;
		case Calendar.MINUTE:
			cal.add(Calendar.MINUTE, 1);
			break;
		case Calendar.HOUR_OF_DAY:
			cal.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case Calendar.DAY_OF_MONTH:
			cal.add(Calendar.DAY_OF_MONTH, 1);
			break;
		case Calendar.MONTH:
			cal.add(Calendar.MONTH, 1);
			break;
		case Calendar.YEAR:
			cal.add(Calendar.YEAR, 1);
			break;
		}

		return cal.getTime();
	}

}
