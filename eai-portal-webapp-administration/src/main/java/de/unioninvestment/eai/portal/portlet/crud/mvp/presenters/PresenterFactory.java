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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import de.unioninvestment.eai.portal.portlet.crud.GuiBuilder;
import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CompoundSearch;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.LazyInitializable;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TextArea;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration.PortletConfigurationPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.RowEditingFormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ViewFactory;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.Presenter;

/**
 * Fabrik, die {@link Presenter} zurückliefert.
 * 
 * @author carsten.mjartan
 */
@Configuration
@Lazy
@Scope("ui")
public class PresenterFactory {

	@Autowired
	private ViewFactory viewFactory;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private Settings settings;

	/**
	 * Liefert eine {@link GuiBuilder} zurück.
	 * 
	 * @return GuiBuilder
	 */
	@Bean
	@Lazy
	@Scope("ui")
	public GuiBuilder guiBuilder() {
		return new GuiBuilder(this);
	}

	/**
	 * Liefert eine {@link PortletPresenter} zurück.
	 * 
	 * @param portlet
	 *            Portlet-Modell
	 * 
	 * @return PortletPresenter
	 */
	public PortletPresenter portletPresenter(Portlet portlet) {
		return new PortletPresenter(viewFactory.portletView(), portlet);
	}

	/**
	 * Liefert eine {@link PanelPresenter} zurück.
	 * 
	 * @param panel
	 *            das Panel-Modell
	 * @param dialogPresenterMap
	 *            die Map mit allen Dialog-Presentern
	 * 
	 * @return {@link PanelPresenter}
	 */
	public PanelPresenter panelPresenter(Panel panel,
			Map<String, DialogPresenter> dialogPresenterMap) {
		return new PanelPresenter(viewFactory.panelView(), panel,
				dialogPresenterMap);
	}

	/**
	 * Liefert eine {@link PanelContentPresenter} zurück.
	 * 
	 * @param panel
	 *            Panel-Modell
	 * 
	 * @return PanelContentPresenter
	 */
	public PanelContentPresenter panelContentPresenter(Panel panel) {
		return new PanelContentPresenter(viewFactory.panelContentView(), panel);
	}

	/**
	 * Liefert einen {@link TextAreaPresenter} zurück.
	 * 
	 * @param textarea
	 *            TextArea-Modell
	 * 
	 * @return TextAreaPresenter
	 */
	public TextAreaPresenter textAreaPresenter(TextArea textarea) {
		return new TextAreaPresenter(viewFactory.textAreaView(
				textarea.getWidth(), textarea.getHeight()), textarea);
	}

	/**
	 * Liefert eine {@link DialogPresenter} zurück.
	 * 
	 * @param dialog
	 *            Panel-Modell
	 * 
	 * @return DialogPresenter
	 */
	public DialogPresenter dialogPresenter(Dialog dialog) {
		return new DialogPresenter(viewFactory.panelContentView(), dialog);
	}

	/**
	 * Liefert einen {@link CollapsibleRegionPresenter} zurück.
	 * 
	 * @param region
	 *            das Modell-Objekt.
	 * 
	 * @return die Presenter Instanz.
	 */
	public RegionPresenter regionPresenter(Region region) {
		RegionPresenter presenter = null;
		if (region.isCollapsible()) {
			presenter = new CollapsibleRegionPresenter(
					this.viewFactory.collapsibleRegionView(region.getWidth(),
							region.getHeight()), region);
		} else {
			presenter = new RegionPresenter(this.viewFactory.regionView(true,
					region.isHorizontalLayout(), region.getWidth(),
					region.getHeight()), region);
		}
		return presenter;
	}

	/**
	 * Liefert eine {@link FormPresenter} zurück.
	 * 
	 * @param form
	 *            Form-Modell
	 * 
	 * @return FormPresenter
	 */
	public FormPresenter formPresenter(Form form) {
		return new FormPresenter(viewFactory.formView(), form);
	}

	/**
	 * Liefert eine {@link TablePresenter} zurück.
	 * 
	 * @param table
	 *            Table-Modell
	 * 
	 * @return TablePresenter
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TablePresenter tablePresenter(Table table) {
		TablePresenter tablePresenter = new TablePresenter(
				viewFactory.tableView(), table);
		table.setPresenter(tablePresenter);

		if (table.getPanel() instanceof LazyInitializable) {
			((LazyInitializable) table.getPanel())
					.addShowEventListener(tablePresenter);
		} else {
			tablePresenter.initializeView();
		}

		return tablePresenter;
	}

	/**
	 * 
	 * @param tabs
	 *            Tabs
	 * @return Presenter der Tabs
	 */
	public TabsPresenter tabsPresenter(Tabs tabs) {
		return new TabsPresenter(viewFactory.getTabsView(tabs.getWidth(),
				tabs.getHeight()), tabs);
	}

	/**
	 * Liefert eine {@link PortletConfigurationPresenter} zurück.
	 * 
	 * @return PortletConfigurationPresenter
	 */
	public PortletConfigurationPresenter portletConfigurationPresenter() {
		return new PortletConfigurationPresenter(
				viewFactory.portletConfigurationView(), configurationService,
				eventBus, settings);
	}

	void setViewFactory(ViewFactory viewFactory) {
		this.viewFactory = viewFactory;
	}

	void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Liefert einen {@link RowEditingFormPresenter} zurück.
	 * 
	 * @param model
	 *            {@link Dialog}-Model. In diesem Falle nur für die
	 *            Back-Button-Caption.
	 * @param parentPanel
	 *            das Parent-{@link Panel} (die Tabelle) an den der Form-Dialog
	 *            attached wird.
	 * @param dialogId
	 *            die Id des Dialogs.
	 * @param tablePresenter
	 *            der dazugehörige {@link TablePresenter}
	 * @return
	 */
	public RowEditingFormPresenter rowEditingFormPresenter(Dialog model,
			Panel parentPanel, String dialogId, Table table,
			TablePresenter tablePresenter) {

		RowEditingFormView rowEditingFormView = viewFactory.rowEditingFormView(
				parentPanel instanceof Tab, parentPanel.isHorizontalLayout(),
				parentPanel.getWidth(), parentPanel.getHeight());

		RowEditingFormPresenter rowEditingFormPresenter = new RowEditingFormPresenter(
				rowEditingFormView, model, parentPanel, dialogId, table,
				tablePresenter);

		return rowEditingFormPresenter;
	}

	/**
	 * @param customComponent
	 *            das Backing-Model
	 * @return den {@link Presenter} für Custom Components
	 */
	public CustomComponentPresenter customComponentPresenter(
			CustomComponent customComponent) {
		return new CustomComponentPresenter(viewFactory.customComponentView(
				customComponent.getWidth(), customComponent.getHeight()),
				customComponent);
	}

	public CompoundSearchPresenter compoundSearchPresenter(
			CompoundSearch compoundSearch) {
		return new CompoundSearchPresenter(viewFactory.compoundSearchView(),
				compoundSearch);
	}
}
