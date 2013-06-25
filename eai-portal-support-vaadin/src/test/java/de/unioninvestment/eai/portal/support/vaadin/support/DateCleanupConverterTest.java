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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class DateCleanupConverterTest {

	@Test
	public void shouldRemoveDetailPart() throws Exception {
		assertCleanup(Calendar.YEAR, date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 0, 1, 0, 0, 0, 0));
		assertCleanup(Calendar.MONTH, date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 6, 1, 0, 0, 0, 0));
		assertCleanup(Calendar.DAY_OF_MONTH,
				date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 6, 24, 0, 0, 0, 0));
		assertCleanup(Calendar.HOUR, date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 6, 24, 16, 0, 0, 0));
		assertCleanup(Calendar.MINUTE, date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 6, 24, 16, 31, 0, 0));
		assertCleanup(Calendar.SECOND, date(2013, 06, 24, 16, 31, 05, 450),
				date(2013, 6, 24, 16, 31, 5, 0));
	}

	private Date date(int year, int month, int day, int hour, int min, int sec,
			int msec) {
		GregorianCalendar value = new GregorianCalendar(year, month, day, hour,
				min, sec);
		value.set(Calendar.MILLISECOND, msec);
		return value.getTime();
	}

	private void assertCleanup(int resolution, Date oldDate, Date newDate)
			throws Exception {
		DateCleanupConverter converter = new DateCleanupConverter(resolution);
		assertThat(converter.translateToDatasource(oldDate),
				is((Object) newDate));
	}
}
