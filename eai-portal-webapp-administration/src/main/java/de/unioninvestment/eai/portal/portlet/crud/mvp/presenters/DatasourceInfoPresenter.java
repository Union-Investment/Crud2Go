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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.ConfigurationProcessor;
import de.unioninvestment.eai.portal.portlet.crud.config.visitor.DatasourceNameCollectingVisitor;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfo;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfos;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DatasourceInfoView;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplication;

/**
 * Der Presenter der Datenquellen-Infoübersicht.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
@Configurable
public class DatasourceInfoPresenter implements ComponentPresenter {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(DatasourceInfoPresenter.class);

	static final String XA_MANAGED_CONNECTION_FACTORY_CLASS_NAME = "org.jboss.resource.adapter.jdbc.xa.XAManagedConnectionFactory";

	static final String LOCAL_MANAGED_CONNECTION_FACTORY_CLASS_NAME = "org.jboss.resource.adapter.jdbc.local.LocalManagedConnectionFactory";

	static final String MANAGED_CONNECTION_FACTORY_CLASS_ATTRIBUTE = "ManagedConnectionFactoryClass";

	static final String GET_MANAGED_CONNECTION_FACTORY_ATTRIBUTE_OPERATION = "getManagedConnectionFactoryAttribute";

	static final String XA_MCF_DATA_SOURCE_PROPERTIES_ATTRIBUTE = "XADataSourceProperties";

	static final String LOCAL_MCF_CONNECTION_URL_ATTRIBUTE = "ConnectionURL";

	static final String LOCAL_MCF_USER_NAME_ATTRIBUTE = "UserName";

	static final String XA_PROPERTIES_CONNECTION_URL = "URL";

	static final String XA_PROPERTIES_USER_NAME = "User";

	private PortletConfig config;
	private boolean modelNeedsUpdate;

	@Autowired
	private Settings settings;

	private MBeanServerConnection mbeanServer;

	private final DatasourceInfoView view;
	private final DatasourceInfos model;

	/**
	 * Erzeugt eine neue Instanz dieses Presenters.
	 */
	public DatasourceInfoPresenter(DatasourceInfoView view,
			DatasourceInfos model) {
		this.model = model;
		this.view = view;
		findJBossMBeanServer();
	}

	private void findJBossMBeanServer() {
		ObjectName systemServerMBeanName;
		try {
			systemServerMBeanName = new ObjectName("jboss.system:type=Server");
			ArrayList<MBeanServer> servers = MBeanServerFactory
					.findMBeanServer(null);
			for (MBeanServer server : servers) {
				if (server.isRegistered(systemServerMBeanName)) {
					mbeanServer = server;
					break;
				}
			}
			if (mbeanServer == null && servers.size() > 0) {
				mbeanServer = servers.get(0);
			}
		} catch (MalformedObjectNameException e) {
			throw new TechnicalCrudPortletException("Wrong object name", e);
		}
	}

	@Override
	public DatasourceInfoView getView() {
		return this.view;
	}

	/**
	 * @param config
	 *            die Konfiguration.
	 */
	public void setPortletConfig(PortletConfig config) {
		this.config = config;
		this.modelNeedsUpdate = true;
	}

	/**
	 * Aktualisiert die Infos über die verwendeten Datenquellen.
	 */
	public void refresh() {
		if (this.config != null && this.modelNeedsUpdate) {
			DatasourceNameCollectingVisitor nameCollectingVisitor = new DatasourceNameCollectingVisitor();
			ConfigurationProcessor configurationProcessor = new ConfigurationProcessor(
					nameCollectingVisitor);
			configurationProcessor.traverse(this.config);

			this.updateModel(nameCollectingVisitor.getDatasourceNames());
		} else {
			this.model.clean();
		}
	}

	/**
	 * @param mBeanServerConnection
	 *            die MBeanServer Instanz.
	 */
	void setMbeanServer(MBeanServerConnection mBeanServerConnection) {
		this.mbeanServer = mBeanServerConnection;
	}

	void updateModel(Set<String> datasourceNames) {
		this.model.clean();

		for (String dsName : datasourceNames) {
			this.model.addInfo(this
					.getDatasourceInfo(new DatasourceInfo(dsName)));
		}
	}

	private DatasourceInfo getDatasourceInfo(DatasourceInfo info) {
		if (this.mbeanServer == null) {
			return info;
		}

		info.setJndiName(retrieveJndiName(info.getDatasourceName()));

		ObjectName mcfMbeanName;
		try {
			mcfMbeanName = new ObjectName(
					"jboss.as:subsystem=datasources,data-source="
							+ info.getJndiName());
			info.setConnectionURL((String) mbeanServer.getAttribute(
					mcfMbeanName, "connectionUrl"));
			info.setUserName((String) mbeanServer.getAttribute(mcfMbeanName,
					"userName"));

		} catch (InstanceNotFoundException e) {
			LOG.info("Failed to get attribute '"
					+ MANAGED_CONNECTION_FACTORY_CLASS_ATTRIBUTE
					+ "' from ManagedConnectionFactory of datasource '"
					+ info.getDatasourceName()
					+ "'! Is this portlet running on JBoss 7?");
			return info;
		} catch (Exception e) {
			throw new TechnicalCrudPortletException("Failed to get attribute '"
					+ MANAGED_CONNECTION_FACTORY_CLASS_ATTRIBUTE
					+ "' from ManagedConnectionFactory of datasource '"
					+ info.getDatasourceName() + "'!", e);
		}

		return info;
	}

	private String retrieveJndiName(String datasourceName) {
		long communityId = LiferayApplication.getCurrentApplication()
				.getCommunityId();
		String pattern = settings.getDatasourceInfoPattern(communityId);
		if (pattern == null) {
			return datasourceName;
		} else {
			return MessageFormat.format(pattern, datasourceName);
		}
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
