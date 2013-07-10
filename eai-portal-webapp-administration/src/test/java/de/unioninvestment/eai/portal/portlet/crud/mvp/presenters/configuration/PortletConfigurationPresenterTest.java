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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Date;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Upload;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsPasswordConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsUsernameConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration.PortletConfigurationPresenter.ConfigUploadFinishedListener;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration.PortletConfigurationPresenter.ConfigurationReceiver;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletConfigurationView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletRolesView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PreferencesView;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.crud.validation.ConfigurationUploadValidator;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class PortletConfigurationPresenterTest extends SpringPortletContextTest {

	private PortletConfigurationPresenter portletConfigurationPresenter;

	@Mock
	private PortletConfigurationView viewMock;

	@Mock
	private ConfigurationService configurationServiceMock;

	@Mock
	private EventBus eventBusMock;

	@Mock
	private Upload uploadMock = new Upload();

	@Mock
	private Button uploadVcsButton = new Button();

	@Mock
	private ResourceResponse responseMock;

	@Mock
	private ResourceRequest requestMock;

	@Mock
	private Settings settingsMock;

	private static final String testWinId = "2";

	@Rule
	public LiferayContext liferayContext = new LiferayContext(testWinId, 18004L);

	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);
		liferayContext.initialize();

		when(liferayContext.getVaadinPortletRequestMock().getRemoteUser())
				.thenReturn("horst");

		when(viewMock.getUpload()).thenReturn(uploadMock);
		when(viewMock.getUploadVcsButton()).thenReturn(uploadVcsButton);
	}

	@Test
	public void shouldInitConfigurationViewWithoutConfigData() {

		when(
				configurationServiceMock.getConfigurationMetaData(testWinId,
						18004)).thenReturn(null);

		portletConfigurationPresenter = createPortletConfigurationPresenter();

		verify(viewMock).setStatus(
				"portlet.crud.page.status.config.notAvailable");
	}

	@Test
	public void shouldInitConfigurationViewWithConfigData() {

		ConfigurationMetaData metaDataMock = mock(ConfigurationMetaData.class);
		Date testDate = new Date();
		String testUser = "testUser";
		when(metaDataMock.getCreated()).thenReturn(testDate);
		when(metaDataMock.getUser()).thenReturn(testUser);

		when(
				configurationServiceMock.getConfigurationMetaData(testWinId,
						18004L)).thenReturn(metaDataMock);

		portletConfigurationPresenter = createPortletConfigurationPresenter();

		verify(viewMock).setStatus("portlet.crud.page.status.config.available",
				testUser, testDate, null);
	}

	private PortletConfigurationPresenter createPortletConfigurationPresenter() {
		return new PortletConfigurationPresenter(viewMock,
				configurationServiceMock, eventBusMock, settingsMock);
	}

	@Test
	public void shouldUpdateValidConfigXML() {
		portletConfigurationPresenter = createPortletConfigurationPresenter();

		ConfigUploadFinishedListener configUploadListener = portletConfigurationPresenter.new ConfigUploadFinishedListener();
		ConfigurationUploadValidator validator = mock(ConfigurationUploadValidator.class);
		ConfigurationReceiver receiver = mock(ConfigurationReceiver.class);

		configUploadListener.setValidator(validator);
		portletConfigurationPresenter.setReceiver(receiver);

		when(validator.isValid(anyObject())).thenReturn(true);
		when(receiver.getConfigurationXML()).thenReturn(new byte[] { 0 });
		when(receiver.getFilename()).thenReturn("fileName");

		configUploadListener.uploadFinished(new Upload.FinishedEvent(
				uploadMock, "", "", System.currentTimeMillis()));

		verify(configurationServiceMock).storeConfigurationFile(eq("fileName"),
				any(byte[].class), eq(testWinId), eq(18004L), eq("horst"));
		verify(eventBusMock).fireEvent(any(ConfigurationUpdatedEvent.class));
		verify(uploadMock).setVisible(true);
	}

	@Test
	public void shouldUpdateInvalidConfigXML() {
		portletConfigurationPresenter = createPortletConfigurationPresenter();

		ConfigUploadFinishedListener configUploadListener = portletConfigurationPresenter.new ConfigUploadFinishedListener();
		ConfigurationUploadValidator validator = mock(ConfigurationUploadValidator.class);
		ConfigurationReceiver receiver = mock(ConfigurationReceiver.class);

		configUploadListener.setValidator(validator);
		portletConfigurationPresenter.setReceiver(receiver);

		when(validator.isValid(anyObject())).thenReturn(false);
		when(receiver.getConfigurationXML()).thenReturn(new byte[] { 0 });

		configUploadListener.uploadFinished(new Upload.FinishedEvent(
				uploadMock, "", "", System.currentTimeMillis()));

		verify(viewMock).showError(
				"Die Konfiguration entspricht nicht dem gültigen XSD!");
	}

	@Test
	public void shouldResetBufferBeforeUpload() {
		portletConfigurationPresenter = createPortletConfigurationPresenter();

		ConfigurationReceiver receiver = portletConfigurationPresenter.new ConfigurationReceiver();
		receiver.getBaos().write(369);

		receiver.receiveUpload("TestFile", "xml");

		assertArrayEquals(new byte[] {}, receiver.getConfigurationXML());
		assertEquals("TestFile", receiver.getFilename());
		assertEquals("xml", receiver.getMimetype());
	}

	@Test
	@Ignore
	public void shouldUpdateViewWithNewSecurityRolesOnRefresh() {
		portletConfigurationPresenter = createPortletConfigurationPresenter();

		PortletConfig portletConfig = createConfigWithRoles();

		Config config = new Config(portletConfig, singletonMap(
				PortletRole.createRoleResourceId(testWinId, 18004L,
						"portletRole"), 1L));
		portletConfigurationPresenter.refresh(config);

		verify(viewMock).displayTab(isA(PortletRolesView.class));
	}

	@Test
	public void shouldUpdateViewWithNewAuthenticationSheetOnRefresh() {
		portletConfigurationPresenter = createPortletConfigurationPresenter();

		PortletConfig portletConfig = createConfigWithPreferences();

		Config config = new Config(portletConfig, singletonMap("a", 1L));
		portletConfigurationPresenter.refresh(config);

		verify(viewMock).displayTab(isA(PreferencesView.class));
	}

	private PortletConfig createConfigWithRoles() {
		RoleConfig portletRoleConfig = new RoleConfig();
		portletRoleConfig.setName("portletRole");

		RoleConfig portalRoleConfig = new RoleConfig();
		portalRoleConfig.setName("portalRole");
		portalRoleConfig.setPortalRole("liferay-role");

		RolesConfig rolesConfig = new RolesConfig();
		rolesConfig.getRole().add(portletRoleConfig);
		rolesConfig.getRole().add(portalRoleConfig);

		PortletConfig portletConfig = new PortletConfig();
		portletConfig.setAuthentication(new AuthenticationConfig());
		portletConfig.setRoles(rolesConfig);
		return portletConfig;
	}

	private PortletConfig createConfigWithPreferences() {
		CredentialsUsernameConfig usernameConfig = new CredentialsUsernameConfig();
		usernameConfig.setPreferenceKey("test");

		CredentialsConfig credentialsConfig = new CredentialsConfig();
		credentialsConfig.setUsername(usernameConfig);
		credentialsConfig.setPassword(new CredentialsPasswordConfig());

		AuthenticationRealmConfig realmConfig = new AuthenticationRealmConfig();
		realmConfig.setCredentials(credentialsConfig);

		PortletConfig portletConfig = new PortletConfig();
		portletConfig.setAuthentication(new AuthenticationConfig());
		portletConfig.getAuthentication().getRealm().add(realmConfig);
		return portletConfig;
	}

}
