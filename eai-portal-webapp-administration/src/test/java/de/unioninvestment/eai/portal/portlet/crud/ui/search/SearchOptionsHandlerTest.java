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

package de.unioninvestment.eai.portal.portlet.crud.ui.search;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import com.google.gwt.thirdparty.guava.common.collect.Sets;

public class SearchOptionsHandlerTest {

	private SearchOptionsHandler handler = new SearchOptionsHandler(asList(
			"MASTER", "SECOND", "THIRD", "MATCH"));

	@Test
	public void shouldDeliverAllColumnNamesOnEmptyString() {
		assertThat(handler.getOptions(""),
				hasItems("MASTER:", "SECOND:", "THIRD:", "MATCH:"));
	}

	@Test
	public void shouldDeliverValidOperatorsOnEmptyString() {
		assertThat(handler.getOptions(""), hasItems("NOT "));
	}

	@Test
	public void shouldNotDeliverAndOrOperatorsOnEmptyString() {
		assertThat(handler.getOptions(""), not(hasItems("AND ", "OR ")));
	}

	@Test
	public void shouldNotDeliverDuplicateAndOrOperators() {
		assertThat(handler.getOptions("Test AND "), not(hasItems("Test AND AND ", "Test AND OR ")));
		assertThat(handler.getOptions("Test OR "), not(hasItems("Test OR AND ", "Test OR OR ")));
	}

	@Test
	public void shouldDeliverAllColumnNamesAfterOpeningBrace() {
		assertThat(handler.getOptions("("),
				hasItems("(MASTER:", "(SECOND:", "(THIRD:", "(MATCH:"));
	}

	@Test
	public void shouldDeliverValidOperatorsAfterOpeningBrace() {
		assertThat(handler.getOptions("("), hasItems("(NOT "));
	}

	@Test
	public void shouldNotDeliverAndOrOperatorsAfterOpeningBrace() {
		assertThat(handler.getOptions("("), not(hasItems("(AND ", "(OR ")));
	}

	@Test
	public void shouldDeliverOnlyMatchingColumnNames() {
		assertThat(handler.getOptions("MA"), is(setOf("MATCH:", "MASTER:")));
	}

	@Test
	public void shouldDeliverAllColumnNamesAfterSpaceCharacter() {
		assertThat(handler.getOptions("MASTER:4711 "),
				hasItems("MASTER:4711 MASTER:", //
						"MASTER:4711 SECOND:", //
						"MASTER:4711 THIRD:", //
						"MASTER:4711 MATCH:"));
	}

	@Test
	public void shouldDeliverValidOperatorsAfterSpaceCharacter() {
		assertThat(handler.getOptions("MASTER:4711 "),
				hasItems("MASTER:4711 AND ", //
						"MASTER:4711 OR ", //
						"MASTER:4711 NOT "));
	}

	@Test
	public void shouldNotDeliverAnythingWhileEnteringTerm() {
		assertThat(handler.getOptions("MASTER:"), is(Collections.<String>emptySet()));
		assertThat(handler.getOptions("MASTER:He"), is(Collections.<String>emptySet()));
	}

	@Test
	public void shouldDeliverTOInIncludingRangeQuery() {
		assertThat(handler.getOptions("MASTER:[He "), is(setOf("MASTER:[He TO ")));
	}

	@Test
	public void shouldDeliverTOInRangeQueryWithoutExplicitField() {
		assertThat(handler.getOptions("[He "), is(setOf("[He TO ")));
	}

	@Test
	public void shouldDeliverTOInExcludingRangeQuery() {
		assertThat(handler.getOptions("MASTER:{He "), is(setOf("MASTER:{He TO ")));
	}

	@Test
	public void shouldNotDeliverAnythingAfterTO() {
		assertThat(handler.getOptions("MASTER:[He TO "), is(Collections.<String>emptySet()));
	}


	private Set<String> setOf(String... options) {
		return (Set<String>) Sets.newHashSet(options);
	}
}
