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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.junit.Test;
import org.mockito.Mock;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportCallback;

public class DatabaseQueryContainerTest
		extends
		AbstractDatabaseContainerTest<DatabaseQueryContainer, SQLContainerEventWrapper> {

	@Mock
	private ConnectionPool connectionPoolMock;

	@Mock
	private FreeformQueryEventWrapper queryDelegateMock;

	@Mock
	private Connection connectionMock;

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private RowId rowIdMock;

	private List<ContainerOrder> orderBys = new ArrayList<ContainerOrder>();

	@Override
	public DatabaseQueryContainer createDataContainer() {
		DatabaseQueryContainer databaseQueryContainer = new DatabaseQueryContainer(
				"eai", "select * from test", true, true, true,
				Arrays.asList("test"), connectionPoolMock, "Benutzer",
				displayPatternMock, orderBys, null, 100, 1000, 0);
		databaseQueryContainer.setVaadinContainer(vaadinContainerMock);
		databaseQueryContainer.setQueryDelegate(queryDelegateMock);

		return databaseQueryContainer;
	}

	@Test
	public void shouldAllowAllOperations() {
		assertTrue(container.isInsertable());
		assertTrue(container.isDeleteable());
		assertTrue(container.isUpdateable());
	}

	@Test
	public void shouldAllowOnlyQuerying() {
		DatabaseQueryContainer container = new DatabaseQueryContainer("eai",
				"select * from test", false, false, false,
				Arrays.asList("test"), connectionPoolMock, "Benutzer",
				displayPatternMock, orderBys, null, 100, 1000, 0);
		assertFalse(container.isInsertable());
		assertFalse(container.isDeleteable());
		assertFalse(container.isUpdateable());
	}

	@Test
	public void shouldAllowEmptyPrimaryKeysIfReadonly() {
		new DatabaseQueryContainer("eai", "select * from test", false, false,
				false, null, connectionPoolMock, "Benutzer",
				displayPatternMock, orderBys, null, 100, 1000, 0);
	}

	@Test(expected = BusinessException.class)
	public void shouldRequirePrimaryKeysForEditing() {
		new DatabaseQueryContainer("eai", "select * from test", true, false,
				false, null, connectionPoolMock, "Benutzer",
				displayPatternMock, orderBys, null, 100, 1000, 0);
	}

	@Test
	public void shouldHandleSQLExceptionAsMissingTable()
			throws NamingException, SQLException {
		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		doThrow(new SQLException("MyMessage")).when(connectionMock)
				.getMetaData();

		try {
			DatabaseQueryContainer container = new DatabaseQueryContainer(
					"eai", "select * from test", true, true, true,
					Arrays.asList("test"), connectionPoolMock, "Benutzer",
					displayPatternMock, orderBys, null, 100, 1000, 0);
			container.getVaadinContainer();

			fail("Exception expected");

		} catch (BusinessException e) {
			assertEquals("portlet.crud.error.wrongQuery", e.getCode());
		}
	}

	@Test
	public void shouldHandleRuntimeExceptionAsDataSourceProblem()
			throws NamingException, SQLException {
		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		doThrow(new RuntimeException("MyMessage")).when(connectionMock)
				.getMetaData();

		try {
			DatabaseQueryContainer container = new DatabaseQueryContainer(
					"eai", "select * from test", true, true, true,
					Arrays.asList("test"), connectionPoolMock, "Benutzer",
					displayPatternMock, orderBys, null, 100, 1000, 0);
			container.getVaadinContainer();

		} catch (BusinessException e) {
			assertEquals("portlet.crud.error.wrongQuery", e.getCode());
			assertEquals("eai", e.getArgs()[0]);
		}

	}

	@Test
	public void shouldCommitContainerWithModifiedClob() throws SQLException {
		container.setVaadinContainer(vaadinContainerMock);
		String value = "testValue";
		RowItem rowItem = createRow(rowIdMock);
		when(queryDelegateMock.getCLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);

		ContainerClob containerClob = container.getCLob(containerRowIdMock,
				"TestCol");
		containerClob.setValue("NewValue");

		when(vaadinContainerMock.getItemUnfiltered(any(RowId.class)))
				.thenReturn(rowItem);

		container.commit();

		verify(queryDelegateMock).storeRow(rowItem);
		verify(queryDelegateMock).commit();

	}

	@Test
	public void shouldRollbackContainerCommitWithModifiedClob()
			throws SQLException {
		container.setVaadinContainer(vaadinContainerMock);
		String value = "testValue";
		RowItem rowItem = createRow(rowIdMock);
		when(queryDelegateMock.getCLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);

		doThrow(new SQLException()).when(queryDelegateMock).storeRow(rowItem);

		ContainerClob containerClob = container.getCLob(containerRowIdMock,
				"TestCol");
		containerClob.setValue("NewValue");

		when(vaadinContainerMock.getItemUnfiltered(any(RowId.class)))
				.thenReturn(rowItem);
		try {
			container.commit();
			assertTrue(false);
		} catch (ContainerException e) {
			assertTrue(true);
		}
		ContainerClob otherContainerClob = container.getCLob(
				containerRowIdMock, "TestCol");
		assertThat(containerClob, is(not(sameInstance(otherContainerClob))));
	}

	@Test
	public void shouldBuildCLobValueForCLobDBColumn() throws IOException {

		when(queryDelegateMock.getCLob(any(RowId.class), anyString()))
				.thenReturn("TestClob");
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);
		ContainerClob clob = container.getCLob(containerRowIdMock, "TestCol");

		assertThat(clob.getValue(), is("TestClob"));
	}

	@Test
	public void shouldBuildContainerClobForNewBlobEntry() {
		TemporaryRowId temporaryRowIdMock = mock(TemporaryRowId.class);
		when(containerRowIdMock.getInternalId()).thenReturn(temporaryRowIdMock);

		ContainerClob newClob = container
				.getCLob(containerRowIdMock, "TestCol");

		assertThat(container.isCLobModified(containerRowIdMock, "TestCol"),
				is(false));
		assertThat(newClob.getSize(), is(0));
	}

	@Test
	public void shouldReturnContainerCLobFromCache() {

		String value = "testValue";
		when(queryDelegateMock.getCLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);

		ContainerClob firstClob = container.getCLob(containerRowIdMock,
				"TestCol");

		ContainerClob secondClob = container.getCLob(containerRowIdMock,
				"TestCol");

		assertThat(firstClob, is(sameInstance(secondClob)));
	}

	@Test
	public void shouldBuildContainerBLobForBLobDBColumn() {

		byte[] value = new byte[] { 1, 2, 4 };
		when(queryDelegateMock.getBLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);
		ContainerBlob blob = container.getBLob(containerRowIdMock, "TestCol");

		assertThat(blob.getValue(), is(value));
	}

	@Test
	public void shouldReturnContainerBLobFromCache() {

		byte[] value = new byte[] { 1, 2, 4 };
		when(queryDelegateMock.getBLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);

		ContainerBlob firstBlob = container.getBLob(containerRowIdMock,
				"TestCol");

		ContainerBlob secondBlob = container.getBLob(containerRowIdMock,
				"TestCol");

		assertThat(firstBlob, is(sameInstance(secondBlob)));
	}

	@Test
	public void shouldBuildContainerBlobForNewBlobEntry() {
		TemporaryRowId temporaryRowIdMock = mock(TemporaryRowId.class);
		when(containerRowIdMock.getInternalId()).thenReturn(temporaryRowIdMock);

		ContainerBlob newBlob = container
				.getBLob(containerRowIdMock, "TestCol");

		assertThat(container.isBLobModified(containerRowIdMock, "TestCol"),
				is(false));
		assertThat(newBlob.hasData(), is(false));
	}

	@Test
	public void shouldCheckIfBlobDBColumnIsEmpty() {
		when(queryDelegateMock.hasBlobData(any(RowId.class), anyString()))
				.thenReturn(false);

		boolean isEmpty = container.isBLobEmpty(containerRowIdMock, "TestCol");
		assertThat(isEmpty, is(false));
	}

	@Test
	public void shouldCommitContainerWithModifiedBlob() throws SQLException {
		container.setVaadinContainer(vaadinContainerMock);
		byte[] value = new byte[] { 1, 2, 3 };
		byte[] newValue = new byte[] { 1, 2, 3, 4, 5 };
		RowItem rowItem = createRow(rowIdMock);
		when(queryDelegateMock.getBLob(any(RowId.class), anyString()))
				.thenReturn(value);
		when(containerRowIdMock.getInternalId()).thenReturn(rowIdMock);
		container.setQueryDelegate(queryDelegateMock);

		ContainerBlob containerBlob = container.getBLob(containerRowIdMock,
				"TestCol");
		containerBlob.setValue(newValue);

		when(vaadinContainerMock.getItemUnfiltered(any(RowId.class)))
				.thenReturn(rowItem);

		container.commit();

		verify(queryDelegateMock).storeRow(rowItem);
		verify(queryDelegateMock).commit();
	}

	@Override
	public SQLContainerEventWrapper createVaadinContainer() {

		return mock(SQLContainerEventWrapper.class, withSettings()
				.extraInterfaces(Filterable.class));
	}

	@Test
	public void shouldChangePageLengthOnExport() {
		container.withExportSettings(new ExportCallback() {
			@Override
			public void export() {
				verify(vaadinContainerMock).setPageLength(1000);
			}
		});
		verify(vaadinContainerMock).setPageLength(100);
	}
}
