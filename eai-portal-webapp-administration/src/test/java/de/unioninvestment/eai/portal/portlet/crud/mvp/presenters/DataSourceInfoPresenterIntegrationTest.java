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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.JMXWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfos;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DatasourceInfoView;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

/**
 * <pre>
 * Prerequisites for this test: 
 * - JBoss 5.1 with remote JMX enabled:
 * -- bin/run.jar needs org/jboss/system/server/jmx/LazyMBeanServer.class and MBeanServerBuilderImp.class from lib/jboss-system-jmx.jar
 * -- run.bat: set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote -Djboss.platform.mbeanserver  -Djavax.management.builder.initial=org.jboss.system.server.jmx.MBeanServerBuilderImpl
 * -- "dev/test" DataSource
 * 
 * </pre>
 * 
 * @author carsten.mjartan
 */
public class DataSourceInfoPresenterIntegrationTest extends
		SpringPortletContextTest {

	@Mock
	private Settings settingsMock;

	private DatasourceInfos model;
	private DatasourceInfoPresenter presenter;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		this.model = new DatasourceInfos();
		DatasourceInfoView view = new DatasourceInfoView(
				this.model.getContainer());
		this.presenter = new DatasourceInfoPresenter(view, this.model);

		presenter.setSettings(settingsMock);
		when(settingsMock.getDatasourceInfoPattern(12345L)).thenReturn(
				"dev/{0}");

		PortletConfig config = new PortletConfig();
		PageConfig pageConfig = new PageConfig();
		TableConfig tableConfig = new TableConfig();
		DatabaseQueryConfig dbQueryConfig = new DatabaseQueryConfig();
		dbQueryConfig.setDatasource("Foo");
		tableConfig.setDatabaseQuery(dbQueryConfig);
		pageConfig.getElements().add(tableConfig);
		config.setPage(pageConfig);

		JMXWrapper jmx = new JMXWrapper("127.0.0.1:1090");
		presenter.setMbeanServer(jmx.getServer());

		this.presenter.setPortletConfig(config);
	}

	@Test
	public void shouldRetrieveConnectionData() {
		presenter.updateModel(new HashSet(asList("test")));
	}
}
