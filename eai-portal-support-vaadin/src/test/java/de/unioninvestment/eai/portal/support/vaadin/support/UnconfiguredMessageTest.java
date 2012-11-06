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
package de.unioninvestment.eai.portal.support.vaadin.support;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.portlet.MockPortalContext;
import org.springframework.mock.web.portlet.MockPortletURL;

import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import de.unioninvestment.eai.portal.support.vaadin.test.commons.SupportVaadinSpringPortletContextTest;

@Ignore
public class UnconfiguredMessageTest extends SupportVaadinSpringPortletContextTest {

	@Mock
	private PortletRequest requestMock;

	@Mock
	private ResourceResponse responseMock;
	
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);

		when(responseMock.createRenderURL()).thenReturn(
				new MockPortletURL(new MockPortalContext(), "myurl"));
	}

	
	@Test
	public void shouldContainLink() {
		UnconfiguredMessage msg = new UnconfiguredMessage();
		assertEquals(Link.class, msg.getComponentIterator().next().getClass());
	}

	@Test
	public void testCrudViewPageWhereLinkIsNotAllowed()
			throws PortletModeException {

		PortletURL urlMock = mock(PortletURL.class);
		when(responseMock.createRenderURL()).thenReturn(urlMock);
		doThrow(new PortletModeException("E", PortletMode.EDIT)).when(urlMock)
				.setPortletMode(PortletMode.EDIT);

		UnconfiguredMessage msg = new UnconfiguredMessage();
		assertEquals(Label.class, msg.getComponentIterator().next().getClass());
	}
}
