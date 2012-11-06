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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;

/**
 * Aggregations-Objekt für Tabellenspalteninformationen, bietet
 * Aggregatfunktionen.
 * 
 * @author carsten.mjartan
 */
public class TableColumns implements Iterable<TableColumn>, Serializable {

	private static final long serialVersionUID = 1L;

	private final SortedMap<String, TableColumn> columns = new TreeMap<String, TableColumn>();
	private final List<TableColumn> columnsList;

	/**
	 * @param cols
	 *            Liste der Tabellenzeilen
	 */
	public TableColumns(List<TableColumn> cols) {
		columnsList = new ArrayList<TableColumn>(cols);
		for (TableColumn col : cols) {
			columns.put(col.getName(), col);
		}
	}

	/**
	 * @param name
	 *            der Name der Spalte
	 * @return das Spaltenobjekt
	 */
	public TableColumn get(String name) {
		return columns.get(name);
	}

	/**
	 * Liefert die Tabellenspalten für Iteration.
	 * 
	 * @return Iterator<TableColumn>
	 */
	@Override
	public Iterator<TableColumn> iterator() {
		return columnsList.iterator();
	}

	/**
	 * @return Liste der Primärschlüsselspaltennamen
	 */
	public List<String> getPrimaryKeyNames() {
		List<String> primaryKeys = new ArrayList<String>();
		for (TableColumn c : columnsList) {
			if (c.isPrimaryKey()) {
				primaryKeys.add(c.getName());
			}
		}
		return primaryKeys;
	}

	/**
	 * @return Liste aller Spaltennamen in Reihenfolge
	 */
	public List<String> getAllNames() {
		List<String> result = new ArrayList<String>();
		for (TableColumn column : columnsList) {
			result.add(column.getName());
		}
		return result;
	}

	public List<String> getVisibleNamesForTable() {
		return getVisibleNames(true);
	}

	public List<String> getVisibleNamesForForm() {
		return getVisibleNames(false);
	}

	/**
	 * @param inTable
	 *            Ob für die Tabelle oder für das Formular die Felder ermittelt
	 *            werden sollen
	 * @return Liste der Namen aller sichtbaren Spalten in Reihenfolge
	 */
	private List<String> getVisibleNames(boolean inTable) {
		List<String> result = new ArrayList<String>();
		for (TableColumn column : columnsList) {
			Hidden hs = column.getHidden();

			if ((hs.equals(Hidden.FALSE))
					&& !(!inTable && column.isGenerated())) {
				result.add(column.getName());

			} else if (inTable && (hs.equals(Hidden.IN_FORM))) {
				result.add(column.getName());

			} else if (!inTable && (hs.equals(Hidden.IN_TABLE))
					&& !column.isGenerated()) {
				result.add(column.getName());
			}
		}
		return result;
	}

	/**
	 * Liste der Namen mit mehrzeiligen Spalten.
	 * 
	 * @return Alle Spalten, die mehrzeilig sind
	 */
	public List<String> getMultilineNames() {
		List<String> colName = new ArrayList<String>();
		for (TableColumn tc : columnsList) {
			if (tc.isMultiline()) {
				colName.add(tc.getName());
			}
		}
		return colName;
	}

	/**
	 * Prüft, ob eine Spalte mehrzeilig ist.
	 * 
	 * @param string
	 *            Name
	 * @return isMultiline
	 */
	public boolean isMultiline(String string) {
		return (columns.containsKey(string) && columns.get(string)
				.isMultiline());
	}

	/**
	 * Gibt den InputPrompt zurück.
	 * 
	 * @param property
	 *            Name
	 * @return Prompt
	 */
	public String getInputPrompt(String property) {
		if (columns.containsKey(property)) {
			String prompt = columns.get(property).getInputPrompt();
			if (prompt != null && !prompt.isEmpty()) {
				return prompt;
			}
		}
		return null;
	}

	/**
	 * Prüft, ob das Property ein Dropdownfeld ist.
	 * 
	 * @param property
	 *            Name
	 * @return isDropdown
	 */
	public boolean isDropdown(String property) {
		return columns.containsKey(property)
				&& columns.get(property).isSelectable();

	}

	/**
	 * Prüft ob das Property eine Checkbox ist.
	 * 
	 * @param property
	 *            Name
	 * @return true wenn das Property eine Checkbox ist
	 */
	public boolean isCheckbox(String property) {
		return columns.containsKey(property)
				&& columns.get(property).isCheckable();
	}

	/**
	 * Gibt die Auswahlboxen zurück.
	 * 
	 * @param property
	 *            Name
	 * @return DropdownSelections
	 */
	public OptionList getDropdownSelections(String property) {
		if (isDropdown(property)) {
			return columns.get(property).getOptionList();
		}
		return null;
	}

	/**
	 * Gibt das Checkbox-Objekt für die übergebene Property zurück.
	 * 
	 * @param property
	 *            Name
	 * @return die Checkbox oder null wenn die übergebene Property keine
	 *         Checkbox ist.
	 */
	public CheckBoxTableColumn getCheckBox(String property) {
		if (isCheckbox(property)) {
			return columns.get(property).getCheckBox();
		}
		return null;
	}

	public Map<String, String> getFormatPattern() {
		Map<String, String> result = new HashMap<String, String>();
		for (TableColumn column : columnsList) {
			result.put(column.getName(), column.getDisplayFormat());
		}

		return result;
	}
}
