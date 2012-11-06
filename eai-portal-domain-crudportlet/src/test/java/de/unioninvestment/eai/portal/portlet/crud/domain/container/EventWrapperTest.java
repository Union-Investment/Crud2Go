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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.SQLContainer;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;
import com.vaadin.addon.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.addon.sqlcontainer.query.QueryDelegate;
import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SQLContainerEventWrapper;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

public abstract class EventWrapperTest {

	protected QueryDelegate wrapper;

	@Mock
	protected EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter;

	@Mock
	protected EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter;

	@Mock
	protected EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter;

	@Mock
	protected EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter;

	@Mock
	protected DataContainer container;

	protected RowItem rowItem;

	@Mock
	protected SQLContainer sqlContainer;

	private SQLContainerEventWrapper sqlContainerEventWrapper;

	private static MyJDBCConnectionPool connectionPool;

	protected abstract QueryDelegate getWrapper() throws SQLException;

	@BeforeClass
	public static void init() throws SQLException {
		connectionPool = new MyJDBCConnectionPool(
				"org.apache.derby.jdbc.EmbeddedDriver",
				"jdbc:derby:memory:MyDB;create=true", "", "");
		Connection connection = connectionPool.reserveConnection();
		JdbcTemplate template = new JdbcTemplate(
				new SingleConnectionDataSource(connection, false));

		try {
			template.execute("DROP TABLE TABLEQUERY_WRAPPER");
		} catch (BadSqlGrammarException e) {
			// ignore
		}
		String createSQL = "CREATE TABLE TABLEQUERY_WRAPPER" + "("
				+ "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
				+ "TESTSTRING VARCHAR(255) NOT NULL)";
		template.execute(createSQL);

		final String insertSQL = "INSERT INTO TABLEQUERY_WRAPPER (TESTSTRING) VALUES('TESTVAL')";
		template.execute(insertSQL);

		connection.commit();

		connectionPool.releaseConnection(connection);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		wrapper = getWrapper();
		sqlContainerEventWrapper = new SQLContainerEventWrapper(wrapper,
				container, onCreateEventRouter);
		wrapper.beginTransaction();
	}

	@After
	public void tearDown() {
		try {
			wrapper.rollback();
		} catch (SQLException e) {
			// ignore, für den Fall das onCommit() getestet wurde
		}
	}

	@Test
	public void shouldFireRemoveEvent() throws UnsupportedOperationException,
			SQLException {
		rowItem = new RowItem(sqlContainer, new RowId(new Object[] { 1 }),
				Arrays.asList(new ColumnProperty("ID", false, true, false, 1,
						Long.class), new ColumnProperty("TESTSTRING", false,
						true, false, "TESTVAL", String.class)));

		wrapper.removeRow(rowItem);

		ArgumentCaptor<DeleteEvent> eventCaptor = ArgumentCaptor
				.forClass(DeleteEvent.class);
		verify(onDeleteEventRouter).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue(), instanceOf(DeleteEvent.class));
	}

	@Test
	public void shouldFireInsertEvent() throws UnsupportedOperationException,
			SQLException {
		rowItem = new RowItem(sqlContainer, new TemporaryRowId(
				new Object[] { 1 }), Arrays.asList(new ColumnProperty(
				"TESTSTRING", false, true, false, "TESTVAL", String.class)));

		wrapper.storeRow(rowItem);

		ArgumentCaptor<InsertEvent> eventCaptor = ArgumentCaptor
				.forClass(InsertEvent.class);
		verify(onInsertEventRouter).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue(), instanceOf(InsertEvent.class));
	}

	@Test
	public void shouldFireUpdateEvent() throws UnsupportedOperationException,
			SQLException {
		rowItem = new RowItem(sqlContainer, new RowId(new Object[] { 1 }),
				Arrays.asList(new ColumnProperty("ID", false, false, false, 1,
						Long.class), new ColumnProperty("TESTSTRING", false,
						true, false, "TESTVAL", String.class)));

		wrapper.storeRow(rowItem);

		ArgumentCaptor<UpdateEvent> eventCaptor = ArgumentCaptor
				.forClass(UpdateEvent.class);
		verify(onUpdateEventRouter).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue(), instanceOf(UpdateEvent.class));
	}

	@Test
	public void shouldFireCreateEvent() throws SQLException {
		sqlContainerEventWrapper.addItem();

		ArgumentCaptor<CreateEvent> eventCaptor = ArgumentCaptor
				.forClass(CreateEvent.class);
		verify(onCreateEventRouter).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue(), instanceOf(CreateEvent.class));
	}

	protected static MyJDBCConnectionPool getConnectionPool()
			throws SQLException {
		return connectionPool;
	}
	
	private static class MyJDBCConnectionPool extends SimpleJDBCConnectionPool implements ConnectionPool {

		private static final long serialVersionUID = 42L;

		public MyJDBCConnectionPool(String driverName, String connectionUri,
				String userName, String password) throws SQLException {
			super(driverName, connectionUri, userName, password);
		}

		@Override
		public <T> List<T> executeWithJdbcTemplate(String query,
				RowMapper<T> callback) {
			throw new RuntimeException("not supported for test");
		}

		@Override
		public DataSource lookupDataSource() throws NamingException {
			throw new RuntimeException("not supported for test");
		}

		@Override
		public <T> T querySingleResultWithJdbcTemplate(StatementHelper statementHelper,
				RowMapper<T> callback) {
			throw new RuntimeException("not supported for test");
		}
	}
}
