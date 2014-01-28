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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

/**
 * Implementation des FreeformStatementDelegate für das CRUD Portlet.
 * 
 * @author markus.bonsch
 * 
 */
public class OracleDatabaseQueryDelegate implements DatabaseQueryDelegate {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(OracleDatabaseQueryDelegate.class);

	private List<Filter> filters;

	private List<OrderBy> orderBys;

	private final String queryString;

	private final List<String> primaryKeyColumns;

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
	public OracleDatabaseQueryDelegate(String queryString,
			List<String> primaryKeyColumns) {
		this.queryString = queryString;
		this.primaryKeyColumns = primaryKeyColumns;
	}

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

	@Override
	@Deprecated
	public String getCountQuery() {
		throw new UnsupportedOperationException("Use getCountStatement method.");
	}

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

	@Override
	public int storeRow(Connection conn, RowItem row) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeRow(Connection conn, RowItem row) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getContainsRowQueryString(Object... keys) {
		throw new UnsupportedOperationException(
				"Please use getContainsRowQueryStatement method.");
	}

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
