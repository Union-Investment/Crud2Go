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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.util.Set;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.support.vaadin.PortletUtils;

/**
 * View-Objekt der CRUD PortletPresenter Konfiguration (PortletPresenter Edit Mode).
 * 
 * @author markus.bonsch
 * @author polina.vinarski
 * 
 */
public class DefaultPortletConfigurationView extends VerticalLayout implements PortletConfigurationView {

    private static final long serialVersionUID = 1L;

    private Upload upload;

    private HorizontalLayout uploadVcsLayout = new HorizontalLayout();

    private TextField uploadVcsUri;

    private Button uploadVcsButton;

    private Label status;

    private VerticalLayout security = new VerticalLayout();

    /**
     * Konstruktor für die View der Portletkonfigurationsseite.
     */
    public DefaultPortletConfigurationView() {
        setSpacing(true);
        status = new Label();
        status.setContentMode(Label.CONTENT_RAW);

        upload = new Upload();
        upload.setImmediate(true);
        upload.setButtonCaption(getMessage("portlet.crud.page.upload.button"));

        uploadVcsUri = new TextField();
        uploadVcsUri.setWidth(400, Sizeable.UNITS_PIXELS);
        uploadVcsButton = new Button();
        uploadVcsButton.setCaption(getMessage("portlet.crud.page.upload.vcs.button"));

        uploadVcsLayout.addComponent(uploadVcsButton);
        uploadVcsLayout.addComponent(uploadVcsUri);
        uploadVcsLayout.setSpacing(true);

        addComponent(status);

        addComponent(uploadVcsLayout);
        addComponent(upload);

        addComponent(security);
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
    public void showNotification(String msgKey, int type) {
        getApplication().getMainWindow().showNotification(msgKey, type);
    }

    @Override
    public void setStatus(String string, Object... args) {
        if (args == null || args.length < 1) {
            status.setValue(getMessage(string));
        } else {
            status.setValue(getMessage(string, args));
        }

    }

    Label getStatusLable() {
        return status;
    }

    @Override
    public void displaySecurity(Set<Role> roles) {
        security.removeAllComponents();
        if (roles.size() > 0) {
            security.addComponent(new Label(PortletUtils.getMessage("portlet.crud.page.edit.securityHeader")));
            for (Role role : roles) {
                ExternalResource res = new ExternalResource(role.getPermissionsURL());
                security.addComponent(new Link(role.getName(), res));
            }

        } else {
            security.addComponent(new Label(PortletUtils.getMessage("portlet.crud.page.edit.notSecuredMessage")));
        }
    }

    @Override
    public void hideSecurity() {
        security.removeAllComponents();
    }

    VerticalLayout getSecurity() {
        return security;
    }

}
