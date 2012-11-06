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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;

public class PortletUtilsTest {

	private URL applicationUrl;
	private TestPortletApplication application;
	private PortletRequest requestMock;
	private PortletResponse responseMock;
	private PortletSession sessionMock;
	private PortletContext contextMock;
	private PortletApplicationContext2 vaadinContextMock;
	private MessageSource messageSourceMock;
	private ApplicationContext applicationContextMock;

	@Before
	public void initializeApplication() throws MalformedURLException {
		applicationUrl = new URL("http://xxx");
		vaadinContextMock = mock(PortletApplicationContext2.class);
		requestMock = mock(RenderRequest.class);
		responseMock = mock(RenderResponse.class);
		sessionMock = mock(PortletSession.class);
		contextMock = mock(PortletContext.class);

		applicationContextMock = mock(ApplicationContext.class);
		messageSourceMock = mock(MessageSource.class);

		when(requestMock.getPortletSession()).thenReturn(sessionMock);
		when(sessionMock.getPortletContext()).thenReturn(contextMock);

		application = new TestPortletApplication();
		application.onRequestStart(requestMock, responseMock);
		application.start(applicationUrl, null, vaadinContextMock);
	}

	@After
	public void resetAppCtx() {
		PortletUtils.setSpringApplicationContextMock(null);
	}

	@Test
	public void testSwitchPortletMode() throws MalformedURLException {
		boolean result = PortletUtils.switchPortletMode(application,
				PortletMode.VIEW);
		assertTrue(result);
	}

	@Test
	public void testSwitchPortletModeInvalidState()
			throws MalformedURLException, IllegalStateException,
			PortletModeException {

		doThrow(new IllegalStateException()).when(vaadinContextMock)
				.setPortletMode(application.getMainWindow(), PortletMode.VIEW);
		boolean result = PortletUtils.switchPortletMode(application,
				PortletMode.VIEW);
		assertFalse(result);
	}

	@Test
	public void testSwitchPortletModeIllegalPortletMode()
			throws MalformedURLException, IllegalStateException,
			PortletModeException {

		doThrow(new PortletModeException("NO NO", PortletMode.VIEW)).when(
				vaadinContextMock).setPortletMode(application.getMainWindow(),
				PortletMode.VIEW);
		boolean result = PortletUtils.switchPortletMode(application,
				PortletMode.VIEW);
		assertFalse(result);
	}

	@Test
	public void shouldReturnValidMessage() throws MalformedURLException {
		when(applicationContextMock.getBean(MessageSource.class)).thenReturn(
				messageSourceMock);
		when(
				messageSourceMock.getMessage("portlet.crud.window.name", null,
						null)).thenReturn("message");
		PortletUtils.setSpringApplicationContextMock(applicationContextMock);

		String msg = PortletUtils.getMessage("portlet.crud.window.name");

		assertEquals("message", msg);
	}

	@Test
	public void shouldReturnValidParameterizedMessage()
			throws MalformedURLException {
		when(applicationContextMock.getBean(MessageSource.class)).thenReturn(
				messageSourceMock);
		when(messageSourceMock.getMessage("code", new Object[] { 1, 2 }, null))
				.thenReturn("1 < 2");
		PortletUtils.setSpringApplicationContextMock(applicationContextMock);

		String msg = PortletUtils.getMessage("code", 1, 2);

		assertEquals("1 < 2", msg);
	}

	@Test
	public void shouldReturnCodeAndParametersOnMissingApplicationContext() {
		assertThat(PortletUtils.getMessage("bla", 1), is("bla[1]"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetSpringApplicationContextNotAvailable() {
		PortletUtils.getSpringApplicationContext();
	}

	@Test(expected = IllegalStateException.class)
	public void testGetSpringApplicationContextNoCurrentRequest() {
		application.onRequestEnd(requestMock, responseMock);
		PortletUtils.getSpringApplicationContext();
	}

	@Test
	public void testGetSpringApplicationContextMock() {
		ApplicationContext context = new StaticApplicationContext();
		PortletUtils.setSpringApplicationContextMock(context);

		ApplicationContext result = PortletUtils.getSpringApplicationContext();
		assertEquals(context, result);
	}

	@Test
	public void testGetSpringApplicationContext() {
		ApplicationContext context = new StaticApplicationContext();
		when(
				contextMock
						.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(context);
		ApplicationContext result = PortletUtils.getSpringApplicationContext();
		assertSame(context, result);
	}

	@Test
	public void testGetBean() {
		StaticApplicationContext context = new StaticApplicationContext();
		context.registerSingleton("test", PortletUtilsTest.class);
		when(
				contextMock
						.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(context);

		PortletUtilsTest bean = PortletUtils.getBean(PortletUtilsTest.class);
		assertNotNull(bean);
	}
}
