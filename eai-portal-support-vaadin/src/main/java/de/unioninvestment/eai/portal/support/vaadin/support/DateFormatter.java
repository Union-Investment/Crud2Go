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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.data.util.PropertyFormatter;

/**
 * PropertyFormatter für Date Datentypen.
 * 
 * @author markus.bonsch
 * 
 */
@SuppressWarnings("unchecked")
public class DateFormatter extends PropertyFormatter {

	private static final long serialVersionUID = 1L;
	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	@Override
	public String format(Object value) {
		if (value instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			return sdf.format(value);
		} else if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

	@Override
	public Object parse(String value) throws Exception {
		return new Date(TimestampUtils.parseTimestamp(value).getTime());
	}

}
