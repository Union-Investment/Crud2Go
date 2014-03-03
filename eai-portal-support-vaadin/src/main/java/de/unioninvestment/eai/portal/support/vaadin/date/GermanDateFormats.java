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

package de.unioninvestment.eai.portal.support.vaadin.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GermanDateFormats {

	private static final String DD_MM_YYYY_HH_MM = "dd.MM.yyyy-HH:mm";
	private static final String DD_MM_YYYY = "dd.MM.yyyy";
	private static final String MM_YYYY = "MM.yyyy";
	private static final String YYYY = "yyyy";

	public String find(String dateString) throws ParseException {
		if (canParse(dateString, DD_MM_YYYY_HH_MM)) {
			return DD_MM_YYYY_HH_MM;
		} else if (canParse(dateString, DD_MM_YYYY)) {
			return DD_MM_YYYY;
		} else if (canParse(dateString, MM_YYYY)) {
			return MM_YYYY;
		} else if (canParse(dateString, YYYY)) {
			return YYYY;
		}
		throw new ParseException(dateString, 0);
	}

	private boolean canParse(String dateString, String pattern) {
		boolean matches;
		try {
			new SimpleDateFormat(pattern).parse(dateString);
			matches = true;
		} catch (ParseException e) {
			matches = false;
		}
		return matches;
	}
}
