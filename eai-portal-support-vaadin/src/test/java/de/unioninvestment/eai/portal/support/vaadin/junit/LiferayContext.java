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
package de.unioninvestment.eai.portal.support.vaadin.junit;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinPortletResponse;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import de.unioninvestment.eai.portal.support.vaadin.CrudVaadinPortletService;
import de.unioninvestment.eai.portal.support.vaadin.LiferayUI;

@SuppressWarnings("deprecation")
public class LiferayContext extends TestWatchman {

	private String portletId;
	private long communityId;

	@Mock
	private LiferayUI uiMock;
	@Mock
	private Page pageMock;
	@Mock
	private VaadinPortletResponse vaadinPortletResponseMock;
	@Mock
	private ResourceResponse portletResponseMock;
	@Mock
	private VaadinPortletRequest vaadinPortletRequestMock;
	@Mock
	private ResourceRequest portletRequestMock;
	@Mock
	private PortletSession portletSessionMock;
	@Mock
	private PortletContext portletContextMock;
	@Mock
	private PortletPreferences portletPreferencesMock;
	@Mock
	private VaadinPortletSession vaadinPortletSessionMock;
	@Mock
	private CrudVaadinPortletService vaadinPortletServiceMock;
	@Mock
	private ConnectorTracker connectorTrackerMock;
	@Mock
	private JavaScript javascriptMock;

	public LiferayContext() {
		initialize();
	}

	public LiferayContext(String portletId, long communityId) {
		this.portletId = portletId;
		this.communityId = communityId;

		initialize();
	}

	private void initialize() {
		MockitoAnnotations.initMocks(this);

		UI.setCurrent(uiMock);
		if (portletId != null) {
			when(uiMock.getPortletId()).thenReturn(portletId);
			when(uiMock.getCommunityId()).thenReturn(communityId);
		}

		when(uiMock.getPage()).thenReturn(pageMock);
		when(uiMock.getConnectorTracker()).thenReturn(connectorTrackerMock);

		when(pageMock.getJavaScript()).thenReturn(javascriptMock);

		CurrentInstance.set(VaadinResponse.class, vaadinPortletResponseMock);
		when(vaadinPortletResponseMock.getPortletResponse()).thenReturn(
				portletResponseMock);

		CurrentInstance.set(VaadinRequest.class, vaadinPortletRequestMock);
		when(vaadinPortletRequestMock.getPortletRequest()).thenReturn(
				portletRequestMock);
		when(portletRequestMock.getPortletSession()).thenReturn(
				portletSessionMock);
		when(portletSessionMock.getPortletContext()).thenReturn(
				portletContextMock);
		when(portletRequestMock.getPreferences()).thenReturn(
				portletPreferencesMock);
		when(vaadinPortletSessionMock.getConverterFactory()).thenReturn(
				new DefaultConverterFactory());
        when(vaadinPortletSessionMock.getPortletSession()).thenReturn(portletSessionMock);

		CurrentInstance.set(VaadinSession.class, vaadinPortletSessionMock);
		CurrentInstance.set(VaadinService.class, vaadinPortletServiceMock);
	}

	@Override
	public void finished(FrameworkMethod method) {
		CurrentInstance.clearAll();
	}

	public void noCurrentRequest() {
		finished(null);
	}

	public void shouldShowNotification(String caption, String description,
			Type type) {
		verify(pageMock).showNotification(
				argThat(new NotificationMatcher(caption, description)));
	}

	public void shouldNotShowNotification(String caption, String description,
			Type type) {
		verify(pageMock, never()).showNotification(
				argThat(new NotificationMatcher(caption, description)));
	}
	
	public LiferayUI getUiMock() {
		return uiMock;
	}

	public Page getPageMock() {
		return pageMock;
	}

	public VaadinPortletResponse getVaadinPortletResponseMock() {
		return vaadinPortletResponseMock;
	}

	public PortletResponse getPortletResponseMock() {
		return portletResponseMock;
	}

	public VaadinPortletRequest getVaadinPortletRequestMock() {
		return vaadinPortletRequestMock;
	}

	public PortletRequest getPortletRequestMock() {
		return portletRequestMock;
	}

	public PortletSession getPortletSessionMock() {
		return portletSessionMock;
	}

	public PortletContext getPortletContextMock() {
		return portletContextMock;
	}

	public PortletPreferences getPortletPreferencesMock() {
		return portletPreferencesMock;
	}

	public VaadinPortletSession getVaadinSessionMock() {
		return vaadinPortletSessionMock;
	}

	public CrudVaadinPortletService getVaadinPortletServiceMock() {
		return vaadinPortletServiceMock;
	}
}
