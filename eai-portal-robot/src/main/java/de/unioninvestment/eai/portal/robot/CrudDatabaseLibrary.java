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
package de.unioninvestment.eai.portal.robot;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CrudDatabaseLibrary {

	private Connection conn = null;

	String driverClass;
	String connectionURL;
	String dbUser;
	String dbPassword;

	public CrudDatabaseLibrary(String driverClass, String connectionURL,
			String dbUser, String dbPassword) throws Exception {
		super();
		this.driverClass = driverClass;
		this.connectionURL = connectionURL;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;

		try {
			Class.forName(driverClass).newInstance();

		} catch (IllegalAccessException e) {
			System.out.println("Illegal Access Exception: (Open Connection).");
			e.printStackTrace();
			throw e;
		} catch (InstantiationException e) {
			System.out.println("Instantiation Exception: (Open Connection).");
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception: (Open Connection).");
			e.printStackTrace();
			throw e;
		}
	}

	public void connectToOracleDatabase() throws IllegalAccessException,
			InstantiationException, ClassNotFoundException, SQLException {

		try {
			conn = DriverManager.getConnection(connectionURL, dbUser,
					dbPassword);
			conn.setAutoCommit(false);
			System.out.println("Connected.\n");

		} catch (SQLException e) {
			System.out.println("Caught SQL Exception: (Open Connection).");
			e.printStackTrace();
			throw e;
		}

	}

	public void disconnectfromOracleDatabase() {
		try {
			if (conn != null) {
				conn.close();
				System.out.println("Disconnected.\n");
			}
		} catch (SQLException e) {
			System.out.println("Caught SQL Exception: (Closing Connection).");
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e2) {
					System.out
							.println("Caught SQL (Rollback Failed) Exception.");
					e2.printStackTrace();
				}
			}
		}

	}

	public void insertBlob(String portletId, String communityId,
			String configFile, String user) throws Exception {
		PreparedStatement prepareStatement = null;
		try {
			connectToOracleDatabase();

			String sql = "INSERT INTO ADM_CONFIG (PORTLET_ID, COMMUNITY_ID, CONFIG_NAME, CONFIG_XML, USER_CREATED, DATE_CREATED)"
					+ "VALUES(?,?,?,?,?,?)";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setString(1, portletId);
			prepareStatement.setLong(2, Long.parseLong(communityId));
			prepareStatement.setString(3, configFile);
			prepareStatement.setBlob(4, getXmlFromClasspath(configFile));
			prepareStatement.setString(5, user);
			prepareStatement.setTimestamp(6,
					new Timestamp(System.currentTimeMillis()));
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate < 1) {
				throw new Exception("Fehler beim einfügen");
			}
			conn.commit();

		} catch (Exception e) {
			rollback();
			throw e;

		} finally {
			closeStatement(prepareStatement);
			disconnectfromOracleDatabase();
		}
	}

	public void deleteAllPortletConfigs() throws Exception {
		PreparedStatement prepareStatement = null;
		try {
			connectToOracleDatabase();

			String sql = "DELETE FROM ADM_CONFIG";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.executeUpdate();
			conn.commit();

		} catch (Exception e) {
			rollback();
			throw e;

		} finally {
			closeStatement(prepareStatement);
			disconnectfromOracleDatabase();
		}
	}

	private void closeStatement(PreparedStatement prepareStatement)
			throws SQLException {
		if (prepareStatement != null) {
			prepareStatement.close();
		}
	}

	private void rollback() {
		if (conn != null) {
			try {
				conn.rollback();

			} catch (SQLException e) {
				System.out.println("Error rolling back: " + e.getMessage());
			}
		}
	}

	private InputStream getXmlFromClasspath(String location) throws IOException {
		InputStream is = CrudDatabaseLibrary.class.getClassLoader()
				.getResourceAsStream(location);
		return is;
	}

	public long readPrimaryKeyForResourceId(String resourceId) throws Exception {
		PreparedStatement prepareStatement = null;
		long primaryKey = 0;
		try {
			connectToOracleDatabase();
			String sql = "SELECT PRIMKEY FROM RESOURCEID_PRIMKEY WHERE RESOURCEID = ?";
			prepareStatement = conn.prepareStatement(sql);
			prepareStatement.setString(1, resourceId);
			ResultSet resultSet = prepareStatement.executeQuery();
			if (!resultSet.next()) {
				throw new RuntimeException(
						"Table RESOURCEID_PRIMKEY has no entry for resourceId "
								+ resourceId + ".");
			}

			primaryKey = resultSet.getLong("PRIMKEY");

			if (resultSet.next()) {
				throw new RuntimeException(
						"Table RESOURCEID_PRIMKEY has more than one entry for resourceId "
								+ resourceId + ".");
			}

			return primaryKey;
		} finally {
			closeStatement(prepareStatement);
			disconnectfromOracleDatabase();
		}
	}
}