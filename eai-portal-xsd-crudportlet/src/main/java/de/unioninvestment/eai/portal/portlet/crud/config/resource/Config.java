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
package de.unioninvestment.eai.portal.portlet.crud.config.resource;

import static java.util.Collections.unmodifiableMap;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;

/**
 * Wrapper der Portlet-Konfiguration, zum Abspeichern der RoleResourceIds
 * 
 * @author max.hartmann
 * 
 */
public class Config implements Serializable {
	private static final long serialVersionUID = 1L;

	private final PortletConfig portletConfig;
	private final Date lastUpdated;
	private final String fileName;
	private Map<String, Long> roleResourceIDs;

	public Config(PortletConfig portletConfig,
			Map<String, Long> roleResourceIDs, String fileName, Date lastUpdated) {
		this.portletConfig = portletConfig;
		this.fileName = fileName;
		this.lastUpdated = lastUpdated;
		if (roleResourceIDs != null) {
			this.roleResourceIDs = unmodifiableMap(roleResourceIDs);
		}
	}

	public Map<String, Long> getRoleResourceIDs() {
		return roleResourceIDs;
	}

	public PortletConfig getPortletConfig() {
		return portletConfig;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public String getFileName() {
		return fileName;
	}
}
