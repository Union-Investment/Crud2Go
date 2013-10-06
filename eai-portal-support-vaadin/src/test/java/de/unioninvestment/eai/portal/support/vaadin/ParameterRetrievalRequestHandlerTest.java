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

package de.unioninvestment.eai.portal.support.vaadin;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class ParameterRetrievalRequestHandlerTest {

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Mock
	private VaadinSession vaadinSessionMock;

	@Mock
	private VaadinRequest vaadinRequestMock;

	@Mock
	private VaadinResponse vaadinResponseMock;

	@Mock
	private RenderRequest renderRequestMock;

	@Mock
	private Portal portalMock;

	@Mock
	private HttpServletRequest servletRequestMock;

	@Mock
	private HttpServletRequest originalServletRequestMock;

	private ParameterRetrievalRequestHandler handler;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		handler = new ParameterRetrievalRequestHandler();

		new PortalUtil().setPortal(portalMock);

		when(liferayContext.getVaadinPortletRequestMock().getPortletRequest())
				.thenReturn(renderRequestMock);
		when(portalMock.getHttpServletRequest(renderRequestMock)).thenReturn(
				servletRequestMock);
		when(portalMock.getOriginalServletRequest(servletRequestMock))
				.thenReturn(originalServletRequestMock);
	}

	@Test
	public void shouldIgnoreNonRenderRequest() throws IOException {
		when(liferayContext.getVaadinPortletRequestMock().getPortletRequest())
		.thenReturn(liferayContext.getPortletRequestMock());

		handler.handleRequest(vaadinSessionMock, vaadinRequestMock,
				vaadinResponseMock);

		verifyZeroInteractions(vaadinSessionMock);
	}

	@Test
	public void shouldRetrieveParametersFromRenderRequest() throws IOException {
		Map<String, String[]> parameters = singletonMap("name",
				new String[] { "Test" });
		when(originalServletRequestMock.getParameterMap()).thenReturn(
				parameters);
		
		handler.handleRequest(vaadinSessionMock, vaadinRequestMock,
				vaadinResponseMock);
		
		verify(vaadinSessionMock).setAttribute("parameters", parameters);
	}
	
	@Test
	public void shouldIgnoreLiferayInternalParameters() throws IOException {
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		String[] anyValue = new String[] { "Test" };
		parameters.put("name", anyValue);
		parameters.put("v_test", anyValue);
		parameters.put("p_test", anyValue);
		when(originalServletRequestMock.getParameterMap()).thenReturn(
				parameters);

		handler.handleRequest(vaadinSessionMock, vaadinRequestMock,
				vaadinResponseMock);

		verify(vaadinSessionMock).setAttribute("parameters",
				singletonMap("name", anyValue));
	}

}
