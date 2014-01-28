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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.TimeoutableQueryDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.TestUser;
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class DatabaseTableContainerTest
		extends
		AbstractDatabaseContainerTest<DatabaseTableContainer, SQLContainerEventWrapper> {

	@Mock
	private ConnectionPool connectionPoolMock;

	@Mock
	private Connection connectionMock;

	@Mock
	private EventBus eventBus;

	@Mock
	private TimeoutableQueryDelegate queryDelegateMock;

	@Override
	public DatabaseTableContainer createDataContainer() {
		DatabaseTableContainer databaseTableContainer = new DatabaseTableContainer(
				eventBus, "eai", "test", connectionPoolMock, true, true, true,
				new TestUser("Benutzer"), displayPatternMock,
				new ArrayList<ContainerOrder>(), null, 100, 1000, 0, DatabaseDialect.ORACLE);

		databaseTableContainer.setQueryDelegate(queryDelegateMock);
		when(queryDelegateMock.getPrimaryKeyColumns()).thenReturn(
				asList("test"));

		return databaseTableContainer;
	}

	@Override
	public SQLContainerEventWrapper createVaadinContainer() {

		return mock(SQLContainerEventWrapper.class, withSettings()
				.extraInterfaces(Filterable.class));

	}

	@Test
	public void shouldHandleSQLExceptionAsMissingTable()
			throws NamingException, SQLException {

		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		doThrow(new SQLException("MyMessage")).when(connectionMock)
				.getMetaData();

		try {
			container.getVaadinContainer();
			fail("Exception expected");

		} catch (BusinessException e) {
			assertEquals("portlet.crud.error.dataSourceNotFound", e.getCode());
		}

	}

	@Test
	public void shouldHandleIllegalArgumentExceptionAsMissingTable()
			throws NamingException, SQLException {

		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		doThrow(new IllegalArgumentException("MyMessage")).when(connectionMock)
				.getMetaData();

		try {
			container.getVaadinContainer();
			fail("Exception expected");

		} catch (BusinessException e) {
			assertEquals("portlet.crud.error.tableNotFound", e.getCode());
		}

	}

	@Test
	public void shouldHandleRuntimeExceptionAsDataSourceProblem()
			throws NamingException, SQLException {

		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		doThrow(new RuntimeException("MyMessage")).when(connectionMock)
				.getMetaData();

		try {
			container.getVaadinContainer();
			fail("Exception expected");

		} catch (BusinessException e) {
			assertEquals("portlet.crud.error.dataSourceNotFound", e.getCode());
			assertEquals("eai", e.getArgs()[0]);
		}

	}

	@Ignore
	// Not Supported by TableContainer
	@Override
	public void shouldNotRemoveDurableFilters() {

	}

}
