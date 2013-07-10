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
package de.unioninvestment.eai.portal.portlet.crud.domain.model.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;

public class CurrentUserTest {

	@Mock
	private PortletRequest request;

	@Mock
	private Principal userPrincipal;

	private Set<Role> portletRoles = new HashSet<Role>();

	@Mock
	private PortletRole roleMock;

	@Mock
	private LiferayUI liferayUIMock;

	@Mock
	private VaadinPortletRequest portletRequestMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(request.getUserPrincipal()).thenReturn(userPrincipal);
		when(userPrincipal.getName()).thenReturn("Jürgen");

		UI.setCurrent(liferayUIMock);
		CurrentInstance.set(VaadinRequest.class, portletRequestMock);
		portletRoles.add(roleMock);
	}

	@After
	public void tearDown() {
		CurrentInstance.clearAll();
	}

	@Test
	public void shouldReturnCurrentUserFromPortletRequest() {
		when(portletRequestMock.getRemoteUser()).thenReturn("Jürgen");
		assertThat(new CurrentUser(portletRoles).getName(), is("Jürgen"));
	}

	@Test
	public void shouldReturnThatUserIsNotAuthenticated() {
		assertThat(new CurrentUser(portletRoles).isAuthenticated(), is(false));
	}

	@Test
	public void shouldReturnThatUserIsAuthenticated() {
		when(portletRequestMock.getRemoteUser()).thenReturn("Jürgen");
		assertThat(new CurrentUser(portletRoles).isAuthenticated(), is(true));
	}

	@Test
	public void shouldReturnAnonymousUserOnMissingRequestForTestingPurposes() {
		tearDown();
		assertThat(new CurrentUser(portletRoles).isAuthenticated(), is(false));
	}

	@Test
	public void shouldGetPortalRoles() {
		assertThat(new CurrentUser(portletRoles).getPortalRoles(),
				equalTo(Collections.EMPTY_SET));
	}

	@Test
	public void shouldGetRoles() {
		assertThat(new CurrentUser(portletRoles).getRoles(),
				equalTo(AnonymousUser.ANONYMOUS_ROLES));
	}
}
