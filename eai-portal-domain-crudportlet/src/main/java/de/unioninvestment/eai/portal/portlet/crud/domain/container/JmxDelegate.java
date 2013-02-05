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
package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import groovy.util.GroovyMBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.ObjectName;

import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;

import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.JmxContainerConfig.Attribute;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.JMXWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.AuditLogger;
import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.container.UpdateContext;

/**
 * Backend-Schnittstelle für {@link ScriptJMXContainer}.
 */
public class JmxDelegate implements GenericDelegate {

	private MetaData metadata;

	private JMXWrapper jmxWrapper;

	private String query;

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(JmxDelegate.class);

	private List<String> columnNamesWithoutPK;

	private ArrayList<String> getterScripts;

	private AuditLogger auditLogger;

	private String server;

	public JmxDelegate(JmxContainerConfig jmxContainerConfig,
			CurrentUser currentUser) {
		metadata = extractMetadataFromConfig(jmxContainerConfig);
		jmxWrapper = new JMXWrapper(jmxContainerConfig.getServer());
		query = jmxContainerConfig.getQuery();
		auditLogger = new AuditLogger(currentUser);
		server = jmxContainerConfig.getServer();
	}

	public JmxDelegate(JmxContainerConfig jmxContainerConfig,
			JMXWrapper wrapper, CurrentUser currentUser) {
		metadata = extractMetadataFromConfig(jmxContainerConfig);
		jmxWrapper = wrapper;
		query = jmxContainerConfig.getQuery();
		auditLogger = new AuditLogger(currentUser);
		server = jmxContainerConfig.getServer();
	}

	private MetaData extractMetadataFromConfig(
			JmxContainerConfig jmxContainerConfig) {

		List<Column> colums = new ArrayList<Column>();
		List<Attribute> attributsliste = jmxContainerConfig.getAttribute();

		colums.add(new Column("mbeanObjectName", String.class, true, true,
				true, null));

		columnNamesWithoutPK = new ArrayList<String>(attributsliste.size());
		getterScripts = new ArrayList<String>(attributsliste.size());

		for (Attribute attribute : attributsliste) {
			colums.add(new Column(
					attribute.getName(),
					attribute.getType(),
					attribute.isReadonly()
							|| attribute.getServerSideGetter() != null,
					attribute.isRequired(),
					false,
					null));

			columnNamesWithoutPK.add(attribute.getName());
			getterScripts.add(attribute.getServerSideGetter());
		}

		MetaData metadata = new MetaData(colums, false, true, false, false,
				false);

		return metadata;
	}

	@Override
	public MetaData getMetaData() {
		return metadata;
	}

	@Override
	public List<Object[]> getRows() {
		try {
			Map<String, ? extends Map<String, ? extends Object>> queryResult = jmxWrapper
					.query(
							query, columnNamesWithoutPK, getterScripts);

			List<Object[]> result = new ArrayList<Object[]>();

			for (Entry<String, ? extends Map<String, ? extends Object>> entry : queryResult
					.entrySet()) {
				Object[] row = new Object[metadata.getColumnNames().size()];

				row[0] = entry.getKey();

				int i = 1;
				for (String columnName : columnNamesWithoutPK) {
					row[i++] = entry.getValue().get(columnName);
				}
				result.add(row);

			}
			return result;

		} catch (Exception e) {
			LOG.error("error executing jmx-query", e);
			throw new BusinessException("portlet.crud.error.jmxReadError");
		}
	}

	@Override
	public void setFilters(Filter[] filters) {
		// nichts
	}

	@Override
	public void update(List<GenericItem> items, UpdateContext context) {
		for (GenericItem item : items) {
			GroovyMBean mbeanProxy;
			try {
				mbeanProxy = jmxWrapper.proxyFor(item.getId().getId()[0]
						.toString());

				for (Object propertyID : item.getItemPropertyIds()) {
					GenericProperty itemProperty = (GenericProperty) item
							.getItemProperty(propertyID);
					if (itemProperty.isModified()) {
						mbeanProxy.setProperty(propertyID.toString(),
								itemProperty.getValue());

						ObjectName objectName = mbeanProxy.name();

						if (server != null && objectName != null) {
							auditLogger.audit(server,
									objectName.getCanonicalName(),
									propertyID.toString(),
									itemProperty);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("error writing jmx-attribute", e);
				throw new BusinessException("portlet.crud.error.jmxWriteError");
			}
		}
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	void setJmxWrapper(JMXWrapper jmxWrapper) {
		this.jmxWrapper = jmxWrapper;
	}

	public JMXWrapper getJmxWrapper() {
		return jmxWrapper;
	}

	public void setServer(String server) {
		this.server = server;
	}
}
