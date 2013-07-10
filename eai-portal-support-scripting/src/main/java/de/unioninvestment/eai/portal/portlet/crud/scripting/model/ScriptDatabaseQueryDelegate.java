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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MissingPropertyException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.AuditLogger;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;
import de.unioninvestment.eai.portal.support.scripting.ScriptingException;
import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

/**
 * Implementation des FreeformStatementDelegate für das CRUD Portlet.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptDatabaseQueryDelegate implements DatabaseQueryDelegate {

	@SuppressWarnings("unused")
	private static final String RETURN_ALL = "*";

	@SuppressWarnings("unused")
	private static final String RETURN_COUNT = "COUNT(*)";

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ScriptDatabaseQueryDelegate.class);

	private List<Filter> filters;

	private List<OrderBy> orderBys;

	private final String queryString;

	private final List<String> primaryKeyColumns;

	private DataContainer container;

	private final StatementWrapper insertStatement;

	private final StatementWrapper updateStatement;

	private final StatementWrapper deleteStatement;

	private AuditLogger auditLogger;

	/**
	 * Konstruktor.
	 * 
	 * @param container
	 *            Datenbankcontainer
	 * @param queryString
	 *            Der SQL Query String
	 * @param primaryKeyColumns
	 *            Liste der Spaltennamen der Primärschlüssel
	 */
	public ScriptDatabaseQueryDelegate(final DataContainer container,
			final String queryString, List<String> primaryKeyColumns) {
		this(container, queryString, null, null, null, primaryKeyColumns, null);
	}

	/**
	 * Konstruktor für editierbare Tabellen.
	 * 
	 * @param container
	 *            Datenbankcontainer
	 * @param queryString
	 *            Der SQL SELECT String
	 * @param insertStatement
	 *            Die Closure für INSERT Statements
	 * @param updateStatement
	 *            Die Closure für UPDATE Statements
	 * @param deleteStatement
	 *            Die Closure für DELETE Statements
	 * @param primaryKeyColumns
	 *            Liste der Spaltennamen der Primärschlüssel
	 */
	public ScriptDatabaseQueryDelegate(DataContainer container,
			String queryString, StatementWrapper insertStatement,
			StatementWrapper updateStatement, StatementWrapper deleteStatement,
			List<String> primaryKeyColumns, CurrentUser currentUser) {
		this.container = container;
		this.queryString = queryString;
		this.primaryKeyColumns = primaryKeyColumns;
		this.insertStatement = insertStatement;
		this.updateStatement = updateStatement;
		this.deleteStatement = deleteStatement;
		this.auditLogger = new AuditLogger(currentUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public String getQueryString(int offset, int limit) {
		throw new UnsupportedOperationException("Use getQueryStatement method.");
	}

	@Override
	public StatementHelper getQueryStatement(int offset, int limit) {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer();

		if (offset >= 0 && limit > 0) {
			query.append("SELECT * FROM (");
			query.append("SELECT x.*, ROWNUM AS \"rownum\" FROM (");
		}
		query.append("SELECT * FROM (");
		query.append(queryString);

		query.append(")");
		if (limit == 1 && (filters == null || filters.size() == 0)) {
			// Abfrage der Metadaten - keine Zeilen zurückliefern
			query.append("WHERE 1=0");
		} else {
			query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
			query.append(getOrderByString());
		}

		if (offset >= 0 && limit > 0) {
			query.append(")x ");
			query.append(") WHERE \"rownum\" BETWEEN ")
					.append(Integer.toString(offset + 1)).append(" AND ")
					.append(Integer.toString(offset + limit));
		}

		sh.setQueryString(query.toString());
		return sh;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatementHelper getIndexStatement(RowId rowId) {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer();

		query.append("SELECT * FROM (");
		query.append("SELECT x.*, ROWNUM AS \"rownum\" FROM (");
		query.append("SELECT * FROM (");
		query.append(queryString);
		query.append(")");
		query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
		query.append(getOrderByString());
		query.append(") x ");
		query.append(")");

		int idx = 0;
		for (String idName : primaryKeyColumns) {
			if (idx == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}
			query.append(QueryBuilder.quote(idName)).append("=?");
			sh.addParameterValue(rowId.getId()[idx]);

			idx++;
		}

		sh.setQueryString(query.toString());
		LOG.debug("Query Index Statement: " + sh.toString());
		return sh;
	}

	private String getOrderByString() {
		StringBuffer orderBuffer = new StringBuffer();
		if (orderBys != null && !orderBys.isEmpty()) {
			orderBuffer.append(" ORDER BY ");
			OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
			for (OrderBy orderBy : orderBys) {
				orderBuffer.append(QueryBuilder.quote(orderBy.getColumn()));
				if (orderBy.isAscending()) {
					orderBuffer.append(" ASC");
				} else {
					orderBuffer.append(" DESC");
				}
				if (orderBy != lastOrderBy) {
					orderBuffer.append(", ");
				}
			}
		}

		return orderBuffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public String getCountQuery() {
		throw new UnsupportedOperationException("Use getCountStatement method.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatementHelper getCountStatement() {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer();

		query.append("SELECT COUNT(*) FROM (");
		query.append(queryString);
		query.append(")").append(
				QueryBuilder.getWhereStringForFilters(filters, sh));

		sh.setQueryString(query.toString());
		LOG.debug("Query Count Statement: " + sh.getQueryString());
		return sh;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrderBy(List<OrderBy> orderBys) {
		this.orderBys = orderBys;
	}

	@Override
	public List<OrderBy> getOrderBy() {
		return orderBys;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int storeRow(Connection conn, RowItem row) throws SQLException {
		if (isNewRow(row)) {
			assertInsertIsPossible();

			ExtendedSql sql = createSingleConnectionSql(conn);

			switch (insertStatement.getType()) {
			case SQL:
				GString insertGString = (GString) callClosureWithContainer(
						insertStatement.getStatementClosure(), row, sql);
				executeInsert(sql, insertGString);
				break;
			case SCRIPT:
				callClosureWithContainer(insertStatement.getStatementClosure(),
						row, sql);
				break;
			default:
				throw new UnsupportedOperationException(
						"Unbekannter Statement-Typ: "
								+ insertStatement.getType());
			}
			return 1;
		} else {
			assertUpdateIsPossible();

			ExtendedSql sql = createSingleConnectionSql(conn);

			switch (updateStatement.getType()) {
			case SQL:
				GString updateGString = (GString) callClosureWithContainer(
						updateStatement.getStatementClosure(), row, sql);
				return executeUpdate(sql, updateGString);
			case SCRIPT:
				Object rowCount = callClosureWithContainer(
						updateStatement.getStatementClosure(), row, sql);
				if (rowCount != null && rowCount instanceof Integer) {
					return (Integer) rowCount;
				} else {
					throw new ScriptingException(null,
							"error.statement.illegalreturnvalue");
				}
			default:
				throw new UnsupportedOperationException(
						"Unbekannter Statement-Typ: "
								+ updateStatement.getType());
			}
		}
	}

	private int executeUpdate(ExtendedSql sql, GString updateGString)
			throws SQLException {
		try {
			auditLogger.audit(updateGString.toString());
			return sql.executeUpdate(updateGString);
		} finally {
			sql.close();
		}
	}

	private void executeInsert(ExtendedSql sql, GString insertGString)
			throws SQLException {
		try {
			auditLogger.audit(insertGString.toString());
			sql.execute(insertGString);
		} finally {
			sql.close();
		}
	}

	private ExtendedSql createSingleConnectionSql(Connection conn) {
		return new ExtendedSql(new SingleConnectionDataSource(conn, true));
	}

	/**
	 * Ruft die übergebende Closure auf und liefert ihre Rückgabe zurück. Die
	 * Closure kann in ihrem Code auf {@code row} zugreifen.
	 * 
	 * @param closure
	 *            die auszuführende Closure
	 * @param row
	 *            zu bearbeitende Zeile
	 * @return das Ergebnis des Closure-Aufrufs
	 * @throws SQLException
	 *             wird bei jedem auftretenden Fehler geworfen, um ein Rollback
	 *             zu garantieren.
	 */
	private Object callClosure(Closure<?> closure, RowItem row, ExtendedSql sql)
			throws SQLException {
		DatabaseContainerRow containerRow = (DatabaseContainerRow) container
				.convertItemToRow(row, false, true);
		ScriptRow scriptRow = new ScriptRow(containerRow);

		return callClosureAndHandleExceptions(closure, scriptRow, sql);
	}

	private Object callClosureWithContainer(Closure<?> closure, RowItem row,
			ExtendedSql sql) throws SQLException {
		DatabaseContainerRow containerRow = (DatabaseContainerRow) container
				.convertItemToRow(row, false, true);
		ScriptRow scriptRow = new ScriptRow(containerRow);
		ScriptContainer scriptContainer = new ScriptDatabaseContainer(container);

		return callClosureAndHandleExceptions(closure, scriptContainer,
				scriptRow, sql);
	}

	private Object callClosureAndHandleExceptions(Closure<?> closure,
			Object... args) throws SQLException {
		try {
			return closure.call(args);

		} catch (MissingPropertyException e) {
			throw new SQLException("Unbekanntes Attribut '" + e.getProperty()
					+ "'.");
		} catch (InvokerInvocationException e) {
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof SQLException) {
				throw (SQLException) cause;
			} else {
				throw new SQLException("Problem beim Skript-Aufruf.", e);
			}
		} catch (Exception e) {
			LOG.debug(e.getMessage(), e);
			throw new SQLException("Problem beim Skript-Aufruf.", e);
		}
	}

	private boolean isNewRow(RowItem row) {
		return row.getId() instanceof TemporaryRowId;
	}

	private void assertInsertIsPossible() {
		if (insertStatement == null) {
			throw new UnsupportedOperationException(
					"Insert is not possible - no INSERT string given");
		}
	}

	private void assertUpdateIsPossible() {
		if (updateStatement == null) {
			throw new UnsupportedOperationException(
					"Update is not possible - no UPDATE string given");
		}
	}

	private void assertDeleteIsPossible() {
		if (deleteStatement == null) {
			throw new UnsupportedOperationException(
					"Delete is not possible - no DELETE string given");
		}
	}

	@Override
	public boolean removeRow(Connection conn, RowItem row) throws SQLException {
		assertDeleteIsPossible();

		ExtendedSql sql = createSingleConnectionSql(conn);

		switch (deleteStatement.getType()) {
		case SQL:
			GString deleteGString = (GString) callClosure(
					deleteStatement.getStatementClosure(), row, sql);
			return executeUpdate(sql, deleteGString) > 0;
		case SCRIPT:
			Object rowCount = callClosure(
					deleteStatement.getStatementClosure(), row, sql);
			if (rowCount != null && rowCount instanceof Integer) {
				return (Integer) rowCount > 0;
			} else {
				throw new ScriptingException(null,
						"error.statement.illegalreturnvalue");
			}
		default:
			throw new UnsupportedOperationException(
					"Unbekannter Statement-Typ: " + deleteStatement.getType());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public String getContainsRowQueryString(Object... keys) {
		throw new UnsupportedOperationException(
				"Please use getContainsRowQueryStatement method.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatementHelper getContainsRowQueryStatement(Object... keys) {
		throw new UnsupportedOperationException(
				"Implemented at the next userstory.");
	}

	@Override
	public StatementHelper getRowByIdStatement(RowId rowId) {
		StatementHelper sh = new StatementHelper();
		StringBuffer query = new StringBuffer();

		query.append("SELECT * FROM (");
		query.append(queryString);
		query.append(")");
		int idx = 0;
		for (String idName : primaryKeyColumns) {
			if (idx == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}
			query.append(QueryBuilder.quote(idName)).append("=?");
			sh.addParameterValue(rowId.getId()[idx]);

			idx++;
		}

		sh.setQueryString(query.toString());
		LOG.debug("Query Count Statement: " + sh.toString());
		return sh;
	}
}
