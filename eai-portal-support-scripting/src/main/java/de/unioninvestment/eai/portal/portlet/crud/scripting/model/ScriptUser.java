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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;

/**
 * Repräsentiert einen User.
 * 
 * @author siva.selvarajah
 */
public class ScriptUser {

	private final User model;

	/**
	 * Konstruktor.
	 * 
	 * @param model
	 *            User
	 */
	public ScriptUser(User model) {
		this.model = model;
	}

	/**
	 * @return den Benutzernamen
	 */
	public String getName() {
		return model.getName();
	}

	/**
	 * @return die Benutzerrollen aus der Liferay-Portal API
	 */
	public Set<String> getPortalRoles() {
		return model.getPortalRoles();
	}

	/**
	 * @return die Rollen des aktuellen Benutzers aus der Konfiguration
	 */
	public Set<String> getRoles() {
		return model.getRoles();
	}
}
