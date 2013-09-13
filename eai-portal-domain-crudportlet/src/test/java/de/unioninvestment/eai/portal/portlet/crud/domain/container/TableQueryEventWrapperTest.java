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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;

import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.AuditLogger;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.TestUser;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.table.CrudOracleGenerator;

public class TableQueryEventWrapperTest {

	private TableQueryEventWrapper tableQueryEventWrapper = null;

	@Mock
	private EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouterMock;

	@Mock
	private EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouterMock;

	@Mock
	private EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouterMock;

	@Mock
	private DataContainer containerMock;

	private String tableName = "TABLEQUERY_WRAPPER";

	@Mock
	private ConnectionPool connectionPoolMock;

	@Mock
	private Connection connectionMock;

	@Mock
	private DatabaseMetaData metaDataMock;

	@Mock
	private ResultSet tablesRSMock;

	@Mock
	private ResultSet pkRSMock;

	@Mock
	private PreparedStatement statementMock;

	@Mock
	private AuditLogger auditLoggerMock;

	@Mock
	private SQLContainer container;

	private Collection<ColumnProperty> properties = new ArrayList<ColumnProperty>();

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);

		when(connectionPoolMock.reserveConnection()).thenReturn(connectionMock);
		when(connectionMock.getMetaData()).thenReturn(metaDataMock);
		when(connectionMock.prepareStatement(anyString())).thenReturn(
				statementMock);
		when(
				connectionPoolMock.querySingleResultWithJdbcTemplate(
						any(StatementHelper.class), any(RowMapper.class)))
				.thenReturn(4711);
		when(metaDataMock.getTables(null, null, tableName.toUpperCase(), null))
				.thenReturn(tablesRSMock);
		when(tablesRSMock.next()).thenReturn(true);
		when(metaDataMock.getPrimaryKeys(null, null, tableName.toUpperCase()))
				.thenReturn(pkRSMock);
		when(pkRSMock.next()).thenReturn(true, false);
		when(pkRSMock.getString("COLUMN_NAME")).thenReturn("ID");

		CrudOracleGenerator sqlGenerator = new CrudOracleGenerator();
		tableQueryEventWrapper = new TableQueryEventWrapper(containerMock,
				tableName, connectionPoolMock, sqlGenerator,
				onInsertEventRouterMock, onUpdateEventRouterMock,
				onDeleteEventRouterMock, new TestUser("Anonymous"));
		sqlGenerator.setPrimaryKeyColumns(tableQueryEventWrapper
				.getPrimaryKeyColumns());
		tableQueryEventWrapper.setAuditLogger(auditLoggerMock);
	}

	@Test
	public void shouldGetIndexById() {
		RowId rowId = new RowId(new Object[] { 1l });
		assertThat(tableQueryEventWrapper.getIndexById(rowId), is(4710));
	}

	@Test
	public void shouldStoreRowUpdate() throws SQLException {
		RowId id = new RowId(new Object[] { "ID1" });

		ColumnProperty cp1 = new ColumnProperty("ID1", true, true, true, false,
				new Integer(1), Integer.TYPE);
		cp1.setReadOnly(false);
		properties.add(cp1);

		ColumnProperty cp2 = new ColumnProperty("value1", true, true, true,
				false, new Integer(1), Integer.TYPE);
		cp2.setVersionColumn(true);
		properties.add(cp2);

		RowItem row = new RowItem(container, id, properties);

		when(statementMock.executeUpdate()).thenReturn(1);

		tableQueryEventWrapper.storeRow(row);

		verify(auditLoggerMock)
				.audit("UPDATE TABLEQUERY_WRAPPER SET \"ID1\" = ? WHERE \"value1\" = ? -> Attribute <ID1 : 1, value1 : 1, >");
	}

	@Test
	public void shouldStoreRowImmediatelyInsert() throws SQLException {
		RowId id = new TemporaryRowId(new Object[] { "ID1" });

		ColumnProperty cp1 = new ColumnProperty("ID1", true, true, true,
				false, new Integer(1), Integer.TYPE);
		cp1.setReadOnly(false);
		properties.add(cp1);

		ColumnProperty cp2 = new ColumnProperty("value1", true, true, true,
				false, new Integer(1), Integer.TYPE);
		cp2.setVersionColumn(true);
		properties.add(cp2);

		RowItem row = new RowItem(container, id, properties);

		when(
				connectionMock.prepareStatement(
						"INSERT INTO TABLEQUERY_WRAPPER (\"ID1\") VALUES (?)",
						new String[] { "ID" })).thenReturn(statementMock);
		when(statementMock.executeUpdate()).thenReturn(1);
		when(statementMock.getGeneratedKeys()).thenReturn(tablesRSMock);

		tableQueryEventWrapper.storeRowImmediately(row);

		verify(auditLoggerMock)
				.audit("INSERT INTO TABLEQUERY_WRAPPER (\"ID1\") VALUES (?) -> Attribute <ID1 : 1, value1 : 1, >");
	}

	@Test
	public void shouldStoreRowInsert() throws SQLException {
		RowId id = new TemporaryRowId(new Object[] { "ID1" });

		ColumnProperty cp1 = new ColumnProperty("ID1", true, true, true,
				false, new Integer(1), Integer.TYPE);
		cp1.setReadOnly(false);
		properties.add(cp1);

		ColumnProperty cp2 = new ColumnProperty("value1", true, true, true,
				false, new Integer(1), Integer.TYPE);
		cp2.setVersionColumn(true);
		properties.add(cp2);

		RowItem row = new RowItem(container, id, properties);

		when(
				connectionMock.prepareStatement(
						"INSERT INTO TABLEQUERY_WRAPPER (\"ID1\") VALUES (?)",
						new String[] { "ID" })).thenReturn(statementMock);

		when(statementMock.executeUpdate()).thenReturn(1);

		tableQueryEventWrapper.storeRow(row);

		verify(auditLoggerMock)
				.audit("INSERT INTO TABLEQUERY_WRAPPER (\"ID1\") VALUES (?) -> Attribute <ID1 : 1, value1 : 1, >");
	}

	@Test
	public void shouldRemoveRow() throws SQLException {
		RowId id = new TemporaryRowId(new Object[] { "ID" });

		ColumnProperty cp1 = new ColumnProperty("ID", false, true, true,
				false, new Integer(1), Integer.TYPE);
		cp1.setReadOnly(true);
		properties.add(cp1);

		ColumnProperty cp2 = new ColumnProperty("value1", false, true, true,
				false, new Integer(1), Integer.TYPE);
		cp2.setVersionColumn(true);
		properties.add(cp2);

		RowItem row = new RowItem(container, id, properties);

		when(statementMock.executeUpdate()).thenReturn(1);

		tableQueryEventWrapper.removeRow(row);

		verify(auditLoggerMock)
				.audit("DELETE FROM TABLEQUERY_WRAPPER WHERE \"ID\" = ? -> Attribute <ID : 1, value1 : 1, >");
	}
}
