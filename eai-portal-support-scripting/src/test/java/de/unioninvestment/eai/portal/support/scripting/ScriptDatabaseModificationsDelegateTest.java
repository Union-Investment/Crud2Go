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
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MissingPropertyException;

import java.sql.Connection;
import java.sql.SQLException;
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
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.config.StatementConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptDatabaseContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptDatabaseModificationsDelegate;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.StatementWrapper;
import de.unioninvestment.eai.portal.support.vaadin.junit.AbstractSpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class ScriptDatabaseModificationsDelegateTest extends
		AbstractSpringPortletContextTest {

	private ScriptDatabaseModificationsDelegate scriptDatabaseQueryDelegate;

	private static final String INSERT_STMT = "INSERT into TestUser values(?, ?)";

	private static final String UPDATE_STMT = "UPDATE TestUser set col=? where id=?";

	private static final String DELETE_STMT = "DELETE FROM TestUser WHERE id=?";

	@Mock
	private Connection connectionMock;

    @Mock
    private ExtendedSql sqlMock;

	@Mock
	private SQLContainer containerMock;

	@Mock
	private DataContainer databaseContainerMock;

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

	private RowItem newRowItem;
	private RowItem existingRowItem;

	@Mock
	private DatabaseQueryDelegate queryDelegateMock;

	@Mock
	private DatabaseContainerRow newRowMock;

	@Mock
	private DatabaseContainerRow existingRowMock;

	@Mock
	private StatementHelper statementHelperMock;

	@Mock
	private RowId rowIdMock;

	@Before
	public void setUp() {
		super.configurePortletUtils();
		MockitoAnnotations.initMocks(this);
		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, queryDelegateMock) {
            @Override
            protected ExtendedSql createSingleConnectionSql(Connection conn) {
                return sqlMock;
            }
        };

		prepareData();
	}

	private void prepareData() {
        RowId temporaryId = new TemporaryRowId(1);

        Collection<ColumnProperty> newColumnProperties = new LinkedList<ColumnProperty>();
		newColumnProperties.add(new ColumnProperty("id", true, false, false,
				false, 1, Integer.class));
		newColumnProperties.add(new ColumnProperty("col", false, true, true,
				false, "Text", String.class));

		newRowItem = new RowItem(containerMock, temporaryId,
				newColumnProperties);

		when(databaseContainerMock.convertItemToRow(newRowItem, false, true))
				.thenReturn(newRowMock);

        RowId id = new RowId(1);
        Collection<ColumnProperty> updatedColumnProperties = new LinkedList<ColumnProperty>();
		updatedColumnProperties.add(new ColumnProperty("id", true, false,
				false, false, 1, Integer.class));
		updatedColumnProperties.add(new ColumnProperty("col", false, true,
				true, false, "Text", String.class));

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
	@Test
	public void shouldDelegateGetQueryString() {
		when(queryDelegateMock.getQueryString(0, 2)).thenReturn("abc");
		assertThat(scriptDatabaseQueryDelegate.getQueryString(0, 2), is("abc"));
	}

	@Test
	public void shouldDelegateGetQueryStatement() {
		when(queryDelegateMock.getQueryStatement(0, 2)).thenReturn(
				statementHelperMock);
		assertThat(scriptDatabaseQueryDelegate.getQueryStatement(0, 2),
				is(statementHelperMock));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldDelegateGetCountQuery() {
		when(queryDelegateMock.getCountQuery()).thenReturn("def");
		assertThat(scriptDatabaseQueryDelegate.getCountQuery(), is("def"));
	}

	@Test
	public void shouldDelegateGetCountStatement() {
		when(queryDelegateMock.getCountStatement()).thenReturn(
				statementHelperMock);
		assertThat(scriptDatabaseQueryDelegate.getCountStatement(),
				is(statementHelperMock));
	}

	@Test
	public void shouldDelegateGetIndexStatement() {
		when(queryDelegateMock.getIndexStatement(rowIdMock)).thenReturn(
				statementHelperMock);
		assertThat(scriptDatabaseQueryDelegate.getIndexStatement(rowIdMock),
				is(statementHelperMock));
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

		scriptDatabaseQueryDelegate = createDelegate();

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = SQLException.class)
	public void shouldWrapAnyExceptionInAnSqlException() throws SQLException {

		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						any(ScriptRow.class), any(ExtendedSql.class)))
				.thenThrow(new RuntimeException("bla"));

		scriptDatabaseQueryDelegate = createDelegate();

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test(expected = SQLException.class)
	public void shouldThrowSqlExceptionOnInvalidInsertSql() throws SQLException {
		when(insertClosureMock.call(rowCaptor.capture())).thenReturn(
				new GStringImpl(new Object[] { 1, "Text" }, new String[] {
						"INSERT into TestUser values(", ", ", ")" }));
		when(connectionMock.prepareStatement(Mockito.anyString())).thenThrow(
				new SQLException("Invalid ExtendedSql"));

		scriptDatabaseQueryDelegate = createDelegate();

		when(connectionMock.prepareStatement(Mockito.anyString())).thenThrow(
				new SQLException("Invalid SQL"));

		scriptDatabaseQueryDelegate.storeRow(connectionMock, newRowItem);
	}

	@Test
	public void shouldInsertANewRowWithSql() throws SQLException {

        GStringImpl insertGString = new GStringImpl(new Object[]{1, "Text"}, INSERT_STMT
                .split("\\?"));
        when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(insertGString);

        when(sqlMock.execute(insertGString)).thenReturn(true);

		scriptDatabaseQueryDelegate = createDelegate();


		int insertCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				newRowItem);

		assertThat(insertCount, is(1));
	}

    private ScriptDatabaseModificationsDelegate createDelegate() {
        return new ScriptDatabaseModificationsDelegate(
                databaseContainerMock, insertStatement, null, null, null,
                null, queryDelegateMock) {
            @Override
            protected ExtendedSql createSingleConnectionSql(Connection conn) {
                return sqlMock;
            }
        };
    }

    @Test
	public void shouldInsertANewRowWithScript() throws SQLException {
		when(
				insertClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, new StatementWrapper(insertClosureMock,
						StatementConfig.Type.SCRIPT), null, null, null,
                null, queryDelegateMock);

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

		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, null, updateStatement, null, null,
                null, queryDelegateMock) {
            @Override
            protected ExtendedSql createSingleConnectionSql(Connection conn) {
                return sqlMock;
            }
        };

        when(sqlMock.executeUpdate(any(GString.class))).thenReturn(1);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				existingRowItem);

		assertThat(updateCount, is(1));
	}

	@Test
	public void shouldUpdateAnExistingRowWithScript() throws SQLException {
		when(
				updateClosureMock.call(any(ScriptDatabaseContainer.class),
						rowCaptor.capture(), any(ExtendedSql.class)))
				.thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, null, new StatementWrapper(
						updateClosureMock, StatementConfig.Type.SCRIPT), null,
                null, null, queryDelegateMock);

		int updateCount = scriptDatabaseQueryDelegate.storeRow(connectionMock,
				existingRowItem);

		assertThat(updateCount, is(1));
		assertThat(rowCaptor.getValue(), notNullValue());
	}

	@Test
	public void shouldDeleteAnExistingRowWithSql() throws SQLException {
        GStringImpl deleteGString = new GStringImpl(new Object[]{1}, DELETE_STMT.split("\\?"));
        when(
				deleteClosureMock.call(any(ScriptDatabaseContainer.class),rowCaptor.capture(),
						any(ExtendedSql.class))).thenReturn(
                deleteGString);

		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, null, null, deleteStatement, null,
                null, queryDelegateMock) {
            @Override
            protected ExtendedSql createSingleConnectionSql(Connection conn) {
                return sqlMock;
            }
        };

        when(sqlMock.executeUpdate(deleteGString)).thenReturn(1);

		boolean deleted = scriptDatabaseQueryDelegate.removeRow(connectionMock,
				existingRowItem);

		assertThat(deleted, is(true));
	}

	@Test
	public void shouldDeleteAnExistingRowWithScript() throws SQLException {
		when(
				deleteClosureMock.call(any(ScriptDatabaseContainer.class),rowCaptor.capture(),
						any(ExtendedSql.class))).thenReturn(1);
		scriptDatabaseQueryDelegate = new ScriptDatabaseModificationsDelegate(
				databaseContainerMock, null, null, new StatementWrapper(
						deleteClosureMock, StatementConfig.Type.SCRIPT), null,
                null, queryDelegateMock);

		boolean deleted = scriptDatabaseQueryDelegate.removeRow(connectionMock,
				existingRowItem);

		assertThat(deleted, is(true));
		assertThat(rowCaptor.getValue(), notNullValue());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldDelegateGetContainsRowQueryString() {
		when(queryDelegateMock.getContainsRowQueryString("1", 2)).thenReturn(
				"def");
		assertThat(
				scriptDatabaseQueryDelegate.getContainsRowQueryString("1", 2),
				is("def"));
	}

	@Test
	public void shouldDelegateGetContainsRowQueryStatement()
			throws SQLException {
		when(queryDelegateMock.getContainsRowQueryStatement("1", 2))
				.thenReturn(statementHelperMock);
		assertThat(scriptDatabaseQueryDelegate.getContainsRowQueryStatement(
				"1", 2), is(statementHelperMock));
	}

	@Test
	public void shouldDelegateGetRowByIdStatement() throws SQLException {
		when(queryDelegateMock.getRowByIdStatement(rowIdMock)).thenReturn(
				statementHelperMock);
		assertThat(scriptDatabaseQueryDelegate.getRowByIdStatement(rowIdMock),
				is(statementHelperMock));
	}

	@Test
	public void shouldDelegateSetFilters() throws SQLException {
		List<Filter> filters = asList((Filter)new Like("a",  "b"));
		scriptDatabaseQueryDelegate.setFilters(filters);
		verify(queryDelegateMock).setFilters(filters);
	}

	@Test
	public void shouldDelegateSetOrderBy() throws SQLException {
		List<OrderBy> orderBys = asList(new OrderBy("a",  true));
		scriptDatabaseQueryDelegate.setOrderBy(orderBys);
		verify(queryDelegateMock).setOrderBy(orderBys);
	}

	@Test
	public void shouldDelegateGetOrderBy() throws SQLException {
		List<OrderBy> orderBys = asList(new OrderBy("a",  true));
		when(queryDelegateMock.getOrderBy()).thenReturn(orderBys);
		assertThat(scriptDatabaseQueryDelegate.getOrderBy(), is(orderBys));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnRemoveRow()
			throws SQLException {
		scriptDatabaseQueryDelegate.removeRow(null, null);
	}
}
