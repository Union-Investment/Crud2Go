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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model.portal;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;

public class ScriptPortalTest {

	@Mock
	private Portal portalMock;
	private ScriptPortal scriptPortal;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		scriptPortal = new ScriptPortal(portalMock);
	}

	@Test
	public void shouldDelegeteGetAllRolesToModel() {
		Set<String> roles = new HashSet<String>(asList("role1", "role2"));
		when(portalMock.getAllRoles()).thenReturn(roles);

		assertThat(scriptPortal.getAllRoles(), is(roles));
	}
}
