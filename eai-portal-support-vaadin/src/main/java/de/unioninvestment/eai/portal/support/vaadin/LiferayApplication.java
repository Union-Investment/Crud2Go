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

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * Unterklasse von PortletApplication für Liferay, die Auskunft über die
 * Portlet-ID und die Community-ID gibt.
 * 
 * @author carsten.mjartan
 */
public abstract class LiferayApplication extends PortletApplication {

	private static final long serialVersionUID = 1L;

	public static LiferayApplication getCurrentApplication() {
		return (LiferayApplication) PortletApplication.getCurrentApplication();
	}

	private String portletId;
	private long communityId = -1;

	@Override
	public final void init() {
		PortletRequest currentRequest = PortletApplication.getCurrentRequest();
		if (currentRequest != null) {
			portletId = (String) currentRequest
					.getAttribute(WebKeys.PORTLET_ID);

			ThemeDisplay themeDisplay = (ThemeDisplay) currentRequest
					.getAttribute(WebKeys.THEME_DISPLAY);
			communityId = themeDisplay.getScopeGroupId();
		}
		doInit();
	}

	/**
	 * @return the Liferay portlet id
	 */
	public String getPortletId() {
		return portletId;
	}

	/**
	 * @return die Liferay Community ID
	 */
	public long getCommunityId() {
		return communityId;
	}

	/**
	 * Von Unterklasse zu implementieren
	 */
	public abstract void doInit();
}
