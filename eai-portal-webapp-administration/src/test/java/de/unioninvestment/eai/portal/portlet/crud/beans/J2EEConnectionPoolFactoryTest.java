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
package de.unioninvestment.eai.portal.portlet.crud.beans;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class J2EEConnectionPoolFactoryTest {

	@Mock
	private Settings settings;

	@Mock
	private Map<String, J2EEConnectionPool> pools;

	private J2EEConnectionPoolFactory poolFactory = new J2EEConnectionPoolFactory();

	@Rule
	public LiferayContext liferayContext = new LiferayContext("id", 12345L);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(pools.get("eai")).thenReturn(null);

		when(settings.getDatasourcePattern(12345L)).thenReturn(
				"java:/comp/env/jdbc/{0}Ds");

		poolFactory.setPools(pools);
		poolFactory.setSettings(settings);
	}

	@Test
	public void shouldGetPool() {
		J2EEConnectionPool pool = poolFactory.getPool("eai");

		verify(pools).put("eai_12345", pool);
	}

	@Test
	public void shouldApplyPatternByCommunityId() {
		J2EEConnectionPool pool = poolFactory.getPool("eai");

		assertThat(pool.getJndiName(), equalTo("java:/comp/env/jdbc/eaiDs"));
	}
}
