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

package de.unioninvestment.eai.portal.support.vaadin.database;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class OracleDatabaseQueryDelegateTest {
	private OracleDatabaseQueryDelegate delegate;

	private static final String QUERY = "Select * from TestUser";

	@Mock
	private Connection connectionMock;

	@Mock
	private SQLContainer containerMock;

	@Mock
	private PreparedStatement statementMock;

	private RowId temporaryId;
	private Collection<ColumnProperty> newColumnProperties;
	private RowItem newRowItem;
	private RowId id;
	private RowItem existingRowItem;
	private Collection<ColumnProperty> updatedColumnProperties;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		delegate = new OracleDatabaseQueryDelegate(QUERY,
				asList(new String[] { "ID" }));
		QueryBuilder.setStringDecorator(DatabaseDialect.ORACLE
				.getStringDecorator());

		prepareData();
	}

	private void prepareData() {
		temporaryId = new TemporaryRowId(new Object[] { 1 });

		newColumnProperties = new LinkedList<ColumnProperty>();
		newColumnProperties.add(new ColumnProperty("id", true, false, false,
				false, 1, Integer.class));
		newColumnProperties.add(new ColumnProperty("col", false, true, true,
				false, "Text", String.class));

		newRowItem = new RowItem(containerMock, temporaryId,
				newColumnProperties);

		id = new RowId(new Object[] { 1 });
		updatedColumnProperties = new LinkedList<ColumnProperty>();
		updatedColumnProperties.add(new ColumnProperty("id", true, false,
				false, false, 1, Integer.class));
		updatedColumnProperties.add(new ColumnProperty("col", false, true,
				true, false, "Text", String.class));

		existingRowItem = new RowItem(containerMock, id,
				updatedColumnProperties);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnQueryString() {
		delegate.getQueryString(1, 1);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnCountQuery() {
		delegate.getCountQuery();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnInser()
			throws SQLException {
		delegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnUpdate()
			throws SQLException {
		delegate.storeRow(connectionMock, existingRowItem);
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnContainsRowQueryString()
			throws SQLException {
		delegate.getContainsRowQueryString((Object[]) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnContainsRowQueryStatement()
			throws SQLException {
		delegate.getContainsRowQueryStatement((Object[]) null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnRemoveRow()
			throws SQLException {
		delegate.removeRow(null, null);
	}

	@Test
	public void shouldCreateHelperWithCorrectCountStatement() {
		StatementHelper countStatement = delegate.getCountStatement();
		assertEquals("SELECT COUNT(*) FROM (Select * from TestUser)",
				countStatement.getQueryString());
	}

	@Test
	public void shouldRetrieveQueryString() {
		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();
		assertEquals("SELECT * FROM (Select * from TestUser)", queryString);
	}

	@Test
	public void shouldRetrieveQueryCountStringWithoutOrderBy() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		delegate.setOrderBy(orderBys);
		StatementHelper queryStatement = delegate.getCountStatement();
		String queryString = queryStatement.getQueryString();

		assertEquals("SELECT COUNT(*) FROM (Select * from TestUser)",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringOrderByASC() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		delegate.setOrderBy(orderBys);
		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) ORDER BY \"id\" ASC",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringOrderByDESC() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", false));
		delegate.setOrderBy(orderBys);
		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
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
		delegate.setOrderBy(orderBys);
		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) ORDER BY \"id\" DESC, \"Name\" ASC",
				queryString);
	}

	@Test
	public void shouldTestFilterBetween() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Between("ID", "2", "3"));
		delegate.setFilters(filters);

		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"ID\" BETWEEN ? AND ?",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringNestedIntoOrderByNestedIntoWhere() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		delegate.setOrderBy(orderBys);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("id", "2"));
		delegate.setFilters(filters);

		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"id\" > ? ORDER BY \"id\" ASC",
				queryString);
	}

	@Test
	public void shouldRetrieveQueryStringNestedIntoOrderByNestedIntoWhereNestedIntoLimits() {

		List<OrderBy> orderBys = new ArrayList<OrderBy>();
		orderBys.add(new OrderBy("id", true));
		delegate.setOrderBy(orderBys);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("id", "2"));
		delegate.setFilters(filters);

		StatementHelper queryStatement = delegate.getQueryStatement(10, 5);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser) WHERE \"id\" > ? ORDER BY \"id\" ASC)x ) WHERE \"rownum\" BETWEEN 11 AND 15",
				queryString);
	}

	@Test
	public void shouldTestFilterGreater() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("ID", "2"));
		delegate.setFilters(filters);

		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals("SELECT * FROM (Select * from TestUser) WHERE \"ID\" > ?",
				queryString);
	}

	@Test
	public void shouldTestFilterGreaterAndEqual() {

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Compare.Greater("ID", "2"));
		filters.add(new Like("SIZE", "%3"));
		delegate.setFilters(filters);

		StatementHelper queryStatement = delegate.getQueryStatement(0, 0);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (Select * from TestUser) WHERE \"ID\" > ? AND \"SIZE\" LIKE ?",
				queryString);
	}


	@Test
	public void shouldTestRightParentheses() {

		OracleDatabaseQueryDelegate scriptDatabaseQueryDelegate = new OracleDatabaseQueryDelegate(
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
	public void shouldGetIndexStatement() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = delegate.getIndexStatement(rowId);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser)) x ) WHERE \"ID\"=?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatementWith2PK() {
		delegate = new OracleDatabaseQueryDelegate(QUERY, asList(new String[] {
				"ID1", "ID2" }));
		prepareData();

		RowId rowId = new RowId(new Object[] { 1l, 2l });
		StatementHelper queryStatement = delegate.getIndexStatement(rowId);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM (Select * from TestUser)) x ) WHERE \"ID1\"=? AND \"ID2\"=?",
				queryString);
	}

}
