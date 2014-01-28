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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;


public class MySQLRegExpFilterTranslatorTest {
	@Mock
	private StatementHelper helperMock;
	private MySQLRegExpFilterTranslator translator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		translator = new MySQLRegExpFilterTranslator();
		QueryBuilder.setStringDecorator(DatabaseDialect.MYSQL.getStringDecorator());
	}

	@Test
	public void shouldCreateSQLWithQuestionmarkForPattern() {
		String sql = translator.getWhereStringForFilter(new DatabaseRegExpFilter(
				"mycol", "abcde", null), helperMock);
		assertThat(sql, is("`mycol` REGEXP ?"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldFailIfModifiersAreGiven() {
		translator.getWhereStringForFilter(new DatabaseRegExpFilter(
				"mycol", "abcde", "i"), helperMock);
	}

	@Test
	public void shouldAddPatternAsSqlParameter() {
		translator.getWhereStringForFilter(new DatabaseRegExpFilter("mycol",
				"abcde", null), helperMock);

		verify(helperMock).addParameterValue("abcde");
		verifyNoMoreInteractions(helperMock);
	}

}
