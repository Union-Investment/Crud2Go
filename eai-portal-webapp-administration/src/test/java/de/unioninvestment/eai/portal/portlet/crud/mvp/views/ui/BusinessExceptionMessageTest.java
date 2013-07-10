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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.DefaultPortletConfigurationView;
import de.unioninvestment.eai.portal.portlet.test.commons.SpringPortletContextTest;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class BusinessExceptionMessageTest extends SpringPortletContextTest {

	private DefaultPortletConfigurationView view;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		view = new DefaultPortletConfigurationView();
	}

	@Test
	public void shouldPlaceMessagePropertie() {

		PortletURL portletUrl = mock(PortletURL.class);

		when(
				((ResourceResponse) liferayContext.getPortletResponseMock())
						.createRenderURL()).thenReturn(portletUrl);

		BusinessExceptionMessage exception = new BusinessExceptionMessage(
				new BusinessException("error.without.args"));
		assertEquals("test", exception.toString());
	}

	@Test
	public void shouldPlaceMessageProperties() {

		PortletURL portletUrl = mock(PortletURL.class);

		when(
				((ResourceResponse) liferayContext.getPortletResponseMock())
						.createRenderURL()).thenReturn(portletUrl);

		BusinessExceptionMessage exception = new BusinessExceptionMessage(
				new BusinessException("error.with.args", new Object[] {
						"param1", new Integer(123) }));
		assertEquals("arg1: param1, arg2: 123", exception.toString());
	}

}
