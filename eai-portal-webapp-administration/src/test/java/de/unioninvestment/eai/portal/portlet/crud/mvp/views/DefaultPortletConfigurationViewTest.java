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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.test.commons.VaadinViewTest;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class DefaultPortletConfigurationViewTest extends VaadinViewTest {

	DefaultPortletConfigurationView view;

	@Mock
	private Role roleMock1;

	@Mock
	private Role roleMock2;

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
			view = new DefaultPortletConfigurationView();
		return view;
	}

	@Test
	public void shouldShowStatusTextWithArgs() {
		Date date = new Date();
		String user = "TestUser";
		view.setStatus("portlet.crud.page.status.config.available", user, date);
		assertEquals(PortletUtils.getMessage(
				"portlet.crud.page.status.config.available", user, date), view
				.getStatusLable().getValue());
	}

	@Test
	public void shouldShowStatusTextWithoutArgs() {
		view.setStatus("portlet.crud.page.status.config.notAvailable");
		assertEquals(
				PortletUtils
						.getMessage("portlet.crud.page.status.config.notAvailable"),
				view.getStatusLable().getValue());
	}

	@Test
	public void shouldShowWindowNotification() {
		view.showNotification("test.alert", Notification.TYPE_ERROR_MESSAGE);
		verify(windowSpy).showNotification("test.alert",
				Notification.TYPE_ERROR_MESSAGE);
	}

	@Test
	public void shouldDisplayNotSecuredMessage() {
		Set<Role> roles = emptySet();
		view.displaySecurity(roles);

		assertThat(view.getSecurity().getComponentCount(), is(1));
		Label label = (Label) view.getSecurity().getComponent(0);
		assertThat(
				label.getValue(),
				is((Object) "Die aktuelle Konfiguration definiert keine Rollen"));
	}

	@Test
	public void shouldDisplaySecurityHeaderOnExistingRoles() {
		Set<Role> roles = new HashSet<Role>(asList(roleMock1));
		view.displaySecurity(roles);

		Label label = (Label) view.getSecurity().getComponent(0);
		assertThat(label.getValue(), is((Object) "Berechtigungen"));
	}

	@Test
	public void shouldDisplaySecurityLinksForRoles() {
		Set<Role> roles = new LinkedHashSet<Role>(asList(roleMock1, roleMock2));

		view.displaySecurity(roles);

		Link link1 = (Link) view.getSecurity().getComponent(1);
		assertThat(link1.getCaption(), is("role1"));
		assertThat(link1.getResource(), instanceOf(ExternalResource.class));

		ExternalResource res1 = (ExternalResource) link1.getResource();
		assertThat(res1.getURL(), is("http://test.de/role1"));
	}

	@Test
	public void shouldRemoveSecurityFromView() {
		Set<Role> roles = new HashSet<Role>(asList(roleMock1));
		view.displaySecurity(roles);

		view.hideSecurity();
		assertThat(view.getSecurity().getComponentCount(), is(0));
	}
}
