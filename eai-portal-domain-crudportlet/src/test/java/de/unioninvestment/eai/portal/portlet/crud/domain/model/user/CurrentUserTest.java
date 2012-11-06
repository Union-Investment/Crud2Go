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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplication;

public class CurrentUserTest {
	private static class MyPortletApplication extends LiferayApplication {
		private static final long serialVersionUID = 1L;

		@Override
		public void doInit() {
			// ...
		}

		@Override
		public void addToView(Component component) {
			// ...
		}

		@Override
		public void removeAddedComponentsFromView() {
			// ...
		}
	}

	@Mock
	private PortletRequest request;

	@Mock
	private Principal userPrincipal;

	private Set<Role> roles = new HashSet<Role>();

	@Mock
	private Role roleMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(request.getUserPrincipal()).thenReturn(userPrincipal);
		when(userPrincipal.getName()).thenReturn("Jürgen");

		new MyPortletApplication().onRequestStart(request, null);

		roles.add(roleMock);
	}

	@Test
	public void shouldReturnCurrentUserFromPortletRequest() {
		assertThat(new CurrentUser(roles).getName(), is("Jürgen"));
	}

	@Test
	public void shouldReturnThatUserIsNotAuthenticated() {
		when(request.getUserPrincipal()).thenReturn(null);

		assertThat(new CurrentUser(roles).isAuthenticated(), is(false));
	}

	@Test
	public void shouldReturnThatUserIsAuthenticated() {
		assertThat(new CurrentUser(roles).isAuthenticated(), is(true));
	}

	@Test
	public void shouldReturnAnonymousUserOnMissingRequestForTestingPurposes() {
		new MyPortletApplication().onRequestEnd(request, null);
		assertThat(new CurrentUser(roles).isAuthenticated(), is(false));
	}

	@Test
	public void shouldGetPortalRoles() {
		new MyPortletApplication().onRequestStart(null, null);

		assertThat(new CurrentUser(roles).getPortalRoles(),
				equalTo(Collections.EMPTY_SET));
	}

	@Test
	public void shouldGetRoles() {
		new MyPortletApplication().onRequestStart(null, null);

		assertThat(new CurrentUser(roles).getRoles(),
				equalTo(AnonymousUser.ANONYMOUS_ROLES));
	}
}
