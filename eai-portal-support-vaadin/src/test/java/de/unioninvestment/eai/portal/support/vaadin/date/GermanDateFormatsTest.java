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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;

import org.junit.Test;

public class GermanDateFormatsTest {

	@Test
	public void shouldFindDateFormatForMinuteResolution() throws ParseException {
		GermanDateFormats formats = new GermanDateFormats();
		assertThat(formats.find("11.12.2013-13:15"), is("dd.MM.yyyy-HH:mm"));
	}

	@Test
	public void shouldFindDateFormatForDayResolution() throws ParseException {
		GermanDateFormats formats = new GermanDateFormats();
		assertThat(formats.find("11.12.2013"), is("dd.MM.yyyy"));
	}

	@Test
	public void shouldFindDateFormatForMonthResolution() throws ParseException {
		GermanDateFormats formats = new GermanDateFormats();
		assertThat(formats.find("12.2013"), is("MM.yyyy"));
	}

	@Test
	public void shouldFindDateFormatForYearResolution() throws ParseException {
		GermanDateFormats formats = new GermanDateFormats();
		assertThat(formats.find("2013"), is("yyyy"));
	}
}
