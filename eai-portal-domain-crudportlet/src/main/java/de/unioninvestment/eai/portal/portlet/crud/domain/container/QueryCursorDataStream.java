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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

public class QueryCursorDataStream implements DataStream {

	private final Container container;
	private final ConnectionPool connectionPool;
	private final StatementHelper currentQuery;
	private Connection connection;
	private PreparedStatement stmt;
	private ResultSet resultSet;
	private Boolean hasNext;

	public QueryCursorDataStream(Container container,
			ConnectionPool connectionPool, StatementHelper currentQuery) {
		this.container = container;
		this.connectionPool = connectionPool;
		this.currentQuery = currentQuery;
	}

	@Override
	public int open(boolean countEntries) {
		int size = countEntries ? container.size() : 0;
		try {
			connection = connectionPool.reserveConnection();
			connection.setAutoCommit(false);
			stmt = connection.prepareStatement(currentQuery.getQueryString());
			stmt.setFetchSize(100);
			currentQuery.setParameterValuesToStatement(stmt);
			resultSet = stmt.executeQuery();
			hasNext = null;

		} catch (SQLException e) {
			throw new TechnicalCrudPortletException(
					"Error opening query data stream", e);
		}
		return size;
	}

	@Override
	public boolean hasNext() {
		if (hasNext == null) {
			try {
				hasNext = resultSet.next();

			} catch (SQLException e) {
				throw new TechnicalCrudPortletException(
						"Error fetching next row of data stream", e);
			}
		}
		return hasNext;
	}

	@Override
	public StreamItem next() {
		hasNext = null;
		return new StreamItem() {
			@Override
			public Object getValue(String columnName) {
				try {
					return resultSet.getObject(columnName);
				} catch (SQLException e) {
					throw new TechnicalCrudPortletException(
							"Error reading column '" + columnName + "'", e);
				}
			}
		};
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// ignore errors
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// ignore errors
			}
		}
		if (connection != null) {
			try {
				connection.commit();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				// ignore errors
			}
			connectionPool.releaseConnection(connection);
		}
	}

}
