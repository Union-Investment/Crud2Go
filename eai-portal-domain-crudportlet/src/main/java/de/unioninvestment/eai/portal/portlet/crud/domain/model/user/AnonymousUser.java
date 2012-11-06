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

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;

/**
 * Repräsentiert einen nicht angemeldeten User.
 * 
 * 
 * @author siva.selvarajah
 */
public class AnonymousUser extends User {

	static final Set<String> ANONYMOUS_ROLES = unmodifiableSet(new HashSet<String>(
			asList(Role.ANONYMOUS, Role.ALL)));

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<String> getPortalRoles() {
		return emptySet();
	}

	@Override
	public Set<String> getRoles() {
		return ANONYMOUS_ROLES;
	}

}
