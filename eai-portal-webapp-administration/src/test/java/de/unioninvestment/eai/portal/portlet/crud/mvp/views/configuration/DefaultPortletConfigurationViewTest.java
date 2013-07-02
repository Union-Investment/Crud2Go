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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.test.commons.VaadinViewTest;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class DefaultPortletConfigurationViewTest extends VaadinViewTest {

	DefaultPortletConfigurationView view;

	@Mock
	private PortletRole roleMock1;

	@Mock
	private PortletRole roleMock2;

	@Before
	public void prepareMocks() {
		when(roleMock1.getName()).thenReturn("role1");
		when(roleMock1.getPermissionsURL()).thenReturn("http://test.de/role1");

		when(roleMock2.getName()).thenReturn("role2");
		when(roleMock2.getPermissionsURL()).thenReturn("http://test.de/role2");
	}

	@Override
	protected View getView() {
		if (view == null)
			view = new DefaultPortletConfigurationView();
		return view;
	}

	@Test
	public void shouldShowStatusTextWithArgs() {
		Date date = new Date();
		String user = "TestUser";
		view.setStatus("portlet.crud.page.status.config.available", user, date);
		assertEquals(PortletUtils.getMessage(
				"portlet.crud.page.status.config.available", user, date), view
				.getStatusLabel().getValue());
	}

	@Test
	public void shouldShowStatusTextWithoutArgs() {
		view.setStatus("portlet.crud.page.status.config.notAvailable");
		assertEquals(
				PortletUtils
						.getMessage("portlet.crud.page.status.config.notAvailable"),
				view.getStatusLabel().getValue());
	}

	@Test
	public void shouldShowWindowNotification() {
		view.showNotification("test.alert", Notification.TYPE_ERROR_MESSAGE);
		verify(windowSpy).showNotification("test.alert",
				Notification.TYPE_ERROR_MESSAGE);
	}

	private boolean tabsheetContains(String caption) {
		return tabsheetComponent(caption) != null;
	}

	private Component tabsheetComponent(String caption) {
		Iterator<Component> iterator = view.tabsheet.getComponentIterator();
		while (iterator.hasNext()) {
			Component component = iterator.next();
			if (caption.equals(component.getCaption())) {
				return component;
			}
		}
		return null;
	}

}
