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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.support.vaadin.support.TimestampConverter;

/**
 * 
 * Formular-Feld für Datums-Felder.
 * 
 * @author max.hartmann
 * 
 */
public class DateFormField extends FormField {
	private static final long serialVersionUID = 42L;

	private TimestampConverter timestampConverter;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param config
	 *            Konfiguration des Datums-Feldes
	 */
	public DateFormField(FormFieldConfig config) {
		super(config);

		timestampConverter = new TimestampConverter(super.getProperty(),
				getResolution(), getDateFormat());
	}

	/**
	 * Liefert das Datumsformat.
	 * 
	 * @return Datumsformat
	 */
	public final String getDateFormat() {
		if (config != null && config.getDate() != null) {
			return config.getDate().getFormat();
		}

		return null;
	}

	/**
	 * Liefert die Genauigkeit des Datums zurück. Dabei wird als Ergebnis eine
	 * Konstante der Klasse {@link Calendar} zurückgeliefert, die die
	 * Genauigkeit anzeigt.
	 * 
	 * Beispiel: {@link Calendar#MINUTE} für Minutengenauigkeit
	 * 
	 * @return Genauigkeit des Datums
	 */
	public final int getResolution() {
		String dateFormat = getDateFormat();
		dateFormat = dateFormat == null ? "" : dateFormat;

		int resolution = -1;

		if (dateFormat.contains("ss")) {
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
		}

		return resolution;
	}

	public String getDefaultValue() {
		return config.getDate().getDefault();
	}

	public TimestampConverter getTimestampProperty() {
		return timestampConverter;
	}

	public Date getBeginDate() {
		return (Date) getTimestampProperty().getValue();
	}

	/**
	 * Berechnet das Enddatum.
	 * 
	 * @return Datum
	 */
	public Date getEndDate() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getBeginDate());

		final int resolution = getResolution();
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
