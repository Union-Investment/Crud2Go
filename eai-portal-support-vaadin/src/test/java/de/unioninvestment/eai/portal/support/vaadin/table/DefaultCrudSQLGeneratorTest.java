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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;

public class DefaultCrudSQLGeneratorTest {

	private DefaultCrudSQLGenerator generator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		generator = new DefaultCrudSQLGenerator();
		generator.setPrimaryKeyColumns(asList(new String[] { "ID" }));
		QueryBuilder.setStringDecorator(DatabaseDialect.MYSQL.getStringDecorator());
	}

	@Test
	public void shouldGetQueryStatement() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = generator.generateSelectQuery(
				"TestCrud2", null, null, 0, 0, "*");
		String queryString = queryStatement.getQueryString();

		assertThat(queryString, is("SELECT * FROM TestCrud2"));
	}

	@Test
	public void shouldGetIndexStatement() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = generator.getIndexStatement(rowId,
				"TestCrud2", null, null);
		String queryString = queryStatement.getQueryString();

		assertThat(queryString, is("SELECT c.`rownum` FROM (SELECT @rn:=@rn+1 `rownum`, a.* FROM ("
						+ "SELECT * FROM TestCrud2" // query
						+ ") a, (SELECT @rn:=0) r) c WHERE `ID`=?"));
	}

	@Test
	public void shouldGetIndexStatementWithOrderBy() {

		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = generator.getIndexStatement(rowId,
				"TestCrud2", null, asList(new OrderBy("TEST", true)));
		String queryString = queryStatement.getQueryString();

		assertThat(queryString, is("SELECT c.`rownum` FROM (SELECT @rn:=@rn+1 `rownum`, a.* FROM ("
				+ "SELECT * FROM TestCrud2 ORDER BY `TEST` ASC" // query
				+ ") a, (SELECT @rn:=0) r) c WHERE `ID`=?"));
	}

	@Test
	public void shouldGetIndexStatementWithFilters() {
		RowId rowId = new RowId(new Object[] { 1l });
		StatementHelper queryStatement = generator.getIndexStatement(rowId,
				"TestCrud2", asList(new Filter[] { new Equal("TEST", 1) }),
				null);
		String queryString = queryStatement.getQueryString();

		assertThat(queryString, is("SELECT c.`rownum` FROM (SELECT @rn:=@rn+1 `rownum`, a.* FROM ("
				+ "SELECT * FROM TestCrud2 WHERE `TEST` = ?" // query
				+ ") a, (SELECT @rn:=0) r) c WHERE `ID`=?"));
	}

	@Test
	public void shouldGetIndexStatementMultiplePKs() {

		generator.setPrimaryKeyColumns(asList(new String[] { "ID", "NAME" }));

		RowId rowId = new RowId(new Object[] { 1l, 2l });
		StatementHelper queryStatement = generator.getIndexStatement(rowId,
				"TestCrud2", null, null);
		String queryString = queryStatement.getQueryString();

		assertThat(queryString, is("SELECT c.`rownum` FROM (SELECT @rn:=@rn+1 `rownum`, a.* FROM ("
				+ "SELECT * FROM TestCrud2" // query
				+ ") a, (SELECT @rn:=0) r) c WHERE `ID`=? AND `NAME`=?"));
	}
}
