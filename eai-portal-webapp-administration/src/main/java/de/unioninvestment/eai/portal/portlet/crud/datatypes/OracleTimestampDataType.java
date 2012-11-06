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
package de.unioninvestment.eai.portal.portlet.crud.datatypes;

import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;

import oracle.sql.TIMESTAMP;

import com.vaadin.data.Property;

import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Enthält die Anzeige- und Filterlogik die spezifisch für
 * {@link oracle.sql.TIMESTAMP} Datentypen ist.
 * 
 * @author carsten.mjartan
 * 
 */
@org.springframework.stereotype.Component("oracleTimestampDataType")
public class OracleTimestampDataType extends AbstractDataType implements
		DisplaySupport {

	public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";

	/**
	 * {@inheritDoc}
	 */
	public boolean supportsDisplaying(Class<?> clazz) {
		return oracle.sql.TIMESTAMP.class.isAssignableFrom(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public String formatPropertyValue(Property property, Format format) {
		if (property == null || property.getValue() == null
				|| property.getValue().equals(Boolean.FALSE)) {
			return "";
		}

		try {
			TIMESTAMP value = (TIMESTAMP) property.getValue();
			return new SimpleDateFormat(TIMESTAMP_FORMAT).format(value
					.timestampValue());

		} catch (SQLException e) {
			return "nicht darstellbar";
		}
	}

}
