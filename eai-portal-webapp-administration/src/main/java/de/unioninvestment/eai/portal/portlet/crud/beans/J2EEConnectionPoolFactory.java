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
package de.unioninvestment.eai.portal.portlet.crud.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;

/**
 * Fabrik, die zu einem gegebenen Daten-Quelles-Kurznamen ein
 * {@link J2EEConnectionPool} zurückliefert.
 * 
 * @author max.hartmann
 * 
 */
@Component
public class J2EEConnectionPoolFactory implements ConnectionPoolFactory {

	@Autowired
	private Settings settings;

	private Map<String, J2EEConnectionPool> pools = new HashMap<String, J2EEConnectionPool>();

	@Override
	public J2EEConnectionPool getPool(String shortName) {
		long communityId = Context.getLiferayCommunityId();
		String key = shortName + "_" + communityId;
		synchronized (pools) {
			J2EEConnectionPool pool = pools.get(key);
			if (pool == null) {
				pool = new J2EEConnectionPool(getPattern(communityId),
						shortName);
				pools.put(key, pool);
			}
			return pool;
		}
	}

	private String getPattern(long communityId) {
		String pattern = settings.getDatasourcePattern(communityId);
		if (pattern == null) {
			throw new TechnicalCrudPortletException(
					"Datasource pattern for community id " + communityId
							+ " is not configured!");
		}
		return pattern;
	}

	/**
	 * @param pools
	 *            Map von Pools für Tests
	 */
	void setPools(Map<String, J2EEConnectionPool> pools) {
		this.pools = pools;
	}

	/**
	 * @param settings
	 *            die Konfiguration des Portlets
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
