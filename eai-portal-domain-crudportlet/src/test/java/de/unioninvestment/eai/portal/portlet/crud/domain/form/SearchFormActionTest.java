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
package de.unioninvestment.eai.portal.portlet.crud.domain.form;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.config.AllFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AnyFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ApplyFiltersConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CheckboxConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ContainsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CustomFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DateConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EndsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.EqualsFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GreaterOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.IncludeFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.LessFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.LessOrEqualFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.NotFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SQLFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchTablesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StartsWithFilterConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TimeoutException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.StaticOptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.CustomFilterMatcher;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.EndsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Not;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Nothing;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLWhereFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.CrudApplication;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;

public class SearchFormActionTest {

	static class CrudApplicationMock extends PortletApplication implements
			CrudApplication {

		private boolean initializing;

		public CrudApplicationMock(boolean initializing) {
			this.initializing = initializing;
		}

		@Override
		public boolean isInitializing() {
			return initializing;
		}

		@Override
		public void addToView(com.vaadin.ui.Component component) {
		}

		@Override
		public void removeAddedComponentsFromView() {
		}

		@Override
		public void init() {
		}

		public void setInitializing(boolean initializing) {
			this.initializing = initializing;
		}

	}

	private SearchFormAction searchAction;

	private FormFields formFields;

	@Mock
	private Form formMock;

	@Mock
	private Page pageMock;

	@Mock
	private Table tableMock;

	@Mock
	private DataContainer dbContainerMock;

	@Mock
	private DataContainer dbContainerMock2;

	@Mock
	private FormActionConfig formActionConfigMock;

	@Mock
	private FormActionConfig formActionConfigMock2;

	@Mock
	private OptionListFormField selectionFormField;

	@Mock
	private Table table2Mock;

	@Mock
	private Form form2Mock;

	@Mock
	private Tabs tabsMock;

	@Mock
	private Tab tab2Mock;

	@Mock
	private Tab tab1Mock;

	@Mock
	private Table table3Mock;

	private ApplyFiltersConfig filtersConfig;

	@Mock
	private Portlet portletMock;

	@Mock
	private TableConfig tableConfigMock;
	@Mock
	private TableConfig table2ConfigMock;

	@Mock
	private SQLWhereFactory sqlWhereFactoryMock;

	@Mock
	private Form formMock2;

	@Mock
	private SearchConfig searchConfigMock;

	@Mock
	private SearchConfig searchConfigMock2;

	@Mock
	private ApplyFiltersConfig applyFiltersMock;

	@Mock
	private ApplyFiltersConfig applyFiltersMock2;

	@Mock
	private SQLFilterConfig sqlFilterConfigMock;

	@Mock
	private FormFields formFieldsMock;

	@Mock
	private MultiOptionListFormField multiSelectFormFieldMock;

	@Mock
	private CustomFilterFactory customFilterFactoryMock;

	private CrudApplicationMock app;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		searchAction = new SearchFormAction(formActionConfigMock, portletMock);
		searchAction.setWhereFactory(sqlWhereFactoryMock);

		when(selectionFormField.getName()).thenReturn("selection1");
		when(selectionFormField.getValue()).thenReturn("value1");

		when(tableMock.getId()).thenReturn("table");

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"), selectionFormField);
		when(formMock.getFields()).thenReturn(formFields);

		// register application with thread
		app = new CrudApplicationMock(false);
		app.onRequestStart(null, null);

	}

	@After
	public void tearDown() {
		// deregister from thread
		app.onRequestEnd(null, null);
	}

	@Test
	public void shouldIndicateExisitingWhereFilter() {
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		when(searchConfigMock.getApplyFilters()).thenReturn(applyFiltersMock);
		ArrayList<FilterConfig> filters = new ArrayList<FilterConfig>();
		filters.add(sqlFilterConfigMock);
		when(applyFiltersMock.getFilters()).thenReturn(filters);

		assertThat(searchAction.whereFilterExists(), is(true));
	}

	@Test
	public void shouldReturnFalseIfNoSQLFilter() {
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		when(searchConfigMock.getApplyFilters()).thenReturn(applyFiltersMock);
		ArrayList<FilterConfig> filters = new ArrayList<FilterConfig>();
		when(applyFiltersMock.getFilters()).thenReturn(filters);

		assertThat(searchAction.whereFilterExists(), is(false));
	}

	@Test
	public void shouldApplyDefaultStartsWithFilterOnTextColumn() {

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", String.class);

		mockPageWithFormAndTable();

		searchAction.execute(formMock);

		verify(dbContainerMock)
				.replaceFilters(
						asList((Filter) new StartsWith("field1",
								"filterValue1", true)), false);
	}

	@Test
	public void shouldApplyDefaultStartsWithFilterOnMultipleColumnsWrappingMultipleFiltersByAllFilter() {

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"), createFormField("field2", "title2",
				"prompt2", "filterValue2"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", String.class);
		stubContainerColumnType("field2", String.class);

		mockPageWithFormAndTable();

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new StartsWith("field1", "filterValue1", true),
						new StartsWith("field2", "filterValue2", true)), false);
	}

	private void stubContainerColumnType(String columnName, final Class<?> type) {
		when(dbContainerMock.getType(columnName)).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return type;
					}
				});
	}

	@Test
	public void shouldNotApplyFilterOnMissingColumn() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false);
	}

	@Test
	public void shouldCallReplaceWithTimeout() {
		searchAction.setTimeout(42);

		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false,
				42);
	}

	@Test(expected = BusinessException.class)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldCallReplaceWithTimeoutOnSQLTimeout() {
		searchAction.setTimeout(42);

		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);
		doThrow(new TimeoutException("portlet.crud.warn.searchQueryTimeout"))
				.when(dbContainerMock).replaceFilters(new ArrayList<Filter>(),
						false, 42);

		searchAction.execute(formMock);
		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false,
				42);
		ArgumentCaptor<List> filterListArgumentCaptor = ArgumentCaptor
				.forClass(List.class);
		verify(dbContainerMock).replaceFilters(
				filterListArgumentCaptor.capture(), false);
		List filterList = filterListArgumentCaptor.getValue();
		assertEquals(1, filterList.size());
		assertTrue(filterList.iterator().next() instanceof Nothing);
	}

	@Test
	public void shouldNotApplyFilterWithExplicitOtherTable() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue", String.class);

		createExplicitFilterConfiguration();

		GreaterOrEqualFilterConfig filter = createGreaterOrEqualsFilter(
				"field1", "column1");
		filter.setTable("table2");
		filtersConfig.getFilters().add(filter);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false);
	}

	@Test
	public void shouldNotApplyFilterOnEmptyField() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", ""));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", String.class);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false);
	}

	@Test
	public void shouldApplyDefaultEqualFilterForSelectionFields() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createSelectFormField("field1", "title1",
				"prompt1", "filterValue1",
				Collections.singletonMap("filterValue1", "Filterwert 1")));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", String.class);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Equal("field1", "filterValue1")), false);
	}

	@Test
	public void shouldApplyDefaultEqualFiltersForDateField() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createDateFormField("field1", "title1",
				"prompt1", "dd.MM.yyyy", "20.05.2011"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", Timestamp.class);

		searchAction.execute(formMock);

		Greater beginDateFilter = new Greater("field1", new GregorianCalendar(
				2011, 04, 20).getTime(), true);
		Less endDateFilter = new Less("field1", timestamp(2011, 4, 21), false);
		verify(dbContainerMock).replaceFilters(
				asList((Filter) new All(Arrays.asList((Filter) beginDateFilter,
						endDateFilter))), false);
	}

	private Timestamp timestamp(int i, int j, int k) {
		return new Timestamp(new GregorianCalendar(i, j, k).getTimeInMillis());
	}

	@Test
	public void shouldNotFilterOnInitializationIfTableHasANothingPolicy() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "1.0"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", BigDecimal.class);

		app.setInitializing(true);
		when(dbContainerMock.getFilterPolicy())
				.thenReturn(FilterPolicy.NOTHING);

		searchAction.execute(formMock);

		verify(dbContainerMock, never()).replaceFilters(any(List.class),
				eq(false));
	}

	@Test
	public void shouldNotFilterOnInitializationIfTableHasANothingAtAllPolicy() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "1.0"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", BigDecimal.class);

		app.setInitializing(true);
		when(dbContainerMock.getFilterPolicy()).thenReturn(
				FilterPolicy.NOTHING_AT_ALL);

		searchAction.execute(formMock);

		verify(dbContainerMock, never()).replaceFilters(any(List.class),
				eq(false));
	}

	@Test
	public void shouldApplyDefaultEqualFilterForCheckboxFields() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createCheckboxFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", String.class);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Equal("field1", "filterValue1")), false);
	}

	@Test
	public void shouldApplyDefaultEqualFilterToNumberField() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "1.0"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("field1", BigDecimal.class);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Equal("field1", new BigDecimal("1"))),
				false);
	}

	@Test
	public void shouldApplyConfiguredEqualFilter() {
		verifyFilterByConfiguration(createEqualsFilter("field1", "column1"),
				new Equal("column1", "filterValue1"));
	}

	@Test
	public void shouldApplyConfiguredEqualFilterToNumberField() {
		verifyFilterByConfiguration(createEqualsFilter("field1", "column1"),
				new Equal("column1", 1.0), "1.0", Double.class);
	}

	@Test
	public void shouldParsingNumberFieldWithWrongLocal() {
		verifyFilterByConfiguration(createEqualsFilter("field1", "column1"),
				new Equal("column1", 1.0), "1.0", Double.class);
	}

	@Test
	public void shouldApplyConfiguredStartsWithFilter() {
		verifyFilterByConfiguration(
				createStartsWithFilter("field1", "column1", true),
				new StartsWith("column1", "filterValue1", true));
	}

	@Test
	public void shouldApplyConfiguredContainsFilter() {
		verifyFilterByConfiguration(
				createContainsFilter("field1", "column1", true), new Contains(
						"column1", "filterValue1", true));
	}

	@Test
	public void shouldApplyConfiguredStartsWithFilterAsCaseInsensitive() {
		verifyFilterByConfiguration(
				createStartsWithFilter("field1", "column1", false),
				new StartsWith("column1", "filterValue1", false));
	}

	@Test
	public void shouldApplyConfiguredCustomFilter() {
		searchAction.setCustomFilterFactory(customFilterFactoryMock);
		CustomFilterMatcher matcher = new CustomFilterMatcher() {
			@Override
			public boolean matches(ContainerRow row) {
				return true;
			}
		};
		CustomFilterConfig customFilterConfig = createCustomFilter();
		CustomFilter customFilter = new CustomFilter(matcher, false);
		when(customFilterFactoryMock.createCustomFilter(customFilterConfig))
				.thenReturn(customFilter);

		verifyFilterByConfiguration(customFilterConfig, customFilter);
	}

	@Test
	public void shouldApplyConfiguredEndsWithFilter() {
		verifyFilterByConfiguration(
				createEndsWithFilter("field1", "column1", true), new EndsWith(
						"column1", "filterValue1", true));
	}

	@Test
	public void shouldApplyConfiguredGreaterFilter() {
		verifyFilterByConfiguration(createGreaterFilter("field1", "column1"),
				new Greater("column1", "filterValue1", false));
	}

	@Test
	public void shouldApplyConfiguredGreaterOrEqualsFilterWithDateField() {
		verifyDateFilterByConfiguration(
				createGreaterOrEqualsFilter("field1", "column1"), new Greater(
						"column1",
						new GregorianCalendar(2011, 4, 20).getTime(), true),
				"20.05.2011", java.sql.Date.class);
	}

	@Test
	public void shouldApplyConfiguredGreaterFilterWithDateField() {
		verifyDateFilterByConfiguration(
				createGreaterFilter("field1", "column1"), new Greater(
						"column1",
						new GregorianCalendar(2011, 4, 21).getTime(), true),
				"20.05.2011", java.sql.Date.class);
	}

	@Test
	public void shouldApplyConfiguredLessFilterWithDateField() {
		verifyDateFilterByConfiguration(
				createLessFilter("field1", "column1"),
				new Less("column1", new GregorianCalendar(2011, 4, 20)
						.getTime(), false), "20.05.2011", java.sql.Date.class);
	}

	@Test
	public void shouldApplyConfiguredLessOrEqualFilterWithDateField() {
		verifyDateFilterByConfiguration(
				createLessOrEqualsFilter("field1", "column1"), new Less(
						"column1",
						new GregorianCalendar(2011, 4, 21).getTime(), false),
				"20.05.2011", java.sql.Date.class);
	}

	@Test
	public void shouldApplyConfiguredGreaterOrEqualFilter() {
		verifyFilterByConfiguration(
				createGreaterOrEqualsFilter("field1", "column1"), new Greater(
						"column1", "filterValue1", true));
	}

	@Test
	public void shouldApplyConfiguredLessOrEqualFilter() {
		verifyFilterByConfiguration(
				createLessOrEqualsFilter("field1", "column1"), new Less(
						"column1", "filterValue1", true));
	}

	@Test
	public void shouldApplyConfiguredLessFilter() {
		verifyFilterByConfiguration(createLessFilter("field1", "column1"),
				new Less("column1", "filterValue1", false));
	}

	private void verifyFilterByConfiguration(FilterConfig filterConfig,
			Filter expectedFilter) {
		verifyFilterByConfiguration(filterConfig, expectedFilter,
				"filterValue1", String.class);
	}

	private void verifyFilterByConfiguration(FilterConfig filterConfig,
			Filter expectedFilter, String fieldValue, Class<?> columnDataType) {
		prepareSimpleFormFieldToStringColumnSearch(fieldValue, columnDataType);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(filterConfig);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(asList((Filter) expectedFilter),
				false);
	}

	private void verifyDateFilterByConfiguration(FilterConfig filterConfig,
			Filter expectedFilter, String fieldValue, Class<?> columnDataType) {

		mockPageWithFormAndTable();

		formFields = new FormFields(createDateFormField("field1", "title1",
				"prompt1", "dd.MM.yyyy", fieldValue));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("column1", columnDataType);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(filterConfig);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(asList((Filter) expectedFilter),
				false);
	}

	@Test
	public void shouldApplyAnyFilterWithSubfilters() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createAnyFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Any(Arrays.asList((Filter) new StartsWith(
						"column1", "filterValue1", true), new EndsWith(
						"column1", "filterValue1", true)))), false);
	}

	@Test
	public void shouldNotApplyEmptyAnyFilter() {
		prepareSimpleFormFieldToStringColumnSearch("", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createAnyFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new LinkedList<Filter>(), false);
	}

	@Test
	public void shouldNotApplyEmptyAllFilter() {
		prepareSimpleFormFieldToStringColumnSearch("", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createAllFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new LinkedList<Filter>(), false);
	}

	@Test
	public void shouldApplySqlWhereFilter() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig
				.getFilters()
				.add(createSqlWhereFilter("column1",
						"in (select column1 from bla where x = $field.CDATE.value)"));

		SQLFilter filter = new SQLFilter("column1",
				"in (select column1 from bla where x = ?)",
				Arrays.asList((Object) "Test"));

		when(
				sqlWhereFactoryMock
						.createFilter("column1",
								"in (select column1 from bla where x = $field.CDATE.value)"))
				.thenReturn(filter);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(asList((Filter) filter), false);
	}

	@Test
	public void shouldApplyNotFilterWithSubfiltersAsEncapsulatedByAll() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createNotFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Not(Arrays.asList((Filter) new StartsWith(
						"column1", "filterValue1", true), new EndsWith(
						"column1", "filterValue1", true)))), false);
	}

	@Test
	public void shouldNotApplyEmptyNotFilter() {
		prepareSimpleFormFieldToStringColumnSearch("", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createNotFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new LinkedList<Filter>(), false);
	}

	@Test
	public void shouldApplyAllFilterWithSubfilters() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createAllFilter(
						createStartsWithFilter("field1", "column1", true),
						createEndsWithFilter("field1", "column1", true)));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new All(Arrays.asList((Filter) new StartsWith(
						"column1", "filterValue1", true), new EndsWith(
						"column1", "filterValue1", true)))), false);
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowBusinessExceptionWhenRequiresFilterAndFormHasNoFilter() {
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		when(searchConfigMock.isRequiresFilter()).thenReturn(true);
		searchAction = new SearchFormAction(formActionConfigMock, portletMock);
		when(formMock.hasFilter()).thenReturn(false);
		searchAction.execute(formMock);
	}

	@Test
	public void shouldSearchWhenRequiresFilterAndFormHasFilter() {
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		when(searchConfigMock.isRequiresFilter()).thenReturn(true);
		searchAction = new SearchFormAction(formActionConfigMock, portletMock);
		when(formMock.hasFilter()).thenReturn(true);

		mockPageWithFormAndTable();

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(anyListOf(Filter.class),
				anyBoolean());
	}

	@Test
	public void shouldSearchWhenNotRequiresFilterAndFormHasNoFilter() {
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		when(searchConfigMock.isRequiresFilter()).thenReturn(false);
		searchAction = new SearchFormAction(formActionConfigMock, portletMock);
		when(formMock.hasFilter()).thenReturn(false);

		mockPageWithFormAndTable();

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(anyListOf(Filter.class),
				anyBoolean());
	}

	private FilterConfig createAnyFilter(FilterConfig... subfilters) {
		AnyFilterConfig config = new AnyFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	private FilterConfig createAllFilter(FilterConfig... subfilters) {
		AllFilterConfig config = new AllFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	private FilterConfig createNotFilter(FilterConfig... subfilters) {
		NotFilterConfig config = new NotFilterConfig();
		config.getFilters().addAll(Arrays.asList(subfilters));
		return config;
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionOnUnkownFormField() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createEqualsFilter("unknownField", "column1"));

		searchAction.execute(formMock);
	}

	private void prepareSimpleFormFieldToStringColumnSearch(String fieldValue,
			Class<?> columnDataType) {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", fieldValue));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("column1", columnDataType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnUnsupportedColumnType() {
		mockPageWithFormAndTable();

		formFields = new FormFields(createFormField("field1", "title1",
				"prompt1", "filterValue1"));
		when(formMock.getFields()).thenReturn(formFields);
		stubContainerColumnType("column1", Collection.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(createEqualsFilter("field1", "column1"));

		searchAction.execute(formMock);
	}

	@Test
	public void shouldIgnoreExplicitFilterForUnkownColumn() {
		prepareSimpleFormFieldToStringColumnSearch("filterValue1", String.class);

		createExplicitFilterConfiguration();
		filtersConfig.getFilters().add(
				createEqualsFilter("field1", "unknownColumn"));

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(new ArrayList<Filter>(), false);
	}

	private void createExplicitFilterConfiguration() {
		SearchConfig search = new SearchConfig();
		filtersConfig = new ApplyFiltersConfig();
		search.setApplyFilters(filtersConfig);
		when(formActionConfigMock.getSearch()).thenReturn(search);
	}

	private EqualsFilterConfig createEqualsFilter(String fieldName,
			String columnName) {
		EqualsFilterConfig filter = new EqualsFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	private GreaterFilterConfig createGreaterFilter(String fieldName,
			String columnName) {
		GreaterFilterConfig filter = new GreaterFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	private GreaterOrEqualFilterConfig createGreaterOrEqualsFilter(
			String fieldName, String columnName) {
		GreaterOrEqualFilterConfig filter = new GreaterOrEqualFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	private LessFilterConfig createLessFilter(String fieldName,
			String columnName) {
		LessFilterConfig filter = new LessFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	private CustomFilterConfig createCustomFilter() {
		CustomFilterConfig filter = new CustomFilterConfig();
		filter.setFilter(new GroovyScript("abcde"));
		return filter;
	}

	private LessOrEqualFilterConfig createLessOrEqualsFilter(String fieldName,
			String columnName) {
		LessOrEqualFilterConfig filter = new LessOrEqualFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		return filter;
	}

	private StartsWithFilterConfig createStartsWithFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		StartsWithFilterConfig filter = new StartsWithFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	private EndsWithFilterConfig createEndsWithFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		EndsWithFilterConfig filter = new EndsWithFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	private ContainsFilterConfig createContainsFilter(String fieldName,
			String columnName, boolean caseSensitive) {
		ContainsFilterConfig filter = new ContainsFilterConfig();
		filter.setField(fieldName);
		filter.setColumn(columnName);
		filter.setCaseSensitive(caseSensitive);
		return filter;
	}

	private FilterConfig createSqlWhereFilter(String columnName,
			String whereCondition) {
		SQLFilterConfig filter = new SQLFilterConfig();
		filter.setColumn(columnName);
		filter.setWhere(whereCondition);
		return filter;
	}

	@Test
	public void shouldFindTableBelowForm() {
		mockPageWithFormAndTable();

		List<Table> tables = searchAction.findSearchableTables(formMock);

		assertThat(tables.size(), is(1));
		assertThat(tables.get(0), is(tableMock));
	}

	private void mockPageWithFormAndTable() {
		when(formMock.getPanel()).thenReturn(pageMock);
		when(pageMock.findNextElement(Component.class, formMock)).thenReturn(
				tableMock);
		when(tableMock.getContainer()).thenReturn(dbContainerMock);
	}

	@Test
	public void shouldIncludeFilters() {
		mockPageWith2FormsAnd2Tables();

		searchAction = new SearchFormAction(formActionConfigMock2, portletMock);

		searchAction.execute(formMock2);

		ArrayList<Filter> filters = new ArrayList<Filter>();
		filters.add(new Equal("column1", "filterValue1"));
		verify(dbContainerMock2, times(1)).replaceFilters(
				asList((Filter) new All(filters)), false);
	}

	private void mockPageWith2FormsAnd2Tables() {
		when(formActionConfigMock.getId()).thenReturn("id1");

		List<FilterConfig> filterConfigs = new ArrayList<FilterConfig>();
		EqualsFilterConfig filterConfig = new EqualsFilterConfig();
		filterConfig.setField("field1");
		filterConfig.setColumn("column1");
		filterConfigs.add(filterConfig);

		when(applyFiltersMock.getFilters()).thenReturn(filterConfigs);
		when(searchConfigMock.getApplyFilters()).thenReturn(applyFiltersMock);
		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		SearchFormAction searchFormAction = new SearchFormAction(
				formActionConfigMock, portletMock);

		FormActionConfig formActionConfig = new FormActionConfig();
		formActionConfig.setId("id1");
		formActionConfig.setTitle("title1");
		FormAction fAction = new FormAction(portletMock, formActionConfig,
				searchFormAction, null);

		FormActions formActions = new FormActions(asList(fAction));
		formActions.attachToForm(formMock);
		when(formMock.getActions()).thenReturn(formActions);
		when(formMock.getFields()).thenReturn(formFields);
		when(formMock.getPanel()).thenReturn(pageMock);

		// ************

		List<FilterConfig> filterConfigs2 = new ArrayList<FilterConfig>();
		IncludeFilterConfig filterConfig2 = new IncludeFilterConfig();
		filterConfig2.setAction(formActionConfigMock);
		filterConfigs2.add(filterConfig2);

		when(applyFiltersMock2.getFilters()).thenReturn(filterConfigs2);
		when(searchConfigMock2.getApplyFilters()).thenReturn(applyFiltersMock2);
		when(formActionConfigMock2.getSearch()).thenReturn(searchConfigMock2);
		SearchFormAction searchFormAction2 = new SearchFormAction(
				formActionConfigMock2, portletMock);

		FormActionConfig formActionConfig2 = new FormActionConfig();
		formActionConfig2.setId("id1");
		formActionConfig2.setTitle("title1");
		FormAction fAction2 = new FormAction(portletMock, formActionConfig2,
				searchFormAction2, null);

		FormActions actions2 = new FormActions(asList(fAction2));
		actions2.attachToForm(formMock2);
		when(formMock2.getActions()).thenReturn(actions2);
		when(formMock2.getFields()).thenReturn(formFields);
		when(formMock2.getPanel()).thenReturn(pageMock);
		// ******

		when(pageMock.findNextElement(Component.class, formMock)).thenReturn(
				tableMock);

		when(pageMock.findNextElement(Component.class, tableMock)).thenReturn(
				formMock2);

		when(pageMock.findNextElement(Component.class, formMock2)).thenReturn(
				table2Mock);

		when(tableMock.getContainer()).thenReturn(dbContainerMock);

		when(table2Mock.getContainer()).thenReturn(dbContainerMock2);

		when(portletMock.getElementById("id1")).thenReturn(fAction);
		// *****

		when(dbContainerMock.getType(any(String.class))).thenAnswer(
				new Answer<Class<?>>() {
					@Override
					public Class<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return String.class;
					}
				});

		when(dbContainerMock2.getType(any(String.class))).thenAnswer(
				new Answer<Class<?>>() {
					@Override
					public Class<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return String.class;
					}
				});

	}

	@Test
	public void shouldHandleMultiSelectFilter() {
		mockPageWithMultiSelectFieldFormAndTable(false);

		searchAction = new SearchFormAction(formActionConfigMock2, portletMock);

		searchAction.execute(formMock);

		verify(dbContainerMock).replaceFilters(
				asList((Filter) new Any(asList((Filter) new Equal("column1",
						"b"), new Equal("column1", "c"), new Equal("column1",
						"a")))), false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWithInvalidMultiSelectFilterConfig() {
		mockPageWithMultiSelectFieldFormAndTable(true);

		searchAction = new SearchFormAction(formActionConfigMock, portletMock);

		searchAction.execute(formMock);
	}

	private void mockPageWithMultiSelectFieldFormAndTable(
			boolean invalidFilterConfig) {
		when(formActionConfigMock.getId()).thenReturn("id1");

		when(formMock.getPanel()).thenReturn(pageMock);
		when(formMock.getFields()).thenReturn(formFieldsMock);
		when(formFieldsMock.get("multiSelectField")).thenReturn(
				multiSelectFormFieldMock);

		when(multiSelectFormFieldMock.getName()).thenReturn("column1");
		when(multiSelectFormFieldMock.getValues()).thenReturn(
				new HashSet<String>(Arrays.asList("a", "b", "c")));

		/*******/
		if (invalidFilterConfig) {
			List<FilterConfig> filterConfigs = new ArrayList<FilterConfig>();
			ContainsFilterConfig filterConfig = new ContainsFilterConfig();
			filterConfig.setField("multiSelectField");
			filterConfig.setColumn("column1");
			filterConfigs.add(filterConfig);

			when(applyFiltersMock.getFilters()).thenReturn(filterConfigs);

			when(searchConfigMock.getApplyFilters()).thenReturn(
					applyFiltersMock);
		}
		/*********/
		else {
			formFields = new FormFields(multiSelectFormFieldMock);
			when(formMock.getFields()).thenReturn(formFields);
		}

		when(formActionConfigMock.getSearch()).thenReturn(searchConfigMock);
		// when(searchConfigMock.getApplyFilters()).thenReturn(filtersConfig);
		SearchFormAction searchFormAction = new SearchFormAction(
				formActionConfigMock, portletMock);

		FormActionConfig formActionConfig = new FormActionConfig();
		formActionConfig.setId("id1");
		formActionConfig.setTitle("title1");
		FormAction fAction = new FormAction(portletMock, formActionConfig,
				searchFormAction, null);

		FormActions formActions = new FormActions(asList(fAction));
		formActions.attachToForm(formMock);

		when(formMock.getActions()).thenReturn(formActions);

		when(formMock.getPanel()).thenReturn(pageMock);

		when(pageMock.findNextElement(Component.class, formMock)).thenReturn(
				tableMock);

		when(tableMock.getContainer()).thenReturn(dbContainerMock);

		when(portletMock.getElementById("id1")).thenReturn(fAction);
		// *****

		when(dbContainerMock.getType(any(String.class))).thenAnswer(
				new Answer<Class<?>>() {
					@Override
					public Class<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return String.class;
					}
				});
	}

	@Test
	public void shouldFindTwoTablesBelowForm() {
		mockPageWithFormAndTable();
		when(pageMock.findNextElement(Component.class, tableMock)).thenReturn(
				table2Mock);

		List<Table> tables = searchAction.findSearchableTables(formMock);

		assertThat(tables.size(), is(2));
		assertThat(tables.get(0), is(tableMock));
		assertThat(tables.get(1), is(table2Mock));
	}

	@Test
	public void shouldReturnConfiguredTables() {
		when(tableConfigMock.getId()).thenReturn("table");
		when(table2ConfigMock.getId()).thenReturn("table2");

		when(portletMock.getElementById("table")).thenReturn(tableMock);
		when(portletMock.getElementById("table2")).thenReturn(table2Mock);

		SearchConfig search = new SearchConfig();
		search.setTables(new SearchTablesConfig());
		SearchTableConfig searchTableConfig = new SearchTableConfig();
		searchTableConfig.setId(tableConfigMock);
		search.getTables().getTable().add(searchTableConfig);
		SearchTableConfig searchTable2Config = new SearchTableConfig();
		searchTable2Config.setId(table2ConfigMock);
		search.getTables().getTable().add(searchTable2Config);
		when(formActionConfigMock.getSearch()).thenReturn(search);

		List<Table> tables = searchAction.findSearchableTables(formMock);

		assertThat(tables.size(), is(2));
		assertThat(tables.get(0), is(tableMock));
		assertThat(tables.get(1), is(table2Mock));
	}

	@Test
	public void shouldFindNotFindTableBelowForm() {
		mockPageWithFormAndTable();
		when(pageMock.findNextElement(Component.class, tableMock)).thenReturn(
				form2Mock);
		when(pageMock.findNextElement(Component.class, form2Mock)).thenReturn(
				table2Mock);

		List<Table> tables = searchAction.findSearchableTables(formMock);

		assertThat(tables.size(), is(1));
		assertThat(tables.get(0), is(tableMock));
	}

	@Test
	public void shouldFindTablesInSubTabs() {
		mockPageWithFormAndTable();
		when(pageMock.findNextElement(Component.class, tableMock)).thenReturn(
				tabsMock);
		when(tabsMock.getElements()).thenReturn(
				Arrays.asList(tab1Mock, tab2Mock));
		when(tab1Mock.findNextElement(Component.class, null)).thenReturn(
				table2Mock);
		when(tab2Mock.findNextElement(Component.class, null)).thenReturn(
				table3Mock);

		List<Table> tables = searchAction.findSearchableTables(formMock);

		assertThat(tables.size(), is(3));
		assertThat(tables.get(0), is(tableMock));
		assertThat(tables.get(1), is(table2Mock));
		assertThat(tables.get(2), is(table3Mock));
	}

	private FormField createFormField(String name, String title,
			String inputPrompt, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		FormField field = new FormField(config);
		field.setValue(value);
		return field;
	}

	private FormFieldConfig createFormFieldConfig(String name, String title,
			String inputPrompt) {
		FormFieldConfig config = new FormFieldConfig();
		config.setName(name);
		config.setTitle(title);
		config.setInputPrompt(inputPrompt);
		return config;
	}

	private FormField createDateFormField(String name, String title,
			String inputPrompt, String format, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		config.setDate(new DateConfig());
		config.getDate().setFormat(format);
		DateFormField field = new DateFormField(config);
		field.setValue(value);
		return field;
	}

	private FormField createSelectFormField(String name, String title,
			String inputPrompt, String value, Map<String, String> selections) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		StaticOptionList selection = new StaticOptionList(selections);
		OptionListFormField field = new OptionListFormField(config, selection);
		field.setValue(value);
		return field;
	}

	private FormField createCheckboxFormField(String name, String title,
			String inputPrompt, String value) {
		FormFieldConfig config = createFormFieldConfig(name, title, inputPrompt);
		CheckboxConfig cbc = new CheckboxConfig();
		config.setCheckbox(cbc);

		CheckBoxFormField field = new CheckBoxFormField(config);
		field.setValue(value);
		return field;
	}

}
