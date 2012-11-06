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
package de.unioninvestment.eai.portal.portlet.crud.domain.portal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.RoleLocalService;
import com.liferay.portal.service.UserLocalService;

public class PortalTest {

	private static LiferayTestHelper helper = LiferayTestHelper.get();

	private Portal portal = new Portal();

	@Mock
	private Company companyMock;

	@Mock
	private Role role;

	@Mock
	private User userMock;

	private String type = de.unioninvestment.eai.portal.portlet.crud.domain.model.Role.class
			.getName();

	private String id = "portletId_admin";

	private Long companyId = 1L;

	private Long userId = 2L;

	private String username = "Horst";

	private CompanyLocalService companyLocalServiceMock;

	private UserLocalService userLocalServiceMock;

	private RoleLocalService roleLocalService;

	private ResourceLocalService resourceLocalService;

	private PermissionChecker checkerMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		helper.resetMocks();
		companyLocalServiceMock = helper.getCompanyLocalServiceMock();
		userLocalServiceMock = helper.getUserLocalServiceMock();
		roleLocalService = helper.getRoleLocalService();
		resourceLocalService = helper.getResourceLocalService();
		checkerMock = helper.getPermissionCheckerMock();

		when(companyLocalServiceMock.getCompanyByWebId("liferay.com"))
				.thenReturn(companyMock);
		when(companyMock.getCompanyId()).thenReturn(companyId);

		when(userLocalServiceMock.getUserByScreenName(companyId, username))
				.thenReturn(userMock);
		when(userMock.getUserId()).thenReturn(userId);
	}

	@Test
	public void shouldGetCompanyId() {
		Long companyId = portal.getCompanyId();

		assertThat(companyId, is(new Long(1L)));
	}

	@Test
	public void shouldGetUser() {
		User user = portal.getUser(username);

		assertThat(user, is(userMock));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionByNotExistingCompanyId()
			throws PortalException, SystemException {
		reset(companyLocalServiceMock);
		when(companyLocalServiceMock.getCompanyByWebId("liferay.com"))
				.thenReturn(null);
		portal.getCompanyId();

	}

	@Test
	public void shouldGetAllRoles() throws SystemException {
		List<Role> lifeRayroles = new ArrayList<Role>();
		lifeRayroles.add(role);
		when(role.getName()).thenReturn("admin");
		when(roleLocalService.getRoles(1L)).thenReturn(lifeRayroles);

		Set<String> result = portal.getAllRoles();

		Set<String> roles = new HashSet<String>();
		roles.add("admin");
		assertThat(result, equalTo(roles));
	}

	@Test
	public void shouldGetUserRoles() throws SystemException {
		List<Role> lifeRayroles = new ArrayList<Role>();
		lifeRayroles.add(role);
		when(role.getName()).thenReturn("admin");
		when(roleLocalService.getUserRoles(2L)).thenReturn(lifeRayroles);

		Set<String> result = portal.getRoles("Horst");

		Set<String> roles = new HashSet<String>();
		roles.add("admin");
		assertThat(result, equalTo(roles));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionWhenUserNotExist() throws SystemException,
			PortalException {
		reset(userLocalServiceMock);
		when(userLocalServiceMock.getUserByScreenName(companyId, username))
				.thenReturn(null);

		portal.getRoles("Horst");

	}

	@Test
	public void shouldRegisterResource() throws PortalException,
			SystemException {

		Long groupId = 0L;
		portal.registerResource(type, id, username);

		verify(resourceLocalService).addResources(companyId, groupId, userId,
				type, id, false, false, false);
	}

	@Test
	public void shouldHavePermission() throws PortalException, SystemException {

		when(checkerMock.hasPermission(0L, type, id, "MEMBER"))
				.thenReturn(true);

		boolean hasPermission = portal.hasPermission(username, "MEMBER", type,
				id);

		assertThat(hasPermission, is(true));
	}

	@Test
	public void shouldNotHavePermission() throws PortalException,
			SystemException {

		boolean hasPermission = portal.hasPermission(username, "MEMBER", type,
				id);

		verify(checkerMock).hasPermission(0L, type, id, "MEMBER");
		assertThat(hasPermission, is(false));
	}

	@Test
	public void testRegisterResource() {

	}

}
