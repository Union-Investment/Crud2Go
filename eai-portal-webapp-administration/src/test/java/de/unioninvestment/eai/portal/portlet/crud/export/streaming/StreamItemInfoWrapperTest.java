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

package de.unioninvestment.eai.portal.portlet.crud.export.streaming;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.converter.Converter;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ExportColumnSelectionContext;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

public class StreamItemInfoWrapperTest {

	private StreamItemInfoWrapper info;

	@Mock
	private Table tableMock;
	@Mock
	private StreamItem itemMock;
	@Mock
	private TableColumns columnsMock;

	@Mock
	private SelectionTableColumn col1Mock;
	private TableColumn col2Mock;

	@Mock
	private OptionList optionListMock;

	@Captor
	private ArgumentCaptor<ExportColumnSelectionContext> context;

	@Mock
	private DataContainer containerMock;

	@Mock
	private DisplaySupport displayerMock;

	@Mock
	private Converter<String, String> converterMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		createInfo();
	}

	private void createInfo() {
		info = new StreamItemInfoWrapper(tableMock, new String[] { "col1",
				"col2" });
		info.setItem(itemMock);
	}

	@Test
	public void shouldDelegateToItemForValue() {
		when(itemMock.getValue("col1")).thenReturn("val1");
		assertThat(info.getValue("col1"), is((Object) "val1"));
	}

	@Test
	public void shouldTellThatColumnIsNoComboBox() {
		assertThat(info.isComboBox("col1"), is(false));
	}

	@Test
	public void shouldTellThatColumnIsNoComboBoxHavingColumnInformation() {
		givenTableColumns();
		assertThat(info.isComboBox("col1"), is(false));
	}

	@Test
	public void shouldTellThatColumnIsAComboBox() {
		givenTableColumns();
		when(columnsMock.isComboBox("col1")).thenReturn(true);

		assertThat(info.isComboBox("col1"), is(true));
	}

	@Test
	public void shouldReturnComboBoxTitle() {
		when(tableMock.getContainer()).thenReturn(containerMock);
		givenTableColumns();
		when(itemMock.getValue("col1")).thenReturn("val1");
		when(columnsMock.isComboBox("col1")).thenReturn(true);
		when(columnsMock.getDropdownSelections("col1")).thenReturn(optionListMock);
		when(optionListMock.getTitle(eq("val1"), context.capture())).thenReturn("Value 1");

		assertThat(info.getTitle("col1"), is("Value 1"));
	}

	@Test
	public void shouldReturnValueAsComboBoxTitle() {
		when(tableMock.getContainer()).thenReturn(containerMock);
		givenTableColumns();
		when(itemMock.getValue("col1")).thenReturn("val1");
		when(columnsMock.isComboBox("col1")).thenReturn(true);
		when(columnsMock.getDropdownSelections("col1")).thenReturn(optionListMock);
		when(optionListMock.getTitle(eq("val1"), context.capture())).thenReturn(null);

		assertThat(info.getTitle("col1"), is("val1"));
	}

	@Test
	public void shouldReturnComboBoxTitleFromUnformattedValue() {
		when(tableMock.getContainer()).thenReturn(containerMock);
		givenTableColumns();
		when(itemMock.getValue("col1")).thenReturn("val1");
		
		doReturn(String.class).when(containerMock).getType("col1");
		when(containerMock.findDisplayer("col1")).thenReturn(displayerMock);
		doReturn(null).when(displayerMock).createFormatter(String.class, null);
		
		when(columnsMock.isComboBox("col1")).thenReturn(true);
		when(columnsMock.getDropdownSelections("col1")).thenReturn(optionListMock);
		when(optionListMock.getTitle(eq("val1"), context.capture())).thenReturn("Value 1");

		assertThat(info.getTitle("col1"), is("Value 1"));
	}

	@Test
	public void shouldReturnComboBoxTitleFromFormattedValue() {
		when(tableMock.getContainer()).thenReturn(containerMock);
		givenTableColumns();
		when(itemMock.getValue("col1")).thenReturn("val1");
		
		doReturn(String.class).when(containerMock).getType("col1");
		when(containerMock.findDisplayer("col1")).thenReturn(displayerMock);
		doReturn(converterMock).when(displayerMock).createFormatter(String.class, null);
		when(converterMock.convertToPresentation("val1", String.class, null)).thenReturn("value1");
		
		when(columnsMock.isComboBox("col1")).thenReturn(true);
		when(columnsMock.getDropdownSelections("col1")).thenReturn(optionListMock);
		when(optionListMock.getTitle(eq("value1"), context.capture())).thenReturn("Value 1");

		assertThat(info.getTitle("col1"), is("Value 1"));
	}

	private void givenTableColumns() {
		when(tableMock.getColumns()).thenReturn(columnsMock);
		when(columnsMock.get("col1")).thenReturn(col1Mock);
		when(columnsMock.get("col2")).thenReturn(col2Mock);
		createInfo();
	}
}
