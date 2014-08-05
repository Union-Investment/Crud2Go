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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.unioninvestment.eai.portal.support.vaadin.junit.ContextMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;

public class PortletRoleTest {

    @Rule
    public ContextMock contextMock = new ContextMock();

	@Mock
	private Portal portalMock;

	private PortletRole portletRole;

	@Mock
	private User userMock;

	@Mock
	private JdbcTemplate jdbcTemplateMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        portletRole = new PortletRole("admin", 1);
        portletRole.portal = portalMock;
	}

	@Test
	public void shouldRegisterRoleAsLiferayResources() {

		portletRole.registerAsLiferayResource("Horst");
		verify(portalMock).registerResource(PortletRole.RESOURCE_KEY, "1",
				"Horst");
	}

	@Test
	public void shouldCheckRoleIfUserIsMember() {
		when(userMock.getName()).thenReturn("horst");
		when(
				portalMock.hasPermission("horst", "MEMBER",
						PortletRole.RESOURCE_KEY, "1")).thenReturn(true);
		boolean result = portletRole.isMember(userMock);
		assertThat(result, is(true));
	}

	@Test
	public void shouldCheckRoleIfUserIsNotMember() {
		when(userMock.getName()).thenReturn("peter");
		when(
				portalMock.hasPermission("peter", "MEMBER",
						PortletRole.RESOURCE_KEY, "PortletId_admin"))
				.thenReturn(false);
		boolean result = portletRole.isMember(userMock);
		assertThat(result, is(false));
	}

	@Test
	public void shouldGetRolename() {
		String name = portletRole.getName();
		assertThat(name, is("admin"));
	}

}
