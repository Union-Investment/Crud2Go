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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;
import de.unioninvestment.eai.portal.support.vaadin.liferay.PermissionsUtil;

/**
 * 
 * Repräsentiert eine Rolle im Portal.
 * 
 * @author siva.selvarajah
 */
@Configurable
public class Role implements Serializable {

	public static final String AUTHENTICATED = "authenticated";
	public static final String ANONYMOUS = "anonymous";
	public static final String ALL = "all";
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private Portal portal;

	private String name;
	private final String primKey;

	/**
	 * Konstruktor.
	 * 
	 * @param name
	 *            Rollenname
	 * @param porletInstanceId
	 *            ID des Portlets
	 */
	public Role(String name, long primKey) {
		this.name = name;
		this.primKey = String.valueOf(primKey);
	}

	/**
	 * Registriert die Rolle als Resource in Liferay.
	 * 
	 * @param owner
	 *            Besitzer
	 */
	public void registerAsLiferayResource(String owner) {
		portal.registerResource(Role.class.getName(), primKey, owner);
	}

	/**
	 * Prüft, ob der Benutzer der Rolle angehört.
	 * 
	 * @param user
	 *            Benutzer
	 * @return ob der Benutzer der Rolle angehört.
	 */
	public boolean isMember(User user) {
		return portal.hasPermission(user.getName(), "MEMBER",
				Role.class.getName(), primKey);
	}

	/**
	 * 
	 * @return Rollenname
	 */
	public String getName() {
		return name;
	}

	public String getPermissionsURL() {
		return PermissionsUtil.buildURL(Role.class.getName(), primKey, name);
	}
}
