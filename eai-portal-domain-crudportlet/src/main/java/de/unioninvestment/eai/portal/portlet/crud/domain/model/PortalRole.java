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

import java.io.Serializable;
import java.security.Principal;

import javax.portlet.PortletRequest;

import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplication;

/**
 * 
 * Repräsentiert eine Rolle im Portal.
 * 
 * @author siva.selvarajah
 */
@Configurable
public class PortalRole implements Serializable, Role {

	private static final long serialVersionUID = 1L;

	private String name;
	private final String portalRoleName;

	/**
	 * Konstruktor.
	 * 
	 * @param name
	 *            Rollenname
	 * @param portalRoleName
	 *            Rollenname im Portal
	 */
	public PortalRole(String name, String portalRoleName) {
		this.name = name;
		this.portalRoleName = portalRoleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMember(User user) {
		PortletRequest request = LiferayApplication.getCurrentRequest();
		Principal principal = request.getUserPrincipal();
		if ((user == null && principal == null)
				|| (user.getName() != null && principal != null && user
						.getName().equals(principal.getName()))) {
			return request.isUserInRole(portalRoleName);
		} else {
			throw new IllegalArgumentException(
					"Membership can only be checked for current user!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

}
