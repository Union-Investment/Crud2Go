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

import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
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
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;

public class ConfigurationProcessorTest {

	@Mock
	private ConfigurationVisitor visitorMock;

	private InOrder inOrder;
	private ConfigurationProcessor processor;

	private PortletConfig portletConfig = new PortletConfig();
	private PageConfig pageConfig = new PageConfig();
	private TabsConfig tabsConfig = new TabsConfig();
	private TabConfig tabConfig = new TabConfig();
	private FormConfig formConfig = new FormConfig();
	private TableConfig tableConfig = new TableConfig();
	private FormActionConfig formActionConfig = new FormActionConfig();
	private FormFieldConfig formFieldConfig = new FormFieldConfig();
	private ColumnsConfig columnsConfig = new ColumnsConfig();
	private ColumnConfig columnConfig = new ColumnConfig();
	private RolesConfig rolesConfig = new RolesConfig();
	private RoleConfig roleConfig = new RoleConfig();
	private FormSelectConfig selectConfig = new FormSelectConfig();
	private QueryConfig queryConfig = new QueryConfig();
	private DatabaseQueryConfig databaseQueryConfig = new DatabaseQueryConfig();
	private DatabaseTableConfig databaseTableConfig = new DatabaseTableConfig();
	private ScriptConfig scriptConfig = new ScriptConfig();
	private RegionConfig regionConfig = new RegionConfig();
	private ScriptContainerConfig scriptContainerConfig = new ScriptContainerConfig();
	private ReSTContainerConfig reSTContainerConfig = new ReSTContainerConfig();
	private TableActionConfig tableActionConfig = new TableActionConfig();
	private AuthenticationConfig authenticationConfig = new AuthenticationConfig();
	private AuthenticationRealmConfig realmConfig = new AuthenticationRealmConfig();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		inOrder = Mockito.inOrder(visitorMock);

		processor = new ConfigurationProcessor(visitorMock);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletConfig() {
		processor.traverse(portletConfig);

		verify(visitorMock).visit(portletConfig);
		verify(visitorMock).visitAfter(portletConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndAuthenticationConfig() {
		portletConfig.setAuthentication(authenticationConfig);
		processor.traverse(portletConfig);
		verifyVisitsInOrder(portletConfig, authenticationConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnAuthenticationAndRealmConfig() {
		authenticationConfig.getRealm().add(realmConfig);
		processor.traverseAuthentication(authenticationConfig);
		verifyVisitsInOrder(authenticationConfig, realmConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnPortletAndScriptConfig() {
		portletConfig.getScript().add(scriptConfig);
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
	public void shouldCallVisitorMethodsOnTableAndTableActionsConfigs() {
		tableConfig.getAction().add(tableActionConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, tableActionConfig);
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

	@Test
	public void shouldCallVisitorMethodsOnTableAndScriptContainerConfig() {
		tableConfig.setScriptContainer(scriptContainerConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, scriptContainerConfig);
	}

	@Test
	public void shouldCallVisitorMethodsOnTableAndReSTContainerConfig() {
		tableConfig.setRestContainer(reSTContainerConfig);
		processor.traverseTable(tableConfig);
		verifyVisitsInOrder(tableConfig, reSTContainerConfig);
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
