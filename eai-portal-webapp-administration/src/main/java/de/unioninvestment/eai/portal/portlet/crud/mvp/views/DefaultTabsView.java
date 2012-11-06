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

import com.vaadin.ui.TabSheet;

import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Standart View Implementierung für Tabs.
 * 
 * @author markus.bonsch
 * 
 */
public class DefaultTabsView extends TabSheet implements TabsView {

	private static final long serialVersionUID = 1L;

	private Presenter presenter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTabView(View view) {
		addComponent(view);
	}

	@Override
	public void initialize(final Presenter tabsPresenter) {
		this.presenter = tabsPresenter;

		this.addListener(new TabSheet.SelectedTabChangeListener() {
			private static final long serialVersionUID = 42L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				presenter.viewSelectionChanged(getSelectedTabIndex());
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeTab(int newIndex) {
		setSelectedTab(getTab(newIndex).getComponent());
	}

	/**
	 * 
	 * @return Index des selektierten Tabs.
	 */
	int getSelectedTabIndex() {
		Tab selectedTab = getTab(getSelectedTab());
		return getTabPosition(selectedTab);
	}
}
