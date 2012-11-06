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
package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;

public class SqlProviderTest {

	@Mock
	private ConnectionPoolFactory factoryMock;
	@Mock
	private ConnectionPool poolMock;
	@Mock
	private DataSource dataSourceMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnSqlInstanceForDataSource() throws NamingException {
		SqlProvider sqlProvider = new SqlProvider(this, factoryMock);

		when(factoryMock.getPool("eai")).thenReturn(poolMock);
		when(poolMock.lookupDataSource()).thenReturn(dataSourceMock);

		ExtendedSql sql = sqlProvider.call("eai");

		assertThat(sql.getDataSource(), is(dataSourceMock));
	}
}
