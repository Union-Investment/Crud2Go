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
package de.unioninvestment.eai.portal.portlet.crud;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;

/**
 * Liefert Einstellugen für die CRUD-Applikation.
 * 
 * @author max.hartmann
 * 
 */
// NOSONAR
@Component
public class Settings {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Settings.class);

	private static final String DATASOURCE_PATTERN_PREFIX = "portlet.crud.datasource.pattern.";
	private static final String DATASOURCEINFO_PATTERN_PREFIX = "portlet.crud.datasourceInfo.pattern.";

	@Value("${portlet.crud.help.url}")
	private String helpUrl;

	@Value("${portlet.crud.storage.configcache.enabled}")
	private boolean cacheEnabled;

	@Value("${portal.instanceId}")
	private String portalInstanceId;

	@Value("${build.number}")
	private String buildNumber;

	@Value("${portlet.crud.vcs.user}")
	private String vcsUser;

	@Value("${portlet.crud.vcs.password}")
	private String vcsPassword;

	private URL propertiesLocation = Settings.class.getClassLoader()
			.getResource("eai/eai-portal-administration.properties");

	private Map<Long, String> datasourcePatterns;
	private String defaultDatasourcePattern;

	private Map<Long, String> datasourceInfoPatterns;
	private String defaultDatasourceInfoPattern;

	private Properties props;

	/**
	 * Default Constructor.
	 */
	public Settings() {
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public String getPortalInstanceId() {
		return portalInstanceId;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setPropertiesLocation(URL url) {
		this.propertiesLocation = url;
	}

	/**
	 * @param communityId
	 *            die Liferay-CommunityId
	 * @return das DataSource-Pattern gemäß Konfigurationsdatei
	 */
	public String getDatasourcePattern(long communityId) {
		if (datasourcePatterns == null) {
			datasourcePatterns = getCommunityPropertyMap(DATASOURCE_PATTERN_PREFIX);
		}
		String pattern = datasourcePatterns.get(communityId);
		return pattern != null ? pattern : defaultDatasourcePattern;
	}

	/**
	 * @param communityId
	 *            die Liferay-CommunityId
	 * @return das DataSourceInfo-Pattern gemäß Konfigurationsdatei
	 */
	public String getDatasourceInfoPattern(long communityId) {
		if (datasourceInfoPatterns == null) {
			datasourceInfoPatterns = getCommunityPropertyMap(DATASOURCEINFO_PATTERN_PREFIX);
		}
		String pattern = datasourceInfoPatterns.get(communityId);
		return pattern != null ? pattern : defaultDatasourceInfoPattern;
	}

	private Map<Long, String> getCommunityPropertyMap(String prefix) {
		loadPropertiesAndDefaults();
		Map<Long, String> results = new HashMap<Long, String>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof String && ((String) key).startsWith(prefix)) {
				long communityId = Long.parseLong(((String) key)
						.substring(prefix.length()));
				results.put(communityId, (String) entry.getValue());
			}
		}
		return results;
	}

	private void loadPropertiesAndDefaults() {
		if (props == null) {
			props = new Properties();
			if (propertiesLocation != null) {
				try {
					props.load(propertiesLocation.openStream());
				} catch (IOException e) {
					throw new TechnicalCrudPortletException(
							"Error reading properties file", e);
				}
			} else {
				LOGGER.error("Cannot determine datasource patterns. Properties file does not exist!");
			}
			defaultDatasourcePattern = props
					.getProperty("portlet.crud.datasource.pattern");
			defaultDatasourceInfoPattern = props
					.getProperty("portlet.crud.datasourceInfo.pattern");
		}
	}

	public String getVcsUser() {
		return vcsUser;
	}

	public String getVcsPassword() {
		return vcsPassword;
	}

}
