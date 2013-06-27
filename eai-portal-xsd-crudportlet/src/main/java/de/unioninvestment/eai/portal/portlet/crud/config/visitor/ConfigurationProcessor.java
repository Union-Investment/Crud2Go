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
package de.unioninvestment.eai.portal.portlet.crud.config.visitor;

import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ColumnsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ComponentConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.DialogConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormFieldConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.FormSelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PanelConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RegionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TabsConfig;

/**
 * 
 * Traversiert die Portlet konfiguration nach dem Visitor-Pattern.
 * 
 * @author siva.selvarajah
 */
public class ConfigurationProcessor {

	private final ConfigurationVisitor visitor;

	/**
	 * Konstruktor.
	 * 
	 * @param visitor
	 *            ConfigurationVisitor-Implementierung
	 */
	public ConfigurationProcessor(ConfigurationVisitor visitor) {
		this.visitor = visitor;
	}

	/**
	 * Traversiert durch die Model-Klasse.
	 * 
	 * @param portletConfig
	 *            Portlet-Model-Klasse
	 */
	public void traverse(PortletConfig portletConfig) {
		visitor.visit(portletConfig);

		traverseRoles(portletConfig.getRoles());

		if (portletConfig.getAuthentication() != null) {
			traverseAuthentication(portletConfig.getAuthentication());
		}

		if (portletConfig.getPage() != null) {
			traversePanel(portletConfig.getPage());
		} else if (portletConfig.getTabs() != null) {
			traverseTabs(portletConfig.getTabs());
		}
		traverseDialogs(portletConfig);

		if (portletConfig.getScript() != null) {
			visitLeaf(portletConfig.getScript());
		}
		visitor.visitAfter(portletConfig);
	}

	void traverseAuthentication(AuthenticationConfig authenticationConfig) {
		visitor.visit(authenticationConfig);
		traverseAuthenticationRealms(authenticationConfig);
		visitor.visitAfter(authenticationConfig);
	}

	private void traverseAuthenticationRealms(
			AuthenticationConfig authenticationConfig) {
		for (AuthenticationRealmConfig realmConfig : authenticationConfig
				.getRealm()) {
			visitLeaf(realmConfig);
		}
	}

	private void traverseDialogs(PortletConfig portletConfig) {
		for (DialogConfig dialog : portletConfig.getDialog()) {
			traversePanel(dialog);
		}
	}

	private void traverseRoles(RolesConfig roles) {
		if (roles != null) {
			visitor.visit(roles);
			for (RoleConfig role : roles.getRole()) {
				visitLeaf(role);
			}
			visitor.visitAfter(roles);
		}
	}

	/**
	 * Traversiert die Tabs.
	 * 
	 * @param tabs
	 *            Tabs-Konfiguration
	 */
	void traverseTabs(TabsConfig tabs) {
		visitor.visit(tabs);
		for (TabConfig tab : tabs.getTab()) {
			traversePanel(tab);
		}
		visitor.visitAfter(tabs);
	}

	/**
	 * Traversiert die Elemente in einem Panel.
	 * 
	 * @param panel
	 *            Panel-Konfiguration
	 */
	void traversePanel(PanelConfig panel) {
		visitor.visit(panel);
		for (ComponentConfig element : panel.getElements()) {
			traverseComponent(element);
		}
		visitor.visitAfter(panel);
	}

	/**
	 * Traversiert die Elemente in einer Componente.
	 * 
	 * @param component
	 *            Compoment-Konfiguration
	 */
	void traverseComponent(ComponentConfig component) {
		if (component instanceof TabsConfig) {
			traverseTabs((TabsConfig) component);
		} else if (component instanceof FormConfig) {
			traverseForm((FormConfig) component);
		} else if (component instanceof TableConfig) {
			traverseTable((TableConfig) component);
		} else if (component instanceof PanelConfig) {
			traversePanel((RegionConfig) component);
		} else {
			visitLeaf(component);
		}
	}

	/**
	 * Traversiert die Elemente in einer Form.
	 * 
	 * @param formConfig
	 *            Formular-Konfiguration
	 */
	void traverseForm(FormConfig formConfig) {
		visitor.visit(formConfig);
		traverseFormFields(formConfig);
		traverseFormActions(formConfig);
		visitor.visitAfter(formConfig);
	}

	private void traverseFormFields(FormConfig formConfig) {
		for (FormFieldConfig field : formConfig.getField()) {
			traverseFormField(field);
		}
	}

	private void traverseFormActions(FormConfig formConfig) {
		for (FormActionConfig action : formConfig.getAction()) {
			visitLeaf(action);
		}
	}

	void traverseFormField(FormFieldConfig config) {
		visitor.visit(config);
		FormSelectConfig selectConfig = config.getSelect();
		if (selectConfig != null) {
			// Insert a visit for the selectConfig if it should be handled by
			// the visitor
			visitLeaf(selectConfig.getQuery());
			// Insert a visitAfter for the selectConfig if it should be handled
			// by the visitor
		}
		visitor.visitAfter(config);
	}

	/**
	 * Traversiert die Elemente in einer Tabelle.
	 * 
	 * @param tableConfig
	 *            Tabellen-Konfiguration
	 */
	void traverseTable(TableConfig tableConfig) {
		visitor.visit(tableConfig);
		visitLeaf(tableConfig.getDatabaseQuery());
		visitLeaf(tableConfig.getDatabaseTable());
		visitLeaf(tableConfig.getScriptContainer());
		visitLeaf(tableConfig.getRestContainer());
		traverseTableColumns(tableConfig);
		traverseTableActions(tableConfig);
		visitor.visitAfter(tableConfig);
	}

	private void traverseTableColumns(TableConfig tableConfig) {
		ColumnsConfig columns = tableConfig.getColumns();
		if (columns != null) {
			visitor.visit(columns);
			for (ColumnConfig column : columns.getColumn()) {
				visitLeaf(column);
			}
			visitor.visitAfter(columns);
		}
	}

	private void traverseTableActions(TableConfig tableConfig) {
		for (TableActionConfig action : tableConfig.getAction()) {
			visitLeaf(action);
		}
	}

	private void visitLeaf(Object leafConfig) {
		if (leafConfig != null) {
			visitor.visit(leafConfig);
			visitor.visitAfter(leafConfig);
		}
	}

}
