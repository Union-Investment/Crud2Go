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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.util.PortalUtil;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

public class ParameterRetrievalRequestHandler implements RequestHandler {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request,
			VaadinResponse response) throws IOException {
		PortletRequest portletRequest = VaadinPortletService.getCurrentPortletRequest();
		if (portletRequest instanceof RenderRequest) {
			Map<String, String[]> allParameters = getOriginalServletRequestParameters(portletRequest);
			Map<String, String[]> parameters = copyParametersIgnoreLiferayPrefix(allParameters);
			session.setAttribute("parameters", parameters);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String[]> getOriginalServletRequestParameters(PortletRequest portletRequest) {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
		HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		return originalRequest.getParameterMap();
	}

	private Map<String, String[]> copyParametersIgnoreLiferayPrefix(
			Map<String, String[]> originalParameters) {
		Map<String, String[]> parameters = new HashMap<String,String[]>();
		for (Entry<String,String[]> entry : originalParameters.entrySet()) {
			String key = entry.getKey();
			if (!(key.startsWith("v_") || key.startsWith("p_"))) {
				parameters.put(key, entry.getValue());
			}
		}
		return parameters;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String[]> getParameters() {
		return (Map<String, String[]>) VaadinSession.getCurrent().getAttribute("parameters");
	}

}
