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

import javax.portlet.PortletPreferences;

import com.vaadin.server.VaadinPortletService;

import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig;

/**
 * A value that is persisted in the portlet preferences.
 * 
 * @author carsten.mjartan
 */
public class Preference implements Serializable {

	private static final long serialVersionUID = 1L;

	private PreferenceConfig config;

	public Preference(PreferenceConfig config) {
		this.config = config;
	}

	/**
	 * @return the preference key
	 */
	public String getKey() {
		return config.getKey();
	}

	/**
	 * @return the value from the portlet preferences, or the configured default
	 *         value if not set
	 */
	public String getValue() {
		PortletPreferences liferayPreferences = VaadinPortletService
				.getCurrentPortletRequest().getPreferences();
		return liferayPreferences.getValue(getKey(), config.getDefault());
	}

}
