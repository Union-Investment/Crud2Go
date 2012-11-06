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

import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.config.AllowConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PermissionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SecurableConfig;

/**
 * Allgemeine Schnittstelle für Benutzeroperationen.
 * 
 * @author carsten.mjartan
 */
public abstract class User {

	/**
	 * @return der Name des aktuellen Benutzers
	 */
	public abstract String getName();

	/**
	 * @return die Rollen im Portal (in diesem Fall Liferay)
	 */
	public abstract Set<String> getPortalRoles();

	/**
	 * @return die Konfigurationsspezifischen Rollen des Users
	 */
	public abstract Set<String> getRoles();

	/**
	 * 
	 * Prüft, ob der Benutzer auf das SecurableConfig-Objekt eine Berechtigung
	 * hat.
	 * 
	 * @param securableConfig
	 *            Gesichertes Objekt
	 * @param permission
	 *            Berechtigung
	 * @param defaultValue
	 *            Wert, der zurückgeliefert wird, wenn keine Berechtigung
	 *            hinterlegt ist
	 * @return ob der Benutzer auf das SecurableConfig-Objekt eine Berechtigung
	 *         hat.
	 */
	public boolean hasPermissions(SecurableConfig securableConfig,
			String permission, boolean defaultValue) {
		if (securableConfig.getPermissions() != null) {
			Set<String> roles = getRoles();
			for (PermissionConfig perm : securableConfig.getPermissions()
					.getEntries()) {
				if (perm.getAction().equals(permission)
						&& rolesMatch(roles, perm.getRoles())) {
					if (perm instanceof AllowConfig) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return defaultValue;
	}

	private boolean rolesMatch(Set<String> roles,
			String commaSeparatedRequiredRoles) {
		String[] requiredRoles = commaSeparatedRequiredRoles.split(",");
		for (String requiredRole : requiredRoles) {
			if (roles.contains(requiredRole)) {
				return true;
			}
		}
		return false;
	}
}