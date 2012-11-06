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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;

public class TableColumnsTest {

	private TableColumn column1;
	private TableColumn column2;
	private TableColumn column3;
	private TableColumn column4;
	private List<TableColumn> columnsList;
	private TableColumns tableColumns;

	@Before
	public void setUp() {
		column1 = new TableColumn.Builder().name("name1").title("title1")
				.longTitle("Long Title 1").hiddenStatus(Hidden.FALSE)
				.editableDefault(false).primaryKey(true).multiline(false)
				.width(100).inputPrompt("prompt1").build();
		column2 = new TableColumn.Builder()
				.name("name2")
				.title("title2")
				.longTitle("Long Title 2")
				.hiddenStatus(Hidden.IN_TABLE)
				.editableDefault(true)
				.primaryKey(false)
				.multiline(false)
				.width(100)
				.inputPrompt("")
				.optionList(
						new QueryOptionList(Collections.singletonMap("key1",
								"value1"))).build();
		column3 = new TableColumn.Builder().name("name3").title("title3")
				.longTitle("Long Title 3").hiddenStatus(Hidden.TRUE)
				.editableDefault(true).primaryKey(false).multiline(true)
				.width(100).build();
		column4 = new TableColumn.Builder().name("name4").title("title4")
				.longTitle("Long Title 4").hiddenStatus(Hidden.IN_FORM)
				.editableDefault(true).primaryKey(false).multiline(true)
				.width(100).inputPrompt("prompt1").build();
		columnsList = asList(column1, column2, column3, column4);
		tableColumns = new TableColumns(columnsList);
	}

	@Test
	public void shouldReturnOrderedColumnIterator() {
		Iterator<TableColumn> it = tableColumns.iterator();
		assertThat(it.next(), is(column1));
		assertThat(it.next(), is(column2));
		assertThat(it.next(), is(column3));
		assertThat(it.next(), is(column4));
		assertThat(it.hasNext(), is(false));
	}

	@Test
	public void shouldReturnColumnsByName() {
		assertThat(tableColumns.get("name2"), is(column2));
	}

	@Test
	public void shouldReturnOrderedListOfAllNames() {
		assertThat(tableColumns.getAllNames(),
				is(asList("name1", "name2", "name3", "name4")));
	}

	@Test
	public void shouldReturnPrimaryKeyNames() {
		assertThat(tableColumns.getPrimaryKeyNames(), is(asList("name1")));
	}

	@Test
	public void shouldReturnVisibleNamesForTable() {
		assertThat(tableColumns.getVisibleNamesForTable(),
				is(asList("name1", "name4")));
	}

	@Test
	public void shouldReturnVisibleNamesForForm() {
		assertThat(tableColumns.getVisibleNamesForForm(),
				is(asList("name1", "name2")));
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
		assertThat(tableColumns.isMultiline("unknownName"), is(false));
	}

	@Test
	public void shouldReturnInputPromptIfNotEmpty() {
		assertThat(tableColumns.getInputPrompt("name1"), is("prompt1"));
		assertThat(tableColumns.getInputPrompt("name2"), is(nullValue()));
		assertThat(tableColumns.getInputPrompt("name3"), is(nullValue()));
	}

	@Test
	public void shouldReturnDropdownFlagByName() {
		assertThat(tableColumns.isDropdown("name2"), is(true));
		assertThat(tableColumns.isDropdown("name3"), is(false));
		assertThat(tableColumns.isDropdown("unknownName"), is(false));
	}

	@Test
	public void shouldReturnDropdownSelections() {
		assertThat(tableColumns.getDropdownSelections("name1"), is(nullValue()));
	}
}
