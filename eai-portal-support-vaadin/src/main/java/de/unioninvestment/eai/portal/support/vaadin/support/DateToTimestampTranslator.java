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
import java.util.Date;

import org.vaadin.addon.propertytranslator.PropertyTranslator;

import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;

/**
 * Konvertieren von Dates nach Timestamps unter Bereinigung gemäß der
 * angegebenen <code>resolution</code>. Wird als Wrapper um eine entsprechende
 * PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
public class DateToTimestampTranslator extends PropertyTranslator {

	private static final long serialVersionUID = 1L;

	private int resolution;

	/**
	 * @param resolution
	 *            Calendar Konstante für kleinste Einheit im Datum
	 */
	public DateToTimestampTranslator(int resolution) {
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
		if (value == null) {
			return null;
		} else {
			Date cleanedDate = DateUtils.cleanup((Date) value, resolution);
			return new Timestamp(cleanedDate.getTime());
		}
	}

}
