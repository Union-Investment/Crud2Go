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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.AllowConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DenyConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;

public class UserTest {
	@Test
	public void shouldReturnDefaultValueWithMissingPermissionConfiguration() {
		assertThat(new AnonymousUser().hasPermissions(new PageConfig(), "test",
				true), is(true));
	}

	@Test
	public void shouldReturnTrueIfAllowIsFirstMatchingEntry() {
		AllowConfig allowConfig = new AllowConfig();
		allowConfig.setAction("test");
		allowConfig.setRoles("anonymous");

		PermissionsConfig permissionsConfig = new PermissionsConfig();
		permissionsConfig.getEntries().add(allowConfig);

		TabConfig tabConfig = new TabConfig();
		tabConfig.setPermissions(permissionsConfig);

		assertThat(
				new AnonymousUser().hasPermissions(tabConfig, "test", false),
				is(true));
	}

	@Test
	public void shouldReturnFalseIfDenyIsFirstMatchingEntry() {
		DenyConfig denyConfig = new DenyConfig();
		denyConfig.setAction("test");
		denyConfig.setRoles("anonymous");

		PermissionsConfig permissionsConfig = new PermissionsConfig();
		permissionsConfig.getEntries().add(denyConfig);

		TabConfig tabConfig = new TabConfig();
		tabConfig.setPermissions(permissionsConfig);

		assertThat(new AnonymousUser().hasPermissions(tabConfig, "test", true),
				is(false));
	}

	@Test
	public void shouldReturnDefaultValueIfNoPermissionsMatchByAction() {
		DenyConfig denyConfig = new DenyConfig();
		denyConfig.setAction("otherTest");
		denyConfig.setRoles("anonymous");

		PermissionsConfig permissionsConfig = new PermissionsConfig();
		permissionsConfig.getEntries().add(denyConfig);

		TabConfig tabConfig = new TabConfig();
		tabConfig.setPermissions(permissionsConfig);

		assertThat(new AnonymousUser().hasPermissions(tabConfig, "test", true),
				is(true));
	}

	@Test
	public void shouldReturnDefaultValueIfNoPermissionsMatchByRole() {
		DenyConfig denyConfig = new DenyConfig();
		denyConfig.setAction("test");
		denyConfig.setRoles("authenticated");

		PermissionsConfig permissionsConfig = new PermissionsConfig();
		permissionsConfig.getEntries().add(denyConfig);

		TabConfig tabConfig = new TabConfig();
		tabConfig.setPermissions(permissionsConfig);

		assertThat(new AnonymousUser().hasPermissions(tabConfig, "test", true),
				is(true));
	}

	@Test
	public void shouldReturnSecondPermissionWhichShouldMatch() {
		AllowConfig allowConfig = new AllowConfig();
		allowConfig.setAction("test");
		allowConfig.setRoles("authenticated");

		DenyConfig denyConfig = new DenyConfig();
		denyConfig.setAction("test");
		denyConfig.setRoles("anonymous");

		PermissionsConfig permissionsConfig = new PermissionsConfig();
		permissionsConfig.getEntries().add(allowConfig);
		permissionsConfig.getEntries().add(denyConfig);

		TabConfig tabConfig = new TabConfig();
		tabConfig.setPermissions(permissionsConfig);

		assertThat(new AnonymousUser().hasPermissions(tabConfig, "test", true),
				is(false));

	}

}
