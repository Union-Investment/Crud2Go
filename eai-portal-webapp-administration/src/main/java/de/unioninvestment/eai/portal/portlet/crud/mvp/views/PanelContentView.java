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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DialogPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelContentPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Beschreibt die Erwartungen des {@link PanelContentPresenter}s an die View.
 * 
 * Die PanelContentViewl kann ein Seite (XML-Tag {@code page}), ein Tab (XML-Tag
 * {@code tab}) oder ein Dialog (XML-Tag {@code dialog}, siehe
 * {@link DialogPresenter}) repräsentieren. Diese View ist dafür zuständig, die
 * einzelnen Element, die die betreffende Seite enthält, anzuzeigen. Im
 * Gegensatz dazu ist die {@link PanelView} eine Wrapper-View, die es
 * ermöglicht, den Inhalt eines Panels komplett auszutauschen (z. B. für
 * Dialoge).
 * 
 * @see PanelView
 * @see DefaultPanelContentView
 */
public interface PanelContentView extends View {

	/**
	 * Fügt dem Panel eine Vaadin-Komponente hinzu.
	 * 
	 * @param component
	 *            der hinzuzufügende Seiteninhalt
	 */
	void addComponent(Component component);

	/**
	 * Aktiviert, Deaktiviert margin für das Panel
	 * 
	 * @param enabled
	 *            Aktiv-Flag
	 */
	void setMargin(boolean enabled);

	/**
	 * Display back button.
	 * 
	 * @param caption
	 * @param clickListener
	 */
	Button addBackButton(String caption, ClickListener clickListener);

	/**
	 * Set the expand ration of an existing subcomponent
	 * 
	 * @param component
	 *            the subcomponent
	 * @param expandRatio
	 *            the expand ratio of the subcomponent
	 */
	void setExpandRatio(Component component, float expandRatio);

	/**
	 * @param useHorizontalLayout
	 *            <code>true</code>, if the view should arrange subcomponents
	 *            horizontally
	 */
	void initialize(boolean useHorizontalLayout);

	@Override
	void setWidth(String newWidth);

	@Override
	void setHeight(String newHeight);

	/**
	 * Tells the view to update the height to fill the browser window
	 * 
	 * @param minimumHeight
	 *            the minimum height of the view or <code>null</code>
	 */
	void setHeightToFitScreen(Integer minimumHeight);
}