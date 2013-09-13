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
package de.unioninvestment.eai.portal.portlet.crud.services;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;

/**
 * Caching Aspekt für CRUD-PortletPresenter-Konfiguration. Beim Schreiben einer
 * Konfiguration wird der Cache invalidiert.
 * 
 * Im Cache wird das deserialisierte Konfigurationsmodell abgelegt. Dies setzt
 * voraus, dass das Modell nicht durch die Anwendung verändert wird (immutable
 * ist).
 * 
 * @author carsten.mjartan
 */
@Aspect
@Configurable
public class ConfigurationCachingServiceAspect {

	@Autowired
	private Ehcache cache;

	@Autowired
	private Settings settings;

	/**
	 * Leerer Konstruktor.
	 */
	@SuppressWarnings("all")
	public ConfigurationCachingServiceAspect() {
		// empty constructor
	}

	/**
	 * @param pjp
	 *            die eigentlich aufgerufene Methode
	 * @param windowId
	 *            die WindowID des aktuellen Portlets
	 * @return eine gecachete oder neu gelesene PortletConfig Instanz
	 * @throws Throwable
	 *             bei Fehler
	 */

	@Around(value = "execution(* de.unioninvestment.eai.portal.portlet.crud.services.DefaultConfigurationService.getPortletConfig(..)) && args(portletId, communityId)")
	public Object findInCache(ProceedingJoinPoint pjp, String portletId,
			long communityId) throws Throwable {
		if (isCacheEnabled() && (cache != null)) {
			String cacheKey = createKey(portletId, communityId);
			Element cacheElement = cache.get(cacheKey);
			if (cacheElement != null) {
				return cacheElement.getObjectValue();
			} else {
				Config config = (Config) pjp.proceed();
				cache.put(new Element(cacheKey, config));
				return config;
			}
		} else {
			return pjp.proceed();
		}

	}

	/**
	 * Leert den Cache bei Speicherung einer neuen Konfiguration.
	 * 
	 * @param filename
	 *            der Dateiname
	 * @param configXml
	 *            die Konfiguration
	 * @param windowId
	 *            die WindowID
	 * @param username
	 *            der aktuelle NamedUser
	 */
	@After(value = "execution(* de.unioninvestment.eai.portal.portlet.crud.services.DefaultConfigurationService.storeConfigurationFile(..)) && args(filename,configXml,portletId,communityId,username)")
	public void removeFromCache(String filename, byte[] configXml,
			String portletId, long communityId, String username) {
		if (isCacheEnabled() && cache != null) {
			cache.remove(createKey
					(portletId, communityId));
		}
	}

	private String createKey(String portletId, long communityId) {
		return portletId + "." + communityId;
	}

	public void setCache(Ehcache cache) {
		this.cache = cache;
	}

	private boolean isCacheEnabled() {
		return settings != null && settings.isCacheEnabled();
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
