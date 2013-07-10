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

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;

public class CrudVaadinPortletService extends VaadinPortletService {

	private static final long serialVersionUID = 1L;

	public CrudVaadinPortletService(VaadinPortlet portlet,
			DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		super(portlet, deploymentConfiguration);
	}

	// /**
	// * Move the PortletUIInitHandler to the last(first after reverse) position
	// * so that the UI instance is available to portlet listeners.
	// */
	// @Override
	// protected List<RequestHandler> createRequestHandlers()
	// throws ServiceException {
	// List<RequestHandler> handlers = super.createRequestHandlers();
	// Iterator<RequestHandler> it = handlers.iterator();
	// while (it.hasNext()) {
	// RequestHandler handler = it.next();
	// if (handler instanceof PortletUIInitHandler) {
	// it.remove();
	// handlers.add(handler);
	// break;
	// }
	// }
	// return handlers;
	// }

	/**
	 * Self-Contained WAR.
	 * 
	 * @see <a
	 *      href="https://vaadin.com/wiki/-/wiki/Main/Portlet%20with%20custom%20Widgetset%20and%20Theme">
	 *      https://vaadin.com/wiki/-/wiki/Main/Portlet%20with%20custom%20Widgetset%20and%20Theme</a>
	 *      (Vaadin 6 specific)
	 */
	@Override
	public String getStaticFileLocation(VaadinRequest request) {
		return getCurrentRequest().getContextPath();
	}
}
