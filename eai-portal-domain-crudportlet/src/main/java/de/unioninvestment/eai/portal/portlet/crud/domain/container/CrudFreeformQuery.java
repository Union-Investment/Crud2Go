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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

/**
 * Kopie des Sourcecodes von {@link FreeformQuery}, der um die Möglichkeit
 * erweitert wurde eine Timeout Zeit zu setzen.<br/>
 * Die Timeout Eigenschaft wurde in die Oberklasse
 * {@link AbstractTimeoutableQueryDelegate} ausgelagert. Der Timeout bezieht
 * sich auf das Count-Query. Wird ein Timeout-Wert größer 0 gesetzt und das
 * Count-Query benötigt länger als diese Zeit, wird eine
 * {@link SQLTimeoutException} geworfen.
 * 
 * Diese Klasse sollte nach Möglichkeit nicht geändert werden. Angepasste
 * Codestellen sollten zum späteren Refactoring mit Kommentaren versehen sein.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class CrudFreeformQuery extends AbstractTimeoutableQueryDelegate
		implements QueryDelegate {

	private static final long serialVersionUID = 1L;

	FreeformQueryDelegate delegate = null;
	private String queryString;
	private List<String> primaryKeyColumns;
	private JDBCConnectionPool connectionPool;
	private transient Connection activeConnection = null;

	/**
	 * Creates a new freeform query delegate to be used with the
	 * {@link SQLContainer}.
	 * 
	 * @param queryString
	 *            The actual query to perform.
	 * @param connectionPool
	 *            the JDBCConnectionPool to use to open connections to the SQL
	 *            database.
	 * @param primaryKeyColumns
	 *            The primary key columns. Read-only mode is forced if none are
	 *            provided. (optional)
	 */
	public CrudFreeformQuery(String queryString,
			JDBCConnectionPool connectionPool, String... primaryKeyColumns) {
		if (queryString == null || "".equals(queryString)) {
			throw new IllegalArgumentException(
					"The query string may not be empty or null!");
		}
		this.queryString = queryString;
		if (connectionPool == null) {
			throw new IllegalArgumentException(
					"The connectionPool may not be null!");
		}
		this.connectionPool = connectionPool;

		List<String> pkColumns = new ArrayList<String>();
		for (String pkColumn : primaryKeyColumns) {
			if (pkColumn != null) {
				String trimmed = pkColumn.trim();
				if (trimmed.isEmpty()) {
					throw new IllegalArgumentException(
							"The primary key columns contain an empty string!");
				}
				pkColumns.add(trimmed);
			}
		}
		this.primaryKeyColumns = Collections.unmodifiableList(pkColumns);
	}

	// --- Codecentric: F.Hardy ---
	private void configureQueryTimeout(Statement statement) throws SQLException {
		if (this.getQueryTimeout() > 0) {
			statement.setQueryTimeout(this.getQueryTimeout());
		}
	}

	// --- Codecentric: F.Hardy ---
	private void configureQueryTimeout(PreparedStatement statement)
			throws SQLException {
		if (this.getQueryTimeout() > 0) {
			statement.setQueryTimeout(this.getQueryTimeout());
		}
	}

	/**
	 * This implementation of getCount() actually fetches all records from the
	 * database, which might be a performance issue. Override this method with a
	 * SELECT COUNT(*) ... query if this is too slow for your needs.
	 * 
	 * {@inheritDoc}
	 */
	public int getCount() throws SQLException {
		// First try the delegate
		int count = countByDelegate();
		if (count < 0) {
			// Couldn't use the delegate, use the bad way.
			Connection conn = getConnection();
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			// --- Codecentric: F.Hardy ---
			this.configureQueryTimeout(statement);

			ResultSet rs = statement.executeQuery(queryString);
			if (rs.last()) {
				count = rs.getRow();
			} else {
				count = 0;
			}
			rs.close();
			statement.close();
			releaseConnection(conn);
		}
		return count;
	}

	@SuppressWarnings("deprecation")
	private int countByDelegate() throws SQLException {
		int count = -1;
		if (delegate == null) {
			return count;
		}
		/* First try using prepared statement */
		if (delegate instanceof FreeformStatementDelegate) {
			try {
				StatementHelper sh = ((FreeformStatementDelegate) delegate)
						.getCountStatement();
				Connection c = getConnection();
				PreparedStatement pstmt = c.prepareStatement(sh
						.getQueryString());

				// --- Codecentric: F.Hardy ---
				this.configureQueryTimeout(pstmt);

				sh.setParameterValuesToStatement(pstmt);
				ResultSet rs = pstmt.executeQuery();
				rs.next();
				count = rs.getInt(1);
				rs.close();
				pstmt.clearParameters();
				pstmt.close();
				releaseConnection(c);
				return count;
			} catch (UnsupportedOperationException e) {
				// Count statement generation not supported
			}
		}
		/* Try using regular statement */
		try {
			String countQuery = delegate.getCountQuery();
			if (countQuery != null) {
				Connection conn = getConnection();
				Statement statement = conn.createStatement();

				// --- Codecentric: F.Hardy ---
				this.configureQueryTimeout(statement);

				ResultSet rs = statement.executeQuery(countQuery);
				rs.next();
				count = rs.getInt(1);
				rs.close();
				statement.close();
				releaseConnection(conn);
				return count;
			}
		} catch (UnsupportedOperationException e) {
			// Count query generation not supported
		}
		return count;
	}

	private Connection getConnection() throws SQLException {
		if (activeConnection != null) {
			return activeConnection;
		}
		return connectionPool.reserveConnection();
	}

	/**
	 * Fetches the results for the query. This implementation always fetches the
	 * entire record set, ignoring the offset and pagelength parameters. In
	 * order to support lazy loading of records, you must supply a
	 * FreeformQueryDelegate that implements the
	 * FreeformQueryDelegate.getQueryString(int,int) method.
	 * 
	 * @throws SQLException
	 * 
	 * @see com.vaadin.addon.sqlcontainer.query.FreeformQueryDelegate#getQueryString(int,
	 *      int) {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public ResultSet getResults(int offset, int pagelength) throws SQLException {
		if (activeConnection == null) {
			throw new SQLException("No active transaction!");
		}
		String query = queryString;
		if (delegate != null) {
			/* First try using prepared statement */
			if (delegate instanceof FreeformStatementDelegate) {
				try {
					StatementHelper sh = ((FreeformStatementDelegate) delegate)
							.getQueryStatement(offset, pagelength);
					PreparedStatement pstmt = activeConnection
							.prepareStatement(sh.getQueryString());
					sh.setParameterValuesToStatement(pstmt);
					return pstmt.executeQuery();
				} catch (UnsupportedOperationException e) {
					// Statement generation not supported, continue...
				}
			}
			try {
				query = delegate.getQueryString(offset, pagelength);
			} catch (UnsupportedOperationException e) {
				// This is fine, we'll just use the default queryString.
			}
		}
		// --- Codecentric: cmj / Sonar Rule Fix ---
		Statement statement = activeConnection.createStatement();
		try {
			ResultSet rs = statement.executeQuery(query);
			return rs;

		} catch (SQLException e) {
			statement.close();
			throw e;
		}
		// --- Codecentric: cmj / Ende Sonar Rule Fix ---
	}

	@SuppressWarnings("deprecation")
	public boolean implementationRespectsPagingLimits() {
		if (delegate == null) {
			return false;
		}
		/* First try using prepared statement */
		if (delegate instanceof FreeformStatementDelegate) {
			try {
				StatementHelper sh = ((FreeformStatementDelegate) delegate)
						.getCountStatement();
				if (sh != null && sh.getQueryString() != null
						&& sh.getQueryString().length() > 0) {
					return true;
				}
			} catch (UnsupportedOperationException e) {
				// Statement generation not supported, continue...
			}
		}
		try {
			String queryString = delegate.getQueryString(0, 50);
			return queryString != null && queryString.length() > 0;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.addon.sqlcontainer.query.QueryDelegate#setFilters(java.util
	 * .List)
	 */
	public void setFilters(List<Filter> filters)
			throws UnsupportedOperationException {
		if (delegate != null) {
			delegate.setFilters(filters);
		} else if (filters != null) {
			throw new UnsupportedOperationException(
					"FreeFormQueryDelegate not set!");
		}
	}

	public void setOrderBy(List<OrderBy> orderBys)
			throws UnsupportedOperationException {
		if (delegate != null) {
			delegate.setOrderBy(orderBys);
		} else if (orderBys != null) {
			throw new UnsupportedOperationException(
					"FreeFormQueryDelegate not set!");
		}
	}

	public int storeRow(RowItem row) throws SQLException {
		if (activeConnection == null) {
			throw new IllegalStateException("No transaction is active!");
		} else if (primaryKeyColumns.isEmpty()) {
			throw new UnsupportedOperationException(
					"Cannot store items fetched with a read-only freeform query!");
		}
		if (delegate != null) {
			return delegate.storeRow(activeConnection, row);
		} else {
			throw new UnsupportedOperationException(
					"FreeFormQueryDelegate not set!");
		}
	}

	public boolean removeRow(RowItem row) throws SQLException {
		if (activeConnection == null) {
			throw new IllegalStateException("No transaction is active!");
		} else if (primaryKeyColumns.isEmpty()) {
			throw new UnsupportedOperationException(
					"Cannot remove items fetched with a read-only freeform query!");
		}
		if (delegate != null) {
			return delegate.removeRow(activeConnection, row);
		} else {
			throw new UnsupportedOperationException(
					"FreeFormQueryDelegate not set!");
		}
	}

	public synchronized void beginTransaction()
			throws UnsupportedOperationException, SQLException {
		if (activeConnection != null) {
			throw new IllegalStateException("A transaction is already active!");
		}
		activeConnection = connectionPool.reserveConnection();
		activeConnection.setAutoCommit(false);
	}

	public synchronized void commit() throws UnsupportedOperationException,
			SQLException {
		if (activeConnection == null) {
			throw new SQLException("No active transaction");
		}
		if (!activeConnection.getAutoCommit()) {
			activeConnection.commit();
		}
		connectionPool.releaseConnection(activeConnection);
		activeConnection = null;
	}

	public synchronized void rollback() throws UnsupportedOperationException,
			SQLException {
		if (activeConnection == null) {
			throw new SQLException("No active transaction");
		}
		activeConnection.rollback();
		connectionPool.releaseConnection(activeConnection);
		activeConnection = null;
	}

	public List<String> getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public String getQueryString() {
		return queryString;
	}

	public FreeformQueryDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(FreeformQueryDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * This implementation of the containsRowWithKey method rewrites existing
	 * WHERE clauses in the query string. The logic is, however, not very
	 * complex and some times can do the Wrong Thing<sup>TM</sup>. For the
	 * situations where this logic is not enough, you can implement the
	 * getContainsRowQueryString method in FreeformQueryDelegate and this will
	 * be used instead of the logic.
	 * 
	 * @see com.vaadin.addon.sqlcontainer.query.FreeformQueryDelegate#getContainsRowQueryString(Object...)
	 * 
	 *      {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public boolean containsRowWithKey(Object... keys) throws SQLException {
		String query = null;
		boolean contains = false;
		if (delegate != null) {
			if (delegate instanceof FreeformStatementDelegate) {
				try {
					StatementHelper sh = ((FreeformStatementDelegate) delegate)
							.getContainsRowQueryStatement(keys);
					Connection c = getConnection();
					PreparedStatement pstmt = c.prepareStatement(sh
							.getQueryString());
					sh.setParameterValuesToStatement(pstmt);
					ResultSet rs = pstmt.executeQuery();
					contains = rs.next();
					rs.close();
					pstmt.clearParameters();
					pstmt.close();
					releaseConnection(c);
					return contains;
				} catch (UnsupportedOperationException e) {
					// Statement generation not supported, continue...
				}
			}
			try {
				query = delegate.getContainsRowQueryString(keys);
			} catch (UnsupportedOperationException e) {
				query = modifyWhereClause(keys);
			}
		} else {
			query = modifyWhereClause(keys);
		}
		Connection conn = getConnection();
		Statement statement = conn.createStatement();
		// --- Codecentric: cmj / Sonar Rule Fix ---
		try {
			ResultSet rs = statement.executeQuery(query);
			contains = rs.next();
			rs.close();
		} finally {
			statement.close();
			if (conn != null) {
				releaseConnection(conn);
			}
		}
		// --- Codecentric: cmj / Ende Sonar Rule Fix ---

		return contains;
	}

	/**
	 * Releases the connection if it is not part of an active transaction.
	 * 
	 * @param conn
	 *            the connection to release
	 */
	private void releaseConnection(Connection conn) {
		if (conn != activeConnection) {
			connectionPool.releaseConnection(conn);
		}
	}

	private String modifyWhereClause(Object... keys) {
		// Build the where rules for the provided keys
		StringBuffer where = new StringBuffer();
		for (int ix = 0; ix < primaryKeyColumns.size(); ix++) {
			where.append(QueryBuilder.quote(primaryKeyColumns.get(ix)));
			if (keys[ix] == null) {
				where.append(" IS NULL");
			} else {
				where.append(" = '").append(keys[ix]).append("'");
			}
			if (ix < primaryKeyColumns.size() - 1) {
				where.append(" AND ");
			}
		}
		// Is there already a WHERE clause in the query string?
		int index = queryString.toLowerCase().indexOf("where ");
		if (index > -1) {
			// Rewrite the where clause
			return queryString.substring(0, index) + "WHERE " + where + " AND "
					+ queryString.substring(index + 6);
		}
		// Append a where clause
		return queryString + " WHERE " + where;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		try {
			rollback();
		} catch (SQLException ignored) {
			// ignore
		}
		out.defaultWriteObject();
	}
}
