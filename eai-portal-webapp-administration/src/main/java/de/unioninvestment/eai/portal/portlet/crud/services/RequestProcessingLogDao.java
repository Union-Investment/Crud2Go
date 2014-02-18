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

package de.unioninvestment.eai.portal.portlet.crud.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

/**
 * DAO that writes request log entries into the database.
 * 
 * @author cmj
 */
@Repository
public class RequestProcessingLogDao {

	protected LobHandler lobHandler = new DefaultLobHandler();
	protected JdbcTemplate jdbcTemplate;

	/**
	 * 
	 * @param jdbcTemplate
	 *            JdbcTemplate
	 */
	@Autowired
	public RequestProcessingLogDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Write a new request log entry
	 * 
	 * @param url
	 *            the browser url
	 * @param svnUrl
	 *            the configuration filename or SVN-URL
	 * @param sqlStatements
	 *            SQL statements executed during the request
	 * @param countOfSqlStatements
	 *            the number of SQL statements executed during the request
	 * @param requestStart
	 *            the request start time
	 * @param requestDuration
	 *            the request duration in milliseconds
	 * @param dbDuration
	 *            the sum of DB operations duration in milliseconds
	 */
	public void storeRequestLogEntry(final String url, final String svnUrl,
			final String sqlStatements, final int countOfSqlStatements,
			final Date requestStart, final long requestDuration,
			final long dbDuration) {

		jdbcTemplate
				.execute(
						"INSERT INTO C2G_REQUEST_LOG (URL, SVN_URL, SQL_STMTS, SQL_COUNT, REQUEST_START, REQUEST_MS, DB_MS) VALUES (?,?,?,?,?,?,?)",
						new AbstractLobCreatingPreparedStatementCallback(
								lobHandler) {
							@Override
							protected void setValues(PreparedStatement ps,
									LobCreator lobCreator) throws SQLException,
									DataAccessException {

								ps.setString(1, url);
								ps.setString(2, svnUrl);
								lobCreator
										.setClobAsString(ps, 3, sqlStatements);
								ps.setInt(4, countOfSqlStatements);
								ps.setTimestamp(5,
										new Timestamp(requestStart.getTime()));
								ps.setLong(6, requestDuration);
								ps.setLong(7, dbDuration);
							}
						});
	}

	/**
	 * Remove all entries older than the given date from the database.
	 * 
	 * @param newestDateToDelete
	 * @return the number of deleted rows
	 */
	public int cleanupRequestLogTable(final Date newestDateToDelete) {
		return jdbcTemplate.execute(
				"DELETE FROM C2G_REQUEST_LOG WHERE REQUEST_START <= ?",
				new PreparedStatementCallback<Integer>() {
					@Override
					public Integer doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ps.setTimestamp(1,
								new Timestamp(newestDateToDelete.getTime()));
						return ps.executeUpdate();
					}
				});
	}
}
