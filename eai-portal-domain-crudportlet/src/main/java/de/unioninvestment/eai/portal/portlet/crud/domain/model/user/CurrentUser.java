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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;

/**
 * Repräsentriert einen User.
 * 
 * 
 * @author siva.selvarajah
 */
public class CurrentUser extends User {

	private static final Logger LOG = LoggerFactory
			.getLogger(CurrentUser.class);

	private static final AnonymousUser ANONYMOUS_USER = new AnonymousUser();

	private final Set<Role> portalRoles;

	private NamedUser cachedUser;

	/**
	 * Konstruktor.
	 * 
	 * @param portalRoles
	 *            Alle Portalrollen
	 */
	public CurrentUser(Set<Role> portalRoles) {
		this.portalRoles = portalRoles;
	}

	@Override
	public String getName() {
		return currentUser().getName();
	}

	@Override
	public Set<String> getPortalRoles() {
		return currentUser().getPortalRoles();
	}

	@Override
	public Set<String> getRoles() {
		return currentUser().getRoles();
	}

	public boolean isAuthenticated() {
		return currentUser() instanceof NamedUser;
	}

	protected User currentUser() {
		VaadinRequest currentRequest = VaadinService.getCurrentRequest();
		if (currentRequest != null) {
			String remoteUser = currentRequest.getRemoteUser();
			if (remoteUser != null) {
				if (cachedUser == null
						|| !cachedUser.getName().equals(remoteUser)) {
					cachedUser = new NamedUser(remoteUser, portalRoles);
				}
				return cachedUser;
			}
		} else {
			LOG.warn("No portlet request found - that's ok for unit testing. Returning anonymous user");
		}
		return ANONYMOUS_USER;
	}

}
