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

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Fabrik, die {@link View}'s zurückliefert.
 * 
 * @author max.hartmann
 * 
 */
@Component
@Scope("session")
public class ViewFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Liefert eine {@link PortletView} zurück.
	 * 
	 * @return PortletView
	 */
	public PortletView portletView() {
		return new DefaultPortletView();
	}

	/**
	 * Liefert eine {@link PanelView} zurück.
	 * 
	 * @return eine {@link PanelView}
	 */
	public PanelView panelView() {
		return new DefaultPanelView();
	}

	/**
	 * Liefert eine {@link PanelContentView} zurück.
	 * 
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll
	 * @return PanelView
	 */
	public PanelContentView panelContentView(boolean withMargin) {
		return new DefaultPanelContentView(withMargin);
	}

	/**
	 * Liefert eine {@link TableView} zurück.
	 * 
	 * @return TableView
	 */
	public TableView tableView() {
		return new DefaultTableView();
	}

	/**
	 * Liefert eine {@link FormView} zurück.
	 * 
	 * @return FormView
	 */
	public FormView formView() {
		return new DefaultFormView();
	}

	/**
	 * Liefert eine {@link PortletConfigurationView} zurück.
	 * 
	 * @return PortletConfigurationView
	 */
	public PortletConfigurationView portletConfigurationView() {
		return new DefaultPortletConfigurationView();
	}

	/**
	 * Liefert eine {@link TabsView}.
	 * 
	 * @return eine {@link TabsView}
	 */
	public TabsView getTabsView() {
		return new DefaultTabsView();
	}

	/**
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll
	 * @param table
	 *            Tabelle
	 * @return die Komponente fürs Editieren im Formularmodus
	 */
	public RowEditingFormView rowEditingFormView(boolean withMargin) {
		return new DefaultRowEditingFormView(withMargin);
	}

	/**
	 * @return das Widget für Custom Components
	 */
	public CustomComponentView customComponentView() {
		return new DefaultCustomComponentView();
	}

	/**
	 * @return eine Instanz einer Standard Region-View.
	 */
	public RegionView regionView() {
		return new DefaultRegionView();
	}

	/**
	 * @return eine Instanz einer auf- und zuklappbaren Region-View.
	 */
	public CollapsibleRegionView collapsibleRegionView() {
		return new DefaultCollapsibleRegionView();
	}
}
