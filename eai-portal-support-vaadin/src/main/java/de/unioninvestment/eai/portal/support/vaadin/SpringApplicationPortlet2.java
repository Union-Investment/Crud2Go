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

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationPortlet2;

/**
 * Erweitert die {@link ApplicationPortlet2} um Spring-Funktionalität.
 * 
 * @author max.hartmann
 * 
 */
public class SpringApplicationPortlet2 extends ApplicationPortlet2 {
	@Override
	protected Application getNewApplication(PortletRequest request)
			throws PortletException {

		try {
			SpringPortletApplication.registerWithSpring(request);
			return super.getNewApplication(request);

		} finally {
			SpringPortletApplication.unregisterWithSpring(request);
		}
	}

	/**
	 * Self-Contained WAR.
	 * 
	 * Not really fitting here, but less inheritance. see
	 * <code>https://vaadin.com/wiki/-/wiki/Main/Portlet%20with%20custom%20Widgetset%20and%20Theme</code>
	 */
	@Override
	protected String getStaticFilesLocation(PortletRequest request) {
		return request.getContextPath();
	}
}
