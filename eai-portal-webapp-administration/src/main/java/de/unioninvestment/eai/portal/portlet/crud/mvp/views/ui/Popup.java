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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;

/**
 * Modales Popup-Fenster.
 * 
 * 
 * @author siva.selvarajah
 */
public class Popup extends Window {
	private static final long serialVersionUID = 8920938721798736919L;
	private Label messageLabel;

	/**
	 * Konstruktor.
	 * 
	 * @param title
	 *            Titel
	 * @param message
	 *            Meldungstext
	 * @param contentType
	 *            Ob die Message Plaintext oder XHTML enthält
	 */
	public Popup(String title, String message, int contentType) {
		init();
		this.setCaption(title);

		switch (contentType) {
		case ShowPopupEvent.CONTENT_TYPE_PLAIN:
			messageLabel.setContentMode(Label.CONTENT_PREFORMATTED);
			break;
		case ShowPopupEvent.CONTENT_TYPE_XHTML:
			messageLabel.setContentMode(Label.CONTENT_XHTML);
			break;
		default:
			messageLabel.setContentMode(Label.CONTENT_DEFAULT);
			break;
		}

		messageLabel.setValue(message);
	}

	private void init() {
		this.setModal(true);
		this.setHeight(325, Window.UNITS_PIXELS);
		this.setWidth(500, Window.UNITS_PIXELS);

		VerticalLayout layout = (VerticalLayout) this.getContent();
		layout.setSizeFull();
		layout.setSpacing(true);
		layout.setMargin(true);

		messageLabel = new Label();
		Panel panel = new Panel();
		panel.setHeight(100, Window.UNITS_PERCENTAGE);
		panel.addStyleName(Runo.PANEL_LIGHT);
		panel.setScrollable(true);
		panel.addComponent(messageLabel);
		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1);

		Button close = new Button("Beenden", new Button.ClickListener() {
			private static final long serialVersionUID = -8385641161488292715L;

			public void buttonClick(ClickEvent event) {
				(Popup.this.getParent()).removeWindow(Popup.this);
			}
		});

		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.BOTTOM_RIGHT);
	}
}
