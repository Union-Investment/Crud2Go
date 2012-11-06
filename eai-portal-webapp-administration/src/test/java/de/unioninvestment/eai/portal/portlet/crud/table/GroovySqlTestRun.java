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
package de.unioninvestment.eai.portal.portlet.crud.table;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.sql.GroovyResultSet;
import groovy.sql.Sql;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class GroovySqlTestRun {

	private void run() throws SQLException, ClassNotFoundException {
		Sql sql = Sql.newInstance(
				"jdbc:derby:memory:test;create=true", "", "",
				"org.apache.derby.jdbc.EmbeddedDriver");

		String ddl = "create table test (id int primary key, col1 varchar(20))";
		sql.execute(ddl);

		String insert = "insert into test values (1, 'text')";
		sql.execute(insert);

		String select = "select * from test";
		sql.eachRow(select, new Closure<Object>(this) {
			private static final long serialVersionUID = 1L;

			public void doCall(GroovyResultSet row) {
				System.out.println(row);
			}
		});

		String insertGString = "insert into test values (1, $MYVAR)";
		GroovyShell shell = new GroovyShell();
		Closure<?> insertClosure = (Closure<?>) shell.evaluate("{-> \"\"\""
				+ insertGString + "\"\"\"}");

		Map<String, String> data1 = Collections.singletonMap("MYVAR", "myVar");
		Map<String, String> data2 = Collections.singletonMap("MYVAR", "myVar2");

		insertClosure.setDelegate(data1);
		Object result = insertClosure.call();
		System.out.println(result.getClass());
		System.out.println(result);

		insertClosure.setDelegate(data2);
		Object result2 = insertClosure.call();
		System.out.println(result2.getClass());
		System.out.println(result2);

		Binding binding = new Binding(data1);
		GroovyShell groovyShell = new GroovyShell(binding);
		GString gs = (GString) groovyShell
				.evaluate(multilineGStringExpression(insertGString));

		System.out.println(gs.getValue(0));

	}

	private String multilineGStringExpression(String insertGString) {
		return "\"\"\"" + insertGString + "\"\"\"";
	}

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException {
		new GroovySqlTestRun().run();
	}
}
