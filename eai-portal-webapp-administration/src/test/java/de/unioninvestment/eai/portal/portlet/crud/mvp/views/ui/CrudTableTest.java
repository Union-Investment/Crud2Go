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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.ui.Select;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

public class CrudTableTest {

	@Mock
	DataContainer databaseContainerMock;

	@Mock
	TableColumns tableColumnsMock;

	@Mock
	DisplaySupport displaySupportMock;

	@Mock
	private OptionListContainer containerMock;

	List<DisplaySupport> displayHelpers = new ArrayList<DisplaySupport>();

	@Mock
	Property propertyMock;

	CrudTable table;
	Object rowId = "rowId";
	Object colId = "colId";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		displayHelpers.add(displaySupportMock);

		table = new CrudTable(databaseContainerMock, tableColumnsMock, false);
	}

	@Test
	public void shouldReturnEmptyStringForNull() {
		assertEquals("", table.formatPropertyValue(rowId, colId, null));
	}

	@Test
	public void shouldReturnEmptyStringForNullValue() {
		when(propertyMock.getValue()).thenReturn(null);
		assertEquals("", table.formatPropertyValue(rowId, colId, propertyMock));
	}

	@Test
	public void shouldCloseOptionListContainerOnUnregister() {
		Select selectWithOptionList = mock(Select.class);
		when(selectWithOptionList.getContainerDataSource()).thenReturn(
				containerMock);

		table.handleUnregisteredField(selectWithOptionList);

		verify(containerMock).close();
	}
}
