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
package de.unioninvestment.eai.portal.portlet.crud.domain.database;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;

public class AbstractConnectionPoolTest {

	@Mock
	private DataSource dataSourceMock;

	@Mock
	private Connection connectionMock;

	private DummyConnectionPool pool;

	@SuppressWarnings("serial")
	private class DummyConnectionPool extends AbstractConnectionPool {

		public DummyConnectionPool() {
			super("dummy");
		}

		@Override
		public DataSource lookupDataSource() throws NamingException {
			return dataSourceMock;
		}

	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		pool = new DummyConnectionPool();
	}

	@Test
	public void shouldReturnConnectionWithoutAutommit() throws SQLException,
			NamingException {

		when(dataSourceMock.getConnection()).thenReturn(connectionMock);

		Connection connection = pool.reserveConnection();
		assertEquals(connectionMock, connection);

		verify(connectionMock).setAutoCommit(false);
	}

	@SuppressWarnings("serial")
	@Test(expected = BusinessException.class)
	public void shouldThrowBusinessInsteadOfNamingException()
			throws SQLException, NamingException {

		pool = new DummyConnectionPool() {
			public DataSource lookupDataSource() throws NamingException {
				throw new NamingException("not found");
			}
		};

		pool.reserveConnection();

	}

	@Test
	public void shouldReleaseConnection() throws SQLException {
		pool.releaseConnection(connectionMock);
		verify(connectionMock).close();
	}

	@Test
	public void shouldGracefullyIgnoreNullConnections() throws SQLException {
		pool.releaseConnection(null);
	}

	@Test
	public void shouldGracefullyIgnoreSQLExceptionsOnClose()
			throws SQLException {
		doThrow(new SQLException()).when(connectionMock).close();

		pool.releaseConnection(connectionMock);
		verify(connectionMock).close();
	}
}
