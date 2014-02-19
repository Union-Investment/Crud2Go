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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;

public class AdvancedStringFilterTranslatorTest {

	private AdvancedStringFilterTranslator translator = new AdvancedStringFilterTranslator();

	@Mock
	private StatementHelper shMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		QueryBuilder.setStringDecorator(DatabaseDialect.ORACLE
				.getStringDecorator());
	}

	@Test
	public void shouldTranslateAdvancedStringFilterInstances() {
		assertEquals("~", AdvancedStringFilterTranslator.ESCAPE_CHARACTER);

		AdvancedStringFilter filter = new AdvancedStringFilter("foo", "",
				false, false, false);

		assertTrue(translator.translatesFilter(filter));
	}

	@Test
	public void shouldCreateValidWhereExpressionForPrefixMatch() {
		AdvancedStringFilter filter = new AdvancedStringFilter("foo",
				"fitzefatze", false, true, false);

		String whereString = translator.getWhereStringForFilter(filter, shMock);

		verify(shMock).addParameterValue("fitzefatze%");

		assertThat(whereString, is("\"foo\" like ? escape '~'"));
	}

	@Test
	public void shouldCreateValidWhereExpressionForPostfixMatch() {
		AdvancedStringFilter filter = new AdvancedStringFilter("foo",
				"fitzefatze", false, false, true);

		String whereString = translator.getWhereStringForFilter(filter, shMock);

		verify(shMock).addParameterValue("%fitzefatze");

		assertThat(whereString, is("\"foo\" like ? escape '~'"));
	}

	@Test
	public void shouldCreateValidWhereExpressionForContainsMatch() {
		AdvancedStringFilter filter = new AdvancedStringFilter("foo",
				"fitzefatze", false, false, false);

		String whereString = translator.getWhereStringForFilter(filter, shMock);

		verify(shMock).addParameterValue("%fitzefatze%");

		assertThat(whereString, is("\"foo\" like ? escape '~'"));
	}

	@Test
	public void shouldCreateValidWhereExpressionForWithIgnoreCase() {
		AdvancedStringFilter filter = new AdvancedStringFilter("foo",
				"FitzeFatze", true, false, false);

		String whereString = translator.getWhereStringForFilter(filter, shMock);

		verify(shMock).addParameterValue("%FITZEFATZE%");

		assertThat(whereString, is("UPPER(\"foo\") like ? escape '~'"));
	}

	@Test
	public void shouldCreateValidWhereExpressionForWithEscaping() {
		AdvancedStringFilter filter = new AdvancedStringFilter("foo",
				"Fitze_Fat%e", false, false, false);

		String whereString = translator.getWhereStringForFilter(filter, shMock);

		verify(shMock).addParameterValue("%Fitze~_Fat~%e%");

		assertThat(whereString, is("\"foo\" like ? escape '~'"));
	}
}
