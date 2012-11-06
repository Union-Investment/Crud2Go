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


import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.ObjectProperty;

public class TimestampConverterTest {
	
	private TimestampConverter timestampConverter;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		ObjectProperty<String> backingProperty = null;
		int dateResolution = Calendar.HOUR_OF_DAY;
		String format = "dd.MM.yyyy";
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
	}
	
	@Test
	public void shouldConvertToBackend() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		
		assertEquals(df.format(date), timestampConverter.convertToBackend(date));
	}
	
	@Test
	public void shouldNotConvertToBackend() {
		assertEquals(null, timestampConverter.convertToBackend(null));
	}
	
	@Test
	public void shouldConvertToFrontend() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		 
		Date date = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		
		Date ts = (Date) timestampConverter.convertToFrontend(df.format(date));
		assertEquals(date.getTime(), ts.getTime());
	}
	
	@Test
	public void shouldNotConvertToFrontend() {
		assertEquals(null, timestampConverter.convertToFrontend(null));
	}
	
	@Test
	public void shouldConfigureTimestamp() {
		String format = "dd.MM.yyyy hh:mm:ss";
		ObjectProperty<String> backingProperty = null;
		
		Calendar cal = Calendar.getInstance();
		int dateResolution = Calendar.SECOND;
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
		timestampConverter.configureTimestamp(cal);
		assertEquals(0, cal.get(Calendar.MILLISECOND));
		
		cal = Calendar.getInstance();
		dateResolution = Calendar.MINUTE;
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
		timestampConverter.configureTimestamp(cal);
		assertEquals(0, cal.get(Calendar.SECOND));
		
		cal = Calendar.getInstance();
		dateResolution = Calendar.HOUR_OF_DAY;
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
		timestampConverter.configureTimestamp(cal);
		assertEquals(0, cal.get(Calendar.MINUTE));
		
		cal = Calendar.getInstance();
		dateResolution = Calendar.MONTH;
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
		timestampConverter.configureTimestamp(cal);
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		
		cal = Calendar.getInstance();
		dateResolution = Calendar.YEAR;
		timestampConverter = new TimestampConverter(backingProperty, dateResolution, format);
		timestampConverter.configureTimestamp(cal);
		assertEquals(0, cal.get(Calendar.MONTH));
	}
}
