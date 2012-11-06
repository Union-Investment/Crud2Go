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
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;


public class NotificationProviderTest {

	@Mock
	private Window window;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldShowError() {
		NotificationProvider provider = new NotificationProvider(null, window, NotificationProvider.Type.ERROR);
		provider.doCall("Dies ist eine Fehlermeldung");
		
		verify(window).showNotification("Fehler", "Dies ist eine Fehlermeldung", Notification.TYPE_ERROR_MESSAGE);
	}
	
	@Test
	public void shouldShowWarning() {
		NotificationProvider provider = new NotificationProvider(null, window, NotificationProvider.Type.WARNING);
		provider.doCall("Dies ist eine Warnung");
		
		verify(window).showNotification("Warnung", "Dies ist eine Warnung", Notification.TYPE_WARNING_MESSAGE);
	}
	
	@Test
	public void shouldShowInfo() {
		NotificationProvider provider = new NotificationProvider(null, window, NotificationProvider.Type.INFO);
		provider.doCall("Dies ist eine Info");
		
		verify(window).showNotification(any(Notification.class));
	}
	
	@Test
	public void shouldShowWarningWehnNoTypeIsGiven() {
		NotificationProvider provider = new NotificationProvider(null, window, null);
		provider.doCall("Dies ist eine nicht definierte Meldung");
		
		verify(window).showNotification(any(Notification.class));
	}
}
