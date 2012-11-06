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
package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event Klasse für Popup-Windows im Portlet.
 * 
 * @author markus.bonsch
 * 
 */
public class ShowPopupEvent implements
		SourceEvent<ShowPopupEventHandler, ShowPopupEvent.PopupContent> {

	public static final int CONTENT_TYPE_PLAIN = 1;
	public static final int CONTENT_TYPE_XHTML = 2;

	private static final long serialVersionUID = 1L;
	private final String title;
	private final String source;
	private final int contentType;

	/**
	 * Event Konstruktor.
	 * 
	 * @param title
	 *            - Titel des Fensters
	 * @param source
	 *            - Anzeigeinhalt
	 * @param contentType
	 *            - Gibt den Typ des Textinhalts an (XHTML, plain Text).
	 */
	public ShowPopupEvent(String title, String source, int contentType) {
		this.title = title;
		this.source = source;
		this.contentType = contentType;
	}

	@Override
	public void dispatch(ShowPopupEventHandler eventHandler) {
		eventHandler.showPopup(this);
	}

	@Override
	public PopupContent getSource() {
		return new PopupContent(title, source, contentType);
	}

	/**
	 * Source des ShowPopupEvents.
	 * 
	 * @author markus.bonsch
	 * 
	 */
	public static class PopupContent {

		private final String title;
		private final String body;
		private final int contentType;

		/**
		 * Konstruktor.
		 * 
		 * @param title
		 *            - Titel des Fensters
		 * @param body
		 *            - Anzeigeinhalt
		 * @param contentType
		 *            - Gibt den Typ des Textinhalts an (XHTML, plain Text).
		 */
		protected PopupContent(String title, String body, int contentType) {
			super();
			this.title = title;
			this.body = body;
			this.contentType = contentType;
		}

		public String getTitle() {
			return title;
		}

		public String getBody() {
			return body;
		}

		public int getContentType() {
			return contentType;
		}
	}

}
