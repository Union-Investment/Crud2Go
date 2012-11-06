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
package de.unioninvestment.eai.portal.support.scripting;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptForm;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormAction;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptFormField;

public class ScriptSQLWhereFactoryTest {

	private ScriptFormSQLWhereFactory factory;

	private ScriptCompiler compiler = new ScriptCompiler();

	@Mock
	private ScriptFormAction formActionMock;

	@Mock
	private ScriptForm scriptForm;

	private Map<String, ScriptFormField> fieldsMap = new HashMap<String, ScriptFormField>();

	@Mock
	private ScriptFormField formFieldMock;

	@Mock
	private ScriptFormField formFieldMock2;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(formActionMock.getForm()).thenReturn(scriptForm);
		when(scriptForm.getFields()).thenReturn(fieldsMap);
		when(formFieldMock.getValue()).thenReturn("Hallo!");
		when(formFieldMock2.getValue()).thenReturn(null);
		fieldsMap.put("FELD1", formFieldMock);
		fieldsMap.put("FELD2", formFieldMock2);
	}

	@Test
	public void shouldCreateFilterFromScript() throws InstantiationException,
			IllegalAccessException {
		Script mainScript = runScript("test1 = 'Hallo!'");
		String where = "select 1 from dual where test = $test1";

		factory = new ScriptFormSQLWhereFactory(mainScript, formActionMock);
		SQLFilter sqlFilter = factory.createFilter("test123", where);

		assertThat(sqlFilter.getValues().size(), is(1));
		assertThat(sqlFilter.getValues().get(0), is((Object) "Hallo!"));
		assertThat(sqlFilter.getWhereString(),
				is("select 1 from dual where test = ?"));
	}

	@Test
	public void shouldCreateFilterFromForm() throws InstantiationException,
			IllegalAccessException {
		Script mainScript = runScript("test1 = 'Hallo!'");
		String where = "select 1 from dual where test = $fields.FELD1.value";

		factory = new ScriptFormSQLWhereFactory(mainScript, formActionMock);
		SQLFilter sqlFilter = factory.createFilter("test123", where);

		assertThat(sqlFilter.getValues().size(), is(1));
		assertThat(sqlFilter.getValues().get(0), is((Object) "Hallo!"));
		assertThat(sqlFilter.getWhereString(),
				is("select 1 from dual where test = ?"));
	}

	@Test
	public void shouldCreateFilterFromFormNULL() throws InstantiationException,
			IllegalAccessException {
		Script mainScript = runScript("test1 = 'Hallo!'");
		String where = "select 1 from dual where test = $fields.FELD2.value AND test = $fields.FELD1.value";

		factory = new ScriptFormSQLWhereFactory(mainScript, formActionMock);
		SQLFilter sqlFilter = factory.createFilter("test123", where);

		assertThat(sqlFilter, is(nullValue()));
	}

	private Script runScript(String script) throws InstantiationException,
			IllegalAccessException {
		Class<Script> compileScript = compiler.compileScript(script);
		Script instance = compileScript.newInstance();
		instance.run();

		return instance;
	}
}
