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
package de.unioninvestment.eai.portal.portlet.crud.config.validation;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import org.junit.Before;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.AllowConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DenyConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;

public class SecurityValidationVisitorTest {

	private RoleConfig role;
	private SecurityValidationVisitor visitor;
	private PermissionsConfig permissionsConfig;
	private AllowConfig allowConfig;
	private DenyConfig denyConfig;

	@Before
	public void setUp() {
		role = new RoleConfig();
		role.setName("myrole");

		allowConfig = new AllowConfig();
		allowConfig.setAction("edit");
		allowConfig.setRoles("authenticated");

		denyConfig = new DenyConfig();
		denyConfig.setAction("test");
		denyConfig.setRoles("anonymous");

		permissionsConfig = new PermissionsConfig();

		visitor = new SecurityValidationVisitor();
	}

	@Test
	public void shouldRegisterRolesFromConfig() {
		visitor.visit(role);
		assertThat(visitor.getRoles(), hasItem("myrole"));
	}

	@Test
	public void shouldIgnoreNonSecurableElements() {
		visitor.visit(new Object());
	}

	@Test
	public void shouldIgnoreSecurableElementsWithoutConfiguredPermissions() {
		visitor.visit(new TableConfig());
	}

	@Test
	public void shouldAcceptConfiguredRolesOnSecurableConfig() {
		visitor.visit(role);

		allowConfig.setRoles("myrole");
		permissionsConfig.getEntries().add(allowConfig);
		TableConfig tableConfig = new TableConfig();
		tableConfig.setPermissions(permissionsConfig);

		visitor.visit(tableConfig);
	}

	@Test
	public void shouldAcceptAnonymousRole() {
		allowConfig.setRoles("anonymous");
		permissionsConfig.getEntries().add(allowConfig);
		TableConfig tableConfig = new TableConfig();
		tableConfig.setPermissions(permissionsConfig);

		visitor.visit(tableConfig);
	}

	@Test
	public void shouldAcceptAuthenticatedRole() {
		allowConfig.setRoles("authenticated");
		permissionsConfig.getEntries().add(allowConfig);
		TableConfig tableConfig = new TableConfig();
		tableConfig.setPermissions(permissionsConfig);

		visitor.visit(tableConfig);
	}

	@Test(expected = ConfigurationException.class)
	public void shouldRejectUnknownRolesOnSecurableConfig() {
		allowConfig.setRoles("unknownRole");
		permissionsConfig.getEntries().add(allowConfig);

		TableConfig tableConfig = new TableConfig();
		tableConfig.setPermissions(permissionsConfig);

		visitor.visit(tableConfig);
	}

	@Test(expected = ConfigurationException.class)
	public void shouldRejectUnknownTablePermission() {
		allowConfig.setRoles("authenticated");
		allowConfig.setAction("editible");
		permissionsConfig.getEntries().add(allowConfig);
		TableConfig tableConfig = new TableConfig();
		tableConfig.setPermissions(permissionsConfig);

		visitor.visit(tableConfig);
	}
}
