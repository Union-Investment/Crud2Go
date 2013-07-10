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

import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

/**
 * Connection-Pool-Schnittstelle.
 * 
 * @author max.hartmann
 * 
 */
public interface ConnectionPool extends JDBCConnectionPool {

	/**
	 * Führt eine Abfrage auf dem Connection-Pool aus.
	 * 
	 * @param query
	 *            Abfrage
	 * @param callback
	 *            Row-Mapper
	 * @return Abfrage-Ergebnis
	 * 
	 * @param <T>
	 *            Typ der Rückgabe
	 * 
	 * @see {@link RowMapper}
	 */
	<T> List<T> executeWithJdbcTemplate(String query, RowMapper<T> callback);

	/**
	 * Führt eine Abfrage auf dem Connection-Pool aus.
	 * 
	 * @param statementHelper
	 *            statement
	 * @param callback
	 *            Row-Mapper
	 * @return Abfrage-Ergebnis
	 * 
	 * @param <T>
	 *            Typ der Rückgabe
	 * 
	 * @see {@link RowMapper}
	 */
	<T> T querySingleResultWithJdbcTemplate(StatementHelper statementHelper,
			RowMapper<T> callback);

	/**
	 * @return eine DataSource aus dem Pool
	 * @throws NamingException
	 *             NamingException
	 */
	DataSource lookupDataSource() throws NamingException;

}