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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.portlet.ValidatorException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.DefaultPreferencesView;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class PreferencesPresenterTest extends SpringPortletContextTest {

	private PreferencesPresenter presenter;

	@Mock
	private DefaultPreferencesView viewMock;

	@Mock
	private EventBus eventBusMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext("portletId",
			18004L);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		presenter = new PreferencesPresenter(viewMock, eventBusMock);
	}

	@Test
	public void shouldStorePreferences() throws ValidatorException, IOException {
		presenter.storePreferencesAndFireConfigChange();

		verify(liferayContext.getPortletPreferencesMock()).store();
	}

	@Test
	public void shouldFireConfigChange() throws ValidatorException, IOException {
		presenter.storePreferencesAndFireConfigChange();

		verify(eventBusMock).fireEvent(new ConfigurationUpdatedEvent(true));
	}

	@Test
	public void shouldShowNotificationOnStorageError()
			throws ValidatorException, IOException {
		doThrow(new ValidatorException(new RuntimeException(), asList("a")))
				.when(liferayContext.getPortletPreferencesMock()).store();

		presenter.storePreferencesAndFireConfigChange();

		verify(viewMock).showError(
				"Einstellungen konnten nicht gespeichert werden");
	}

}
