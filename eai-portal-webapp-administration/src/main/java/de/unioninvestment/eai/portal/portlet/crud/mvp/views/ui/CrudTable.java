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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.TableColumnSelectionContext;
import de.unioninvestment.eai.portal.support.vaadin.support.BufferedTable;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Erweiterte Vaadin Table für das CRUD-Portlet.
 * 
 * @author markus.bonsch
 * 
 */
public class CrudTable extends BufferedTable {
	private static final long serialVersionUID = 1L;

	private TableColumns columns;
	private final DataContainer dataContainer;

	/**
	 * Konstruktor.
	 * 
	 * @param databaseContainer
	 *            - Datencontainer
	 * @param columns
	 *            - TableColumns des Models
	 * @param enableSorting
	 *            - ob die Table im Spaltenkopf sortierbar ist.
	 * @param displayTypeHelpers
	 *            - Hilfsobjekte für die Darstellung
	 * @param cacheRate
	 *            Faktor an Zeilen im Verhältnis zu den angezeigten Zeilen, die
	 *            gerendert werden sollen.
	 */
	public CrudTable(DataContainer databaseContainer, TableColumns columns,
			boolean enableSorting) {
		super(null, databaseContainer.getDataSourceContainer());
		this.dataContainer = databaseContainer;
		this.columns = columns;
	}

	@Override
	public String formatPropertyValue(Object rowId, Object colId,
			Property property) {
		if (property == null || property.getValue() == null) {
			return "";
		} else {
			String columnName = colId.toString();
			DisplaySupport displayer = dataContainer.findDisplayer(columnName);
			if (displayer != null) {
				String value = displayer.formatPropertyValue(property,
						dataContainer.getFormat(columnName));
				if (columns != null && columns.isComboBox(columnName)) {
					return getDropdownTitle(rowId, columnName, value);
				}
				return value;
			}
			return super.formatPropertyValue(rowId, colId, property);
		}
	}

	/**
	 * @param rowId
	 *            die ID der aktuellen Zeile
	 * @param columnName
	 *            der Spaltenname
	 * @param value
	 *            der Optionswert
	 * @return der Optionstitel, falls vorhanden, sonst der Optionswert
	 */
	private String getDropdownTitle(Object rowId, String columnName,
			String value) {
		TableColumnSelectionContext context = new TableColumnSelectionContext(
				dataContainer.convertInternalRowId(rowId), columnName);
		String title = columns.getDropdownSelections(columnName).getTitle(
				value, context);
		return title != null ? title : value;
	}

	// Sichtbarkeit der Methode erhöhen.
	/**
	 * {@inheritDoc}
	 * 
	 * @param refreshContent
	 *            wenn {@code true}, wird direkt ein requestRepaint abgesetzt,
	 *            ansonsten wird nur das automatische Refreshing wieder
	 *            eingeschaltet
	 */
	@Override
	public void enableContentRefreshing(boolean refreshContent) {
		super.enableContentRefreshing(refreshContent);
	}

	// Sichtbarkeit der Methode erhöhen.
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean disableContentRefreshing() {
		return super.disableContentRefreshing();
	}

	@Override
	protected void handleUnregisteredField(Field field) {
		if (field instanceof AbstractSelect) {
			AbstractSelect select = (AbstractSelect) field;
			if (select.getContainerDataSource() instanceof OptionListContainer) {
				OptionListContainer container = (OptionListContainer) select
						.getContainerDataSource();
				container.close();
			}
		}
	}
}
