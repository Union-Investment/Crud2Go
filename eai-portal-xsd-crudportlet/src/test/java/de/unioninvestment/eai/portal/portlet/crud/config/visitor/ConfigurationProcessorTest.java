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

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormSelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.QueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;

public class ConfigurationProcessorTest {

	@Mock
	private ConfigurationVisitor visitorMock;
	private PortletConfig portletConfig;
	private PageConfig pageConfig;
	private InOrder inOrder;
	private TabsConfig tabsConfig;
	private ConfigurationProcessor processor;
	private TabConfig tabConfig;
	private FormConfig formConfig;
	private TableConfig tableConfig;
	private FormActionConfig formActionConfig;
	private FormFieldConfig formFieldConfig;
	private ColumnsConfig columnsConfig;
	private ColumnConfig columnConfig;
	private RolesConfig rolesConfig;
	private RoleConfig roleConfig;
	private FormSelectConfig selectConfig;
	private QueryConfig queryConfig;
	private DatabaseQueryConfig databaseQueryConfig;
	private DatabaseTableConfig databaseTableConfig;
	private ScriptConfig scriptConfig;
	private RegionConfig regionConfig;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		inOrder = Mockito.inOrder(visitorMock);

		portletConfig = new PortletConfig();
		rolesConfig = new RolesConfig();
		roleConfig = new RoleConfig();
		scriptConfig = new ScriptConfig();

		pageConfig = new PageConfig();
		tabsConfig = new TabsConfig();
		tabConfig = new TabConfig();

		formConfig = new FormConfig();
		formActionConfig = new FormActionConfig();
		formFieldConfig = new FormFieldConfig();
		selectConfig = new FormSelectConfig();
		queryConfig = new QueryConfig();
		regionConfig = new RegionConfig();

		tableConfig = new TableConfig();
		columnsConfig = new ColumnsConfig();
		columnConfig = new ColumnConfig();
		databaseQueryConfig = new DatabaseQueryConfig();
		databaseTableConfig = new DatabaseTableConfig();

		processor = new ConfigurationProcessor(visitorMock);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletConfig() {
		processor.traverse(portletConfig);

		verify(visitorMock).visit(portletConfig);
		verify(visitorMock).visitAfter(portletConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndScriptConfig() {
		portletConfig.setScript(scriptConfig);

		processor.traverse(portletConfig);

		verifyVisitsInOrder(portletConfig, scriptConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndRoleConfigs() {
		portletConfig.setRoles(rolesConfig);
		rolesConfig.getRole().add(roleConfig);

		processor.traverse(portletConfig);

		verifyVisitsInOrder(portletConfig, roleConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndPageConfig() {
		portletConfig.setPage(pageConfig);

		processor.traverse(portletConfig);

		verifyVisitsInOrder(portletConfig, pageConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnTabsAndTabConfig() {
		tabsConfig.getTab().add(tabConfig);

		processor.traverseTabs(tabsConfig);

		verifyVisitsInOrder(tabsConfig, tabConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndTabsConfig() {
		portletConfig.setTabs(tabsConfig);

		processor.traverse(portletConfig);

		verifyVisitsInOrder(portletConfig, tabsConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPanelAndElementConfigs() {
		pageConfig.getElements().add(formConfig);
		pageConfig.getElements().add(tableConfig);
		pageConfig.getElements().add(tabsConfig);

		processor.traversePanel(pageConfig);

		verifyVisitsInOrder(pageConfig, formConfig, tableConfig, tabsConfig);

	}

	@Test
	public void shouldCallVisitorMethodsOnFormAndActionConfigs() {
		formConfig.getAction().add(formActionConfig);
		processor.traverseForm(formConfig);
		verifyVisitsInOrder(formConfig, formActionConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnRegion() {
		pageConfig.getElements().add(regionConfig);
		processor.traversePanel(pageConfig);
		verifyVisitsInOrder(pageConfig, regionConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnRegionElements() {
		pageConfig.getElements().add(regionConfig);
		regionConfig.getElements().add(formConfig);
		processor.traversePanel(pageConfig);
		verifyVisitsInOrder(regionConfig, formConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnFormAndFieldConfigs() {
		formConfig.getField().add(formFieldConfig);
		processor.traverseForm(formConfig);
		verifyVisitsInOrder(formConfig, formFieldConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnFormFieldConfigAndQueryConfig() {
		selectConfig.setQuery(queryConfig);
		formFieldConfig.setSelect(selectConfig);
		processor.traverseFormField(formFieldConfig);
		verifyVisitsInOrder(formFieldConfig, queryConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnTableAndColumnsConfigs() {
		tableConfig.setColumns(columnsConfig);
		columnsConfig.getColumn().add(columnConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, columnsConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnTableAndDatabaseQueryConfig() {
		tableConfig.setDatabaseQuery(databaseQueryConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, databaseQueryConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnTableAndDatabaseTableConfig() {
		tableConfig.setDatabaseTable(databaseTableConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, databaseTableConfig);
	}

	private void verifyVisitsInOrder(Object parentConfig,
			Object... childConfigs) {
		inOrder.verify(visitorMock).visit(parentConfig);
		for (Object childConfig : childConfigs) {
			inOrder.verify(visitorMock).visit(childConfig);
			inOrder.verify(visitorMock).visitAfter(childConfig);
		}
		inOrder.verify(visitorMock).visitAfter(parentConfig);
	}
}
