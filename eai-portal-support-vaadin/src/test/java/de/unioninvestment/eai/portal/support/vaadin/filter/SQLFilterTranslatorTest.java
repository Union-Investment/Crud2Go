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
package de.unioninvestment.eai.portal.support.vaadin.filter;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class SQLFilterTranslatorTest {

	@Mock
	private SQLFilter sqlFilter;

	private SQLFilterTranslator sqlFilterTranslator;

	@Mock
	private StatementHelper shMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldTranslatesFilter() {
		sqlFilterTranslator = new SQLFilterTranslator();
		assertThat(sqlFilterTranslator.translatesFilter(sqlFilter), is(TRUE));
	}

	@Test
	public void shouldGetWhereStringForFilter() {

		when(sqlFilter.getWhereString()).thenReturn(
				"IN (SELECT 1 FROM TEST WHERE 1 = ?)");
		when(sqlFilter.getColumn()).thenReturn("TESTCOL");
		when(sqlFilter.getValues()).thenReturn(
				Arrays.asList(new Object[] { "TESTPARAM" }));

		sqlFilterTranslator = new SQLFilterTranslator();

		assertThat(
				sqlFilterTranslator.getWhereStringForFilter(sqlFilter, shMock),
				is("\"TESTCOL\" IN (SELECT 1 FROM TEST WHERE 1 = ?)"));
		verify(shMock).addParameterValue(any());
	}

	@Test
	public void shouldGetWhereStringForFilterWithoutColumn() {

		when(sqlFilter.getWhereString()).thenReturn(
				"A = ?");
		when(sqlFilter.getColumn()).thenReturn(null);
		when(sqlFilter.getValues()).thenReturn(
				Arrays.asList(new Object[] { "TESTPARAM" }));

		sqlFilterTranslator = new SQLFilterTranslator();

		assertThat(
				sqlFilterTranslator.getWhereStringForFilter(sqlFilter, shMock),
				is("(A = ?)"));
		verify(shMock).addParameterValue(any());
	}
}
