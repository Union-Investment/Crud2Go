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
package de.unioninvestment.eai.portal.portlet.test.commons;

import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.vaadin.ui.Window;

import de.unioninvestment.eai.portal.portlet.crud.CrudPortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public abstract class VaadinViewTest extends SpringPortletContextTest {

	@Spy
	protected Window windowSpy = new Window();
	@Mock
	protected ResourceResponse responseMock;
	@Mock
	protected ResourceRequest requestMock;

	@Mock
	protected PortletPreferences preferencesMock;

	protected CrudPortletApplication app;

	@Before
	public void setupVaadinView() {
		MockitoAnnotations.initMocks(this);
		when(requestMock.getPreferences()).thenReturn(preferencesMock);

		app = new CrudPortletApplication();
		app.onRequestStart(requestMock, responseMock);
		app.setMainWindow(windowSpy);
		windowSpy.setApplication(app);

		getView().setParent(app.getMainWindow());
		getView().attach();
	}

	@After
	public void cleanUpVaadinView() {
		app.onRequestEnd(requestMock, responseMock);
	}

	protected abstract View getView();

}
