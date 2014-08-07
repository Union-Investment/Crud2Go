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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.RoleService;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.UserService;
import com.liferay.portal.service.UserServiceUtil;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;
import de.unioninvestment.eai.portal.support.vaadin.junit.ContextMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class NamedUserTest {
	@Mock
	private UserService userService;

	@Mock
	private RoleService roleService;

	@Mock
	private com.liferay.portal.model.User userMock;

	@Mock
	private com.liferay.portal.model.Role roleMock;

	@Mock
	private Portal portalMock;

	private NamedUser user;

	@Mock
	private PortletRole domainRoleMock;

    @Rule
    public ContextMock context = new ContextMock();

	@Before
	public void setUp() throws PortalException, SystemException {
		Set<Role> portalRoles = new HashSet<Role>();
		portalRoles.add(domainRoleMock);

		MockitoAnnotations.initMocks(this);
        when(context.getProvider().getBean(Portal.class)).thenReturn(portalMock);

        user = new NamedUser("Jürgen", portalRoles);

		new UserServiceUtil().setService(userService);
		new RoleServiceUtil().setService(roleService);

		when(userService.getUserByScreenName(1, "Jürgen")).thenReturn(userMock);
		when(userMock.getUserId()).thenReturn(25l);
		when(roleService.getUserRoles(25l)).thenReturn(asList(roleMock));
		when(roleMock.getName()).thenReturn("admin");
		Set<String> roles = new HashSet<String>();
		roles.add("admin");
		when(portalMock.getAllRoles()).thenReturn(roles);
	}

	@After
	public void cleanUp() {
		new UserServiceUtil().setService(null);
		new RoleServiceUtil().setService(null);
	}

	@Test
	public void shouldReturnCurrentLiferayRoles() throws PortalException,
			SystemException {

		Set<String> roles = singleton("admin");

		when(portalMock.getRoles("Jürgen")).thenReturn(roles);

		Set<String> roleNames = user.getPortalRoles();
		assertThat(roleNames, is(roles));
	}

	@Test
	public void shouldGetRoles() {
		Set<Role> prtalRoles = new HashSet<Role>();
		prtalRoles.add(domainRoleMock);
		user = new NamedUser("Jürgen", prtalRoles);

		when(domainRoleMock.getName()).thenReturn("adminRolle");
		when(domainRoleMock.isMember(user)).thenReturn(true);

		Set<String> result = user.getRoles();

		Set<String> roles = new HashSet<String>();
		roles.add("adminRolle");
		roles.add(Role.AUTHENTICATED);
		roles.add(Role.ALL);
		assertThat(result, equalTo(roles));
	}
}
