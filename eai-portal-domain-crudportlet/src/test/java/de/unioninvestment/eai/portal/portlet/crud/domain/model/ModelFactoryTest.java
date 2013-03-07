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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory;

@RunWith(MockitoJUnitRunner.class)
public class ModelFactoryTest {

	private ModelFactory factory = new ModelFactory();

	@Mock
	private ConnectionPoolFactory connectionPoolFactoryMock;

	@Mock
	private ConnectionPool connectionPoolMock;

	private HashMap<String, String> displayPattern = new HashMap<String, String>();

	@Mock
	private EventBus eventBus;

	@Mock
	private AuthenticationRealmConfig realmConfigMock;

	@Mock
	private ExecutorService prefetchExecutorMock;

	@Mock
	private ResetFormAction resetFormActionMock;

	@Mock
	private FieldValidatorFactory fieldValidatorFactoryMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		factory = new ModelFactory(connectionPoolFactoryMock,
				prefetchExecutorMock, resetFormActionMock,
				fieldValidatorFactoryMock, 20);
	}

	@Test
	public void shouldCreateDatabaseTableContainer() {
		when(connectionPoolFactoryMock.getPool("eai")).thenReturn(
				connectionPoolMock);

		DatabaseTableContainer container = factory.getDatabaseTableContainer(
				eventBus,
				"eai", "test_table", true, true, true, null, displayPattern,
				null, null, 0, 0, 0);

		assertThat(container, notNullValue());
	}

}
