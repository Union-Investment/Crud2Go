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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.AbstractPresenter;

/**
 * Repräsentiert das PortletPresenter selbst.
 * 
 * @author carsten.mjartan
 */
public class PortletPresenter extends AbstractPresenter<PortletView> {

	private static final long serialVersionUID = 2L;

	private PanelPresenter mainPage;

	@SuppressWarnings("unused")
	private final Portlet model;

	private TabsPresenter mainTabs;

	/**
	 * Initialisiert die PortletPresenter-Instanz.
	 * 
	 * @param view
	 *            PortletView
	 * @param model
	 *            Portlet
	 */
	public PortletPresenter(PortletView view, Portlet model) {
		super(view);
		this.model = model;
	}

	/**
	 * @param pagePresenter
	 *            die Seite
	 */
	public void setPage(PanelPresenter pagePresenter) {
		if (mainTabs != null) {
			throw new IllegalStateException(
					"Es ist bereits ein Tabs-Element gesetzt, daher ist das Setzen eines Page-Elements nicht zulässig.");
		}
		this.mainPage = pagePresenter;
		getView().setContent(mainPage.getView());
	}

	public PanelPresenter getMainPage() {
		return mainPage;
	}

	/**
	 * 
	 * @param tabsPresenter
	 *            TabsPresenter
	 */
	public void setTabs(TabsPresenter tabsPresenter) {
		if (mainPage != null) {
			throw new IllegalStateException(
					"Es ist bereits ein Page-Element gesetzt, daher ist das Setzen eines Tabs-Elements nicht zulässig.");
		}
		mainTabs = tabsPresenter;
		getView().setContent(mainTabs.getView());

	}
}
