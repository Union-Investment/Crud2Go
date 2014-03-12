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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.collect.ImmutableMap;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.config.CompoundSearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;
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
import de.unioninvestment.eai.portal.support.vaadin.junit.AbstractSpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class CompoundSearchTest extends AbstractSpringPortletContextTest {

	private CompoundSearch search;

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

	@Mock
	private OptionList selectOptionsListMock, selectTextOptionsListMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		CompoundSearchConfig config = new CompoundSearchConfig();

		when(tableMock.getColumns()).thenReturn(columnsMock);
		when(tableMock.getContainer()).thenReturn(containerMock);
		TableColumn masterColumn = new TableColumn.Builder() //
				.name("MASTER") //
				.searchable(Searchable.DEFAULT) //
				.build();
		TableColumn secondColumn = new TableColumn.Builder() //
				.name("SECOND") //
				.searchable(Searchable.DEFAULT) //
				.build();
		TableColumn thirdColumn = new TableColumn.Builder() //
				.name("THIRD") //
				.searchable(Searchable.TRUE) //
				.build();
		TableColumn numericColumn = new TableColumn.Builder() //
				.name("NUMERIC") //
				.searchable(Searchable.TRUE) //
				.build();
		DateTableColumn timestampColumn = new DateTableColumn.Builder() //
				.name("TIMESTAMP") //
				.searchable(Searchable.TRUE) //
				.build();
		SelectionTableColumn selectColumn = new SelectionTableColumn.Builder() //
				.name("SELECT") //
				.searchable(Searchable.TRUE) //
				.optionList(selectOptionsListMock) //
				.build();
		SelectionTableColumn selectTextColumn = new SelectionTableColumn.Builder() //
				.name("SELECTTEXT") //
				.searchable(Searchable.TRUE) //
				.optionList(selectTextOptionsListMock) //
				.build();
		when(columnsMock.iterator()).thenReturn(
				asList(masterColumn, secondColumn, thirdColumn, numericColumn,
						timestampColumn, selectColumn, selectTextColumn)
						.iterator());
		doReturn(
				asList("MASTER", "SECOND", "THIRD", "NUMERIC", "TIMESTAMP",
						"SELECT", "SELECTTEXT", "NOTINTABLE"))
				.when(columnsMock).getSearchableColumnNames();
		doReturn(asList("MASTER", "SECOND")).when(columnsMock)
				.getDefaultSearchableColumnNames();

		doReturn(String.class).when(containerMock).getType("MASTER");
		doReturn(String.class).when(containerMock).getType("SECOND");
		doReturn(String.class).when(containerMock).getType("THIRD");
		doReturn(BigDecimal.class).when(containerMock).getType("NUMERIC");
		doReturn(Timestamp.class).when(containerMock).getType("TIMESTAMP");
		doReturn(Integer.class).when(containerMock).getType("SELECT");
		doReturn(String.class).when(containerMock).getType("SELECTTEXT");

		when(columnsMock.contains("SELECT")).thenReturn(true);
		when(columnsMock.isSelection("SELECT")).thenReturn(true);
		when(columnsMock.getDropdownSelections("SELECT")).thenReturn(
				selectOptionsListMock);
		when(selectOptionsListMock.getOptions(null)).thenReturn(
				ImmutableMap.of("1", "Option 1", "2", "Option 2", "3",
						"Option 3"));
		when(selectOptionsListMock.getKey("Option 3", null)).thenReturn("3");

		when(columnsMock.contains("SELECTTEXT")).thenReturn(true);
		when(columnsMock.isSelection("SELECTTEXT")).thenReturn(true);
		when(columnsMock.getDropdownSelections("SELECTTEXT")).thenReturn(
				selectTextOptionsListMock);
		when(selectTextOptionsListMock.getOptions(null)).thenReturn(
				ImmutableMap.of("Option 1", "Option 1", "Option 2", "Option 2",
						"Option 3", "Option 3"));
		when(selectTextOptionsListMock.getKey("Option 3", null)).thenReturn(
				"Option 3");

		search = new CompoundSearch(config);
		search.searchableTablesFinder = searchableTablesFinderMock;

		ArrayList<Table> tables = new ArrayList<Table>();
		tables.add(tableMock);

		when(searchableTablesFinderMock.findSearchableTables(search, null))
				.thenReturn((tables));

		when(UI.getCurrent().getLocale()).thenReturn(Locale.GERMANY);
	}

	@Test
	public void shouldDeliverSearchableColumnsInRightOrder() {
		assertThat(
				search.getSearchableColumns().getSearchableColumnNames(),
				equalTo((Collection<String>)asList("MASTER", "SECOND", "NUMERIC", "SELECT", "SELECTTEXT",
						"THIRD", "TIMESTAMP")));
	}

	/* Text fields */

	@Test
	public void shouldConvertDefaultQueryToStartsWithFilter()
			throws ParseException {
		search.search("myterm");
		List<Filter> filters = asList((Filter) new Any(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	private void verifyReplaceFilters(List<Filter> filters) {
		verify(containerMock).replaceFilters(filters, false, true);
	}

	@Test
	public void shouldConvertEmptyQueryStringToRemoveAllFilters()
			throws ParseException {
		search.search("");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldConvertNullQueryStringToRemoveAllFilters()
			throws ParseException {
		search.search(null);
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldConvertBooleanORQuery() throws ParseException {
		search.search("MASTER:myterm OR SECOND:myterm");
		List<Filter> filters = asList((Filter) new Any(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldIgnoreInvalidPartsOfORQuery() throws ParseException {
		search.search("MASTER:myterm OR NUMERIC:invalidNumber");
		List<Filter> filters = filterList(new StartsWith("MASTER", "myterm",
				false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldFailWithFirstMessageIfAllPartsOfORQueryAreInvalid()
			throws ParseException {
		try {
			search.search("TIMESTAMP:invalidTimestamp OR NUMERIC:invalidNumber");
			fail();
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.dateConversionFailed"));
		}
	}

	private List<Filter> filterList(Filter... filterArray) {
		return asList(filterArray);
	}

	@Test
	public void shouldConvertBooleanANDQuery() throws ParseException {
		search.search("MASTER:myterm AND SECOND:myterm");
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new StartsWith("MASTER", "myterm", false),
				new StartsWith("SECOND", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldRejectBooleanMixedQuery() throws ParseException {
		try {
			search.search("MASTER:myterm AND SECOND:myterm OR THIRD:myterm");
			fail("Expected BusinessException");
		} catch (BusinessException e) {
			assertThat(
					e.getCode(),
					is("portlet.crud.error.compoundsearch.mixedBooleansProhibited"));
		}
	}

	@Test
	public void shouldConvertBooleanNotAndQuery() throws ParseException {
		search.search("NOT MASTER:myterm AND SECOND:myterm");
		List<Filter> filters = asList((Filter) new All(asList((Filter) new Not(
				asList((Filter) new StartsWith("MASTER", "myterm", false))),
				new StartsWith("SECOND", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertBooleanNotOrQuery() throws ParseException {
		search.search("NOT MASTER:myterm OR SECOND:myterm");
		List<Filter> filters = asList((Filter) new Any(asList((Filter) new Not(
				asList((Filter) new StartsWith("MASTER", "myterm", false))),
				new StartsWith("SECOND", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertBooleanMixedQueryWithBraces()
			throws ParseException {
		search.search("(MASTER:myterm AND SECOND:myterm) OR THIRD:myterm");
		Filter allFilter = new All(asList((Filter) new StartsWith("MASTER",
				"myterm", false), new StartsWith("SECOND", "myterm", false)),
				false);
		List<Filter> filters = asList((Filter) new Any(asList(allFilter,
				new StartsWith("THIRD", "myterm", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertMultiwordTermToStartsWith() throws ParseException {
		search.search("THIRD:\"my long  phrase\"");
		List<Filter> filters = asList((Filter) new StartsWith("THIRD",
				"my long  phrase", false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertMultiwordSelectionToEqualsOnIntegerKey()
			throws ParseException {
		search.search("SELECT:\"Option 3\"");
		List<Filter> filters = asList((Filter) new Equal("SELECT", 3, false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertMultiwordSelectionToEqualsOnTextualKey()
			throws ParseException {
		search.search("SELECTTEXT:\"Option 3\"");
		List<Filter> filters = asList((Filter) new Equal("SELECTTEXT",
				"Option 3", false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertDefaultNegatedQuery() throws ParseException {
		search.search("-myterm");
		List<Filter> filters = asList((Filter) new Not(asList((Filter) new Any(
				asList((Filter) new StartsWith("MASTER", "myterm", false),
						new StartsWith("SECOND", "myterm", false))))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldIgnoreNonMatchingField() throws ParseException {
		search.search("NOTINTABLE:4711");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInRangeQuery()
			throws ParseException {
		search.search("NOTINTABLE:[4711 TO 5822]");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInMatchAllQuery()
			throws ParseException {
		search.search("NOTINTABLE:\"*\"");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInPrefixQuery()
			throws ParseException {
		search.search("NOTINTABLE:hello*");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInWildcardQuery()
			throws ParseException {
		search.search("NOTINTABLE:hell*o");
		verifyRemoveAllFilters();
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInPhraseQuery()
			throws ParseException {
		search.search("NOTINTABLE:\"a b c\"");
		verifyRemoveAllFilters();
	}

	private void verifyRemoveAllFilters() {
		verifyReplaceFilters(Collections.<Filter> emptyList());
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInAnyClause() throws ParseException {
		search.search("(MASTER:1315 OR NOTINTABLE:4711)");
		List<Filter> filters = asList((Filter) new StartsWith("MASTER", "1315",
				false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldIgnoreNonMatchingFieldInAllClause() throws ParseException {
		search.search("(MASTER:1315 AND NOTINTABLE:4711)");
		List<Filter> filters = asList((Filter) new StartsWith("MASTER", "1315",
				false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertNotEqualsQuery() throws ParseException {
		search.search("-THIRD:myter");
		List<Filter> filters = asList((Filter) new Not(
				asList((Filter) new StartsWith("THIRD", "myter", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertNotNullQuery() throws ParseException {
		search.search("THIRD:\"*\"");
		List<Filter> filters = asList((Filter) new Not(
				asList((Filter) new IsNull("THIRD"))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertStartsWithQuery() throws ParseException {
		search.search("THIRD:myter*");
		List<Filter> filters = asList((Filter) new StartsWith("THIRD", "myter",
				false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertWildcardTermInBracketsToWildcard()
			throws ParseException {
		search.search("THIRD:\"*myter*\"");
		List<Filter> filters = asList((Filter) new Wildcard("THIRD", "*myter*",
				false));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertWildcardQueryWithStarInMiddle()
			throws ParseException {
		search.search("THIRD:my*ter");
		List<Filter> filters = asList((Filter) new Wildcard("THIRD", "my*ter",
				false));
		verifyReplaceFilters(filters);
	}

	/* Numeric fields */

	@Test
	public void shouldConvertToEqualForNumericFields() throws ParseException {
		search.search("NUMERIC:4711");
		List<Filter> filters = asList((Filter) new Equal("NUMERIC",
				new BigDecimal("4711")));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertSingleDateToRangeForDateFields()
			throws ParseException {
		search.search("TIMESTAMP:23.12.2013");
		Date startDate = new GregorianCalendar(2013, 11, 23).getTime();
		Date endDate = new GregorianCalendar(2013, 11, 24).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForTextFields() throws ParseException {
		search.search("MASTER:[4711 TO 5228]");
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("MASTER", "4711", true), new Less(
						"MASTER", "5228", true))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForTextFieldsWithExclusion()
			throws ParseException {
		search.search("MASTER:{4711 TO 5228}");
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("MASTER", "4711", false), new Less(
						"MASTER", "5228", false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForNumericFields() throws ParseException {
		search.search("NUMERIC:[4711 TO 5228]");
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("NUMERIC", new BigDecimal(4711), true),
				new Less("NUMERIC", new BigDecimal("5228"), true))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForNumericFieldsWithExclusion()
			throws ParseException {
		search.search("NUMERIC:{4711 TO 5228}");
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("NUMERIC", new BigDecimal(4711), false),
				new Less("NUMERIC", new BigDecimal("5228"), false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForDateFieldsWithMonthGranularity()
			throws ParseException {
		search.search("TIMESTAMP:[11.12.2013 TO 31.12.2013]");
		Date startDate = new GregorianCalendar(2013, 11, 11).getTime();
		Date endDate = new GregorianCalendar(2014, 0, 1).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldConvertRangeForDateFieldsWithMonthGranularityAndExclusion()
			throws ParseException {
		search.search("TIMESTAMP:{11.12.2013 TO 31.12.2013}");
		Date startDate = new GregorianCalendar(2013, 11, 12).getTime();
		Date endDate = new GregorianCalendar(2013, 11, 31).getTime();
		List<Filter> filters = asList((Filter) new All(asList(
				(Filter) new Greater("TIMESTAMP", startDate, true), new Less(
						"TIMESTAMP", endDate, false))));
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldFailWithReadableMessageOnUnknownQueryType()
			throws ParseException {
		try {
			search.search("MASTER:Test~");
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
			search.search("NUMERIC:Test");
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
		verifyReplaceFilters(filters);
	}

	@Test
	public void shouldFailOnInvalidQueryString() {
		try {
			search.search("*");
			fail();
		} catch (BusinessException e) {
			assertThat(e.getCode(),
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

	@Test
	public void shouldInformIfQueryIsInvalid() {
		assertThat(search.isValidQuery("*"), is(false));
	}

	@Test
	public void shouldInformIfQueryIsValid() {
		assertThat(search.isValidQuery("4711"), is(true));
	}
}
