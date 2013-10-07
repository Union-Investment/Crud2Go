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

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class PortletUtilsTest {

	@Mock
	private MessageSource messageSourceMock;
	@Mock
	private ApplicationContext applicationContextMock;
	@Mock
	private PortletURL portletUrlMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void initializeApplication() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);

		applicationContextMock = mock(ApplicationContext.class);
		messageSourceMock = mock(MessageSource.class);
	}

	@Test
	public void testSwitchPortletMode() throws MalformedURLException {
		when(
				((ResourceResponse) liferayContext.getPortletResponseMock())
						.createRenderURL()).thenReturn(portletUrlMock);
		boolean result = PortletUtils.switchPortletMode(PortletMode.VIEW);
		assertTrue(result);
	}

	@Test
	public void testSwitchPortletModeInvalidState()
			throws MalformedURLException, IllegalStateException,
			PortletModeException {

		doThrow(new IllegalStateException()).when(
				(ResourceResponse) liferayContext.getPortletResponseMock())
				.createRenderURL();
		boolean result = PortletUtils.switchPortletMode(PortletMode.VIEW);
		assertFalse(result);
	}

}
