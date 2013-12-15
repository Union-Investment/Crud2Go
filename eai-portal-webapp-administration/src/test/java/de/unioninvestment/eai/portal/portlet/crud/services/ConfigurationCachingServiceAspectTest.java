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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationDao;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;

public class ConfigurationCachingServiceAspectTest {

	@InjectMocks
	private ConfigurationCachingServiceAspect cachingAspect = new ConfigurationCachingServiceAspect();;

	@Mock
	private Ehcache cacheMock;

	@Mock
	private Settings settingsMock;

	@Mock
	private ConfigurationDao configurationDaoMock;

	@Mock
	private ProceedingJoinPoint jpMock;

	@Mock
	private Config configMock;

	@Mock
	private Config newerConfigMock;

	@Captor
	private ArgumentCaptor<Element> elementCaptor;

	private Date lastUpdateDate;

	private Date newerUpdateDate;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(settingsMock.isCacheEnabled()).thenReturn(true);
		when(settingsMock.isCacheCheckForUpdates()).thenReturn(false);

		lastUpdateDate = new Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(lastUpdateDate);
		cal.add(Calendar.MINUTE, 1);
		newerUpdateDate = cal.getTime();
	}

	@Test
	public void shouldStoreConfigInCache() throws Throwable {
		when(cacheMock.get("testPortletId.4711")).thenReturn(null);
		when(jpMock.proceed()).thenReturn(configMock);

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), is(configMock));

		verify(cacheMock).put(elementCaptor.capture());
		assertThat(elementCaptor.getValue().getObjectKey(),
				is((Object) "testPortletId.4711"));
		assertThat(elementCaptor.getValue().getObjectValue(),
				is((Object) configMock));
	}

	@Test
	public void shouldCallDelegateIfCacheIsDisabled() throws Throwable {
		when(settingsMock.isCacheEnabled()).thenReturn(false);
		when(jpMock.proceed()).thenReturn(configMock);

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), is(configMock));

		verifyZeroInteractions(cacheMock);
	}

	@Test
	public void shouldReturnCachedConfig() throws Throwable {
		when(cacheMock.get("testPortletId.4711")).thenReturn(
				new Element("testPortletId.4711", configMock));

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), is(configMock));

		verifyZeroInteractions(jpMock);
	}

	@Test
	public void shouldReturnCachedConfigIfNotUpdatedInDatabase()
			throws Throwable {
		when(settingsMock.isCacheCheckForUpdates()).thenReturn(true);
		when(cacheMock.get("testPortletId.4711")).thenReturn(
				new Element("testPortletId.4711", configMock));

		when(configMock.getLastUpdated()).thenReturn(lastUpdateDate);
		when(configurationDaoMock.readConfigMetaData("testPortletId", 4711L))
				.thenReturn(
						new ConfigurationMetaData("myuser", null,
								lastUpdateDate, null));

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), is(configMock));

		verifyZeroInteractions(jpMock);
	}

	@Test
	public void shouldReturnNewConfigIfUpdatedInDatabase() throws Throwable {
		when(settingsMock.isCacheCheckForUpdates()).thenReturn(true);
		when(cacheMock.get("testPortletId.4711")).thenReturn(
				new Element("testPortletId.4711", configMock));
		when(jpMock.proceed()).thenReturn(newerConfigMock);

		when(configMock.getLastUpdated()).thenReturn(lastUpdateDate);
		when(configurationDaoMock.readConfigMetaData("testPortletId", 4711L))
				.thenReturn(
						new ConfigurationMetaData("myuser", null,
								newerUpdateDate, null));

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), is(newerConfigMock));
	}

	@Test
	public void shouldReturnNullIfRemovedInDatabase() throws Throwable {
		when(settingsMock.isCacheCheckForUpdates()).thenReturn(true);
		when(cacheMock.get("testPortletId.4711")).thenReturn(
				new Element("testPortletId.4711", configMock));
		when(jpMock.proceed()).thenReturn(null);

		when(configMock.getLastUpdated()).thenReturn(lastUpdateDate);
		when(configurationDaoMock.readConfigMetaData("testPortletId", 4711L))
				.thenReturn(null);

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId",
				4711L), nullValue());
	}
}
