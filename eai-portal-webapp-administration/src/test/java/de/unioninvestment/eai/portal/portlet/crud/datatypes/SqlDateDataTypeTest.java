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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.vaadin.data.util.converter.Converter;

import de.unioninvestment.eai.portal.support.vaadin.support.DateFormatter;

public class SqlDateDataTypeTest extends AbstractDataTypeTest<SqlDateDataType> {

	@Override
	protected SqlDateDataType createDataType() {
		return new SqlDateDataType();
	}

	@Test
	public void shouldSupportsTimestampsForDisplayFilterButNotEditing() {
		assertTrue(type.supportsDisplaying(Date.class));
		assertTrue(type.supportsEditing(Date.class));
	}

	@Test
	public void shouldCreateADateFormatter() {
		Converter<String, java.util.Date> formatter = (Converter<String, java.util.Date>) type
				.createFormatter(Date.class, new SimpleDateFormat("dd.MM.yyyy"));
		assertThat(formatter, instanceOf(DateFormatter.class));
		assertThat(formatter.convertToPresentation(new GregorianCalendar(2013,
				6, 10).getTime(), String.class, null), is("10.07.2013"));
	}

	@Test
	public void shouldSupportWriting() {
		assertFalse(type.isReadonly());
	}
}
