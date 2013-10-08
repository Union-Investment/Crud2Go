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
package de.unioninvestment.eai.portal.portlet.crud;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.RowEditingFormDialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TextArea;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.ComponentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.CustomComponentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.DialogPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.FormPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelContentPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PortletPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PresenterFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.RegionPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.RowEditingFormPresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TablePresenter;
import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.TabsPresenter;

/**
 * Erstellt und initialisiert anhand einer gegebenen
 * PortletPresenter-Konfiguration die entsprechenden Domain-Objekte.
 * 
 * @author carsten.mjartan
 */
public class GuiBuilder implements Serializable {

	private static final long serialVersionUID = 1L;

	private final PresenterFactory factory;

	/**
	 * @param factory
	 *            die für die Instanzierung zu verwendende
	 *            {@link PresenterFactory}
	 */
	@Autowired
	public GuiBuilder(PresenterFactory factory) {
		this.factory = factory;
	}

	/**
	 * Baut den PortletPresenter für das Portlet.
	 * 
	 * @param portlet
	 *            Repräsentation eines Portletmodells
	 * @return eine PortletPresenter-Instanz
	 */
	public PortletPresenter build(Portlet portlet) {

		PortletPresenter portletPresenter = factory.portletPresenter(portlet);
		Map<String, DialogPresenter> dialogPresenterMap = buildDialogPresenters(
				portlet, portletPresenter);
		if (portlet.getPage() != null) {
			PanelContentPresenter panelContentPresenter = buildPanelContentPresenter(
					portlet.getPage(), dialogPresenterMap);
			PanelPresenter panelPresenter = buildPanelPresenter(
					portlet.getPage(), dialogPresenterMap);
			panelPresenter.setDefaultPresenter(panelContentPresenter);
			portletPresenter.setPage(panelPresenter);
		} else {
			TabsPresenter tabsPresenter = buildTabsPresenter(portlet.getTabs(),
					dialogPresenterMap);
			portletPresenter.setTabs(tabsPresenter);
		}
		return portletPresenter;
	}

	private TabsPresenter buildTabsPresenter(Tabs tabs,
			Map<String, DialogPresenter> dialogPresenterMap) {
		TabsPresenter tabsPresenter = factory.tabsPresenter(tabs);

		for (Tab tab : tabs.getElements()) {
			PanelContentPresenter panelContentPresenter = buildPanelContentPresenter(
					tab, dialogPresenterMap);
			PanelPresenter panelPresenter = buildPanelPresenter(tab,
					dialogPresenterMap);
			panelPresenter.setDefaultPresenter(panelContentPresenter);
			tabsPresenter.addPanel(panelPresenter);
		}
		return tabsPresenter;
	}

	private PanelPresenter buildPanelPresenter(Panel panel,
			Map<String, DialogPresenter> dialogPresenterMap) {
		return factory.panelPresenter(panel, dialogPresenterMap);
	}

	private PanelContentPresenter buildPanelContentPresenter(Panel panel,
			Map<String, DialogPresenter> dialogPresenterMap) {
		PanelContentPresenter panelPresenter = factory
				.panelContentPresenter(panel);
		addChildComponentPresenter(panel, dialogPresenterMap, panelPresenter);
		return panelPresenter;
	}

	private void addChildComponentPresenter(Panel panel,
			Map<String, DialogPresenter> dialogPresenterMap,
			PanelContentPresenter panelContentPresenter) {
		for (Component element : panel.getElements()) {
			ComponentPresenter presenter = buildComponentPresenter(element,
					dialogPresenterMap);
			panelContentPresenter.addComponent(presenter);

			if (element instanceof Table
					&& ((Table) element).isFormEditEnabled()) {

				Table table = (Table) element;
				RowEditingFormPresenter rowEditingFormDialogPresenter = buildRowEditingFormDialogPresenter(
						table, panel, dialogPresenterMap,
						(TablePresenter) presenter);

				((TablePresenter) presenter)
						.setRowEditingFormPresenter(rowEditingFormDialogPresenter);
			}
		}
	}

	private RowEditingFormPresenter buildRowEditingFormDialogPresenter(
			Table table, Panel parentPanel,
			Map<String, DialogPresenter> dialogPresenterMap,
			TablePresenter tablePresenter) {

		RowEditingFormDialog rowEditingFormDialog = new RowEditingFormDialog(
				table.getId(), "Zurück");

		String dialogId = String.valueOf(table.hashCode());
		RowEditingFormPresenter presenter = factory.rowEditingFormPresenter(
				rowEditingFormDialog, parentPanel, dialogId, table,
				tablePresenter);

		dialogPresenterMap.put(dialogId, presenter);

		return presenter;
	}

	private Map<String, DialogPresenter> buildDialogPresenters(Portlet portlet,
			PortletPresenter portletPresenter) {
		Map<String, DialogPresenter> dialogPresenterMap = new HashMap<String, DialogPresenter>();
		Collection<Dialog> dialogList = portlet.getDialogsById().values();
		if (dialogList != null && !dialogList.isEmpty()) {
			for (Dialog dialog : dialogList) {
				DialogPresenter dialogPresenter = buildDialogPresenter(dialog,
						dialogPresenterMap);
				dialogPresenterMap.put(dialog.getId(), dialogPresenter);
			}
		}
		return dialogPresenterMap;
	}

	private DialogPresenter buildDialogPresenter(Dialog dialog,
			Map<String, DialogPresenter> dialogPresenterMap) {
		DialogPresenter dialogPresenter = factory.dialogPresenter(dialog);
		addChildComponentPresenter(dialog, dialogPresenterMap, dialogPresenter);
		return dialogPresenter;
	}

	private RegionPresenter buildRegionPresenter(Region region,
			Map<String, DialogPresenter> dialogPresenterMap) {
		RegionPresenter regionPresenter = this.factory.regionPresenter(region);
		this.addChildComponentPresenter(region, dialogPresenterMap,
				regionPresenter);
		return regionPresenter;
	}

	private ComponentPresenter buildComponentPresenter(Component element,
			Map<String, DialogPresenter> dialogPresenterMap) {
		if (element instanceof Form) {
			return buildFormPresenter((Form) element);
		} else if (element instanceof TextArea) {
			return buildTextAreaPresenter((TextArea) element);
		} else if (element instanceof Table) {
			return buildTablePresenter((Table) element);
		} else if (element instanceof CustomComponent) {
			return buildCustomComponentPresenter((CustomComponent) element);
		} else if (element instanceof Tabs) {
			return buildTabsPresenter((Tabs) element, dialogPresenterMap);
		} else if (element instanceof Region) {
			return buildRegionPresenter((Region) element, dialogPresenterMap);
		}
		return null;
	}

	private ComponentPresenter buildTextAreaPresenter(TextArea element) {
		return factory.textAreaPresenter(element);
	}

	private CustomComponentPresenter buildCustomComponentPresenter(
			CustomComponent customComponent) {
		return factory.customComponentPresenter(customComponent);
	}

	private FormPresenter buildFormPresenter(Form form) {
		return factory.formPresenter(form);
	}

	private TablePresenter buildTablePresenter(Table table) {
		return factory.tablePresenter(table);
	}
}
