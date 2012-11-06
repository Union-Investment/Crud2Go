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

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TabsView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Presenter für den Tab-Container.
 * 
 * @author markus.bonsch
 * 
 */
@Configurable
public class TabsPresenter implements ComponentPresenter, TabsView.Presenter,
		ValueChangeListener {

	private static final long serialVersionUID = 1L;

	private TabsView view;
	private final Tabs tabs;

	/**
	 * 
	 * @param tabsView
	 *            TabsView
	 * @param tabs
	 *            Tabs
	 */
	public TabsPresenter(TabsView tabsView, Tabs tabs) {
		this.view = tabsView;
		this.tabs = tabs;
		view.initialize(this);
		if (tabs.getActiveTabProperty() != null) {
			tabs.getActiveTabProperty().addListener(this);
		}
	}

	@Override
	public View getView() {
		return view;
	}

	/**
	 * 
	 * @param panelPresenter
	 *            PanelPresenter
	 */
	public void addPanel(PanelPresenter panelPresenter) {
		view.addTabView(panelPresenter.getView());
	}

	@Override
	public void viewSelectionChanged(int pos) {
		tabs.setActiveTabById(tabs.getElements().get(pos).getId());
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Tab tabName = (Tab) event.getProperty().getValue();
		for (Tab tab : tabs.getElements()) {
			if (tab == tabName) {
				view.changeTab(tabs.indexOf(tab));
			}
		}
	}

}
