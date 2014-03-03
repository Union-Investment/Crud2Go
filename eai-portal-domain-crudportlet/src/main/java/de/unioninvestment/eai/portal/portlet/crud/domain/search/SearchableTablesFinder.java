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

package de.unioninvestment.eai.portal.portlet.crud.domain.search;

import java.util.ArrayList;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.config.SearchTableConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SearchTablesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Form;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tabs;

public class SearchableTablesFinder {

	/**
	 * Durchläuft des Modelbaum und liefert alle für die Suche nötigen
	 * Tabellenkomponenten.
	 * 
	 * @param startingPoint
	 *            - die Aktuelle Komponente als Ausgangspunkt für die Suche
	 * @return - eine Liste aller relevanten Tabellen für die Suche.
	 */
	public List<Table> findSearchableTables(Component startingPoint,
			SearchTablesConfig tables) {
		if (tables != null) {
			return findConfiguredTables(startingPoint, tables);
		} else {
			List<Table> result = new ArrayList<Table>();
			findSearchableTables(startingPoint.getPanel(), startingPoint,
					result);
			return result;
		}
	}

	private List<Table> findConfiguredTables(Component startingPoint,
			SearchTablesConfig tables) {
		List<Table> result = new ArrayList<Table>();
		Portlet portlet = startingPoint.getPanel().getPortlet();

		for (SearchTableConfig searchTableConfig : tables.getTable()) {
			TableConfig tableConfig = (TableConfig) searchTableConfig.getId();
			Table table = (Table) portlet.getElementById(tableConfig.getId());
			result.add(table);
		}
		return result;
	}

	private void findSearchableTables(Panel panel,
			Component startingWithElement, List<Table> result) {
		Component nextElement = panel.findNextElement(Component.class,
				startingWithElement);
		while (nextElement != null && !(nextElement instanceof Form)) {
			if (nextElement instanceof Table) {
				result.add((Table) nextElement);
			} else if (nextElement instanceof Tabs) {
				for (Tab tab : ((Tabs) nextElement).getElements()) {
					findSearchableTables(tab, null, result);
				}
			}
			nextElement = panel.findNextElement(Component.class, nextElement);
		}
	}

}
