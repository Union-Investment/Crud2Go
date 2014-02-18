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

import static java.util.Collections.sort;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;
import de.unioninvestment.eai.portal.support.vaadin.filter.AdvancedStringFilterTranslator;
import de.unioninvestment.eai.portal.support.vaadin.filter.NothingFilterTranslator;
import de.unioninvestment.eai.portal.support.vaadin.filter.OracleRegExpFilterTranslator;
import de.unioninvestment.eai.portal.support.vaadin.filter.SQLFilterTranslator;

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

	@Value("${portlet.crud.storage.dialect}")
	private DatabaseDialect storageDialect;

	@Value("${portlet.crud.databaseBackend.dialect}")
	private DatabaseDialect databaseBackendDialect;

	@Value("${portlet.crud.storage.configcache.enabled}")
	private boolean cacheEnabled;

	@Value("${portlet.crud.storage.configcache.checkForUpdates}")
	private boolean cacheCheckForUpdates;

	@Value("${portal.instanceId}")
	private String portalInstanceId;

	@Value("${build.number}")
	private String buildNumber;

	@Value("${portlet.crud.vcs.user}")
	private String vcsUser;

	@Value("${portlet.crud.vcs.password}")
	private String vcsPassword;

	@Value("${portlet.crud.revision.portal.role}")
	private String revisionPortalRole;

	@Value("${portlet.crud.displayRequestProcessingInfo}")
	private boolean displayRequestProcessingInfo;

	@Value("${portlet.crud.requestLog.enabled}")
	private boolean isRequestLogEnabled;

	@Value("${portlet.crud.requestLog.minimalDurationMillis}")
	private int requestLogMinimalDurationMillis;

	@Value("${portlet.crud.requestLog.cleanup.cronExpression}")
	private String requestLogCleanupCronExpression;

	@Value("${portlet.crud.requestLog.cleanup.maxAgeDays}")
	private Integer requestLogCleanupMaxAgeDays;

	private URL defaultPropertiesLocation = Settings.class.getClassLoader()
			.getResource("eai-portal-administration.properties");
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
					props.load(defaultPropertiesLocation.openStream());
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

	public String getRevisionPortalRole() {
		return revisionPortalRole;
	}

	public boolean isDisplayRequestProcessingInfo() {
		return displayRequestProcessingInfo;
	}

	public void setDisplayRequestProcessingInfo(
			boolean displayRequestProcessingInfo) {
		this.displayRequestProcessingInfo = displayRequestProcessingInfo;
	}

	public boolean isCacheCheckForUpdates() {
		return cacheCheckForUpdates;
	}

	@PostConstruct
	public void bootstrap() {
		logConfigurationInfo();
		prepareVaadinFilterTranslators();
		prepareVaadinSqlQuoting();
	}

	private void logConfigurationInfo() {
		StringBuilder builder = new StringBuilder("\n");
		String minusLine = Strings.repeat("-", 80) + '\n';
		builder.append(minusLine);
		builder.append("Crud2Go\n");
		builder.append(minusLine);
		addSortedListOfConfigurationProperties(builder);
		builder.append(minusLine);
		LOGGER.info(builder.toString());
	}

	private void addSortedListOfConfigurationProperties(StringBuilder builder) {
		loadPropertiesAndDefaults();
		List<String> lines = new ArrayList<String>(props.size());
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = entry.getKey().toString();
			if (!key.contains("password")) {
				lines.add(Strings.padEnd(key + ":", 50, ' ') + entry.getValue());
			}
		}
		sort(lines);
		for (String line : lines) {
			builder.append(line).append('\n');
		}
	}

	private void prepareVaadinSqlQuoting() {
		QueryBuilder.setStringDecorator(databaseBackendDialect
				.getStringDecorator());
	}

	void prepareVaadinFilterTranslators() {
		QueryBuilder.addFilterTranslator(new AdvancedStringFilterTranslator());
		QueryBuilder.addFilterTranslator(new SQLFilterTranslator());
		QueryBuilder.addFilterTranslator(new NothingFilterTranslator());

		switch (databaseBackendDialect) {
		case ORACLE:
			QueryBuilder
					.addFilterTranslator(new OracleRegExpFilterTranslator());
			break;
		case MYSQL:
			break;
		default:
			break;
		}
	}

	public boolean isRequestLogEnabled() {
		return isRequestLogEnabled;
	}

	public int getRequestLogMinimalDurationMillis() {
		return requestLogMinimalDurationMillis;
	}

	public String getRequestLogCleanupCronExpression() {
		return requestLogCleanupCronExpression;
	}

	public Integer getRequestLogCleanupMaxAgeDays() {
		return requestLogCleanupMaxAgeDays;
	}

}
