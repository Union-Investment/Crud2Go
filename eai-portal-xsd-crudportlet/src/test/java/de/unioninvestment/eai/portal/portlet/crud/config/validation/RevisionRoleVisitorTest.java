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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import de.unioninvestment.eai.portal.portlet.crud.config.AllowConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DenyConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SecurableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;

public class RevisionRoleVisitorTest {

	private RevisionRoleVisitor visitor;
	private RolesConfig rolesConfig;

	@Before
	public void setup() {
		visitor = new RevisionRoleVisitor("revision", "revision-ou");
		rolesConfig = new RolesConfig();

	}

	@Test
	public void shouldAddRevisionRoleAsFirstRole() {
		PortletConfig portletConfig = new PortletConfig();

		visitor.visit(portletConfig);

		assertThat(portletConfig.getRoles(), is(notNullValue()));
	}

	@Test
	public void shouldAddRevisionRoleToExistingRoles() {
		visitor.visitAfter(rolesConfig);

		assertThat(rolesConfig.getRole().get(0).getName(), is("revision"));
		assertThat(rolesConfig.getRole().get(0).getPortalRole(),
				is("revision-ou"));
	}

	@Test
	public void shouldLeaveConfigAloneIfRevisionRoleAlreadyExists() {
		RoleConfig revisionRole = new RoleConfig();
		revisionRole.setName("revision");
		rolesConfig.getRole().add(revisionRole);

		PortletConfig portletConfig = new PortletConfig();
		portletConfig.setRoles(rolesConfig);

		visitor.visit(portletConfig);

		TableConfig tableConfig = new TableConfig();
		visitor.visit(tableConfig);

		assertThat(rolesConfig.getRole().size(), is(1));
		assertThat(rolesConfig.getRole().get(0), is(revisionRole));

		assertThat(tableConfig.getPermissions(), is(nullValue()));
	}

	@Test
	public void shouldAddRevisionPermissionsToUnconfiguredElement() {
		TableConfig tableConfig = new TableConfig();

		visitor.visit(tableConfig);

		List<PermissionConfig> entries = tableConfig.getPermissions()
				.getEntries();
		assertThat(entries.size(), is(2));

		PermissionConfig firstEntry = entries.get(0);
		assertThat(firstEntry, instanceOf(AllowConfig.class));
		assertThat(firstEntry.getRoles(), is("revision"));
		assertThat(firstEntry.getAction(), is("build"));

		PermissionConfig secondEntry = entries.get(1);
		assertThat(secondEntry, instanceOf(DenyConfig.class));
		assertThat(secondEntry.getRoles(), is("revision"));
		assertThat(secondEntry.getAction(), is("all"));
	}

	@Test
	public void shouldOnlyAddDenyRuleIfNoActionsAreAllowed() {
		TableActionConfig element = new TableActionConfig();

		visitor.visit(element);

		List<PermissionConfig> entries = element.getPermissions().getEntries();
		assertThat(entries.size(), is(1));

		PermissionConfig secondEntry = entries.get(0);
		assertThat(secondEntry, instanceOf(DenyConfig.class));
		assertThat(secondEntry.getRoles(), is("revision"));
		assertThat(secondEntry.getAction(), is("all"));
	}

	@Test
	public void shouldNotTouchElementIfNothingToDo() {
		FormConfig element = new FormConfig();

		visitor.visit(element);

		assertThat(element.getPermissions(), is(nullValue()));
	}

	@Test
	public void shouldHaveRulesForAllSecurableElements() {
		Set<Class<? extends SecurableConfig>> classes = findAllConfigClassesSubclassing(SecurableConfig.class);
		for (Class<? extends SecurableConfig> clazz : classes) {
			assertTrue("Class " + clazz.getSimpleName() + " is not supported",
					RevisionRoleVisitor.VALID_ACTIONS.keySet().contains(clazz));
		}
	}

	private Set<Class<? extends SecurableConfig>> findAllConfigClassesSubclassing(
			Class<?> superType) {
		Set<Class<? extends SecurableConfig>> classes = new HashSet<Class<? extends SecurableConfig>>();

		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
				false);
		provider.addIncludeFilter(new AssignableTypeFilter(superType));

		// scan in org.example.package
		String packageFolder = SecurableConfig.class.getPackage().getName()
				.replace('.', '/');
		Set<BeanDefinition> components = provider
				.findCandidateComponents(packageFolder);
		for (BeanDefinition component : components) {
			try {
				Class<?> cls = Class.forName(component.getBeanClassName());
				classes.add((Class<? extends SecurableConfig>) cls);

			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Class not found???");
			}
		}
		return classes;
	}
}
