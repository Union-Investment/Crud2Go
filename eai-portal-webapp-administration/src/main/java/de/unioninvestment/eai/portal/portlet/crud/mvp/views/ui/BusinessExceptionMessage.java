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

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;

/**
 * Fehler Componente, welche angezeigt wird wenn eine BusinessException.
 * 
 * @author markus.bonsch
 * 
 */
public class BusinessExceptionMessage extends CustomComponent {
	private static final long serialVersionUID = 1L;

	/**
	 * Message Key für die Anzeige.
	 * 
	 * @param e
	 *            The BusinessException that is thrown.
	 */
	public BusinessExceptionMessage(BusinessException e) {
		String message = e.getMessage();
		buildUi(message);
	}

	/**
	 * Message Key für die Anzeige.
	 * 
	 * @param code
	 *            Der anzuzeigende Text als Message Key  
	 */
	public BusinessExceptionMessage(String code) {
		buildUi(getMessage(code));
	}

	/**
	 * Message Key für die Anzeige.
	 * 
	 * @param code
	 *            Der anzuzeigende Text als Message Key  
	 * @param args
	 *            liste von Message-Parametern
	 */
	public BusinessExceptionMessage(String code, Object[] args) {
		buildUi(getMessage(code, args));
	}

	private void buildUi(String message) {
		try {
			PortletURL url = ((MimeResponse) PortletApplication
					.getCurrentResponse()).createRenderURL();
			url.setPortletMode(PortletMode.EDIT);
			ExternalResource editUrl = new ExternalResource(url.toString());
			setCompositionRoot(new Link(message, editUrl));

		} catch (PortletModeException e1) {
			// not allowed
			setCompositionRoot(new Label(message));
		}
	}

	@Override
	public String toString() {
		return getCompositionRoot().getCaption();
	}
}
