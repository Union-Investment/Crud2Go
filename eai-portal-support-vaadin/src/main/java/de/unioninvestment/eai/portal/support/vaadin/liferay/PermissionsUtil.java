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
package de.unioninvestment.eai.portal.support.vaadin.liferay;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.vaadin.server.VaadinPortletService;

/**
 * Util-Klasse zum Erstellen der URL zum Ändern der Berechtigungen.
 * 
 * 
 * @author siva.selvarajah
 */
public abstract class PermissionsUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(PermissionsUtil.class);

	/**
	 * Erzeugt einen Link zum Ändern der Berechtigungen.
	 * 
	 * @param modelResource
	 *            modelResource
	 * @param resourcePrimKey
	 *            resourcePrimKey
	 * @param modelResourceDescription
	 *            modelResourceDescription
	 * @return URL
	 */
	public static String buildURL(String modelResource, String resourcePrimKey,
			String modelResourceDescription) {

		PortletRequest portletRequest = VaadinPortletService
				.getCurrentPortletRequest();
		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(portletRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute("THEME_DISPLAY");
		Layout layout = themeDisplay.getLayout();

		PortletURL portletURL = PortletURLFactoryUtil.create(request, "86",
				layout.getPlid(), "RENDER_PHASE");

		try {
			if (themeDisplay.isStatePopUp()) {

				portletURL.setWindowState(LiferayWindowState.POP_UP);

			} else {
				portletURL.setWindowState(WindowState.MAXIMIZED);
			}
		} catch (WindowStateException e) {
			LOG.error(e.getMessage(), e);
		}
		portletURL.setParameter("struts_action",
				"/portlet_configuration/edit_permissions");
		portletURL.setParameter("portletResource", themeDisplay
				.getPortletDisplay().getId());
		portletURL.setParameter("modelResource", modelResource);
		portletURL.setParameter("modelResourceDescription",
				modelResourceDescription);
		portletURL.setParameter("resourcePrimKey", resourcePrimKey);

		String portletURLToString = portletURL.toString();

		return portletURLToString;
	}
}
