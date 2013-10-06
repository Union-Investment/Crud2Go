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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model.portal;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal;

/**
 * 
 * Klasse für den Zugriff auf das Portal.
 * 
 * @author siva.selvarajah
 */
public class ScriptPortal implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Portal portal;

	/**
	 * Konstruktor.
	 */
	public ScriptPortal(Portal portal) {
		this.portal = portal;
	}

	/**
	 * @return alle regulären Rollennamen des Portals.
	 */
	public Set<String> getAllRoles() {
		return portal.getAllRoles();
	}

	/**
	 * Öffnet eine Portalseite im Browser.
	 * 
	 * @param friendlyUrl
	 *            "friendly URL" der Portalseite
	 * @param args
	 *            optionale query-Parameter
	 * 
	 * @throws URISyntaxException
	 *             falls die sich ergebende URI syntaktisch inkorrekt ist
	 */
	public void open(Map<String, String> args, String friendlyUrl)
			throws URISyntaxException {
		portal.open(friendlyUrl, args);
	}
	
	public Map<String,String[]> getParameters() {
		return portal.getParameters();
	}
}
