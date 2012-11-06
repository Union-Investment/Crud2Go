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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import oracle.sql.TIMESTAMP;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;

public class OracleTimestampDataTypeTest {

	private OracleTimestampDataType type;

	private TIMESTAMP timestamp;

	@Before
	public void setUp() {
		type = new OracleTimestampDataType();
		MockitoAnnotations.initMocks(this);

		long time = new GregorianCalendar(2011, 0, 1, 15, 23, 22).getTime()
				.getTime();
		timestamp = new TIMESTAMP(new Timestamp(time));

	}

	@Test
	public void shouldSupportsTimestampsForDisplayFilterButNotEditing() {

		assertTrue(type.supportsDisplaying(TIMESTAMP.class));
		assertThat(type, not(instanceOf(EditorSupport.class)));
	}

	@Test
	public void shouldFormatCorrectly() {

		assertThat(type.formatPropertyValue(new ObjectProperty<TIMESTAMP>(null,
				TIMESTAMP.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<TIMESTAMP>(
				timestamp, TIMESTAMP.class), null),
				is("01.01.2011 15:23:22.000"));
	}

	@Test
	public void shouldShowSomeTextWhenFormattingFails() throws SQLException {
		TIMESTAMP timestampMock = mock(TIMESTAMP.class);
		when(timestampMock.timestampValue()).thenThrow(new SQLException());

		assertThat(type.formatPropertyValue(new ObjectProperty<TIMESTAMP>(
				timestampMock, TIMESTAMP.class), null), is("nicht darstellbar"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

}
