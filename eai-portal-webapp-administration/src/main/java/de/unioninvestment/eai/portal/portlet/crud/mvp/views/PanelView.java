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

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Beschreibt die Erwartungen des {@link PanelPresenter}s an die View.
 * 
 * 
 * Die PanelView ist eine Wrapper-View, die es ermöglicht, den Inhalt eines
 * Panels komplett auszutauschen (z. B. für Dialoge). Im Gegensatz dazu, ist die
 * PanelContentView dafür zuständig, die einzelnen Element, die die betreffende
 * Seite enthält, anzuzeigen.
 * 
 * @see PanelContentView
 * @see DefaultPanelView
 */
public interface PanelView extends View {

	/**
	 * Setzt den anzuzeigenden Inhalt. Dies sollte eine Page, ein Tab oder ein
	 * Dialog sein.
	 * 
	 * @param content
	 *            der Inhalt, der angezeigt werden soll
	 */
	public void setContent(Component content);

}