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
package de.unioninvestment.eai.portal.portlet.crud.export;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.lar.PortletDataException;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;

public class CrudPortletDataHandlerTest {

	private static final long COMMUNITY_ID = 18004L;

	@Mock
	private ConfigurationService configurationServiceMock;
	@Mock
	private PortletDataContext contextMock;
	@Mock
	private PortletPreferences preferencesMock;

	@InjectMocks
	private CrudPortletDataHandler crudPortletDataHandler = new CrudPortletDataHandler();

	@Mock
	private Config configMock;
	@Mock
	private PortletConfig portletConfigMock;
	@Mock
	private RolesConfig rolesConfigMock;
	@Mock
	private RoleConfig roleConfigMock;
	@Mock
	private ConfigurationMetaData configurationMetaDataMock;
	@Mock
	private ZipWriter zipWriterMock;
	@Mock
	private ZipReader zipReaderMock;

	private byte[] data = "4711".getBytes();

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		when(preferencesMock.getValue("VERSION", "1")).thenReturn("2");
		when(
				configurationServiceMock.hasConfigData(anyString(),
						eq(COMMUNITY_ID))).thenReturn(true);
		when(configurationServiceMock.getPortletConfig("4711", 18004))
				.thenReturn(configMock);
		when(configMock.getPortletConfig()).thenReturn(portletConfigMock);
		when(
				configurationServiceMock.readRoleResourceIdPrimKey("4711",
						COMMUNITY_ID, "admin")).thenReturn(1L);
		when(
				configurationServiceMock.getConfigurationMetaData("4711",
						COMMUNITY_ID)).thenReturn(configurationMetaDataMock);
		when(configurationServiceMock.readConfigAsString("4711", COMMUNITY_ID))
				.thenReturn("<Konfiguration>");

		when(contextMock.getScopeGroupId()).thenReturn(COMMUNITY_ID);
		when(contextMock.getZipWriter()).thenReturn(zipWriterMock);
		when(configurationMetaDataMock.getFileName()).thenReturn(
				"validCinfig.xml");

		when(contextMock.getZipReader()).thenReturn(zipReaderMock);
		when(zipReaderMock.getEntryAsByteArray(anyString())).thenReturn(data);
	}

	private void addRolesToTestConfig() {
		when(roleConfigMock.getName()).thenReturn("admin");

		List<RoleConfig> role = new ArrayList<RoleConfig>();
		role.add(roleConfigMock);

		when(portletConfigMock.getRoles()).thenReturn(rolesConfigMock);
		when(rolesConfigMock.getRole()).thenReturn(role);
	}

	@Test
	public void shouldNotExportAny() throws Exception {
		when(
				configurationServiceMock.hasConfigData(anyString(),
						eq(COMMUNITY_ID))).thenReturn(false);
		assertThat(crudPortletDataHandler.doExportData(contextMock, "4711",
				preferencesMock), is("Keine Konfiguration vorhanden"));

	}

	@Test
	public void shouldExportConfig() throws Exception {
		addRolesToTestConfig();

		String data = crudPortletDataHandler.doExportData(contextMock, "4711",
				preferencesMock);

		assertThat(data, notNullValue());

		verify(preferencesMock).setValue("ROLE_1", "admin");
		verify(contextMock).addPermissions(Role.class.getName(), 1);
		verify(preferencesMock).store();
		verify(zipWriterMock).addEntry("4711/portletConfiguration.xml",
				"<Konfiguration>");
	}

	@Test
	public void shouldNotExportPermissions() throws Exception {
		crudPortletDataHandler.doExportData(contextMock, "4711",
				preferencesMock);

		verify(contextMock, never()).addPermissions(Role.class.getName(), 1);
		verify(zipWriterMock).addEntry("4711/portletConfiguration.xml",
				"<Konfiguration>");
	}

	@Test
	public void shouldExportRoleResourceNames() throws Exception {
		addRolesToTestConfig();

		crudPortletDataHandler.doExportData(contextMock, "4711",
				preferencesMock);

		verify(preferencesMock)
				.setValues("roleNames", new String[] { "admin" });
	}

	@Test
	public void shouldImportConfig() throws Exception {
		when(preferencesMock.getValues("roleNames", new String[0])).thenReturn(
				new String[0]);

		crudPortletDataHandler.doImportData(contextMock, "4711",
				preferencesMock, "validConfig.xml");

		verify(configurationServiceMock).storeConfigurationFile(
				"validConfig.xml", data, "4711", COMMUNITY_ID, "import");
	}

	@Test
	public void shouldImportResourceRoles() throws Exception {
		when(preferencesMock.getValues("roleNames", new String[0])).thenReturn(
				new String[] { "admin" });
		crudPortletDataHandler.doImportData(contextMock, "4711",
				preferencesMock, "validConfig.xml");

		verify(configurationServiceMock).storeRoleResourceId("4711",
				COMMUNITY_ID, "admin");
	}

	@Test
	public void shouldImportConfigWithPermissions() throws Exception {
		when(preferencesMock.getValues("roleNames", new String[0])).thenReturn(
				new String[] { "admin" });

		Map<String, List<KeyValuePair>> perm = new HashMap<String, List<KeyValuePair>>();
		perm.put(Role.class.getName() + "#3",
				Arrays.asList(new KeyValuePair[] { new KeyValuePair("DUMMY",
						"MEMBER") }));
		when(contextMock.getPermissions()).thenReturn(perm);
		when(preferencesMock.getValue("ROLE_3", null)).thenReturn("admin");
		when(
				configurationServiceMock.storeRoleResourceId("4711",
						COMMUNITY_ID, "admin")).thenReturn(1L);

		crudPortletDataHandler.doImportData(contextMock, "4711",
				preferencesMock, "validConfig.xml");

		verify(configurationServiceMock).storeRoleResourceId("4711",
				COMMUNITY_ID, "admin");
		verify(contextMock).importPermissions(Role.class.getName(), 3L, 1L);
	}

	@Test(expected = PortletDataException.class)
	public void shouldRejectIncompatibleExportFormat()
			throws PortletDataException {
		when(preferencesMock.getValue("VERSION", "1")).thenReturn("1");
		crudPortletDataHandler.doImportData(contextMock, "4711",
				preferencesMock, "validConfig.xml");

	}
}
