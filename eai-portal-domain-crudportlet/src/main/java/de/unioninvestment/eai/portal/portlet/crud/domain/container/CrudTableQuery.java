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
import java.sql.SQLTimeoutException;
import java.sql.Statement;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;

/**
 * Erweiterung von {@link TableQuery}, die um die Möglichkeit erweitert wurde
 * eine Timeout Zeit zu setzen.<br/>
 * Wird ein Timeout-Wert größer 0 gesetzt und das Count-Query benötigt länger
 * als diese Zeit, wird eine {@link SQLTimeoutException} geworfen.
 * 
 * Diese Klasse sollte nach Möglichkeit nicht geändert werden. Angepasste
 * Codestellen sollten zum späteren Refactoring mit Kommentaren versehen sein.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
@SuppressWarnings("serial")
public class CrudTableQuery extends TableQuery implements
		TimeoutableQueryDelegate {

	private int queryTimeout;

	public CrudTableQuery(String tableName, JDBCConnectionPool connectionPool) {
		super(tableName, TimeoutableConnectionWrapper.wrapPool(connectionPool));
	}

	public CrudTableQuery(String tableName, JDBCConnectionPool connectionPool,
			SQLGenerator sqlGenerator) {
		super(tableName, TimeoutableConnectionWrapper.wrapPool(connectionPool),
				sqlGenerator);
	}

	@Override
	protected Connection getConnection() throws SQLException {
		Connection connection = super.getConnection();
		((TimeoutableConnection) connection).setDefaultTimeout(queryTimeout);
		return connection;
	}

	/*
	 * This method is overridden as sometimes the wrapped delegate of the active
	 * connection is passed and in that case doesn't have to be released.
	 */
	@Override
	protected void releaseConnection(Connection conn, Statement statement,
            ResultSet rs) throws SQLException {
        try {
            try {
                if (null != rs) {
                    rs.close();
                }
            } finally {
                if (null != statement) {
                    if (statement instanceof PreparedStatement) {
                        try {
                            ((PreparedStatement) statement).clearParameters();
                        } catch (Exception e) {
                            // will be closed below anyway
                        }
                    }
                    statement.close();
                }
            }
        } finally {
        	if (conn != null) {
        		if (!isInTransaction()) {
        			conn.close();
        		} else {
        			Connection activeConnection = getConnection();
        			if (conn != activeConnection && (!(activeConnection instanceof TimeoutableConnection) || (((TimeoutableConnection)activeConnection).getWrappedConnection() != conn))) {
        				conn.close();
        			}
        		}
        	}
        }
    }

	@Override
	public int getQueryTimeout() {
		return queryTimeout;
	}

	@Override
	public void setQueryTimeout(int seconds) {
		this.queryTimeout = seconds;
	}
}
