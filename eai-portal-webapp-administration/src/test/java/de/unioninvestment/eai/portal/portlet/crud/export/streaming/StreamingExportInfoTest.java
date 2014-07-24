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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GeneratedValueGenerator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;

public class StreamingExportInfoTest {

	private StreamingExportInfo info;

	@Mock
	private DataContainer containerMock;
	@Mock
	private Table tableMock;
	@Mock
	private TableColumns columnsMock;
	@Mock
	private TableColumn col1Mock, col2Mock;

	@Mock
	private GeneratedValueGenerator generatorMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(tableMock.getVisibleColumns()).thenReturn(asList("col1", "col2"));
	}

	@Test
	public void shouldReturnVisibleColumnNames() {
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnNames(), is(new String[] { "col1", "col2" }));
	}

	@Test
	public void shouldIncludeGeneratedExportableColumns() {
		givenColumnMocks();
		when(col2Mock.isGenerated()).thenReturn(true);
		doReturn(Double.class).when(col2Mock).getGeneratedType();
		when(col2Mock.getGeneratedValueGenerator()).thenReturn(generatorMock);

		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnNames(), is(new String[] { "col1", "col2" }));
	}

	@Test
	public void shouldExcludeGeneratedNonExportableColumns() {
		givenColumnMocks();
		when(col2Mock.isGenerated()).thenReturn(true);
		when(col2Mock.getGeneratedType()).thenReturn(null);
		when(col2Mock.getGeneratedValueGenerator()).thenReturn(null);

		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnNames(), is(new String[] { "col1" }));
	}

	@Test
	public void shouldReturnVisibleColumnNamesAsTitlesByDefault() {
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnTitles(), is(new String[] { "col1", "col2" }));
	}

	@Test
	public void shouldReturnExplicitTitlesIfConfigured() {
		givenColumnMocks();
		when(col1Mock.getTitle()).thenReturn("Column 1");

		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnTitles(),
				is(new String[] { "Column 1", "col2" }));
	}

	@Test
	public void shouldReturnColumnTypes() {
		doReturn(String.class).when(containerMock).getType("col1");
		doReturn(BigDecimal.class).when(containerMock).getType("col2");
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnTypes(), is(new Class[] { String.class, BigDecimal.class}));
	}

	@Test
	public void shouldReturnGeneratedColumnTypes() {
		givenColumnMocks();
		when(col2Mock.isGenerated()).thenReturn(true);
		doReturn(BigDecimal.class).when(col2Mock).getGeneratedType();
		when(col2Mock.getGeneratedValueGenerator()).thenReturn(generatorMock);

		doReturn(String.class).when(containerMock).getType("col1");
		doReturn(null).when(containerMock).getType("col2");
		
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getColumnTypes(), is(new Class[] { String.class, BigDecimal.class}));
	}

	@Test
	public void shouldReturnNullsAsDisplayFormatsByDefault() {
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getDisplayFormats(), is(new String[] { null, null }));
	}

	@Test
	public void shouldReturnExplicitDisplayFormatsIfConfigured() {
		givenColumnMocks();
		when(col2Mock.getDisplayFormat()).thenReturn("#");

		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getDisplayFormats(), is(new String[] { null, "#" }));
	}

    @Test
    public void shouldReturnMultilineFlags() {
        givenColumnMocks();
        when(col2Mock.isMultiline()).thenReturn(true);

        info = new StreamingExportInfo(containerMock, tableMock);
        assertThat(info.getMultilineFlags(), is(new boolean[] { false, true}));
    }

    @Test
	public void shouldReturnNullsAsExcelFormatsByDefault() {
		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getExcelFormats(), is(new String[] { null, null }));
	}

	@Test
	public void shouldReturnExplicitExcelFormatsIfConfigured() {
		givenColumnMocks();
		when(col2Mock.getExcelFormat()).thenReturn("bla");

		info = new StreamingExportInfo(containerMock, tableMock);
		assertThat(info.getExcelFormats(), is(new String[] { null, "bla" }));
	}

	private void givenColumnMocks() {
		when(tableMock.getColumns()).thenReturn(columnsMock);
		when(columnsMock.get("col1")).thenReturn(col1Mock);
		when(columnsMock.get("col2")).thenReturn(col2Mock);
	}
}
