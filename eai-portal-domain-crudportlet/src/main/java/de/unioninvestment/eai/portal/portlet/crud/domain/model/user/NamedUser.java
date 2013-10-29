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

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;

/**
 * Benannter User.
 * 
 * @author carsten.mjartan
 */
@Configurable
public class NamedUser extends User {

	private final String name;
	private final Set<Role> portletRoles;

	@Autowired
	private transient Portal portal;
	
	private Set<String> cachedRoles;

	/**
	 * Konstruktor.
	 * 
	 * @param username
	 *            Benutzername
	 * @param portletRoles
	 *            Alle Portalrollen
	 */
	NamedUser(String username, Set<Role> portletRoles) {
		this.name = username;
		this.portletRoles = portletRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getPortalRoles() {
		return unmodifiableSet(portal.getRoles(name));
	}

	@Override
	public Set<String> getRoles() {
		if (cachedRoles == null) {
			cachedRoles = new HashSet<String>();
			if (portletRoles != null) {
				for (Role portletRole : portletRoles) {
					if (portletRole.isMember(this)) {
						cachedRoles.add(portletRole.getName());
					}
				}
			}

			cachedRoles.add(Role.AUTHENTICATED);
			cachedRoles.add(Role.ALL);

		}
		return cachedRoles;
	}
}
