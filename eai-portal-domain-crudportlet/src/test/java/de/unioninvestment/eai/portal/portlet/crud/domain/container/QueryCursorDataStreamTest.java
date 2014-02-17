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

package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

public class QueryCursorDataStreamTest {

	private QueryCursorDataStream stream;

	@Mock
	private Container containerMock;

	@Mock
	private ConnectionPool connectionPoolMock;

	@Mock
	private StatementHelper currentQueryMock;

	@Mock
	private Connection connectionMock;

	@Mock
	private PreparedStatement statementMock;

	@Mock
	private ResultSet resultsetMock;


	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);
		stream = new QueryCursorDataStream(containerMock, connectionPoolMock,
				currentQueryMock);
		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		when(currentQueryMock.getQueryString()).thenReturn("my select");
		when(connectionMock.prepareStatement("my select")).thenReturn(statementMock);
		when(statementMock.executeQuery()).thenReturn(resultsetMock);
	}

	@Test
	public void shouldAskContainerForQuerySize() {
		when(containerMock.size()).thenReturn(42);
		
		int count = stream.open(true);
		assertThat(count, is(42));
	}
	
	@Test
	public void shouldObtainResultsetAsCursor() throws SQLException {
		stream.open(false);
		
		verify(connectionMock).setAutoCommit(false);
		verify(currentQueryMock).setParameterValuesToStatement(statementMock);
		verify(statementMock).setFetchSize(Mockito.anyInt());
		verify(statementMock).executeQuery();
	}
	
	@Test
	public void shouldReturnStreamItemsForRows() throws SQLException {
		when(resultsetMock.next()).thenReturn(true, false);
		when(resultsetMock.getObject("mycol")).thenReturn("value");
		
		stream.open(false);
		assertThat(stream.hasNext(), is(true));
		
		StreamItem item = stream.next();
		assertThat(item.getValue("mycol"), is((Object)"value"));
		
		assertThat(stream.hasNext(), is(false));
	}

	@Test
	public void shouldCloseResources() throws SQLException {
		stream.open(false);
		
		stream.close();
		
		verify(resultsetMock).close();
		verify(statementMock).close();
		verify(connectionPoolMock).releaseConnection(connectionMock);
	}

	@Test
	public void shouldCloseResourcesIfQueryFails() throws SQLException {
		when(statementMock.executeQuery()).thenThrow(new SQLException("invalid query"));

		try {
			stream.open(false);
		} catch(RuntimeException e) {
			stream.close();
		}
		
		verify(statementMock).close();
		verify(connectionPoolMock).releaseConnection(connectionMock);
	}

	@Test
	public void shouldCloseResourcesIfStatementCreationFails() throws SQLException {
		when(connectionMock.prepareStatement(Mockito.anyString())).thenThrow(new SQLException("cannot create statement"));

		try {
			stream.open(false);
		} catch(RuntimeException e) {
			stream.close();
		}
		
		verify(connectionPoolMock).releaseConnection(connectionMock);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldNotSupportRemoval() throws SQLException {
		when(resultsetMock.next()).thenReturn(true, false);
		stream.open(false);
		stream.hasNext();
		stream.next();
		stream.remove();
	}
}
