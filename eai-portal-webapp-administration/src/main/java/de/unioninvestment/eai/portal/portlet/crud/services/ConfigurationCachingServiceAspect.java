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

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;

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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConfigurationCachingServiceAspect.class);
	
	@Autowired @Qualifier("portletCache")
	private Ehcache cache;

	@Autowired
	private Settings settings;

	@Autowired
	private ConfigurationDao dao;

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
			if (returnCachedElement(portletId, communityId, cacheElement)) {
				Config config = (Config) cacheElement.getObjectValue();
				LOGGER.info("Cache hit for config {}", config.getFileName());
				return config;
			} else {
				Config config = (Config) pjp.proceed();
				cache.put(new Element(cacheKey, config));
				if (config != null) {
					LOGGER.info("Cache miss for config {}", config.getFileName());
				}
				return config;
			}
		} else {
			return pjp.proceed();
		}

	}

	private boolean returnCachedElement(String portletId,
			long communityId, Element cacheElement) {
		if (cacheElement == null || cacheElement.getObjectValue() == null) {
			return false;
		} else if (!settings.isCacheCheckForUpdates()) {
			return true;
		} else {
			Config cachedConfig = (Config) cacheElement.getObjectValue();
			ConfigurationMetaData metaData = dao.readConfigMetaData(portletId, communityId);
			return metaData != null && !cachedConfig.getLastUpdated().before(metaData.getUpdated());
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
			cache.remove(createKey(portletId, communityId));
		}
	}
	

	private String createKey(String portletId, long communityId) {
		return portletId + "." + communityId;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	private boolean isCacheEnabled() {
		return settings != null && settings.isCacheEnabled();
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
