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

import com.vaadin.server.*;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

/**
 * Code from SpringVaadinPortlet, modified by our custom
 * {@link CrudVaadinPortletService}.
 * 
 * @author carsten.mjartan
 */
public class CrudVaadinPortlet extends VaadinPortlet {

	public static final String PORTLET_TITLE_PREF_KEY = "ui-portlet-title";
	private static final long serialVersionUID = 1L;

	@Override
	protected VaadinPortletService createPortletService(
			DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {

		CrudVaadinPortletService service = new CrudVaadinPortletService(this,
				deploymentConfiguration);
		service.init();

		// initializePlugin(service);
		return service;
	}

    @Override
    protected void portletInitialized() throws PortletException {
        super.portletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) {
                event.getSession().addBootstrapListener(new LoadingIndicatorBootstrapListener());
            }
        });
    }

	/**
	 * Return the portlet title from the portlet preferences,
	 */
	@Override
	protected String getTitle(RenderRequest request) {
		PortletPreferences prefs = request.getPreferences();
		String portletTitle = prefs.getValue(PORTLET_TITLE_PREF_KEY, null);
		if (portletTitle != null) {
			return portletTitle;
		}
		return super.getTitle(request);
	}
}
