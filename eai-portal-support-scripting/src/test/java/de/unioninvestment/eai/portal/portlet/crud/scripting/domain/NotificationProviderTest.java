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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class NotificationProviderTest {

	@Mock
	private Window window;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Rule
	public LiferayContext vaadinContext = new LiferayContext();

	@Test
	public void shouldShowError() {
		NotificationProvider provider = new NotificationProvider(null,
				NotificationProvider.Type.ERROR);
		provider.doCall("Dies ist eine Fehlermeldung");

		verify(Page.getCurrent()).showNotification(any(Notification.class));
	}

	@Test
	public void shouldShowWarning() {
		NotificationProvider provider = new NotificationProvider(null,
				NotificationProvider.Type.WARNING);
		provider.doCall("Dies ist eine Warnung");

		verify(Page.getCurrent()).showNotification(any(Notification.class));
	}

	@Test
	public void shouldShowInfo() {
		NotificationProvider provider = new NotificationProvider(null,
				NotificationProvider.Type.INFO);
		provider.doCall("Dies ist eine Info");

		verify(Page.getCurrent()).showNotification(any(Notification.class));
	}

	@Test
	public void shouldShowWarningWehnNoTypeIsGiven() {
		NotificationProvider provider = new NotificationProvider(null, null);
		provider.doCall("Dies ist eine nicht definierte Meldung");

		verify(Page.getCurrent()).showNotification(any(Notification.class));
	}
}
