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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import net.sf.ehcache.Ehcache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;

public class ConfigurationCachingServiceAspectTest {

	@InjectMocks
	private ConfigurationCachingServiceAspect cachingAspect = new ConfigurationCachingServiceAspect();;

	@Mock
	private Ehcache cacheMock;

	@Mock
	private Settings settingsMock;

	@Mock
	private ProceedingJoinPoint jpMock;

	@Mock
	private Config configMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCacheReturnConfig() throws Throwable {
		when(cacheMock.get("testPortletId")).thenReturn(null);
		when(settingsMock.isCacheEnabled()).thenReturn(true);
		when(jpMock.proceed()).thenReturn(configMock);

		assertThat((Config) cachingAspect.findInCache(jpMock, "testPortletId"),
				is(configMock));
	}

}
