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

/**
 * Presenter für den Tab-Container.
 * 
 * @author markus.bonsch
 * 
 */
@Configurable
public class TabsPresenter extends AbstractComponentPresenter<Tabs, TabsView>
		implements TabsView.Presenter, ValueChangeListener {

	private static final long serialVersionUID = 2L;

	/**
	 * 
	 * @param tabsView
	 *            TabsView
	 * @param tabs
	 *            Tabs
	 */
	public TabsPresenter(TabsView tabsView, Tabs tabs) {
		super(tabsView, tabs);
		getView().initialize(this);
		if (tabs.getActiveTabProperty() != null) {
			tabs.getActiveTabProperty().addListener(this);
		}
	}

	/**
	 * 
	 * @param panelPresenter
	 *            PanelPresenter
	 */
	public void addPanel(PanelPresenter panelPresenter) {
		getView().addTabView(panelPresenter.getView());
	}

	@Override
	public void viewSelectionChanged(int pos) {
		getModel().setActiveTabById(getModel().getElements().get(pos).getId());
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Tab tabName = (Tab) event.getProperty().getValue();
		for (Tab tab : getModel().getElements()) {
			if (tab == tabName) {
				getView().changeTab(getModel().indexOf(tab));
			}
		}
	}

}
