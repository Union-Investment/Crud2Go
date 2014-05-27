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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FileMetadata;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;

public class BLobColumnGeneratorTest {

	private BLobColumnGenerator bLobColumnGenerator;
	@Mock
	private DataContainer containerMock;
	@Mock
	private TableColumns columnsMock;
	@Mock
	private Object itemIdMock;
	@Mock
	private Table tableMock;
	@Mock
	private ContainerRowId containerRowIdMock;
	@Mock
	private ContainerRow containerRowMock;
	@Mock
	private Map<String, ContainerField> fieldsMock;
	@Mock
	private ContainerField containerFieldMock;
	@Mock
	private ContainerBlob containerBlobMock;
	@Mock
	private TableColumn tableColumnMock;
	@Mock
	private FileMetadata fileMetadataMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		bLobColumnGenerator = new BLobColumnGenerator(containerMock,
				columnsMock);
	}

	@Test
	public void shouldNotBuildDownloadLinkWithEmptyBlob() {
		when(containerMock.convertInternalRowId(itemIdMock)).thenReturn(
				containerRowIdMock);
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(
				containerRowMock);
		when(containerRowMock.getFields()).thenReturn(fieldsMock);
		when(fieldsMock.get("blobField")).thenReturn(containerFieldMock);
		when(containerFieldMock.getValue()).thenReturn(containerBlobMock);
		when(containerBlobMock.isEmpty()).thenReturn(true);
		Component downloadLink = bLobColumnGenerator.generateCell(tableMock,
				itemIdMock, "blobField");
		assertThat(downloadLink, nullValue());
	}

	@Test
	public void shouldBuildDownloadLink() {
		when(containerMock.convertInternalRowId(itemIdMock)).thenReturn(
				containerRowIdMock);
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(
				containerRowMock);
		when(containerRowMock.getFields()).thenReturn(fieldsMock);
		when(fieldsMock.get("blobField")).thenReturn(containerFieldMock);
		when(containerFieldMock.getValue()).thenReturn(containerBlobMock);
		when(containerBlobMock.isEmpty()).thenReturn(false);
		when(columnsMock.get(anyString())).thenReturn(tableColumnMock);
		when(tableColumnMock.getFileMetadata()).thenReturn(fileMetadataMock);
		when(fileMetadataMock.getCurrentDisplayname(containerRowMock)).thenReturn("Download");

		Component downloadLink = bLobColumnGenerator.generateCell(tableMock,
				itemIdMock, "blobField");
		assertThat(downloadLink.getCaption(), is("Download"));
	}

}
