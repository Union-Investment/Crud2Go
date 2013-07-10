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

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

/**
 * Darstellung einer Meldung, dass das Portlet noch zu konfigurieren ist. Es
 * wird außerdem ein Link auf den Portlet-EDIT-Modus hinterlegt.
 * 
 * @author carsten.mjartan
 */
public class UnconfiguredMessage extends CustomComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 */
	public UnconfiguredMessage() {
		String message = getMessage("portlet.crud.info.unconfigured");
		try {
			PortletResponse portletResponse = VaadinPortletService
					.getCurrentResponse().getPortletResponse();
			PortletURL url = ((MimeResponse) portletResponse).createRenderURL();
			url.setPortletMode(PortletMode.EDIT);
			ExternalResource editUrl = new ExternalResource(url.toString());
			setCompositionRoot(new Link(message, editUrl));

		} catch (PortletModeException e) {
			// not allowed
			setCompositionRoot(new Label(message));
		}
	}
}
