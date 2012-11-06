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
package de.unioninvestment.eai.portal.portlet.crud.config.visitor;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.QueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptConfig;

public class DatasourceNameCollectingVisitorTest {

	private DatasourceNameCollectingVisitor visitor = new DatasourceNameCollectingVisitor();

	@Test
	public void shouldVisitQueryConfig() {
		QueryConfig config = new QueryConfig();
		config.setDatasource("foo");
		visitor.visit(config);

		assertEquals(
				new HashSet<String>(Arrays.asList(new String[] { "foo" })),
				visitor.getDatasourceNames());
	}

	@Test
	public void shouldVisitDatabaseQueryConfig() {
		DatabaseQueryConfig config = new DatabaseQueryConfig();
		config.setDatasource("foo");
		visitor.visit(config);

		assertEquals(
				new HashSet<String>(Arrays.asList(new String[] { "foo" })),
				visitor.getDatasourceNames());
	}

	@Test
	public void shouldVisitDatabaseTableConfig() {
		DatabaseTableConfig config = new DatabaseTableConfig();
		config.setDatasource("foo");
		visitor.visit(config);

		assertEquals(
				new HashSet<String>(Arrays.asList(new String[] { "foo" })),
				visitor.getDatasourceNames());
	}

	@Test
	public void shouldVisitScriptConfig() {
		ScriptConfig config = new ScriptConfig();
		config.setValue(new GroovyScript(
				"aslkdjfkl alsdkfjlk f sql ('foo' )kdajfalkjf la sk sql( \"bar\") "));
		visitor.visit(config);

		assertEquals(
				new HashSet<String>(
						Arrays.asList(new String[] { "foo", "bar" })),
				visitor.getDatasourceNames());
	}
}
