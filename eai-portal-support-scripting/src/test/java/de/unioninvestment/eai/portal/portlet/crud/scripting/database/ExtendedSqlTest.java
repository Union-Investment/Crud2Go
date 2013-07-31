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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.GString;

import java.sql.Connection;
import java.sql.SQLException;

import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptBlob;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptClob;

public class ExtendedSqlTest {
	private ExtendedSql eSql;

	@Mock
	private Connection connectionMock;

	@Mock
	private ScriptClob scriptClobMock;

	@Mock
	private ScriptBlob scriptBlobMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		eSql = new ExtendedSql(connectionMock);
	}

	@Test
	public void shouldFindWhereAtRightPosition() {
		String sql = "UPDATE bewegungsdaten_out set ATTRIBUT_TYP = null, "
				+ "ANLEGER_ID = ?, DATENTYP = null, "
				+ "WKN_ANTEILSCHEIN = null, ZIELFONDS = null, "
				+ "VERWAHRART = null, DATUM = null, WERT = ?, "
				+ "IKDT_RELEASE = ?, IKDT_UPDATE_TS = CURRENT_TIMESTAMP, "
				+ "IKDT_UPDATE_USER = 'EAI' WHERE ID = ?";
		assertThat(eSql.findWhereKeyword(sql),
				is(sql.length() - "WHERE ID = ?".length()));
	}

	@Test
	public void shouldReplaceEmptyScriptClobWithNull() throws SQLException {
		GString sql = new GStringImpl( //
				new Object[] { scriptClobMock }, //
				new String[] { "a", "b" });
		when(scriptClobMock.isEmpty()).thenReturn(true);

		eSql.replaceScriptLob(sql);

		assertThat(sql.getValue(0), is(nullValue()));
	}

	@Test
	public void shouldReplaceEmptyScriptBlobWithNull() throws SQLException {
		GString sql = new GStringImpl( //
				new Object[] { scriptBlobMock }, //
				new String[] { "a", "b" });
		when(scriptBlobMock.isEmpty()).thenReturn(true);

		eSql.replaceScriptLob(sql);

		assertThat(sql.getValue(0), is(nullValue()));
	}
}
