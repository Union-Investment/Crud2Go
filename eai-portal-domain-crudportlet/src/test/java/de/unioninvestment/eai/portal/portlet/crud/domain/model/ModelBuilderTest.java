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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.jdbc.core.RowMapper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Resource;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.ResourceActionLocalService;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.TestUser;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory;
import de.unioninvestment.eai.portal.support.vaadin.validation.ValidationException;

@SuppressWarnings("unchecked")
public class ModelBuilderTest {

	@Mock
	private DatabaseTableContainer tableContainerMock;

	@Mock
	private DatabaseQueryContainer queryContainerMock;

	@Mock
	private ConnectionPool connectionPoolMock;

	@Mock
	private ConnectionPoolFactory connectionPoolFactoryMock;

	@Mock
	private ModelFactory factoryMock;

	@Mock
	private ResetFormAction resetFormActionMock;

	@Mock
	private FieldValidatorFactory fieldValidatorFactoryMock;

	@Mock
	private ResourceLocalService resourceLocalServiceMock;

	@Mock
	private ResourceActionLocalService resourceActionLocalServiceMock;

	List<ContainerOrder> orderBys = new ArrayList<ContainerOrder>();

	@Mock
	private Resource resourceMock;

	@Mock
	private ResourceAction resourceActionMock;

	@Captor
	ArgumentCaptor<String> insertStringCaptor;

	@Captor
	ArgumentCaptor<String> updateStringCaptor;

	@Captor
	ArgumentCaptor<String> deleteStringCaptor;

	@Captor
	ArgumentCaptor<Boolean> insertBooleanCaptor;

	@Captor
	ArgumentCaptor<Boolean> updateBooleanCaptor;

	@Captor
	ArgumentCaptor<Boolean> deleteBooleanCaptor;

	private Map<String, Long> resourceIds = new HashMap<String, Long>();

	private HashMap<String, String> displayPatternMock = new HashMap<String, String>();

	@Mock
	private EventBus eventBus;

	@Mock
	private QueryOptionList queryOptionListMock;

	@Mock
	private Realm realmMock;

	@Mock
	private LiferayUI uiMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Mock
	private UserFactory userFactoryMock;
	
	private static PortletConfigurationUnmarshaller unmarshaller = new PortletConfigurationUnmarshaller();

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		new ResourceLocalServiceUtil().setService(resourceLocalServiceMock);
		new ResourceActionLocalServiceUtil()
				.setService(resourceActionLocalServiceMock);

		mockExistingResourceWithTestAction();

		when(userFactoryMock.getCurrentUser(any(Portlet.class))).thenAnswer(new Answer<CurrentUser>() {
			@Override
			public CurrentUser answer(InvocationOnMock invocation)
					throws Throwable {
				return new CurrentUser(((Portlet)invocation.getArguments()[0]).getRoles());
			}
		});

		when(connectionPoolFactoryMock.getPool(Mockito.eq("test"))).thenReturn(
				connectionPoolMock);
		resourceIds.put("PortletId_17808_admin", 1l);
		resourceIds.put("PortletId_17808_benutzer", 1l);

		UI.setCurrent(uiMock);
		when(uiMock.getPortletId()).thenReturn("PortletId");
		when(uiMock.getCommunityId()).thenReturn(17808L);
	}

	@After
	public void tearDown() {
		UI.setCurrent(null);
	}

	private void mockExistingResourceWithTestAction() throws SystemException,
			PortalException {

		when(
				resourceLocalServiceMock.addResource(1, "PortletId",
						ResourceConstants.SCOPE_COMPANY, "PortletId"))
				.thenReturn(resourceMock);
		when(resourceMock.getName()).thenReturn("PortletId");
		when(resourceActionLocalServiceMock.getResourceActions("PortletId"))
				.thenReturn(asList(resourceActionMock));
		when(resourceActionMock.getActionId()).thenReturn("test");
	}

	private ModelBuilder createTestBuilder(Config config) {
		return new ModelBuilder(eventBus, factoryMock, userFactoryMock, resetFormActionMock,
				fieldValidatorFactoryMock, 300, config, true);
	}

	@Test
	public void shouldCreateDatabaseTableModelFromConfiguration()
			throws Exception {
		PortletConfig config = createConfiguration("validConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eq(eventBus), eq("test"),
						eq("test_crud2"), eq(true), eq(true), eq(true),
						any(CurrentUser.class), any(Map.class),
						any(List.class), eq(FilterPolicy.ALL), anyInt(),
						anyInt(), anyInt())).thenReturn(tableContainerMock);

		Portlet build = builder.build();

		verify(factoryMock).getDatabaseTableContainer(eq(eventBus), eq("test"),
				eq("test_crud2"), eq(true), eq(true), eq(true),
				any(CurrentUser.class), any(Map.class), any(List.class),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));
		assertThat(
				((Table) build.getPage().getElements().get(0)).getContainer(),
				instanceOf(DatabaseTableContainer.class));
	}

	@Test
	public void shouldCreateRolesFromConfiguration() throws Exception {
		PortletConfig config = createConfiguration("validRolesConfig.xml");
		Map<String, Long> roleResourceIds = new HashMap<String, Long>();
		roleResourceIds.put("PortletId_17808_admin5", 1L);
		roleResourceIds.put("PortletId_17808_user5", 2L);
		ModelBuilder builder = createTestBuilder(new Config(config,
				roleResourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eq(eventBus), eq("test"),
						eq("test_crud2"), eq(true), eq(true), eq(true),
						any(CurrentUser.class), any(Map.class),
						any(List.class), eq(FilterPolicy.ALL), anyInt(),
						anyInt(), anyInt())).thenReturn(tableContainerMock);

		Portlet build = builder.build();

		assertThat(findRole(build.getRoles(), "admin5"),
				instanceOf(PortletRole.class));
		assertThat(findRole(build.getRoles(), "user5"),
				instanceOf(PortletRole.class));
		assertThat(findRole(build.getRoles(), "myRole"),
				instanceOf(PortalRole.class));
	}

	private Role findRole(Set<Role> roles, String roleName) {
		for (Role role : roles) {
			if (role.getName().equals(roleName)) {
				return role;
			}
		}
		return null;
	}

	@Test
	public void shouldCreateFormModelFromConfiguration() throws Exception {

		PortletConfig config = createConfiguration("validFormConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eq(eventBus), eq("test"),
						eq("test_crud2"), eq(true), eq(true), eq(true),
						any(CurrentUser.class), any(Map.class),
						any(List.class), eq(FilterPolicy.ALL), anyInt(),
						anyInt(), anyInt())).thenReturn(tableContainerMock);

		Portlet build = builder.build();

		verify(factoryMock).getDatabaseTableContainer(eq(eventBus), eq("test"),
				eq("test_crud2"), eq(true), eq(true), eq(true),
				any(CurrentUser.class), any(Map.class), any(List.class),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(build.getPage().getElements().get(0), instanceOf(Form.class));

		assertThat(build.getPage().getElements().get(1),
				instanceOf(Table.class));
		assertThat(
				((Table) build.getPage().getElements().get(1)).getContainer(),
				instanceOf(DatabaseTableContainer.class));
	}

	@Test
	public void shouldCreateTextAreaModelFromConfiguration() throws Exception {

		PortletConfig config = createConfiguration("validTextAreaConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(TextArea.class));
	}

	@Test
	public void shouldCreateFormModelFromConfigurationWithDateConfig()
			throws Exception {

		PortletConfig config = createConfiguration("validFormConfigWithScript.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, orderBys, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		verify(factoryMock).getDatabaseTableContainer(eq(eventBus), eq("test"),
				eq("test_crud2"), eq(true), eq(true), eq(true),
				any(CurrentUser.class), any(Map.class), any(List.class),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(build.getPage().getElements().get(0), instanceOf(Form.class));

		assertThat(((Form) build.getPage().getElements().get(0)).getFields()
				.get("CDATE"), instanceOf(DateFormField.class));
	}

	@Test
	public void shouldCreateTabsModelFromConfiguration() throws Exception {

		PortletConfig config = createConfiguration("validTabsConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true,
						new TestUser("Benutzer"), displayPatternMock, orderBys,
						null, 100, 1000, 10)).thenReturn(tableContainerMock);

		Portlet build = builder.build();

		verify(factoryMock, times(2)).getDatabaseTableContainer(eq(eventBus),
				eq("test"), eq("test_crud2"), eq(true), eq(true), eq(true),
				any(CurrentUser.class), any(Map.class), any(List.class),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(build.getPage(), nullValue());
		Tab firstTab = build.getTabs().getElements().get(0);
		Component form = firstTab.getElements().get(0);
		assertThat(form, instanceOf(Form.class));

		assertThat(((Form) form).getFields().get("CDATE"),
				instanceOf(DateFormField.class));
	}

	@Test
	public void shouldCreateDatabaseQueryModelFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validReadonlyQueryConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		mockGetDatabaseQueryContainerCall();

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));
		assertThat(
				((Table) build.getPage().getElements().get(0)).getContainer(),
				instanceOf(DatabaseQueryContainer.class));
	}

	private void mockGetDatabaseQueryContainerCall() {
		when(
				factoryMock.getDatabaseQueryContainer(eq(eventBus), eq("test"),
						Mockito.anyString(), eq(false), eq(false), eq(false),
						eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
						anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
						anyInt(), anyInt(), anyInt(), anyBoolean())).thenReturn(
				queryContainerMock);
	}

	@Test
	public void shouldCreateDatabaseQueryModelFromConfigurationSelectWithQuery()
			throws Exception {
		PortletConfig config = createConfiguration("validSelectConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseQueryContainer(eq(eventBus), eq("test"),
						Mockito.anyString(), eq(true), eq(true), eq(true),
						eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
						anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
						anyInt(), anyInt(), anyInt(), anyBoolean())).thenReturn(
				queryContainerMock);
		when(
				factoryMock.getQueryOptionList(eq(eventBus),
						isA(SelectConfig.class), eq("test"))).thenReturn(
				queryOptionListMock);

		when(
				connectionPoolMock.executeWithJdbcTemplate(Mockito.anyString(),
						Mockito.any(RowMapper.class))).thenReturn(
				Arrays.asList(new String[][] { new String[] { "TEST-KEY",
						"TEST-VALUE" } }));

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));
		assertThat(
				((Table) build.getPage().getElements().get(0)).getContainer(),
				instanceOf(DatabaseQueryContainer.class));

		assertTrue(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("CNUMBER5_2"));
		assertTrue(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("CVARCHAR5_NN"));
		assertTrue(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("CDATE_NN"));
		assertTrue(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("TESTDATA"));
		assertFalse(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("CDATE"));
		assertFalse(((Table) build.getPage().getElements().get(0)).getColumns()
				.isComboBox("CTIMESTAMP"));
	}

	@Test
	public void shouldCreateDatabaseQueryModelFromConfigurationSelectWithOptionList()
			throws Exception {
		PortletConfig config = createConfiguration("validSelectConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseQueryContainer(eq(eventBus), eq("test"),
						Mockito.anyString(), eq(true), eq(true), eq(true),
						eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
						anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
						anyInt(), anyInt(), anyInt(), anyBoolean())).thenReturn(
				queryContainerMock);

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));

		Table table = (Table) build.getPage().getElements().get(0);
		assertThat(table.getContainer(),
				instanceOf(DatabaseQueryContainer.class));
		assertTrue(table.getColumns().isComboBox("CVARCHAR5_NN"));

		SelectionTableColumn column = (SelectionTableColumn) table.getColumns()
				.get("CVARCHAR5_NN");
		assertNotNull(column.getOptionList().getTitle("TEST", null));
		assertNotNull(column.getOptionList().getTitle("PROD", null));
	}

	@Test
	public void shouldCreateDatabaseQueryModelFromConfigurationDynamicSelect()
			throws Exception {

		PortletConfig config = createConfiguration("validDynamicDropDownConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseQueryContainer(eq(eventBus), eq("test"),
						Mockito.anyString(), eq(true), eq(true), eq(true),
						eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
						anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
						anyInt(), anyInt(), anyInt(), anyBoolean())).thenReturn(
				queryContainerMock);

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));
		Table table = (Table) build.getPage().getElements().get(0);
		assertThat(table.getContainer(),
				instanceOf(DatabaseQueryContainer.class));

		assertThat(table.getColumns().get("CNUMBER5_2"),
				instanceOf(SelectionTableColumn.class));
	}

	@Test
	public void shouldFillTableColumnsGeneratedTypeProperty() throws Exception {

		PortletConfig config = createConfiguration("validTableExportOfGeneratedColumnConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		mockGetDatabaseQueryContainerCall();

		Portlet build = builder.build();

		assertThat(build.getPage().getElements().get(0),
				instanceOf(Table.class));
		assertThat(
				((Table) build.getPage().getElements().get(0)).getContainer(),
				instanceOf(DatabaseQueryContainer.class));

		assertThat(((Table) build.getPage().getElements().get(0)).getColumns()
				.get("Generiert4").getGeneratedType().getName(),
				is("java.math.BigDecimal"));
	}

	public static PortletConfig createConfiguration(String configRessource)
			throws Exception {

		InputStream stream = ModelBuilderTest.class.getClassLoader()
				.getResourceAsStream(configRessource);
		return unmarshaller.unmarshal(stream);
	}

	@Test
	public void shouldAddTabsById() throws Exception {

		PortletConfig config = createConfiguration("validNestedTabsConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, orderBys, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		try {
			Portlet build = builder.build();

			Map<String, Tab> tabRefMap = build.getTabsById();
			assertEquals(tabRefMap.keySet().size(), 4);

			String tabName = "tab1";
			assertTrue(tabRefMap.containsKey(tabName));
			Tab tab = tabRefMap.get(tabName);
			assertTrue(Tab.class.isAssignableFrom(tab.getClass()));
			assertEquals(tabName, tab.getId());

			tabName = "tab2";
			assertTrue(tabRefMap.containsKey(tabName));
			tab = tabRefMap.get(tabName);
			assertTrue(Tab.class.isAssignableFrom(tab.getClass()));
			assertEquals(tabName, tab.getId());

			tabName = "tab3";
			assertTrue(tabRefMap.containsKey(tabName));
			tab = tabRefMap.get(tabName);
			assertTrue(Tab.class.isAssignableFrom(tab.getClass()));
			assertEquals(tabName, tab.getId());

			tabName = "tab4";
			assertTrue(tabRefMap.containsKey(tabName));
			tab = tabRefMap.get(tabName);
			assertTrue(Tab.class.isAssignableFrom(tab.getClass()));
			assertEquals(tabName, tab.getId());

		} catch (ValidationException ve) {
			throw new BusinessException(ve.getCode(), ve.getArgs());
		}
	}

	@Test
	public void shouldAddDialogsToDialogByIdMap() throws Exception {

		// Given a valid configuration with dialogs and a ModelBuilder for this
		// config
		PortletConfig config = createConfiguration("validDialogConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		// when the ModelBuilder builds a portlet instance for this config
		Portlet portlet = builder.build();

		// then the portlet instance's dialog map should contain the dialogs
		Map<String, Dialog> dialogMap = portlet.getDialogsById();
		assertThat(dialogMap, notNullValue());
		assertThat(dialogMap.size(), is(1));
		Dialog dialog1 = dialogMap.get("dialog-1");
		assertNotNull(dialog1);

	}

	@Test
	public void shouldAddDialogsToElementsByIdMap() throws Exception {

		// Given a valid configuration with dialogs and a ModelBuilder for this
		// config
		PortletConfig config = createConfiguration("validDialogConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		// when the ModelBuilder builds a portlet instance for this config
		Portlet portlet = builder.build();

		// then the portlet instance's element map should contain the dialogs
		Dialog dialog1 = (Dialog) portlet.getElementById("dialog-1");
		assertNotNull(dialog1);
	}

	@Test
	public void shouldAddTriggers() throws Exception {

		PortletConfig config = createConfiguration("validActionTriggersConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, orderBys, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		try {
			Portlet build = builder.build();

			FormAction formAction = (FormAction) build
					.getElementById("searchAction01");
			Triggers triggers = formAction.getTriggers();

			assertThat(triggers.getTriggers().size(), is(1));
			assertThat(triggers.getTriggers().get(0).getAction(),
					is("searchAction02"));

		} catch (ValidationException ve) {
			throw new BusinessException(ve.getCode(), ve.getArgs());
		}
	}

	@Test
	public void shouldCreateTableActionModelFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validTableActionConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, orderBys, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		verify(factoryMock).getDatabaseTableContainer(eq(eventBus), eq("test"),
				eq("test_crud2"), eq(true), eq(true), eq(true),
				any(CurrentUser.class), any(Map.class), any(List.class),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(((Table) build.getPage().getElements().get(0)).getActions()
				.get(0).getTitle(), is("Button mit Aktion"));
	}

	@Test
	public void shouldCreateAuthenticationRealmFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validReSTSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getAuthenticationRealm(config.getAuthentication()
						.getRealm().get(0))).thenReturn(realmMock);

		Portlet build = builder.build();

		assertThat(build.getAuthenticationRealms().get("testserver"),
				is(realmMock));
	}

	@Test
	public void shouldCreateExcelExportTableModelFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validXLSExportConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, orderBys, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();
		assertThat(((Table) build.getPage().getElements().get(0)).isExport(),
				is(true));
		assertThat(
				((Table) build.getPage().getElements().get(0)).getExportType(),
				is("xls"));
	}

	@Test
	public void shouldCreateMultiselectModelFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validMultiselectFormConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		FormField field = ((Form) build.getPage().getElements().get(0))
				.getFields().get("CVARCHAR5_NN");

		assertThat(field, instanceOf(MultiOptionListFormField.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBuildDeniedTab1FromConfiguration() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		build.getElementById("tab1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBuildDeniedFormActionFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		build.getElementById("formAction");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBuildDeniedTableActionFromConfiguration()
			throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		build.getElementById("tableAction");
	}

	@Test
	public void shouldSetTableReadonlyByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();

		Table table1 = (Table) build.getElementById("table1");
		assertThat(table1.isEditable(), is(false));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBuildTableByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();
		build.getElementById("table2");
		fail();
	}

	@Test
	public void shouldBuildTableByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();
		Object elementById = build.getElementById("table3");

		assertThat(elementById, is(instanceOf(Table.class)));
	}

	@Test
	public void shouldNotEditableColumnByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();
		Table table = (Table) build.getElementById("table4");

		TableColumns columns = table.getColumns();

		assertThat(
				columns.get("CNUMBER5_2").isEditable(mock(ContainerRow.class)),
				is(false));
	}

	@Test
	public void shouldNotVisibleColumnByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eventBus, "test",
						"test_crud2", true, true, true, null,
						displayPatternMock, null, null, 100, 1000, 10))
				.thenReturn(tableContainerMock);

		Portlet build = builder.build();
		Table table = (Table) build.getElementById("table5");

		TableColumns columns = table.getColumns();

		assertThat(columns.get("CNUMBER5_2").getHidden(), is(Hidden.TRUE));
	}

	@Test
	public void shouldNotCrudQueryContainerByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		mockGetDatabaseQueryContainerCall();

		builder.build();

		verify(factoryMock).getDatabaseQueryContainer(eq(eventBus), eq("test"),
				Mockito.anyString(), eq(true), eq(true), eq(true),
				eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
				anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
				anyInt(), anyInt(), anyInt(), anyBoolean());
		verify(factoryMock).getDatabaseQueryContainer(eq(eventBus), eq("test"),
				Mockito.anyString(), eq(false), eq(false), eq(false),
				eq(Arrays.asList("ID")), Mockito.anyString(), anyMap(),
				anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
				anyInt(), anyInt(), anyInt(), anyBoolean());
	}

	@Test
	public void shouldNotCrudTableContainerByPermission() throws Exception {

		PortletConfig config = createConfiguration("validSecurityConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eq(eventBus), eq("test"),
						eq("test_crud2"), anyBoolean(), anyBoolean(),
						anyBoolean(), Mockito.any(CurrentUser.class), anyMap(),
						anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
						anyInt(), anyInt(), anyInt())).thenReturn(
				tableContainerMock);

		builder.build();

		verify(factoryMock, times(4)).getDatabaseTableContainer(eq(eventBus),
				eq("test"), Mockito.anyString(), insertBooleanCaptor.capture(),
				updateBooleanCaptor.capture(), deleteBooleanCaptor.capture(),
				Mockito.any(CurrentUser.class), anyMap(),
				anyListOf(ContainerOrder.class), eq(FilterPolicy.ALL),
				anyInt(), anyInt(), anyInt());

		assertThat(insertBooleanCaptor.getAllValues().get(3), is(false));
		assertThat(updateBooleanCaptor.getAllValues().get(3), is(false));
		assertThat(deleteBooleanCaptor.getAllValues().get(3), is(false));
	}

	@Test
	public void shouldBuildCustomComponentInPage() throws Exception {
		PortletConfig config = createConfiguration("validCustomComponentConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		Portlet portlet = builder.build();

		Component component = portlet.getPage().getElements().get(0);

		assertNotNull(component);
		assertThat(component, new InstanceOf(CustomComponent.class));
	}

	@Test
	public void shouldBuildCustomComponentInTab() throws Exception {
		PortletConfig config = createConfiguration("validCustomComponentConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		Portlet portlet = builder.build();

		Tab tab = portlet.getTabsById().get("tab001");

		Component component = tab.getElements().get(0);

		assertNotNull(component);
		assertThat(component, new InstanceOf(CustomComponent.class));
	}

	@Test
	public void shouldAddCustomComponentsById() throws Exception {
		PortletConfig config = createConfiguration("validCustomComponentConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		Portlet portlet = builder.build();

		// one component in the page, one within a tab
		CustomComponent component = (CustomComponent) portlet
				.getElementById("scriptComponent001");
		CustomComponent component2 = (CustomComponent) portlet
				.getElementById("scriptComponent002");

		assertNotNull(component);
		assertNotNull(component2);
	}

	@Test
	public void shouldAddCustomComponentToTabElementList() throws Exception {
		PortletConfig config = createConfiguration("validCustomComponentConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		Portlet portlet = builder.build();

		Component component = portlet.getTabsById().get("tab001").getElements()
				.get(0);

		assertNotNull(component);
		assertThat(component, new InstanceOf(CustomComponent.class));
	}

	@Test
	public void shouldNotBuildEditableConfigWithoutPrimaryKeyColumn()
			throws Exception {
		// Gegegeben eine editierbare Tabelle ohne Primary-Key-Column,
		PortletConfig config = createConfiguration("invalidEditableWithoutPrimaryKey.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		// wenn die Konfiguration gebaut wird
		try {
			builder.build();
			fail("Exception erwartet");
		} catch (BusinessException e) {
			// dann wird eine Fehlermeldung erzeugt
			assertThat(
					e.getCode(),
					is(equalTo("portlet.crud.unsupported.tableconfig.editable.without.primary.key")));
		}
	}

	@Test
	public void shouldNotBuildExportableConfigWithoutPrimaryKeyColumn()
			throws Exception {
		// Gegegeben eine exportierbare Tabelle ohne Primary-Key-Column,
		PortletConfig config = createConfiguration("invalidExportableWithoutPrimaryKey.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		// wenn die Konfiguration gebaut wird
		try {
			builder.build();
			fail("Exception erwartet");
		} catch (BusinessException e) {
			// dann wird eine Fehlermeldung erzeugt
			assertThat(
					e.getCode(),
					is(equalTo("portlet.crud.unsupported.tableconfig.export.without.primary.key")));
		}
	}

	@Test
	public void shouldBuildNonEditableNonExportableConfigWithoutPrimaryKeyColumn()
			throws Exception {
		// Gegegeben eine nicht editierbare und nicht exportierbare Tabelle ohne
		// Primary-Key-Column,
		PortletConfig config = createConfiguration("validNonEditableNonExportableWithoutPrimaryKey.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		// wenn die Konfiguration gebaut wird
		Portlet portlet = builder.build();

		// dann wird keine Fehlermeldung erzeugt
		assertThat(portlet, is(notNullValue()));
		assertThat(portlet.getElementById("table"), is(notNullValue()));
	}

	@Test
	public void shouldBuildImplicitNonEditableConfigWithoutPrimaryKeyColumn()
			throws Exception {
		// Gegegeben eine Tabelle, die nicht implizit nicht editierbar ist,
		// da sie ein Query-Backend ohne Update/Insert/Delete-Teil hat,
		// ohne Primary-Key-Column,
		PortletConfig config = createConfiguration("validImplicitNonEditableWithoutPrimaryKey.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, resourceIds, null, null));

		// wenn die Konfiguration gebaut wird
		Portlet portlet = builder.build();

		// dann wird keine Fehlermeldung erzeugt
		assertThat(portlet, is(notNullValue()));
		assertThat(portlet.getElementById("table"), is(notNullValue()));
	}

	@Test
	public void shouldCreateRegionModelFromConfiguration() throws Exception {
		PortletConfig config = createConfiguration("validRegionConfig.xml");
		ModelBuilder builder = createTestBuilder(new Config(config, null, null, null));

		when(
				factoryMock.getDatabaseTableContainer(eq(eventBus), eq("test"),
						eq("test_crud2"), eq(true), eq(true), eq(true),
						isA(CurrentUser.class), eq(displayPatternMock),
						eq(orderBys), eq(FilterPolicy.ALL), anyInt(), anyInt(),
						anyInt())).thenReturn(tableContainerMock);

		Portlet portlet = builder.build();

		Region region = (Region) portlet.getPage().getElements().get(0);
		assertThat(region, instanceOf(Region.class));

		verify(factoryMock, times(2)).getDatabaseTableContainer(eq(eventBus),
				eq("test"), eq("test_crud2"), eq(true), eq(true), eq(true),
				isA(CurrentUser.class), eq(displayPatternMock), eq(orderBys),
				eq(FilterPolicy.ALL), anyInt(), anyInt(), anyInt());

		assertThat(region.getElements().get(0), instanceOf(Table.class));
		assertThat(((Table) region.getElements().get(0)).getContainer(),
				instanceOf(DatabaseTableContainer.class));

		assertThat(region.getElements().get(1), instanceOf(Region.class));

		Region innerRegion = (Region) region.getElements().get(1);
		assertThat(innerRegion.getElements().get(0), instanceOf(Table.class));
		assertThat(((Table) innerRegion.getElements().get(0)).getContainer(),
				instanceOf(DatabaseTableContainer.class));
	}

}
