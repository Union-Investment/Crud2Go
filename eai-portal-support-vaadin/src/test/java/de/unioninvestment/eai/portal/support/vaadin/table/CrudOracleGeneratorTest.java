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
package de.unioninvestment.eai.portal.support.vaadin.table;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.query.OrderBy;
import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare.Equal;

public class CrudOracleGeneratorTest {

	private CrudOracleGenerator crudOracleGenerator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		crudOracleGenerator = new CrudOracleGenerator();
		crudOracleGenerator.setPrimaryKeyColumns(asList(new String[] { "ID" }));
	}

	@Test
	public void shouldGetIndexStatement() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = crudOracleGenerator.getIndexStatement(
				rowId, "TestCrud2", null, null);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM TestCrud2 ORDER BY \"ID\" ASC) x ) WHERE ID=?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatementWithOrderBy() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = crudOracleGenerator.getIndexStatement(
				rowId, "TestCrud2", null, asList(new OrderBy("TEST", true)));
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM TestCrud2 ORDER BY \"TEST\" ASC) x ) WHERE ID=?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatementWithFilters() {
		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = crudOracleGenerator.getIndexStatement(
				rowId, "TestCrud2",
				asList(new Filter[] { new Equal("TEST", 1) }), null);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM TestCrud2 ORDER BY \"ID\" ASC) x  WHERE \"TEST\" = ?) WHERE ID=?",
				queryString);
	}

	@Test
	public void shouldGetIndexStatementMultiplePKs() {

		crudOracleGenerator.setPrimaryKeyColumns(asList(new String[] { "ID",
				"NAME" }));

		RowId rowId = new RowId(new Object[] { 1l, 2l });
		StatementHelper queryStatement = crudOracleGenerator.getIndexStatement(
				rowId, "TestCrud2", null, null);
		String queryString = queryStatement.getQueryString();

		assertEquals(
				"SELECT * FROM (SELECT x.*, ROWNUM AS \"rownum\" FROM (SELECT * FROM TestCrud2 ORDER BY \"ID\" ASC) x ) WHERE ID=? AND NAME=?",
				queryString);
	}
}
