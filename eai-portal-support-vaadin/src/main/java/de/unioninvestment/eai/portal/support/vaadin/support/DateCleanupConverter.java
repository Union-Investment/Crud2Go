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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.vaadin.addon.propertytranslator.PropertyTranslator;

/**
 * Formatierung und Parsing von SQL Timestamps. Wird als Wrapper um eine
 * entsprechende PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
public class DateCleanupConverter extends PropertyTranslator {

	private static final long serialVersionUID = 1L;

	private int resolution;

	/**
	 * @param resolution
	 *            Calendar Konstante für kleinste Einheit im Datum
	 */
	public DateCleanupConverter(int resolution) {
		this.resolution = resolution;
	}

	@Override
	public Class<?> getType() {
		return Date.class;
	}

	@Override
	public Object translateFromDatasource(Object value) {
		return value;
	}

	@Override
	public Object translateToDatasource(Object value) throws Exception {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime((Date) value);
		switch (resolution) {
		case Calendar.YEAR:
			calendar.set(Calendar.MONTH, 0);
		case Calendar.MONTH:
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR:
		case Calendar.HOUR_OF_DAY:
			calendar.set(Calendar.MINUTE, 0);
		case Calendar.MINUTE:
			calendar.set(Calendar.SECOND, 0);
		case Calendar.SECOND:
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		default:
			return value;
		}
		return calendar.getTime();
	}

}
