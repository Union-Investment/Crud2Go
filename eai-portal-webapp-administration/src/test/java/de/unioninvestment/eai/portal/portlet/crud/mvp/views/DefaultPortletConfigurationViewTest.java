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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.xml.sax.SAXException;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.crud2go.spi.security.CryptorFactory;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.ui.security.SecurePasswordField;
import de.unioninvestment.eai.portal.portlet.test.commons.VaadinViewTest;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class DefaultPortletConfigurationViewTest extends VaadinViewTest {

	DefaultPortletConfigurationView view;

	@Mock
	private Role roleMock1;

	@Mock
	private Role roleMock2;

	@Mock
	private CryptorFactory cryptorFactoryMock;

	@Before
	public void prepareMocks() {
		when(roleMock1.getName()).thenReturn("role1");
		when(roleMock1.getPermissionsURL()).thenReturn("http://test.de/role1");

		when(roleMock2.getName()).thenReturn("role2");
		when(roleMock2.getPermissionsURL()).thenReturn("http://test.de/role2");
	}

	@Override
	protected View getView() {
		if (view == null)
			view = new DefaultPortletConfigurationView(cryptorFactoryMock);
		return view;
	}

	@Test
	public void shouldShowStatusTextWithArgs() {
		Date date = new Date();
		String user = "TestUser";
		view.setStatus("portlet.crud.page.status.config.available", user, date);
		assertEquals(PortletUtils.getMessage(
				"portlet.crud.page.status.config.available", user, date), view
				.getStatusLabel().getValue());
	}

	@Test
	public void shouldShowStatusTextWithoutArgs() {
		view.setStatus("portlet.crud.page.status.config.notAvailable");
		assertEquals(
				PortletUtils
						.getMessage("portlet.crud.page.status.config.notAvailable"),
				view.getStatusLabel().getValue());
	}

	@Test
	public void shouldShowWindowNotification() {
		view.showNotification("test.alert", Notification.TYPE_ERROR_MESSAGE);
		verify(windowSpy).showNotification("test.alert",
				Notification.TYPE_ERROR_MESSAGE);
	}

	@Test
	public void shouldHideRolesTabIfNoRolesArePresent() {
		Set<Role> roles = emptySet();
		view.displayRoles(roles);

		assertThat(view.rolesLayout, nullValue());
	}

	@Test
	public void shouldDisplayRolesTabOnExistingRoles() {
		Set<Role> roles = new HashSet<Role>(asList(roleMock1));

		view.displayRoles(roles);

		assertTrue(tabsheetContains("Berechtigungen"));
	}

	@Test
	public void shouldDisplaySecurityLinksForRoles() {
		Set<Role> roles = new LinkedHashSet<Role>(asList(roleMock1, roleMock2));

		view.displayRoles(roles);

		Link link1 = (Link) view.rolesLayout.getComponent(1);
		assertThat(link1.getCaption(), is("role1"));
		assertThat(link1.getResource(), instanceOf(ExternalResource.class));

		ExternalResource res1 = (ExternalResource) link1.getResource();
		assertThat(res1.getURL(), is("http://test.de/role1"));
	}

	@Test
	public void shouldRemoveSecurityFromView() {
		Set<Role> roles = new HashSet<Role>(asList(roleMock1));
		view.displayRoles(roles);

		view.hideRoles();

		assertThat(view.rolesLayout, nullValue());
		assertFalse(tabsheetContains("Berechtigungen"));
	}

	@Test
	public void shouldHideAuthenticationIfNothingToConfigure()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validConfig.xml");
		PortletConfig portletConfig = new PortletConfigurationUnmarshaller()
				.unmarshal(configStream);

		view.displayAuthenticationPreferences(new Config(portletConfig, null));

		assertFalse(tabsheetContains("Authentifizierung"));
	}

	@Test
	public void shouldDisplayAuthentication()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");
		PortletConfig portletConfig = new PortletConfigurationUnmarshaller()
				.unmarshal(configStream);

		view.displayAuthenticationPreferences(new Config(portletConfig, null));

		assertTrue(tabsheetContains("Authentifizierung"));
	}

	@Test
	public void shouldDisplayAuthenticationPreferences()
			throws JAXBException, SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");
		PortletConfig portletConfig = new PortletConfigurationUnmarshaller()
				.unmarshal(configStream);

		view.displayAuthenticationPreferences(new Config(portletConfig, null));

		Form form = (Form) view.authenticationPreferencesLayout.getComponent(1);
		assertThat(form.getField("testserver.username"),
				instanceOf(TextField.class));
		assertThat(form.getField("testserver.password"),
				instanceOf(SecurePasswordField.class));
	}

	@Test
	public void shouldRemoveAuthenticationFromView() throws JAXBException,
			SAXException {
		InputStream configStream = getClass().getClassLoader()
				.getResourceAsStream("validReSTSecurityConfig.xml");
		PortletConfig portletConfig = new PortletConfigurationUnmarshaller()
				.unmarshal(configStream);
		view.displayAuthenticationPreferences(new Config(portletConfig, null));

		view.hideAuthenticationPreferences();

		assertThat(view.authenticationPreferencesLayout, nullValue());
		assertFalse(tabsheetContains("Authentifizierung"));
	}

	private boolean tabsheetContains(String caption) {
		return tabsheetComponent(caption) != null;
	}

	private Component tabsheetComponent(String caption) {
		Iterator<Component> iterator = view.tabsheet.getComponentIterator();
		while (iterator.hasNext()) {
			Component component = iterator.next();
			if (caption.equals(component.getCaption())) {
				return component;
			}
		}
		return null;
	}

}
