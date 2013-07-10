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

import static de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DatasourceInfoPresenter.MANAGED_CONNECTION_FACTORY_CLASS_ATTRIBUTE;
import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.setSpringApplicationContextMock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PageConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfo;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource.DatasourceInfos;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DatasourceInfoView;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class DatasourceInfoPresenterTest {

	@Mock
	private Settings settingsMock;

	@Mock
	private ApplicationContext ctxMock;
	@Mock
	private MessageSource messageSourceMock;

	private DatasourceInfos model;
	private DatasourceInfoPresenter presenter;

	@Rule
	public LiferayContext liferayContext = new LiferayContext("portletId",
			12345L);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.model = new DatasourceInfos();

		prepareMessageMocking();
		DatasourceInfoView view = new DatasourceInfoView(
				this.model.getContainer());
		this.presenter = new DatasourceInfoPresenter(view, this.model);

		prepareDatasourceInfoPatternMocking();

		PortletConfig config = new PortletConfig();
		PageConfig pageConfig = new PageConfig();
		TableConfig tableConfig = new TableConfig();
		DatabaseQueryConfig dbQueryConfig = new DatabaseQueryConfig();
		dbQueryConfig.setDatasource("Foo");
		tableConfig.setDatabaseQuery(dbQueryConfig);
		pageConfig.getElements().add(tableConfig);
		config.setPage(pageConfig);

		this.presenter.setPortletConfig(config);
	}

	private void prepareDatasourceInfoPatternMocking() {
		presenter.setSettings(settingsMock);
		when(settingsMock.getDatasourceInfoPattern(12345L)).thenReturn(
				"dev/{0}");
	}

	private void prepareMessageMocking() {
		setSpringApplicationContextMock(ctxMock);
		when(ctxMock.getBean(MessageSource.class))
				.thenReturn(messageSourceMock);
	}

	@Test
	public void shouldRefreshWithoutMBeanServerQuery() {
		this.presenter.refresh();

		Container container = this.model.getContainer();
		assertEquals(1, container.size());
		@SuppressWarnings("unchecked")
		DatasourceInfo info = ((BeanItem<DatasourceInfo>) container
				.getItem(container.getItemIds().iterator().next())).getBean();
		assertEquals("Foo", info.getDatasourceName());
	}

	@Test(expected = TechnicalCrudPortletException.class)
	public void refreshShouldThrowTechnicalExceptionOnUnexpectedErrors()
			throws Exception {
		MBeanServer mbeanServerMock = mock(MBeanServer.class);
		this.presenter.setMbeanServer(mbeanServerMock);

		ObjectName objectName = createDevFooDataSourceObjectName();

		when(mbeanServerMock.getAttribute(objectName, "connectionUrl"))
				.thenThrow(new MBeanException(new Exception("dummy")));

		this.presenter.refresh();
	}

	@Test
	public void refreshShouldIgnoreJmxDataOnInstanceNotFoundException()
			throws Exception {
		MBeanServer mbeanServerMock = mock(MBeanServer.class);
		this.presenter.setMbeanServer(mbeanServerMock);

		ObjectName objectName = createDevFooDataSourceObjectName();

		when(
				mbeanServerMock.getAttribute(objectName,
						MANAGED_CONNECTION_FACTORY_CLASS_ATTRIBUTE)).thenThrow(
				new InstanceNotFoundException());

		this.presenter.refresh();

		Container container = this.model.getContainer();
		assertEquals(1, container.size());
		@SuppressWarnings("unchecked")
		DatasourceInfo info = ((BeanItem<DatasourceInfo>) container
				.getItem(container.getItemIds().iterator().next())).getBean();
		assertEquals("Foo", info.getDatasourceName());
	}

	@Test
	public void refreshShouldIgnoreMissingJmxDataForURL() throws Exception {
		MBeanServer mbeanServerMock = mock(MBeanServer.class);
		this.presenter.setMbeanServer(mbeanServerMock);

		ObjectName objectName = createDevFooDataSourceObjectName();

		when(mbeanServerMock.getAttribute(objectName, "connectionUrl"))
				.thenThrow(new InstanceNotFoundException());

		this.presenter.refresh();

		Container container = this.model.getContainer();
		assertEquals(1, container.size());
		@SuppressWarnings("unchecked")
		DatasourceInfo info = ((BeanItem<DatasourceInfo>) container
				.getItem(container.getItemIds().iterator().next())).getBean();
		assertThat(info.getDatasourceName(), is("Foo"));
		assertThat(info.getUserName(), nullValue());
	}

	@Test
	public void refreshShouldReturnConnectionProperties() throws Exception {
		MBeanServer mbeanServerMock = mock(MBeanServer.class);
		this.presenter.setMbeanServer(mbeanServerMock);

		ObjectName objectName = createDevFooDataSourceObjectName();

		when(mbeanServerMock.getAttribute(objectName, "connectionUrl"))
				.thenReturn("url");
		when(mbeanServerMock.getAttribute(objectName, "userName")).thenReturn(
				"user");

		this.presenter.refresh();

		Container container = this.model.getContainer();
		assertEquals(1, container.size());
		@SuppressWarnings("unchecked")
		DatasourceInfo info = ((BeanItem<DatasourceInfo>) container
				.getItem(container.getItemIds().iterator().next())).getBean();
		assertEquals("Foo", info.getDatasourceName());
		assertEquals("user", info.getUserName());
		assertEquals("url", info.getConnectionURL());
	}

	private ObjectName createDevFooDataSourceObjectName()
			throws MalformedObjectNameException {
		ObjectName objectName = new ObjectName(
				"jboss.as:subsystem=datasources,data-source=dev/Foo");
		return objectName;
	}
}
