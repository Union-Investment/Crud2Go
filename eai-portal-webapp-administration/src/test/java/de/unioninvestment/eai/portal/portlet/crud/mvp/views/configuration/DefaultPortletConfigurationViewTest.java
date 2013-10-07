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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Notification.Type;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class DefaultPortletConfigurationViewTest extends
		SpringPortletContextTest {

	private DefaultPortletConfigurationView view;

	@Mock
	private PortletRole roleMock1;

	@Mock
	private PortletRole roleMock2;

	@Rule
	public LiferayContext liferayContext = new LiferayContext("portletId",
			18004L);

	@Before
	public void prepareMocks() {
		MockitoAnnotations.initMocks(this);

		view = new DefaultPortletConfigurationView();

		when(roleMock1.getName()).thenReturn("role1");
		when(roleMock1.getPermissionsURL()).thenReturn("http://test.de/role1");

		when(roleMock2.getName()).thenReturn("role2");
		when(roleMock2.getPermissionsURL()).thenReturn("http://test.de/role2");
	}

	@Test
	public void shouldShowStatusTextWithArgs() {
		Date date = new GregorianCalendar(2013,5,17,8,30,0).getTime();
		String user = "TestUser";
		view.setStatus("portlet.crud.page.status.config.available", user, date);
		assertThat(
				view.getStatusLabel().getValue(),
				is("<p>Die aktuelle Portlet-Konfiguration wurde am 17.06.13 08:30 von Benutzer: TestUser eingespielt.<br/>Version: {2}</p><p>Wollen sie diese überschreiben?</p>"));
	}

	@Test
	public void shouldShowStatusTextWithoutArgs() {
		view.setStatus("portlet.crud.page.status.config.notAvailable");
		assertThat(view.getStatusLabel().getValue(), is("Es existiert noch keine Konfiguration für dieses Portlet.</br>Hier können Sie eine gültige Konfiguration hochladen."));
	}

	@Test
	public void shouldShowWindowNotification() {
		view.showError("test.alert");
		liferayContext.shouldShowNotification("test.alert", null,
				Type.ERROR_MESSAGE);
	}

}
