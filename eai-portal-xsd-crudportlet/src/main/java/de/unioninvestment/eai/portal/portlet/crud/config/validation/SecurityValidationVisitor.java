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

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SecurableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;

/**
 * 
 * Validiert die Konfiguration auf Berechtigungsangaben.
 * 
 * @author siva.selvarajah
 */
public class SecurityValidationVisitor implements ConfigurationVisitor {

	private static final Set<String> NO_ACTIONS = new HashSet<String>();

	private static final Map<Class<? extends SecurableConfig>, Set<String>> VALID_ACTIONS = new HashMap<Class<? extends SecurableConfig>, Set<String>>();

	static {
		VALID_ACTIONS.put(TabsConfig.class,
				new HashSet<String>(asList("build")));
		VALID_ACTIONS.put(FormConfig.class,
				new HashSet<String>(asList("build")));
		VALID_ACTIONS
				.put(TabConfig.class, new HashSet<String>(asList("build")));
		VALID_ACTIONS.put(TableConfig.class,
				new HashSet<String>(asList("build", "edit")));
		VALID_ACTIONS.put(DatabaseQueryConfig.class, new HashSet<String>(
				asList("update", "insert", "delete")));
		VALID_ACTIONS.put(DatabaseTableConfig.class, new HashSet<String>(
				asList("update", "insert", "delete")));
		VALID_ACTIONS.put(DatabaseQueryConfig.class, new HashSet<String>(
				asList("update", "insert", "delete")));
		VALID_ACTIONS.put(JmxContainerConfig.class,
				new HashSet<String>(asList("update", "insert", "delete")));
		VALID_ACTIONS.put(ScriptContainerConfig.class, new HashSet<String>(
				asList("update", "insert", "delete")));
		VALID_ACTIONS.put(FormActionConfig.class, new HashSet<String>(
				asList("build")));
		VALID_ACTIONS.put(TableActionConfig.class, new HashSet<String>(
				asList("build")));
		VALID_ACTIONS.put(ColumnConfig.class,
				new HashSet<String>(asList("display", "edit")));
		VALID_ACTIONS.put(ScriptComponentConfig.class, new HashSet<String>(
				asList("build")));
	}

	private Set<String> roles = new HashSet<String>();

	/**
	 * Konstruktor.
	 */
	public SecurityValidationVisitor() {
		roles.add("all");
		roles.add("anonymous");
		roles.add("authenticated");
	}

	@Override
	public void visit(Object element) {
		if (element instanceof RoleConfig) {
			rememberRole((RoleConfig) element);
		} else if (element instanceof SecurableConfig) {
			validatePermissions((SecurableConfig) element);
		}
	}

	private void validatePermissions(SecurableConfig securable) {
		if (securable.getPermissions() != null) {
			Set<String> validActions = getValidActions(securable);
			for (PermissionConfig perm : securable.getPermissions()
					.getEntries()) {
				checkThatRolesExist(perm);
				validatePermission(validActions, perm);
			}
		}
	}

	private void validatePermission(Set<String> validActions,
			PermissionConfig perm) {
		if (!validActions.contains(perm.getAction())) {
			throw new ConfigurationException("Unknown action '"
					+ perm.getAction() + "'. Known actions: " + validActions);
		}
	}

	private Set<String> getValidActions(SecurableConfig securable) {
		Set<String> perms = VALID_ACTIONS.get(securable.getClass());
		if (perms == null) {
			return NO_ACTIONS;
		}
		return perms;
	}

	private void checkThatRolesExist(PermissionConfig perm) {
		if (perm.getRoles().trim().equals("")) {
			throw new ConfigurationException("Empty roles attribute");
		} else {
			String[] permRoles = perm.getRoles().split(",");
			for (String role : permRoles) {
				if (!roles.contains(role)) {
					throw new ConfigurationException("Unknown role '" + role
							+ "'. Known roles: " + roles);
				}
			}
		}
	}

	private void rememberRole(RoleConfig roleConfig) {
		roles.add(roleConfig.getName());
	}

	@Override
	public void visitAfter(Object element) {
		// do nothing
	}

	Set<String> getRoles() {
		return roles;
	}

}
