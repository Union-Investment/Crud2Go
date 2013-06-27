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

import de.unioninvestment.eai.portal.portlet.crud.domain.aspects.DuringBuild;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;

public class PortletContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private User currentUser;

	PortletContext() {
	}

	public User getCurrentUser() {
		return currentUser;
	}

	@DuringBuild
	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;

	}
}
