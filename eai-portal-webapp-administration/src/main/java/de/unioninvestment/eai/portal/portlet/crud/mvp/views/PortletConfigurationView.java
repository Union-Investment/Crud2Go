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

import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * View für die PortletPresenter-Konfigurationsseite.
 */
public interface PortletConfigurationView extends View {

    /**
     * @return Upload Vaadin Komponente.
     */
    Upload getUpload();

    /**
     * Erstellt auf der Anzeige einen entsprechenden Hinweis.
     * 
     * @param msgKey Message Key für die Anzeige.
     * @param type Notification Type {@link Notification}
     */
    void showNotification(String msgKey, int type);

    /**
     * Dient der Anzeige von Statusinformationen.
     * 
     * @param msgKey Message Key
     * @param args Argumente für das Message Key
     */
    void setStatus(String msgKey, Object... args);

    /**
     * Zeigt die Links zum setzten der Berechtigungen.
     * 
     * @param roles Rollen
     */
    void displaySecurity(Set<Role> roles);

    /**
     * Entfernt die Links zum setzten der Berechtigungen.
     */
    void hideSecurity();

    /**
     * Startet Upload der Konfiguration aus VCS
     * 
     * @return Button Vaadin Komponente.
     */
    Button getUploadVcsButton();

    /**
     * Dient der Eingabe von Upload-URL
     * 
     * @return TextField Vaadin Komponente.
     */
    TextField getUploadVcsUri();
}