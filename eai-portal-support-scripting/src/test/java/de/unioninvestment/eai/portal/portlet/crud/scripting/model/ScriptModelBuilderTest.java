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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.unioninvestment.eai.portal.support.scripting.*;
import groovy.lang.Closure;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponentGenerator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.ColumnStyleRenderer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.RowStyleRenderer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ConfirmationDialogProvider;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.DynamicOptionList;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.NotificationProvider;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ShowPopupProvider;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest.ReSTDelegateImpl;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.portal.ScriptPortal;
import de.unioninvestment.eai.portal.support.scripting.http.HttpProvider;
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class ScriptModelBuilderTest extends ModelSupport {

	@Mock
	private ScriptBuilder scriptBuilderMock;

	@Mock
	protected Closure<Object> closureMock;

	@Mock
	private UserFactory userFactoryMock;

	@Mock
	private ConnectionPoolFactory connectionPoolFactoryMock;

	private ScriptModelFactory factory;

	private ScriptModelBuilder scriptModelBuilder;

	private Map<Object, Object> mapping;

	private ModelBuilder modelBuilder;

	private PortletConfig portletConfig;

	private Portlet portlet;

	@Mock
	private ScriptModelFactory factoryMock;

	@Mock
	private Portlet portletMock;

	@Mock
	private Map<Object, Object> mappingMock;

	@Mock
	private PortletConfig portletConfigMock;

	@Mock
	private ScriptPortlet scriptPortletMock;

	@Mock
	private Config configMock;

	@Mock
	private ExecutorService prefetchExecutorMock;

	@Mock
	private Portal portalMock;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		factory = new ScriptModelFactory(connectionPoolFactoryMock,
				userFactoryMock, portalMock, new ScriptCompiler(), DatabaseDialect.ORACLE, false);

		when(scriptBuilderMock.buildClosure(any(GroovyScript.class)))
				.thenAnswer(new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {

						return closureMock;
					}
				});

		when(userFactoryMock.getCurrentUser(any(Portlet.class))).thenReturn(
				new CurrentUser(null));

	}

	@Test
	public void shouldBuildScriptCustomComponentById() throws JAXBException {
		prepare("validCustomComponentConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptCustomComponent component = (ScriptCustomComponent) scriptPortlet
				.getElementById("scriptComponent001");

		assertThat(component, notNullValue());
	}

	@Test
	public void shouldBuildScriptCustomComponentInScriptPage()
			throws JAXBException {
		prepare("validCustomComponentConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptComponent scriptComponent = scriptPortlet.getPage().getElements()
				.get(0);

		assertThat(scriptComponent, notNullValue());
		assertThat(scriptComponent, instanceOf(ScriptCustomComponent.class));
		assertThat(((ScriptCustomComponent) scriptComponent).getId(),
				is("scriptComponent001"));
	}

	@Test
	public void shouldBuildScriptCustomComponentInScriptTab()
			throws JAXBException {
		prepare("validCustomComponentConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptComponent scriptComponent = ((ScriptTab) scriptPortlet
				.getElementById("tab001")).getElements().get(0);

		assertThat(scriptComponent, notNullValue());
		assertThat(scriptComponent, instanceOf(ScriptCustomComponent.class));
		assertThat(((ScriptCustomComponent) scriptComponent).getId(),
				is("scriptComponent002"));
	}

	@Test
	public void shouldBuildCustomComponentGenerator() throws JAXBException {
		prepare("validCustomComponentConfig.xml");

		CustomComponent customComponent = (CustomComponent) portlet.getPage()
				.getElements().get(0);

		assertThat(customComponent.getGenerator(),
				is((CustomComponentGenerator) null));

		scriptModelBuilder.build();

		assertThat(customComponent.getGenerator(),
				instanceOf(CustomComponentGenerator.class));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException() {
		when(mappingMock.get(portletMock)).thenReturn(configMock);
		when(configMock.getPortletConfig()).thenReturn(portletConfigMock);
		when(factoryMock.getScriptPortlet(portletMock)).thenReturn(
				scriptPortletMock);
		when(portletMock.getPage()).thenReturn(null);
		when(portletMock.getTabs()).thenReturn(null);

		scriptModelBuilder = new ScriptModelBuilder(factoryMock, eventBus,
				connectionPoolFactoryMock, userFactoryMock, new ScriptCompiler(), scriptBuilderMock,
				portletMock, mappingMock);

		// sollte Exception werfen, weil weder Page noch Tabs vorhanden sind.
		scriptModelBuilder.build();
	}

	@Test
	public void shouldBuildScriptPortlet() throws JAXBException {

		prepare("validConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet, notNullValue());
	}

	@Test
	public void shouldAddTableElementIdToPortlet() throws JAXBException {

		prepare("validIdsConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getElementById("table"),
				is((Object) scriptPortlet.getPage().getElements().get(1)));
		assertThat(scriptPortlet, notNullValue());
	}

	@Test
	public void shouldAddFormActionElementIdToPortlet() throws JAXBException {

		prepare("validIdsConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptForm form = (ScriptForm) scriptPortlet.getPage().getElements()
				.get(0);

		assertThat(scriptPortlet.getElementById("formAction1"),
				is((Object) form.getActions().get(0)));
	}

	@Test
	public void shouldAddDatabaseQueryDelegateOnModelContainer()
			throws JAXBException {

		prepare("validQueryConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getElementById("table");
		DatabaseQueryContainer container = (DatabaseQueryContainer) table
				.getContainer();

		assertThat(container.getDatabaseQueryDelegate(), notNullValue());
	}

	@Test
	public void shouldAddDatabaseQueryDelegateOnModelContainerWhenTypeIsPresent()
			throws JAXBException {

		prepare("validScriptCrudConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getElementById("table");
		DatabaseQueryContainer container = (DatabaseQueryContainer) table
				.getContainer();

		assertThat(container.getDatabaseQueryDelegate(), notNullValue());
	}

	@Test
	public void shouldAddTableActionElementIdToPortlet() throws JAXBException {

		prepare("validIdsConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getElementById("tableAction1"),
				is((Object) ((ScriptTable) scriptPortlet
						.getElementById("table")).getActions().get(0)));
	}

	@Test
	public void shouldBuildPage() throws JAXBException {

		prepare("validConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getPage(), notNullValue());
	}

	@Test
	public void shouldBuildRegion() throws JAXBException {
		prepare("validRegionConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getPage().getElements().get(0),
				instanceOf(ScriptRegion.class));
	}

	@Test
	public void shouldBuildTable() throws JAXBException {

		prepare("validConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getPage().getElements().get(0),
				instanceOf(ScriptTable.class));
	}

	@Test
	public void shouldBuildForm() throws JAXBException {
		// ignore prefetch operations
		setPrefetchExecutor(prefetchExecutorMock);

		prepare("validFormConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(scriptPortlet.getPage().getElements().get(0),
				instanceOf(ScriptForm.class));
	}

	@Test
	public void shouldBuildFormField() throws JAXBException {

		prepare("validFormConfigWithScript.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptForm form = (ScriptForm) scriptPortlet.getPage().getElements()
				.get(0);

		assertThat(form.getFields().get("CNUMBER5_2"),
				instanceOf(ScriptFormField.class));
		assertThat(form.getFields().get("CVARCHAR5_NN"),
				instanceOf(ScriptFormField.class));
		assertThat(form.getFields().get("CDATE"),
				instanceOf(ScriptFormField.class));
		assertThat(form.getFields().get("CNUMBER5_2_NN"),
				instanceOf(ScriptFormField.class));

		assertThat(form.getActions().size(), is(3));
		assertThat(form.getActions().get(0), instanceOf(ScriptFormAction.class));

		assertThat(form.getFields().get("CNUMBER5_2").getOnChange(),
				notNullValue());
		assertThat(form.getFields().get("CVARCHAR5_NN").getOnChange(),
				notNullValue());
		assertThat(form.getFields().get("CDATE").getOnChange(), notNullValue());
		assertThat(form.getFields().get("CNUMBER5_2_NN").getOnChange(),
				notNullValue());
	}

	@Test
	public void shouldBuildMultiselectionFormField() throws JAXBException {

		prepare("validMultiselectFormConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptForm form = (ScriptForm) scriptPortlet.getPage().getElements()
				.get(0);

		assertThat(form.getFields().get("CVARCHAR5_NN"),
				instanceOf(ScriptMultiOptionListFormField.class));

	}

	@Test
	public void shouldBuildTableAction() throws JAXBException {

		prepare("validTableActionConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptTable table = (ScriptTable) scriptPortlet.getPage().getElements()
				.get(0);

		assertThat(table.getActions().get(0),
				instanceOf(ScriptTableAction.class));
	}

	@Test
	public void shouldBuildExportTableAction() throws JAXBException {

		prepare("validTableExportConfig.xml");

		scriptModelBuilder.build();

		Tab tab = portlet.getTabs().getElements().get(2);
		Table table = (Table) tab.getElements().get(0);
		TableAction action = table.getActions().get(0);

		assertThat(action.hasExportFilenameGenerator(), is(true));
	}

	@Test
	public void shouldBuildDynamicSelection() throws JAXBException {

		prepare("validDynamicDropDownConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getPage().getElements().get(0);

		SelectionTableColumn tableColumn = (SelectionTableColumn) table
				.getColumns().get("CNUMBER5_2");
		assertThat(tableColumn.getOptionList(),
				instanceOf(DynamicOptionList.class));
	}

	@Test
	public void shouldBuildRowStyleRenderer() throws JAXBException {

		prepare("validRowStyleRendererConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getPage().getElements().get(0);

		assertThat(table.getRowStyleRenderer(),
				instanceOf(RowStyleRenderer.class));
	}

	@Test
	public void shouldBuildColumnStyleRenderer() throws JAXBException {

		prepare("validColumnStyleRendererConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getPage().getElements().get(0);

		assertThat(table.getColumns().get("CNUMBER5_2")
				.getColumnStyleRenderer(),
				instanceOf(ColumnStyleRenderer.class));
	}

	@Test
	public void shouldBuildColumnValidator() throws JAXBException {

		prepare("validValidationConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getPage().getElements().get(0);

		assertThat(table.getColumns().get("CNUMBER5_2_NN").getValidators()
				.get(0), instanceOf(ScriptFieldValidator.class));
	}

	@Test
	public void shouldBuildRowValidator() throws JAXBException {

		prepare("validValidationConfig.xml");

		scriptModelBuilder.build();

		Table table = (Table) portlet.getPage().getElements().get(0);

		assertThat(table.hasRowValidator(), is(true));
	}

	@Test
	public void shouldBuildTabs() throws JAXBException {

		prepare("validTabsConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptTab tab = scriptPortlet.getTabs().getElements().get("tab1");
		assertThat(tab, notNullValue());
	}

	@Test
	public void shouldBuildNestedTabs() throws JAXBException {

		prepare("validNestedTabsConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptTab tab2 = scriptPortlet.getTabs().getElements().get("tab2");
		ScriptTabs tabs = (ScriptTabs) tab2.getElements().get(1);
		ScriptTab tab3 = tabs.getElements().get("tab3");
		assertThat(tab3, notNullValue());
	}

	@Test
	public void shouldBuildDialogs() throws JAXBException {

		// Given a configuration with dialogs
		prepare("validDialogConfig.xml");

		// when the ScriptModelBuilder builds this configuration
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		// the resulting script model should have dialogs
		ScriptDialog dialog1 = (ScriptDialog) scriptPortlet
				.getElementById("dialog-1");
		assertThat(dialog1, notNullValue());
		assertThat(scriptPortlet.getDialogs().get(0), sameInstance(dialog1));
	}

	@Test
	public void shouldBuildRowChangeEvent() throws JAXBException {

		prepare("validRowChangeEventConfig.xml");

		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		ScriptTable scriptTable = (ScriptTable) scriptPortlet
				.getElementById("table1");

		Closure<?> closure = scriptTable.getOnRowChange();

		assertThat(closure, notNullValue());
	}

	@Test
	public void shouldBindPopupClosure() throws JAXBException {

		prepare("validPopupConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("showPopup"),
				any(ShowPopupProvider.class));
	}

	@Test
	public void shouldBindConfirmClosure() throws JAXBException {

		prepare("validConfirmationDialogConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("confirm"),
				any(ConfirmationDialogProvider.class));
	}

	@Test
	public void shouldBindInfoNotificationClosure() throws JAXBException {

		prepare("validNotificationConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("showInfo"),
				any(NotificationProvider.class));
	}

	@Test
	public void shouldBindErrorNotificationClosure() throws JAXBException {

		prepare("validNotificationConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("showError"),
				any(NotificationProvider.class));
	}

	@Test
	public void shouldBindWarningNotificationClosure() throws JAXBException {

		prepare("validNotificationConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("showWarning"),
				any(NotificationProvider.class));
	}

	@Test
	public void shouldBindSqlProviderClosure() throws JAXBException {

		prepare("validConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("sql"),
				any(SqlProvider.class));
	}

	@Test
	public void shouldBindJMXProviderClosure() throws JAXBException {

		prepare("validConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("jmx"),
				any(JMXProvider.class));
	}

	@Test
	public void shouldBindHttpProviderClosure() throws JAXBException {

		prepare("validConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("http"),
				any(HttpProvider.class));
	}

	@Test
	public void shouldRegisterQueryOptionLists() throws JAXBException {

		prepare("validOptionListRefresh.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		Object queryOptionList = scriptPortlet
				.getElementById("queryOptionListID");

		assertThat(queryOptionList, notNullValue());
		assertThat(queryOptionList instanceof ScriptOptionList, is(true));
	}

	@Test
	public void shouldBindScriptPortalAsPortal() throws JAXBException {

		prepare("validConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("portal"),
				any(ScriptPortal.class));
	}

	@Test
	public void shouldBindAuditLog() throws JAXBException {

		prepare("validConfig.xml");
		scriptModelBuilder.build();

		verify(scriptBuilderMock).addBindingVariable(eq("audit"),
				any(ScriptAuditLogger.class));
	}

	@Test
	public void shouldBuildScriptJmxContainer() throws JAXBException {
		prepare("validJMXContainerConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(1))
						.getContainer(),
				instanceOf(ScriptJMXContainer.class));
	}

	@Test
	public void shouldBuildScriptReSTContainer() throws JAXBException {
		prepare("validReSTContainerConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(0))
						.getContainer(),
				instanceOf(ScriptReSTContainer.class));
	}

	@Test
	public void shouldBuildScriptBackendContainer() throws JAXBException {
		prepare("validScriptContainerConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(1))
						.getContainer(),
				instanceOf(ScriptContainer.class));
	}

	@Test
	public void shouldBuildRestBackendContainer() throws JAXBException {
		prepare("validTwitterConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(0))
						.getContainer(),
				instanceOf(ScriptContainer.class));
	}

	@Test
	public void shouldBuildScriptSQLContainer() throws JAXBException {
		prepare("validConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(0))
						.getContainer(),
				instanceOf(ScriptDatabaseContainer.class));
	}

	@Test
	public void shouldBuildScriptSQLQueryContainer() throws JAXBException {
		prepare("validQueryConfig.xml");
		ScriptPortlet scriptPortlet = scriptModelBuilder.build();

		assertThat(
				((ScriptTable) scriptPortlet.getPage().getElements().get(0))
						.getContainer(),
				instanceOf(ScriptDatabaseQueryContainer.class));
	}

	@Test
	public void shouldBindRestContainerToReSTDelegate() throws JAXBException {
		prepare("validTwitterConfig.xml");

		Table table = (Table) portlet.getPage().getElements().get(0);
		ReSTContainer container = (ReSTContainer) table.getContainer();

		scriptModelBuilder.build();

		assertThat(container.getDelegate(), instanceOf(ReSTDelegateImpl.class));
	}

	private void prepare(String configFile) throws JAXBException {

		portletConfig = createConfiguration(configFile);
		modelBuilder = createModelBuilder(portletConfig);
		portlet = modelBuilder.build();
		mapping = modelBuilder.getModelToConfigMapping();
		scriptModelBuilder = new ScriptModelBuilder(factory, eventBus,
				connectionPoolFactoryMock, userFactoryMock, new ScriptCompiler(), scriptBuilderMock,
				portlet, mapping);
	}

    @Test
    public void operationEnumShouldProvideMethodNameFromScriptModelBuilder() {
        assertThat(ScriptModelBuilder.Operation.INSERT.getMethodName(), is("generateInsertStatement"));
    }
}
