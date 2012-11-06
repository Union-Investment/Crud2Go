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
package de.unioninvestment.eai.portal.portlet.crud.scripting.database;

import groovy.lang.GString;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import oracle.sql.CLOB;

import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.jdbc.support.nativejdbc.JBossNativeJdbcExtractor;

import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptBlob;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptClob;

/**
 * Erweiterung der Groovy Sql Klasse
 * 
 * @author markus.bonsch
 * 
 */
public class ExtendedSql extends Sql {

	public ExtendedSql(DataSource dataSource) {
		super(dataSource);
	}

	public ExtendedSql(Connection connection) {
		super(connection);
	}

	public void updateCLob(GString statement, ScriptClob newClob)
			throws Exception {
		GroovyRowResult row = firstRow(statement);

		CLOB clob;
		try {
			clob = (CLOB) row.getAt(0);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Das Statement liefert keinen CLOB.", e);
		}
		writeClobData(clob, newClob);
	}

	public void updateBLob(GString statement, ScriptBlob scriptBlob)
			throws Exception {
		GroovyRowResult row = firstRow(statement);

		oracle.sql.BLOB blob;
		try {
			blob = (oracle.sql.BLOB) row.getAt(0);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Das Statement liefert keinen CLOB.", e);
		}
		writeBlobData(blob, scriptBlob);
	}

	@Override
	public boolean execute(GString gstring) throws SQLException {
		replaceScriptLob(gstring);
		return super.execute(gstring);
	}

	@Override
	public int executeUpdate(GString gstring) throws SQLException {
		replaceScriptLob(gstring);
		return super.executeUpdate(gstring);
	}

	@Override
	public List<List<Object>> executeInsert(GString gstring)
			throws SQLException {
		replaceScriptLob(gstring);
		return super.executeInsert(gstring);
	}

	private void replaceScriptLob(GString gstring) throws SQLException {
		Object[] values = gstring.getValues();
		for (int i = 0; i < values.length; i++) {
			Object o = values[i];
			if (o instanceof ScriptClob) {
				ScriptClob scriptClob = (ScriptClob) o;
				if (scriptClob.getValue() != null) {
				oracle.sql.CLOB oracleCLOB;
				try {
					oracleCLOB = oracle.sql.CLOB.createTemporary(
							unwrappedJBossConnection(), false,
							oracle.sql.CLOB.DURATION_SESSION);
				} catch (Exception e) {
					oracleCLOB = oracle.sql.CLOB.createTemporary(
							unwrappedDbcpConnection(), false,
							oracle.sql.CLOB.DURATION_SESSION);
				}
				try {
						writeClobData(oracleCLOB, scriptClob);
				} catch (IOException e) {
					throw new SQLException("Exception while writing CLob: ", e);
				}
				values[i] = oracleCLOB;
				} else {
					values[i] = null;
				}
			} else if (o instanceof ScriptBlob) {
				oracle.sql.BLOB oracleBLOB;
				try {
					oracleBLOB = oracle.sql.BLOB.createTemporary(
							unwrappedJBossConnection(), false,
							oracle.sql.BLOB.DURATION_SESSION);
				} catch (Exception e) {
					oracleBLOB = oracle.sql.BLOB.createTemporary(
							unwrappedDbcpConnection(), false,
							oracle.sql.BLOB.DURATION_SESSION);
				}
				try {
					writeBlobData(oracleBLOB, (ScriptBlob) o);
				} catch (IOException e) {
					throw new SQLException("Exception while writing BLob: ", e);
				}
				values[i] = oracleBLOB;
			}
		}
	}

	private Connection unwrappedDbcpConnection() throws SQLException {
		return new CommonsDbcpNativeJdbcExtractor()
				.getNativeConnection(createConnection());
	}

	private Connection unwrappedJBossConnection() throws SQLException {

		return new JBossNativeJdbcExtractor()
				.getNativeConnection(createConnection());
	}

	private void writeClobData(CLOB clob, ScriptClob newClob)
			throws SQLException, IOException {
		Reader reader = newClob.getReader();
		if (reader != null) {
			Writer clobWriter = clob.setCharacterStream(1L);

			char[] buffer = new char[65536];
			int count = 0;
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				clobWriter.write(buffer, 0, n);
				count += n;
			}
			reader.close();
			clobWriter.close();
			newClob.commit();
		}
	}

	private void writeBlobData(oracle.sql.BLOB blob, ScriptBlob scriptBlob)
			throws SQLException, IOException {
		InputStream inputStream = scriptBlob.getInputStream();
		OutputStream outputStream = blob.setBinaryStream(0L);

		byte[] buffer = new byte[65536];
		int count = 0;
		int n = 0;
		while (-1 != (n = inputStream.read(buffer))) {
			outputStream.write(buffer, 0, n);
			count += n;
		}
		inputStream.close();
		outputStream.close();
		scriptBlob.commit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Bugfix für fehlerhafte Groovy-SQL Implementierung.
	 */
	@Override
	protected int findWhereKeyword(String sql) {
		char[] chars = sql.toLowerCase().toCharArray();
		char[] whereChars = "where".toCharArray();
		int i = 0;
		boolean inString = false; // TODO: Cater for comments?
		int inWhere = 0;
		while (i < chars.length) {
			switch (chars[i]) {
			case '\'':
				inString = !inString;
				break;
			default:
				if (!inString && chars[i] == whereChars[inWhere]) {
					inWhere++;
					if (inWhere == whereChars.length) {
						return i - whereChars.length + 1;
					}
				} else {
					inWhere = 0;
				}
			}
			i++;
		}
		return -1;
	}

	/**
	 * Liest den kompletten Inhalt des CLOBs in einen String und gibt diesen
	 * zurück.
	 * 
	 * @param row
	 *            ein {@link GroovyRowResult}
	 * @param columnId
	 *            die ID der CLOB-Spalte
	 * @return den Inhalt des CLOB-Feldes
	 */
	public String readClob(GroovyRowResult row, String columnId) {
		try {
			oracle.sql.CLOB clob = (oracle.sql.CLOB) row.get(columnId);
			return (clob != null ? clob.getSubString(1, (int) clob.length())
					: null);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
