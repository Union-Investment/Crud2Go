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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;

/**
 * 
 * Erweitert die {@link PortletApplication} um Spring-Funktionalität.
 * 
 * @author max.hartmann
 * 
 */
public abstract class SpringPortletApplication extends LiferayApplication {

	private static final long serialVersionUID = 1L;

	private static final String REQUEST_ATTRIBUTES_ATTRIBUTE = SpringPortletApplication.class
			.getName() + ".REQUEST_ATTRIBUTES";

	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		super.onRequestStart(request, response);

		registerWithSpring(request);
	}

	/**
	 * 
	 * @param request
	 *            PortletRequest
	 */
	static void registerWithSpring(PortletRequest request) {
		PortletRequestAttributes attributes = new PortletRequestAttributes(
				request);
		request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
		LocaleContextHolder.setLocale(request.getLocale());
		RequestContextHolder.setRequestAttributes(attributes);
	}

	@Override
	public void onRequestEnd(PortletRequest request, PortletResponse response) {
		unregisterWithSpring(request);

		super.onRequestEnd(request, response);
	}

	/**
	 * 
	 * @param request
	 *            PortletRequest
	 */
	static void unregisterWithSpring(PortletRequest request) {
		PortletRequestAttributes attributes = (PortletRequestAttributes) request
				.getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
		PortletRequestAttributes threadAttributes = (PortletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (threadAttributes != null) {
			// We're assumably within the original request thread...
			if (attributes == null) {
				attributes = threadAttributes;
			}
			LocaleContextHolder.resetLocaleContext();
			RequestContextHolder.resetRequestAttributes();
		}
		if (attributes != null) {
			attributes.requestCompleted();
		}
	}
}
