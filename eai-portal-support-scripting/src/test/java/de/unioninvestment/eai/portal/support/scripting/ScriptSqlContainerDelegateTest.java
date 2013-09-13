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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelSupport;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptContainerBackend;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class ScriptSqlContainerDelegateTest extends ModelSupport {

	private ConfigurationScriptsCompiler compiler;
	private ScriptContainerDelegate scriptContainerDelegate;

	@Mock
	private GenericItem genericItemMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private GenericItemId genericItemIdMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(containerMock.getPrimaryKeyColumns()).thenReturn(asList("col2"));
		when(genericItemMock.getId()).thenReturn(genericItemIdMock);

		compiler = new ConfigurationScriptsCompiler(new ScriptCompiler());
		PortletConfig portletConfig = createConfiguration("validScriptContainerConfigTest.xml");
		compiler.compileAllScripts(portletConfig);

		Closure<ScriptContainerBackend> delegate = (Closure<ScriptContainerBackend>) getDelegate(
				portletConfig).getClazz().newInstance().run();
		scriptContainerDelegate = new ScriptContainerDelegate(delegate.call(),
				containerMock);
	}

	@Test
	public void shouldGetRows() {
		List<Object[]> rows = scriptContainerDelegate.getRows();
		assertThat(rows.get(0), equalTo(new Object[] {1,null,null,null,"Hello Horst",4711,null,null,null }));


	}

	@Test
	public void shouldGetMetaDataColumnNames() {
		MetaData metaData = scriptContainerDelegate.getMetaData();
		assertThat((List<String>) metaData.getColumnNames(),
				equalTo(asList("ID", "CNUMBER5_2", "CDATE", "CTIMESTAMP", "CVARCHAR5_NN", "CNUMBER5_2_NN", "CDATE_NN", "CTIMESTAMP_NN", "TESTDATA")));
	}

	@Test
	public void shouldGetMetaDataPrimaryKeys() {
		MetaData metaData = scriptContainerDelegate.getMetaData();
		assertThat((List<String>) metaData.getPrimaryKeys(),
				equalTo(asList("ID")));
	}

	private GroovyScript getDelegate(PortletConfig portletConfig) {
		return ((TableConfig) portletConfig.getPage().getElements().get(1))
				.getScriptContainer().getDelegate();
	}

}
