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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * View-Objekt der CRUD PortletPresenter Konfiguration (PortletPresenter Edit
 * Mode).
 * 
 * @author markus.bonsch
 * @author polina.vinarski
 * 
 */
public class DefaultPortletConfigurationView extends VerticalLayout implements
		PortletConfigurationView {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultPortletConfigurationView.class);

	private Upload upload;

	private HorizontalLayout uploadVcsLayout = new HorizontalLayout();

	private TextField uploadVcsUri;

	private Button uploadVcsButton;

	private Label status;

	VerticalLayout rolesLayout;

	VerticalLayout authenticationPreferencesLayout;

	private Presenter presenter;

	TabSheet tabsheet;

	/**
	 * Konstruktor für die View der Portletkonfigurationsseite.
	 */
	public DefaultPortletConfigurationView() {
		tabsheet = new TabSheet();

		VerticalLayout statusLayout = createStatusLayout();
		tabsheet.addComponent(statusLayout);

		addComponent(tabsheet);
	}

	private VerticalLayout createStatusLayout() {

		status = new Label();
		status.setContentMode(Label.CONTENT_RAW);

		upload = new Upload();
		upload.setImmediate(true);
		upload.setButtonCaption(getMessage("portlet.crud.page.upload.button"));

		uploadVcsUri = new TextField();
		uploadVcsUri.setWidth(400, Sizeable.UNITS_PIXELS);
		uploadVcsButton = new Button();
		uploadVcsButton
				.setCaption(getMessage("portlet.crud.page.upload.vcs.button"));

		uploadVcsLayout.addComponent(uploadVcsButton);
		uploadVcsLayout.addComponent(uploadVcsUri);
		uploadVcsLayout.setSpacing(true);

		VerticalLayout statusLayout = new VerticalLayout();
		statusLayout
				.setCaption(getMessage("portlet.crud.page.edit.statusHeader"));
		statusLayout.setSpacing(true);
		statusLayout.setMargin(true);

		statusLayout.addComponent(status);
		statusLayout.addComponent(uploadVcsLayout);
		statusLayout.addComponent(upload);
		return statusLayout;
	}

	public Button getUploadVcsButton() {
		return uploadVcsButton;
	}

	public TextField getUploadVcsUri() {
		return uploadVcsUri;
	}

	public Upload getUpload() {
		return upload;
	}

	@Override
	public void setStatus(String string, Object... args) {
		if (args == null || args.length < 1) {
			status.setValue(getMessage(string));
		} else {
			status.setValue(getMessage(string, args));
		}
	}

	Label getStatusLabel() {
		return status;
	}

	@Override
	public void displayTab(View view) {
		tabsheet.addComponent(view);
	}

	@Override
	public void removeTab(View view) {
		tabsheet.removeComponent(view);
	}

	@Override
	public void switchTo(View view) {
		tabsheet.setSelectedTab(view);
	}

	@Override
	public void showError(String message) {
		Notification.show(message, Type.ERROR_MESSAGE);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
