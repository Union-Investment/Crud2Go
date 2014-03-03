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

package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.config.CompoundSearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.IsNull;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Not;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Wildcard;
import de.unioninvestment.eai.portal.portlet.crud.domain.search.SearchableTablesFinder;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class CompoundSearchTest {

	private CompoundSearch search;
	private QueryParser luceneParser;

	@Mock
	private SearchableTablesFinder searchableTablesFinderMock;
	@Mock
	private Table tableMock;
	@Mock
	private TableColumns columnsMock;
	@Mock
	private DataContainer containerMock;
	@Mock
	private CompoundQueryChangedEventHandler handlerMock;

	@Rule
	public LiferayContext context = new LiferayContext();
	
	@Captor
	private ArgumentCaptor<CompoundQueryChangedEvent> eventCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		CompoundSearchConfig config = new CompoundSearchConfig();

		Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		luceneParser = new MultiFieldQueryParser(Version.LUCENE_46,
				new String[] { "MASTER", "SECOND" }, analyzer);

		when(tableMock.getColumns()).thenReturn(columnsMock);
		when(tableMock.getContainer()).thenReturn(containerMock);
		doReturn(
				asList("MASTER", "SECOND", "THIRD", "NUMERIC", "TIMESTAMP",
						"NOTINTABLE")).when(columnsMock)
				.getSearchableColumnNames();
		doReturn(asList("MASTER", "SECOND")).when(columnsMock)
				.getDefaultSearchableColumnNames();

		doReturn(String.class).when(containerMock).getType("MASTER");
		doReturn(String.class).when(containerMock).getType("SECOND");
		doReturn(String.class).when(containerMock).getType("THIRD");
		doReturn(BigDecimal.class).when(containerMock).getType("NUMERIC");
		doReturn(Timestamp.class).when(containerMock).getType("TIMESTAMP");

		search = new CompoundSearch(config);
		search.searchableTablesFinder = searchableTablesFinderMock;

		ArrayList<Table> tables = new ArrayList<Table>();
		tables.add(tableMock);

		when(searchableTablesFinderMock.findSearchableTables(search, null))
				.thenReturn((tables));

		when(UI.getCurrent().getLocale()).thenReturn(Locale.GERMANY);
	}

	/* Text fields */

	@Test
	public void shouldConvertDefaultQueryToStartsWithFilter()
			throws ParseException {
		search.search(query("myterm"));
		List<Filter> filters = asList((Filter) new Any(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertBooleanORQuery() throws ParseException {
		search.search(query("MASTER:myterm OR SECOND:myterm"));
		List<Filter> filters = asList((Filter) new Any(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertBooleanANDQuery() throws ParseException {
		search.search(query("MASTER:myterm AND SECOND:myterm"));
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldRejectBooleanMixedQuery() throws ParseException {
		try {
			search.search(query("MASTER:myterm AND SECOND:myterm OR THIRD:myterm"));
			fail("Expected BusinessException");
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.mixedBooleansProhibited"));
		}
	}

	@Test
	public void shouldConvertBooleanNotAndQuery() throws ParseException {
		search.search(query("NOT MASTER:myterm AND SECOND:myterm"));
		List<Filter> filters = asList((Filter) new All(asList((Filter) new Not(
				asList((Filter) new StartsWith("MASTER", "myterm", false))),
				new StartsWith("SECOND", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertBooleanNotOrQuery() throws ParseException {
		search.search(query("NOT MASTER:myterm OR SECOND:myterm"));
		List<Filter> filters = asList((Filter) new Any(asList((Filter) new Not(
				asList((Filter) new StartsWith("MASTER", "myterm", false))),
				new StartsWith("SECOND", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertBooleanMixedQueryWithBraces()
			throws ParseException {
		search.search(query("(MASTER:myterm AND SECOND:myterm) OR THIRD:myterm"));
		Filter allFilter = new All(asList((Filter) new StartsWith("MASTER",
				"myterm", false), new StartsWith("SECOND", "myterm", false)),
				false);
		List<Filter> filters = asList((Filter) new Any(asList(allFilter,
				new StartsWith("THIRD", "myterm", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertMultiwordTermToStartsWith() throws ParseException {
		search.search(query("THIRD:\"my long phrase\""));
		List<Filter> filters = asList((Filter) new StartsWith("THIRD",
				"my long phrase", false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertDefaultNegatedQuery() throws ParseException {
		search.search(query("-myterm"));
		List<Filter> filters = asList((Filter) new Not(asList((Filter) new Any(
				asList((Filter) new StartsWith("MASTER", "myterm", false),
						new StartsWith("SECOND", "myterm", false))))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingField() throws ParseException {
		search.search(query("NOTINTABLE:4711"));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInRangeQuery()
			throws ParseException {
		search.search(query("NOTINTABLE:[4711 TO 5822]"));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInMatchAllQuery()
			throws ParseException {
		search.search(query("NOTINTABLE:\"*\""));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInPrefixQuery()
			throws ParseException {
		search.search(query("NOTINTABLE:hello*"));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInWildcardQuery()
			throws ParseException {
		search.search(query("NOTINTABLE:hell*o"));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInPhraseQuery()
			throws ParseException {
		search.search(query("NOTINTABLE:\"a b c\""));
		List<Filter> filters = emptyList();
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInAnyClause() throws ParseException {
		search.search(query("(MASTER:1315 OR NOTINTABLE:4711)"));
		List<Filter> filters = asList((Filter) new StartsWith("MASTER", "1315",
				false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInAllClause() throws ParseException {
		search.search(query("(MASTER:1315 AND NOTINTABLE:4711)"));
		List<Filter> filters = asList((Filter) new StartsWith("MASTER", "1315",
				false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertNotEqualsQuery() throws ParseException {
		search.search(query("-THIRD:myter"));
		List<Filter> filters = asList((Filter) new Not(
				asList((Filter) new StartsWith("THIRD", "myter", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertNotNullQuery() throws ParseException {
		search.search(query("THIRD:\"*\""));
		List<Filter> filters = asList((Filter) new Not(
				asList((Filter) new IsNull("THIRD"))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertStartsWithQuery() throws ParseException {
		search.search(query("THIRD:myter*"));
		List<Filter> filters = asList((Filter) new StartsWith("THIRD", "myter",
				false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertWildcardTermInBracketsToWildcard()
			throws ParseException {
		search.search(query("THIRD:\"*myter*\""));
		List<Filter> filters = asList((Filter) new Wildcard("THIRD", "*myter*",
				false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertWildcardQueryWithStarInMiddle()
			throws ParseException {
		search.search(query("THIRD:my*ter"));
		List<Filter> filters = asList((Filter) new Wildcard("THIRD", "my*ter",
				false));
		verify(containerMock).replaceFilters(filters, false);
	}

	private Query query(String expression) throws ParseException {
		return luceneParser.parse(expression);
	}

	/* Numeric fields */

	@Test
	public void shouldConvertToEqualForNumericFields() throws ParseException {
		search.search(query("NUMERIC:4711"));
		List<Filter> filters = asList((Filter) new Equal("NUMERIC",
				new BigDecimal("4711")));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertSingleDateToRangeForDateFields()
			throws ParseException {
		search.search(query("TIMESTAMP:23.12.2013"));
		Date startDate = new GregorianCalendar(2013, 11, 23).getTime();
		Date endDate = new GregorianCalendar(2013, 11, 24).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForTextFields() throws ParseException {
		search.search(query("MASTER:[4711 TO 5228]"));
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("MASTER", "4711", true), new Less(
						"MASTER", "5228", true))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForTextFieldsWithExclusion()
			throws ParseException {
		search.search(query("MASTER:{4711 TO 5228}"));
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("MASTER", "4711", false), new Less(
						"MASTER", "5228", false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForNumericFields() throws ParseException {
		search.search(query("NUMERIC:[4711 TO 5228]"));
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("NUMERIC", new BigDecimal(4711), true),
				new Less("NUMERIC", new BigDecimal("5228"), true))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForNumericFieldsWithExclusion()
			throws ParseException {
		search.search(query("NUMERIC:{4711 TO 5228}"));
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("NUMERIC", new BigDecimal(4711), false),
				new Less("NUMERIC", new BigDecimal("5228"), false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForDateFieldsWithMonthGranularity()
			throws ParseException {
		search.search(query("TIMESTAMP:[11.12.2013 TO 31.12.2013]"));
		Date startDate = new GregorianCalendar(2013, 11, 11).getTime();
		Date endDate = new GregorianCalendar(2014, 0, 1).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldConvertRangeForDateFieldsWithMonthGranularityAndExclusion()
			throws ParseException {
		search.search(query("TIMESTAMP:{11.12.2013 TO 31.12.2013}"));
		Date startDate = new GregorianCalendar(2013, 11, 12).getTime();
		Date endDate = new GregorianCalendar(2013, 11, 31).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldFailWithReadableMessageOnUnknownQueryType()
			throws ParseException {
		try {
			search.search(query("MASTER:Test~"));
			fail("Expected BusinessException");
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.unsupportedQuerySyntax"));
			assertThat(e.getArgs()[0], is((Object) "MASTER:test~2"));
		}
	}

	@Test
	public void shouldFailWithReadableMessageOnNumberConversionFailure()
			throws ParseException {
		try {
			search.search(query("NUMERIC:Test"));
			fail("Expected BusinessException");
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.numberConversionFailed"));
			assertThat(e.getArgs()[0], is((Object) "NUMERIC"));
			assertThat(e.getArgs()[1], is((Object) "Test"));
		}
	}

	@Test
	public void shouldSearchByQueryString() {
		search.search("MASTER:myterm");
		List<Filter> filters = asList((Filter) (Filter) new StartsWith(
				"MASTER", "myterm", false));
		verify(containerMock).replaceFilters(filters, false);
	}

	@Test
	public void shouldFailOnInvalidQueryString() {
		try {
			search.search("*");
			fail();
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.invalidQuery"));
			assertThat(e.getArgs()[0], is((Object) "*"));
		}
	}
	
	@Test
	public void shouldInformUIAboutChangedQueryString() {
		search.addQueryChangedEventHandler(handlerMock);
		search.search("MASTER:myterm");
		verify(handlerMock).onQueryChange(eventCaptor.capture());
		assertThat(eventCaptor.getValue().getQueryString(), is("MASTER:myterm"));
		assertThat(eventCaptor.getValue().getSource(), is(search));
	}
}
