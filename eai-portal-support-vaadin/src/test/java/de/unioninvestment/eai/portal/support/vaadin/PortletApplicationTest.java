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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.mock.web.portlet.MockPortletRequest;
import org.springframework.mock.web.portlet.MockPortletResponse;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class PortletApplicationTest {

	@Test
	public void testThreadLocals() {
		final MockPortletRequest request = new MockPortletRequest();
		final MockPortletResponse response = new MockPortletResponse();

		PortletApplication application = new PortletApplication() {
			@Override
			public void init() {
				assertEquals(request, PortletApplication.getCurrentRequest());
				assertEquals(response, PortletApplication.getCurrentResponse());
			}

			@Override
			public void addToView(Component component) {
				// ...
			}

			@Override
			public void removeAddedComponentsFromView() {
				// ...
			}
		};

		application.onRequestStart(request, response);
		application.init();
		application.onRequestEnd(request, response);

		assertNull(PortletApplication.getCurrentRequest());
		assertNull(PortletApplication.getCurrentResponse());
	}

}
