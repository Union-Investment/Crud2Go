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
package de.unioninvestment.eai.portal.portlet.crud.domain.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;

/**
 * Abstrakte Klasse des Connectionpools.
 * 
 */
public abstract class AbstractConnectionPool implements ConnectionPool {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractConnectionPool.class);

	private String shortName;

	/**
	 * Konstruktor.
	 * 
	 * @param displayName
	 *            Name
	 */
	public AbstractConnectionPool(String displayName) {
		this.shortName = displayName;
	}

	@Override
	public abstract DataSource lookupDataSource() throws NamingException;

	/**
	 * Führt ein query aus .
	 * 
	 * @param <T>
	 *            Typ
	 * @param query
	 *            QueryString
	 * @param callback
	 *            Zum generieren der Liste
	 * @return result Ergebnissliste
	 */
	@Override
	public <T> List<T> executeWithJdbcTemplate(String query,
			RowMapper<T> callback) {
		try {
			JdbcTemplate template = new JdbcTemplate(this.lookupDataSource());
			return template.query(query, callback);
		} catch (NamingException e) {
			throw new BusinessException(
					"portlet.crud.error.dataSourceNotFound", shortName);
		}
	}


	@Override
	public <T> T querySingleResultWithJdbcTemplate(
			final StatementHelper statementHelper, RowMapper<T> callback) {
		try {
			JdbcTemplate template = new JdbcTemplate(this.lookupDataSource());
			List<T> result = template.query(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement pstmt = con
							.prepareStatement(statementHelper.getQueryString());
					statementHelper.setParameterValuesToStatement(pstmt);
					return pstmt;
				}
			}, callback);
			return result.size() > 0 ? result.get(0) : null;
		} catch (NamingException e) {
			throw new BusinessException(
					"portlet.crud.error.dataSourceNotFound", shortName);
		}
	}

	/**
	 * @throws SQLException
	 *             wenn der Verbindungsversuch fehlschlägt
	 * 
	 *             {@inheritDoc}
	 */
	@Override
	public Connection reserveConnection() throws SQLException {
		Connection conn = null;
		try {
			DataSource ds = lookupDataSource();
			conn = ds.getConnection();
			conn.setAutoCommit(false);

		} catch (NamingException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(
					"portlet.crud.error.dataSourceNotFound", shortName);
		}
		return conn;
	}

	/**
	 * @param conn
	 *            die Datenbankverbindung die geschlossen werden soll. Falls
	 *            <code>null</code>, wird der Aufruf ignoriert
	 */
	@Override
	public void releaseConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LOG.error("Error closing database connection" + e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		// Nothing to do.
	}

}