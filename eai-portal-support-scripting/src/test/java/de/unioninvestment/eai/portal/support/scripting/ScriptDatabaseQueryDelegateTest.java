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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.SQLContainer;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;
import com.vaadin.addon.sqlcontainer.filters.Between;
import com.vaadin.addon.sqlcontainer.filters.Like;
import com.vaadin.addon.sqlcontainer.query.OrderBy;
import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;

import de.unioninvestment.eai.portal.portlet.crud.config.StatementConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptDatabaseQueryDelegate;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptDatabaseContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.StatementWrapper;
import de.unioninvestment.eai.portal.support.scripting.test.commons.ScriptingSpringPortletContextTest;

public class ScriptDatabaseQueryDelegateTest extends
		ScriptingSpringPortletContextTest {

	private ScriptDatabaseQueryDelegate scriptDatabaseQueryDelegate;
	private static final String QUERY = "Select * from TestUser";

	private static final String INSERT_STMT = "INSERT into TestUser values(?, ?)";

	private static final String UPDATE_STMT = "UPDATE TestUser set col=? where id=?";

	private static final String DELETE_STMT = "DELETE FROM TestUser WHERE id=?";

	@Mock
	private Connection connectionMock;

	@Mock
	private SQLContainer containerMock;

	@Mock
	private DataContainer databaseContainerMock;

	@Mock
	private PreparedStatement statementMock;

	@Mock
	private Closure<Object> insertClosureMock;
	private StatementWrapper insertStatement;

	@Mock
	private Closure<Object> updateClosureMock;
	private StatementWrapper updateStatement;

	@Mock
	private Closure<Object> deleteClosureMock;
	private StatementWrapper deleteStatement;

	@Captor
	private ArgumentCaptor<ScriptRow> rowCaptor;

	private RowId temporaryId;
	private Collection<ColumnProperty> newColumnProperties;
	private RowItem newRowItem;
	private RowId id;
	private RowItem existingRowItem;
	private Collection<ColumnProperty> updatedColumnProperties;

	@Mock
	private DatabaseContainerRow newRowMock;

	@Mock
	private DatabaseContainerRow existingRowMock;

	@Before
	public void setUp() {
		super.configurePortletUtils();
		MockitoAnnotations.initMocks(this);
		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, asList(new String[] { "ID" }));

		prepareData();
	}

	private void prepareData() {
		temporaryId = new TemporaryRowId(new Object[] { 1 });

		newColumnProperties = new LinkedList<ColumnProperty>();
		newColumnProperties.add(new ColumnProperty("id", true, false, false, 1,
				Integer.class));
		newColumnProperties.add(new ColumnProperty("col", false, true, true,
				"Text", String.class));

		newRowItem = new RowItem(containerMock, temporaryId,
				newColumnProperties);

		when(databaseContainerMock.convertItemToRow(newRowItem, false, true))
				.thenReturn(newRowMock);

		id = new RowId(new Object[] { 1 });
		updatedColumnProperties = new LinkedList<ColumnProperty>();
		updatedColumnProperties.add(new ColumnProperty("id", true, false,
				false, 1, Integer.class));
		updatedColumnProperties.add(new ColumnProperty("col", false, true,
				true, "Text", String.class));

		existingRowItem = new RowItem(containerMock, id,
				updatedColumnProperties);
		when(
				databaseContainerMock.convertItemToRow(existingRowItem, false,
						true)).thenReturn(existingRowMock);

		insertStatement = new StatementWrapper(insertClosureMock,
				StatementConfig.Type.SQL);
		updateStatement = new StatementWrapper(updateClosureMock,
				StatementConfig.Type.SQL);
		deleteStatement = new StatementWrapper(deleteClosureMock,
				StatementConfig.Type.SQL);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnQueryString() {

		scriptDatabaseQueryDelegate.getQueryString(1, 1);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnCountQuery() {
		scriptDatabaseQueryDelegate.getCountQuery();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnInsertWithoutConfiguredStatement()
			throws SQLException {
		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnUpdateWithoutConfiguredStatement()
			throws SQLException {
		scriptDatabaseQueryDelegate.storeRow(connectionMock, existingRowItem);
	}

	@Test(expected = SQLException.class)
	public void shouldThrowSqlExceptionOnMissingProperties()
			throws SQLException {

		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						any(ScriptRow.class), any(ExtendedSql.class)))
				.thenThrow(new MissingPropertyException("bla"));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, insertStatement, null, null,
				asList(new String[] { "ID" }), null);

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = SQLException.class)
	public void shouldWrapAnyExceptionInAnSqlException() throws SQLException {

		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						any(ScriptRow.class), any(ExtendedSql.class)))
				.thenThrow(new RuntimeException("bla"));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, insertStatement, null, null,
				asList(new String[] { "ID" }), null);

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = SQLException.class)
	public void shouldThrowSqlExceptionOnInvalidInsertSql() throws SQLException {
		when(insertClosureMock.call(rowCaptor.capture())).thenReturn(
				new GStringImpl(new Object[] { 1, "Text" }, new String[] {
						"INSERT into TestUser values(", ", ", ")" }));
		when(connectionMock.prepareStatement(Mockito.anyString())).thenThrow(
				new SQLException("Invalid ExtendedSql"));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, insertStatement, null, null,
				asList(new String[] { "ID" }), null);

		when(connectionMock.prepareStatement(Mockito.anyString())).thenThrow(
				new SQLException("Invalid SQL"));

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test
	public void shouldInsertANewRowWithSql() throws SQLException {

		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(
						new GStringImpl(new Object[] { 1, "Text" }, INSERT_STMT
								.split("\\?")));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, insertStatement, null, null,
				asList(new String[] { "ID" }), null);

		when(connectionMock.prepareStatement(Mockito.anyString())).thenReturn(
				statementMock);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				newRowItem);

		verify(connectionMock).prepareStatement(INSERT_STMT);
		verify(statementMock).setObject(1, 1);
		verify(statementMock).setObject(2, "Text");

		verify(connectionMock, never()).close();
		assertThat(updateCount, is(1));
	}

	@Test
	public void shouldInsertANewRowWithScript() throws SQLException {
		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, new StatementWrapper(
						insertClosureMock, StatementConfig.Type.SCRIPT), null,
				null, asList(new String[] { "ID" }), null);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				newRowItem);

		assertThat(updateCount, is(1));
		assertThat(rowCaptor.getValue(), notNullValue());
	}

	@Test
	public void shouldUpdateAnExistingRowWithSql() throws SQLException {
		when(
				updateClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(
						new GStringImpl(new Object[] { "Text", 1 }, UPDATE_STMT
								.split("\\?")));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, null, updateStatement, null,
				asList(new String[] { "ID" }), null);

		when(connectionMock.prepareStatement(Mockito.anyString())).thenReturn(
				statementMock);
		when(statementMock.executeUpdate()).thenReturn(1);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				existingRowItem);

		verify(connectionMock).prepareStatement(UPDATE_STMT);
		verify(statementMock).setObject(1, "Text");
		verify(statementMock).setObject(2, 1);

		verify(connectionMock, never()).close();

		assertThat(updateCount, is(1));
	}

	@Test
	public void shouldUpdateAnExistingRowWithScript() throws SQLException {
		when(
				updateClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, null, new StatementWrapper(
						updateClosureMock, StatementConfig.Type.SCRIPT), null,
				asList(new String[] { "ID" }), null);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				existingRowItem);

		assertThat(updateCount, is(1));
		assertThat(rowCaptor.getValue(), notNullValue());
	}

	@Test
	public void shouldDeleteAnExistingRowWithSql() throws SQLException {
		when(
				deleteClosureMock.call(rowCaptor.capture(),
						any(ExtendedSql.class))).thenReturn(
				new GStringImpl(new Object[] { 1 }, DELETE_STMT.split("\\?")));

		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, null, null, deleteStatement,
				asList(new String[] { "ID" }), null);

		when(connectionMock.prepareStatement(Mockito.anyString())).thenReturn(
				statementMock);
		when(statementMock.executeUpdate()).thenReturn(1);

		boolean deleted = scriptDatabaseQueryDelegate.removeRow(connectionMock,
				existingRowItem);

		verify(connectionMock).prepareStatement(DELETE_STMT);
		verify(statementMock).setObject(1, 1);

		verify(connectionMock, never()).close();

		assertThat(deleted, is(true));
	}

	@Test
	public void shouldDeleteAnExistingRowWithScript() throws SQLException {
		when(
				deleteClosureMock.call(rowCaptor.capture(),
						any(ExtendedSql.class))).thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, null, null, new StatementWrapper(
						deleteClosureMock, StatementConfig.Type.SCRIPT),
				asList(new String[] { "ID" }), null);

		boolean deleted = scriptDatabaseQueryDelegate.removeRow(connectionMock,
				existingRowItem);

		assertThat(deleted, is(true));
		assertThat(rowCaptor.getValue(), notNullValue());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnContainsRowQueryString()
			throws SQLException {
		scriptDatabaseQueryDelegate.getContainsRowQueryString((Object[]) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnContainsRowQueryStatement()
			throws SQLException {
		scriptDatabaseQueryDelegate
				.getContainsRowQueryStatement((Object[]) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnRemoveRow()
			throws SQLException {
		scriptDatabaseQueryDelegate.removeRow(null, null);
	}

	@Test
	public void shouldCreateHelperWithCorrectCountStatement() {
		StatementHelper countStatement = scriptDatabaseQueryDelegate
				.getCountStatement();
		assertEquals("SELECT COUNT(*) FROM (Select * from TestUser)",
				countStatement.getQueryString());
	}

	@Test
	public void shouldRetrieveQueryString() {
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();
		assertEquals("SELECT * FROM (Select * from TestUser)", queryString);
	}

	@Test
	public void shouldRetrieveQueryCountStringWithoutOrderBy() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getCountStatement();
		String queryString = queryStatement.getQueryString();

		assertEquals("SELECT COUNT(*) FROM (Select * from TestUser)",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringOrderByASC() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) ORDER BY \"id\" ASC",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringOrderByDESC() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", false));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) ORDER BY \"id\" DESC",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringOrderByDESCAndASC() {
		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", false));
		orderBys.add(new OrderBy("Name", true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) ORDER BY \"id\" DESC, \"Name\" ASC",
				queryString);
	}

	@Test
	public void shouldTestFilterBetween() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Between("ID", "2", "3"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"ID\" BETWEEN ? AND ?",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringNestedIntoOrderByNestedIntoWhere() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("id", "2"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"id\" > ? ORDER BY \"id\" ASC",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringNestedIntoOrderByNestedIntoWhereNestedIntoLimits() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("id", "2"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(10, 5);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser) WHERE \"id\" > ? ORDER BY \"id\" ASC)x ) WHERE \"rownum\" BETWEEN 11 AND 15",
				queryString);
	}

	@Test
	public void shouldTestFilterGreater() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("ID", "2"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals("SELECT * FROM (Select * from TestUser) WHERE \"ID\" > ?",
				queryString);
	}

	@Test
	public void shouldTestFilterGreaterAndEqual() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("ID", "2"));
		filters.add(new Like("SIZE", "%3"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"ID\" > ? AND \"SIZE\" LIKE ?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatement() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getIndexStatement(rowId);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser)) x ) WHERE \"ID\"=?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatementWith2PK() {
		scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock, QUERY, asList(new String[] { "ID1",
						"ID2" }));
		prepareData();

		RowId rowId = new RowId(new Object[] { 1l, 2l });
		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getIndexStatement(rowId);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser)) x ) WHERE \"ID1\"=? AND \"ID2\"=?",
				queryString);
	}

	@Test
	public void shouldTestRightParentheses() {

		ScriptDatabaseQueryDelegate scriptDatabaseQueryDelegate = new ScriptDatabaseQueryDelegate(
				databaseContainerMock,
				"select OS_DEPLOY_LOG.*, case when error is null then 'Deployment successful' else 'Error' end MESSAGE from OS_DEPLOY_LOG  order by STARTDATE desc",
				asList(new String[] { "ID" }));

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Equal("MESSAGE", "Error"));
		scriptDatabaseQueryDelegate.setFilters(filters);

		StatementHelper queryStatement = scriptDatabaseQueryDelegate
				.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (select OS_DEPLOY_LOG.*, case when error is null then 'Deployment successful' else 'Error' end MESSAGE from OS_DEPLOY_LOG  order by STARTDATE desc) WHERE \"MESSAGE\" = ?",
				queryString);
	}

	@Test
	public void testGetIndexStatement() {

	}

}
