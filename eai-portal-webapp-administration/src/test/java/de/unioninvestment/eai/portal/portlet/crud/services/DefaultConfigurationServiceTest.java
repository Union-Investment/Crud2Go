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
package de.unioninvestment.eai.portal.portlet.crud.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Date;

import javax.portlet.PortletPreferences;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler;

public class DefaultConfigurationServiceTest {

	private static final class FileStreamingConfigurationDao extends
			ConfigurationDao {

		private final InputStream configXmlStream;

		private FileStreamingConfigurationDao(JdbcTemplate jdbcTemplate,
				InputStream configXmlStream) {
			super(jdbcTemplate);
			this.configXmlStream = configXmlStream;
		}

		@Override
		public <T> T readConfigStream(String portletId, long communityId,
				StreamProcessor<T> processor) {
			return processor.process(configXmlStream,
					new ConfigurationMetaData("test", new Date(), new Date(),
							"test.xml"));
		}
	}

	private static final class ExceptionThrowingConfigurationDao extends
			ConfigurationDao {

		private ExceptionThrowingConfigurationDao() {
			super(null);
		}

		@Override
		public <T> T readConfigStream(String windowId, long communityId,
				StreamProcessor<T> processor) {
			throw new EmptyResultDataAccessException(1);
		}
	}

	@Mock
	private ConfigurationDao daoMock;

	private DefaultConfigurationService service;

	@Mock
	private ConfigurationScriptsCompiler compilerMock;

	@Mock
	private PortletPreferences preferencesMock;

	@Mock
	private Settings settingsMock;

	@Mock
	private ConfigurationMetaData metaDataMock;

	private static PortletConfigurationUnmarshaller unmarshaller = new PortletConfigurationUnmarshaller();

	@Before
	public void setUp() throws JAXBException, SAXException {
		MockitoAnnotations.initMocks(this);
		service = new DefaultConfigurationService(daoMock, compilerMock,
				settingsMock);
	}

	@Test
	public void shouldReturnNullIfConfigNotFound() throws JAXBException,
			SAXException {

		service = new DefaultConfigurationService(
				new ExceptionThrowingConfigurationDao(), compilerMock,
				settingsMock);

		Config config = service.getPortletConfig("myWindowId", 17002L);

		assertNull(config);
	}

	@Test
	public void shouldReturnNullWithInvalidConfiguration()
			throws JAXBException, SAXException {
		createServiceForConfig("invalidConfig.xml.txt");

		Config config = service.getPortletConfig("myWindowId", 17002L);

		assertNull(config);
	}

	@Test
	public void shouldReturnDeserializedConfigurationByWindowId()
			throws JAXBException, SAXException {
		createServiceForConfig("validConfig.xml");

		PortletConfig config = service.getPortletConfig("myWindowId", 17002L)
				.getPortletConfig();

		assertNotNull(config);
	}

	@Test
	public void shouldStoreConfigurationFile() {
		service.storeConfigurationFile("bla", "xml".getBytes(), "myWindowId",
				17002L, "cmj");
		verify(daoMock).saveOrUpdateConfigData("myWindowId", 17002L, "bla",
				"xml".getBytes(), "cmj");
	}

	@Test
	public void shouldReturnMetaDataFromDao() {
		ConfigurationMetaData data = new ConfigurationMetaData("user",
				new Date(), null, null);
		when(daoMock.readConfigMetaData("myWindowId", 17002L)).thenReturn(data);

		assertEquals(data,
				service.getConfigurationMetaData("myWindowId", 17002L));
	}

	@Test
	public void shouldReturnNullWhenMetaDataIsMissing() {
		when(daoMock.readConfigMetaData("myWindowId", 17002L)).thenThrow(
				new EmptyResultDataAccessException(1));

		assertNull(service.getConfigurationMetaData("myWindowId", 17002L));
	}

	@Test
	public void shouldStateConfiguredIfAuthenticationConfigIsMissing()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validConfig.xml");
		PortletConfig portletConfig = unmarshaller.unmarshal(configStream);
		Config config = new Config(portletConfig, null, "test.xml", null);

		boolean configured = service.isConfigured(config, preferencesMock);

		assertThat(configured, is(true));
	}

	@Test
	public void shouldApplyRevisionSettingsIfPortalRoleIsConfigured()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validConfig.xml");
		when(settingsMock.getRevisionPortalRole()).thenReturn("revisionRole");

		Config config = service.buildPortletConfig(configStream, "PortletId",
				17808L, metaDataMock);

		assertThat(config.getPortletConfig().getRoles().getRole().get(0)
				.getName(), is("revision"));
	}

	@Test
	public void shouldCreateNewResourceIDsForPortletRoles()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validRolesConfig.xml");

		when(daoMock.readRoleResourceIdPrimKey("PortletId_17808_admin5"))
				.thenReturn(null);
		when(daoMock.readRoleResourceIdPrimKey("PortletId_17808_user5"))
				.thenReturn(null);
		when(daoMock.readNextRoleResourceIdPrimKey()).thenReturn(1L, 2L);

		Config config = service.buildPortletConfig(configStream, "PortletId",
				17808L, metaDataMock);

		assertThat(config.getRoleResourceIDs().size(), is(2));
		assertThat(config.getRoleResourceIDs().get("PortletId_17808_admin5"),
				is(1L));
		assertThat(config.getRoleResourceIDs().get("PortletId_17808_user5"),
				is(2L));
	}

	@Test
	public void shouldRetrieveExistingResourceIDsForPortletRoles()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validRolesConfig.xml");

		when(daoMock.readRoleResourceIdPrimKey("PortletId_17808_admin5"))
				.thenReturn(3L);
		when(daoMock.readRoleResourceIdPrimKey("PortletId_17808_user5"))
				.thenReturn(4L);

		Config config = service.buildPortletConfig(configStream, "PortletId",
				17808L, metaDataMock);

		assertThat(config.getRoleResourceIDs().size(), is(2));
		assertThat(config.getRoleResourceIDs().get("PortletId_17808_admin5"),
				is(3L));
		assertThat(config.getRoleResourceIDs().get("PortletId_17808_user5"),
				is(4L));
	}

	@Test
	public void shouldStateUnconfiguredIfAnyUsernameIsMissing()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");
		PortletConfig portletConfig = unmarshaller.unmarshal(configStream);
		Config config = new Config(portletConfig, null, "test.xml", null);
		when(preferencesMock.getValue("testserver.password", null)).thenReturn(
				"pwd");

		boolean configured = service.isConfigured(config, preferencesMock);

		assertThat(configured, is(false));
	}

	@Test
	public void shouldStateUnconfiguredIfAnyPasswordIsMissing()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");
		PortletConfig portletConfig = unmarshaller.unmarshal(configStream);
		Config config = new Config(portletConfig, null, "test.xml", null);
		when(preferencesMock.getValue("testserver.username", null)).thenReturn(
				"user");

		boolean configured = service.isConfigured(config, preferencesMock);

		assertThat(configured, is(false));
	}

	@Test
	public void shouldStateConfiguredIfAllPreferencesHaveText()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");

		PortletConfig portletConfig = unmarshaller.unmarshal(configStream);
		Config config = new Config(portletConfig, null, "test.xml", null);
		when(preferencesMock.getValue("testserver.username", null)).thenReturn(
				"user");
		when(preferencesMock.getValue("testserver.password", null)).thenReturn(
				"pwd");

		boolean configured = service.isConfigured(config, preferencesMock);

		assertThat(configured, is(true));
	}

	private void createServiceForConfig(String configXml) throws JAXBException,
			SAXException {
		final InputStream configXmlStream = getClass().getClassLoader()
				.getResourceAsStream(configXml);

		ConfigurationDao dao = new FileStreamingConfigurationDao(null,
				configXmlStream);
		service = new DefaultConfigurationService(dao, compilerMock,
				settingsMock);
	}

}
