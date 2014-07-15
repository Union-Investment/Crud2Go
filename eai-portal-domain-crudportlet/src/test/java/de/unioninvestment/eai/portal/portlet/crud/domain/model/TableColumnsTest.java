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
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;

import de.unioninvestment.eai.portal.portlet.crud.config.DateDisplayType;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectDisplayType;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;

public class TableColumnsTest {

	private TableColumn column1;
	private TableColumn column2;
	private TableColumn column3;
	private TableColumn column4;
	private List<TableColumn> columnsList;
	private TableColumns tableColumns;
	private DateTableColumn column5;
	private CheckBoxTableColumn column6;
	private OptionList optionList;
	private SelectionTableColumn column7;

	@Mock
	private Table tableMock;
	@Mock
	private DataContainer containerMock;
	private Map<String, String> options;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		options = ImmutableMap.of("key1", "value1", "key2", "value2");
		optionList = new StaticOptionList(options);

		column1 = new TableColumn.Builder() //
				.name("name1") //
				.title("title1") //
				.longTitle("Long Title 1") //
				.hiddenStatus(Hidden.FALSE) //
				.editableDefault(false) //
				.primaryKey(true) //
				.multiline(false) //
				.width(100) //
				.inputPrompt("prompt1") //
				.searchable(Searchable.DEFAULT) //
				.build();
		column2 = new SelectionTableColumn.Builder() //
				.name("name2") //
				.title("title2") //
				.longTitle("Long Title 2") //
				.hiddenStatus(Hidden.IN_TABLE) //
				.editableDefault(true) //
				.primaryKey(false) //
				.multiline(false) //
				.width(100) //
				.inputPrompt("") //
				.optionList(optionList) //
				.searchable(Searchable.DEFAULT) //
				.build();
		column3 = new TableColumn.Builder() //
				.name("name3") //
				.title("title3")//
				.longTitle("Long Title 3") //
				.hiddenStatus(Hidden.TRUE) //
				.editableDefault(true) //
				.primaryKey(false) //
				.multiline(true) //
				.width(100) //
				.build();
		column4 = new TableColumn.Builder() //
				.name("name4") //
				.title("title4") //
				.longTitle("Long Title 4") //
				.hiddenStatus(Hidden.IN_FORM) //
				.editableDefault(true) //
				.primaryKey(false) //
				.multiline(true).width(100) //
				.inputPrompt("prompt1") //
				.build();
		column5 = new DateTableColumn.Builder() //
				.name("name5") //
				.dateDisplayType(DateDisplayType.INPUT) //
				.displayFormat("dd.MM.yyyy") //
				.build();
		column6 = new CheckBoxTableColumn.Builder() //
				.name("name6") //
				.searchable(Searchable.FALSE) //
				.checkedValue("1") //
				.uncheckedValue("0") //
				.build();
		column7 = new SelectionTableColumn.Builder() //
				.name("name7") //
				.title("title2") //
				.longTitle("Long Title 2") //
				.hiddenStatus(Hidden.IN_TABLE) //
				.editableDefault(true) //
				.primaryKey(false) //
				.multiline(false) //
				.width(100) //
				.inputPrompt("") //
				.optionList(optionList) //
				.displayType(SelectDisplayType.TOKENS) //
				.searchable(Searchable.FALSE) //
				.build();
		columnsList = asList(column1, column2, column3, column4, column5,
				column6, column7);
		tableColumns = new TableColumns(columnsList);

		when(tableMock.getContainer()).thenReturn(containerMock);
	}

	@Test
	public void shouldReturnOrderedColumnIterator() {
		Iterator<TableColumn> it = tableColumns.iterator();
		assertThat(it.next(), is(column1));
		assertThat(it.next(), is(column2));
		assertThat(it.next(), is(column3));
		assertThat(it.next(), is(column4));
		assertThat(it.next(), is((TableColumn) column5));
		assertThat(it.next(), is((TableColumn) column6));
		assertThat(it.next(), is((TableColumn) column7));
		assertThat(it.hasNext(), is(false));
	}

	@Test
	public void shouldReturnColumnsByName() {
		assertThat(tableColumns.get("name2"), is(column2));
	}

	@Test(expected = BusinessException.class)
	public void shouldFailReturningUnknownColumn() {
		tableColumns.get("unknown");
	}

	@Test
	public void shouldReturnOrderedListOfAllNames() {
		assertThat(
				tableColumns.getAllNames(),
				is(asList("name1", "name2", "name3", "name4", "name5", "name6",
						"name7")));
	}

	@Test
	public void shouldReturnPrimaryKeyNames() {
		assertThat(tableColumns.getPrimaryKeyNames(), is(asList("name1")));
	}

	@Test
	public void shouldReturnVisibleNamesForTable() {
		assertThat(tableColumns.getVisibleNamesForTable(),
				is(asList("name1", "name4", "name5", "name6")));
	}

	@Test
	public void shouldReturnVisibleNamesForForm() {
		assertThat(tableColumns.getVisibleNamesForForm(),
				is(asList("name1", "name2", "name5", "name6", "name7")));
	}

	@Test
	public void shouldReturnVisibleNamesForFormWithoutGeneratedCols() {
		TableColumn gencol = new TableColumn.Builder().name("genCol")
				.hiddenStatus(Hidden.FALSE).editableDefault(false)
				.primaryKey(true).multiline(false).width(100).build();
		CustomColumnGenerator scriptColumnGeneratorMock = mock(CustomColumnGenerator.class);
		gencol.setCustomColumnGenerator(scriptColumnGeneratorMock);
		columnsList = asList(column1, column2, column3, column4, gencol);
		tableColumns = new TableColumns(columnsList);
		assertThat(tableColumns.getVisibleNamesForForm(),
				is(asList("name1", "name2")));
	}

	@Test
	public void shouldReturnHiddenNamesForFormWithoutGeneratedCols() {
		TableColumn gencol = new TableColumn.Builder().name("genCol")
				.hiddenStatus(Hidden.IN_TABLE).editableDefault(false)
				.primaryKey(true).multiline(false).width(100).build();
		CustomColumnGenerator scriptColumnGeneratorMock = mock(CustomColumnGenerator.class);
		gencol.setCustomColumnGenerator(scriptColumnGeneratorMock);
		columnsList = asList(column1, column2, column3, column4, gencol);
		tableColumns = new TableColumns(columnsList);
		assertThat(tableColumns.getVisibleNamesForForm(),
				is(asList("name1", "name2")));
	}

	@Test
	public void shouldReturnMultilineNames() {
		assertThat(tableColumns.getMultilineNames(),
				is(asList("name3", "name4")));
	}

	@Test
	public void shouldReturnMultilineFlagByName() {
		assertThat(tableColumns.isMultiline("name2"), is(false));
		assertThat(tableColumns.isMultiline("name3"), is(true));
	}

	@Test
	public void shouldReturnInputPromptIfNotEmpty() {
		assertThat(tableColumns.getInputPrompt("name1"), is("prompt1"));
		assertThat(tableColumns.getInputPrompt("name2"), is(nullValue()));
		assertThat(tableColumns.getInputPrompt("name3"), is(nullValue()));
	}

	@Test
	public void shouldReturnDropdownFlagByName() {
		assertThat(tableColumns.isComboBox("name2"), is(true));
		assertThat(tableColumns.isComboBox("name3"), is(false));
	}

	@Test
	public void shouldReturnDropdownSelections() {
		assertThat(tableColumns.getDropdownSelections("name1"), is(nullValue()));
	}

	@Test
	public void shouldReturnTrueForDate() {
		assertThat(tableColumns.isDate("name5"), is(true));
	}

	@Test
	public void shouldReturnFalseForNonDate() {
		assertThat(tableColumns.isDate("name4"), is(false));
	}

	@Test
	public void shouldReturnCheckBoxTableColumn() {
		assertThat(tableColumns.getCheckBox("name6"), is(column6));
	}

	@Test
	public void shouldReturnIfIsCheckBox() {
		assertThat(tableColumns.isCheckbox("name1"), is(false));
		assertThat(tableColumns.isCheckbox("name6"), is(true));
	}

	@Test
	public void shouldReturnIfIsSelection() {
		assertThat(tableColumns.isSelection("name1"), is(false));
		assertThat(tableColumns.isSelection("name2"), is(true));
		assertThat(tableColumns.isSelection("name7"), is(true));
	}

	@Test
	public void shouldReturnIfIsComboBox() {
		assertThat(tableColumns.isComboBox("name1"), is(false));
		assertThat(tableColumns.isComboBox("name2"), is(true));
		assertThat(tableColumns.isComboBox("name7"), is(false));
	}

	@Test
	public void shouldTellIfIsTokenField() {
		assertThat(tableColumns.isTokenfield("name1"), is(false));
		assertThat(tableColumns.isTokenfield("name2"), is(false));
		assertThat(tableColumns.isTokenfield("name7"), is(true));
	}

	@Test
	public void shouldReturnDateTableColumn() {
		assertThat(tableColumns.getDateColumn("name5"), is(column5));
	}

	@Test
	public void shouldReturnOptionListOfSelectionColumn() {
		assertThat(tableColumns.getDropdownSelections("name2"), is(optionList));
	}

	@Test
	public void shouldReturnDisplayFormats() {
		Map<String, String> expectedFormats = ImmutableMap
				.<String, String> builder().put("name5", "dd.MM.yyyy").build();
		assertThat(tableColumns.getFormatPattern(), is(expectedFormats));
	}

	@Test
	public void shouldReturnSearchableColumns() {
		assertThat(
				tableColumns.getSearchableColumnNames(),
				is((Collection<String>) asList("name1", "name2", "name3",
						"name4", "name5")));
	}

	@Test
	public void shouldReturnDefaultSearchableColumns() {
		assertThat(tableColumns.getDefaultSearchableColumnNames(),
				is((Collection<String>) asList("name1", "name2")));
	}

	@Test
	public void shouldTellIfColumnExists() {
		assertThat(tableColumns.contains("name1"), is(true));
		assertThat(tableColumns.contains("unknown"), is(false));
	}

	@Test
	public void shouldSetTableOnColumns() {
		tableColumns.setTable(tableMock);
		assertThat(column1.getTable(), is(tableMock));
	}

	@Test
	public void shouldReturnSize() {
		assertThat(tableColumns.size(), is(7));
	}

	@Test
	public void shouldReturnColumnTypeByName() {
		tableColumns.setTable(tableMock);
		doReturn(String.class).when(containerMock).getType("name1");
		assertThat(tableColumns.getType("name1"), equalTo((Class) String.class));
	}

	@Test
	public void shouldReturnFilteredDropdownSelectionsByPrefix() {
		assertThat(tableColumns.getDropdownSelections("name2", "va", 100),
				equalTo(options));
	}

	@Test
	public void shouldReturnFilteredDropdownSelectionsBelowLimit() {
		assertThat(tableColumns.getDropdownSelections("name2", null, 1),
				equalTo(singletonMap("key1", "value1")));
	}

	@Test
	public void shouldReturnFilteredDropdownSelection() {
		assertThat(tableColumns.getDropdownSelections("name2", "value2", 0),
				equalTo(singletonMap("key2", "value2")));
	}

	@Test
	public void shouldReturnLowerCaseTableNamesMapping() {
		prepareUppercaseColumns();
		assertThat(tableColumns.getLowerCaseColumnNamesMapping().size(), is(2));
		assertThat(tableColumns.getLowerCaseColumnNamesMapping().get("name1"), is("Name1"));
		assertThat(tableColumns.getLowerCaseColumnNamesMapping().get("name2"), is("NAME2"));
	}

	@Test
	public void shouldCacheLowerCaseTableNamesMapping() {
		prepareUppercaseColumns();
		assertThat(tableColumns.getLowerCaseColumnNamesMapping(), sameInstance(tableColumns.getLowerCaseColumnNamesMapping()));
	}

    @Test
    public void shouldReturnUpdateColumns() {
        assertThat(tableColumns.getUpdateColumns(), equalTo(asList(column2, column3, column4, column7)));
    }

    @Test
    public void shouldReturnInsertColumns() {
        assertThat(tableColumns.getUpdateColumns(), equalTo(asList(column2, column3, column4, column7)));
    }

    private void prepareUppercaseColumns() {
		column1 = new TableColumn.Builder() //
				.name("Name1") //
				.build();
		column2 = new TableColumn.Builder() //
				.name("NAME2") //
				.build();
		columnsList = asList(column1, column2);
		tableColumns = new TableColumns(columnsList);
	}
}
