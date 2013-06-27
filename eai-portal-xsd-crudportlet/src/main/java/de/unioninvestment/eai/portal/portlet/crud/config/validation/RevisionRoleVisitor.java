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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.config.AllowConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DenyConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DialogConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ScriptContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SecurableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationVisitor;

/**
 * {@link ConfigurationVisitor} that adds a "revision" role to the configuration
 * and restricts it's permissions to that the configurations are displayed in
 * "read-only" mode. Writable operations and dynamic content are disabled.
 * 
 * @author carsten.mjartan
 */
public class RevisionRoleVisitor implements ConfigurationVisitor {

	static final String DO_NOTHING = null;
	static final String NO_ACTIONS = "";
	static final Map<Class<? extends SecurableConfig>, String> VALID_ACTIONS = new HashMap<Class<? extends SecurableConfig>, String>();

	static {
		VALID_ACTIONS.put(PageConfig.class, DO_NOTHING);
		VALID_ACTIONS.put(DialogConfig.class, DO_NOTHING);
		VALID_ACTIONS.put(RegionConfig.class, DO_NOTHING);

		VALID_ACTIONS.put(TabsConfig.class, DO_NOTHING);
		VALID_ACTIONS.put(TabConfig.class, DO_NOTHING);
		VALID_ACTIONS.put(FormConfig.class, DO_NOTHING);
		VALID_ACTIONS.put(TableConfig.class, "build");
		VALID_ACTIONS.put(PortletConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(DatabaseQueryConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(DatabaseTableConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(ReSTContainerConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(JmxContainerConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(ScriptContainerConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(FormActionConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(TableActionConfig.class, NO_ACTIONS);
		VALID_ACTIONS.put(ColumnConfig.class, "display");
		VALID_ACTIONS.put(ScriptComponentConfig.class, NO_ACTIONS);
	}

	private String revisionRoleName;
	private String portalRoleName;

	private boolean enabled = true;

	public RevisionRoleVisitor(String revisionRoleName, String portalRoleName) {
		this.revisionRoleName = revisionRoleName;
		this.portalRoleName = portalRoleName;
	}

	@Override
	public void visit(Object element) {
		if (element instanceof PortletConfig) {
			PortletConfig portletConfig = (PortletConfig) element;
			createNewRolesConfig(portletConfig);
			enabled = !revisionRoleExists(portletConfig);
		}
		if (enabled) {
			if (element instanceof SecurableConfig) {
				applyRevisionTo((SecurableConfig) element);
			}
		}
	}

	private void createNewRolesConfig(PortletConfig portletConfig) {
		if (portletConfig.getRoles() == null) {
			portletConfig.setRoles(new RolesConfig());
		}
	}

	private void applyRevisionTo(SecurableConfig securableConfig) {
		if (VALID_ACTIONS.containsKey(securableConfig.getClass())) {
			String actions = VALID_ACTIONS.get(securableConfig.getClass());
			if (actions != DO_NOTHING) {
				maybeCreateNewPermissionsConfig(securableConfig);
				if (isNonScriptAction(securableConfig)) {
					createPermissionEntries(securableConfig, "build");
				} else {
					createPermissionEntries(securableConfig, actions);
				}
			}
		} else {
			throw new UnsupportedOperationException("Element of type '"
					+ securableConfig.getClass().getName() + "' not supported!");
		}
	}

	private boolean isNonScriptAction(SecurableConfig securableConfig) {
		return (securableConfig instanceof FormActionConfig)
				&& (((FormActionConfig) securableConfig).getOnExecution() == null);
	}

	private void createPermissionEntries(SecurableConfig securableConfig,
			String actions) {
		List<PermissionConfig> entries = securableConfig.getPermissions()
				.getEntries();

		if (actions == null || actions.equals(NO_ACTIONS)) {
			entries.add(0, createDenyAllConfig());
		} else {
			entries.add(0, createAllowConfig(actions));
			entries.add(1, createDenyAllConfig());
		}
	}

	private boolean revisionRoleExists(PortletConfig portletConfig) {
		for (RoleConfig roleConfig : portletConfig.getRoles().getRole()) {
			if (roleConfig.getName().equals(revisionRoleName)) {
				return true;
			}
		}
		return false;
	}

	private PermissionConfig createAllowConfig(String actions) {
		AllowConfig allowConfig = new AllowConfig();
		allowConfig.setRoles(revisionRoleName);
		allowConfig.setAction(actions);
		return allowConfig;
	}

	private void maybeCreateNewPermissionsConfig(SecurableConfig securableConfig) {
		if (securableConfig.getPermissions() == null) {
			securableConfig.setPermissions(new PermissionsConfig());
		}
	}

	private DenyConfig createDenyAllConfig() {
		DenyConfig denyConfig = new DenyConfig();
		denyConfig.setRoles("revision");
		denyConfig.setAction("all");
		return denyConfig;
	}

	@Override
	public void visitAfter(Object element) {
		if (enabled) {
			if (element instanceof RolesConfig) {
				addRevisionRole((RolesConfig) element);
			}
		}
	}

	private void addRevisionRole(RolesConfig rolesConfig) {
		RoleConfig revisionRole = new RoleConfig();
		revisionRole.setName(revisionRoleName);
		revisionRole.setPortalRole(portalRoleName);
		rolesConfig.getRole().add(revisionRole);
	}

}
