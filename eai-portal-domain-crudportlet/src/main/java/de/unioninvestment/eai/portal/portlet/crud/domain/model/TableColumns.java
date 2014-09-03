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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Hidden;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;
import org.vaadin.tokenfield.TokenField;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Aggregations-Objekt für Tabellenspalteninformationen, bietet
 * Aggregatfunktionen.
 * 
 * @author carsten.mjartan
 */
public class TableColumns implements Iterable<TableColumn>, Serializable {

	private static final long serialVersionUID = 1L;

	private final HashMap<String, TableColumn> columns = new HashMap<String, TableColumn>();
	private final List<TableColumn> columnsList;

	private ImmutableMap<String, String> lowerCaseColumnNamesMapping;

	/**
	 * @param cols
	 *            Liste der Tabellenzeilen
	 */
	public TableColumns(Collection<TableColumn> cols) {
		columnsList = new ArrayList<TableColumn>(cols);
		for (TableColumn col : cols) {
			columns.put(col.getName(), col);
		}
	}

	/**
	 * @param columnName
	 *            der Name der Spalte
	 * @return das Spaltenobjekt oder
	 */
	public TableColumn get(String columnName) {
		TableColumn column = columns.get(columnName);
		if (column == null) {
			throw new BusinessException("portlet.crud.error.columnNotFound",
					columnName);
		}
		return column;
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
	 * @param columnName
	 *            Name
	 * @return isMultiline
	 */
	public boolean isMultiline(String columnName) {
		return get(columnName).isMultiline();
	}

	/**
	 * Gibt den InputPrompt zurück.
	 * 
	 * @param columnName
	 *            Name
	 * @return Prompt
	 */
	public String getInputPrompt(String columnName) {
		String prompt = get(columnName).getInputPrompt();
		if (prompt != null && !prompt.isEmpty()) {
			return prompt;
		} else {
			return null;
		}
	}

	/**
	 * Prüft, ob das Dropdown eine Selektion darstellt.
	 * 
	 * @param columnName
	 * @return
	 */
	public boolean isSelection(String columnName) {
		TableColumn column = get(columnName);
		return column instanceof SelectionTableColumn;
	}

	/**
	 * Prüft, ob das Property eine Dropdown-Selektion darstellt.
	 * 
	 * @param columnName
	 *            Name
	 * @return <code>true</code>, falls es eine Auswahl als Dropdown darstellt
	 */
	public boolean isComboBox(String columnName) {
		TableColumn column = get(columnName);
		return (column instanceof SelectionTableColumn)
				&& ((SelectionTableColumn) column).isComboBox();
	}

	/**
	 * Prüft, ob das Property eine TokenField-Selektion darstellt.
	 * 
	 * @param columnName
	 *            Name
	 * @return <code>true</code>, falls es eine Auswahl als {@link TokenField}
	 *         darstellt
	 */
	public boolean isTokenfield(String columnName) {
		TableColumn column = get(columnName);
		return (column instanceof SelectionTableColumn)
				&& ((SelectionTableColumn) column).isTokenfield();
	}

	/**
	 * Prüft ob das Property eine Checkbox ist.
	 * 
	 * @param columnName
	 *            Name
	 * @return true wenn das Property eine Checkbox ist
	 */
	public boolean isCheckbox(String columnName) {
		return get(columnName) instanceof CheckBoxTableColumn;
	}

	/**
	 * Prüft ob zusätzliche Infos zu Datum existieren
	 * 
	 * @param columnName
	 *            Name
	 * @return true wenn weitere Konfigurationdaten existieren
	 */
	public boolean isDate(String columnName) {
		return get(columnName) instanceof DateTableColumn;
	}

	/**
	 * Gibt die Auswahlboxen zurück.
	 * 
	 * @param columnName
	 *            Name
	 * @return DropdownSelections
	 */
	public OptionList getDropdownSelections(String columnName) {
		TableColumn tableColumn = get(columnName);
		if (tableColumn instanceof SelectionTableColumn) {
			return ((SelectionTableColumn) tableColumn).getOptionList();
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
		return (CheckBoxTableColumn) get(property);
	}

	/**
	 * @return the display-format patterns by column name
	 */
	public Map<String, String> getFormatPattern() {
		Map<String, String> result = new HashMap<String, String>();
		for (TableColumn column : columnsList) {
			if (column.getDisplayFormat() != null) {
				result.put(column.getName(), column.getDisplayFormat());
			}
		}

		return result;
	}

	/**
	 * @param columnName
	 * @return the column data for date columns
	 */
	public DateTableColumn getDateColumn(String columnName) {
		return (DateTableColumn) get(columnName);
	}

	/**
	 * @param columnName
	 * @return <code>true</code>, it the column exists
	 */
	public boolean contains(String columnName) {
		return columns.containsKey(columnName);
	}

	public Collection<String> getSearchableColumnPrefixes() {
		List<String> searchable = new LinkedList<String>();
		for (TableColumn column : columnsList) {
			if (column.getSearchable() != Searchable.FALSE) {
				searchable.add(column.getSearchPrefix());
			}
		}
		return searchable;
	}

	public Map<String,String> getDefaultSearchablePrefixes() {
		Map<String,String> defaultFields = new LinkedHashMap<String,String>();
		for (TableColumn column : columnsList) {
			if (column.getSearchable() == Searchable.DEFAULT) {
				defaultFields.put(column.getName(), column.getSearchPrefix());
			}
		}
		return defaultFields;
	}

	public void setTable(Table table) {
		for (TableColumn column : columnsList) {
			column.setTable(table);
		}
	}

	public int size() {
		return columnsList.size();
	}

	public Class<?> getType(String name) {
		return get(name).getType();
	}

	public Map<String, String> getDropdownSelections(String columnName,
			String titleStartingWith, int maximumEntries) {
		Map<String, String> allOptions = getDropdownSelections(columnName)
				.getOptions(null);
		if (allOptions != null) {
			Map<String, String> results = Maps.newLinkedHashMap();
			int left = maximumEntries <= 0 ? Integer.MAX_VALUE : maximumEntries;
			String prefix = titleStartingWith == null ? null
					: titleStartingWith.toLowerCase();
			for (Entry<String, String> entry : allOptions.entrySet()) {
				if (left <= 0) {
					break;
				}
				if (prefix == null
						|| entry.getValue().toLowerCase().startsWith(prefix)) {
					results.put(entry.getKey(), entry.getValue());
					left--;
				}
			}
			return results;
		}
		return null;
	}

	public Map<String, String> getLowerCaseColumnNamesMapping() {
		if (lowerCaseColumnNamesMapping == null) {
			Builder<String, String> builder = ImmutableMap
					.<String, String> builder();
			for (TableColumn column : columnsList) {
                builder.put(column.getSearchPrefix().toLowerCase(), column.getName());
			}
			lowerCaseColumnNamesMapping = builder.build();
		}
		return lowerCaseColumnNamesMapping;
		
		
	}

    public List<TableColumn> getUpdateColumns() {
        List<TableColumn> results = new LinkedList<TableColumn>();
        for (TableColumn column : columnsList) {
            if (column.isUpdateColumn()) {
                results.add(column);
            }
        }
        return results;
    }

    public List<TableColumn> getInsertColumns() {
        List<TableColumn> results = new LinkedList<TableColumn>();
        for (TableColumn column : columnsList) {
            if (column.isInsertColumn()) {
                results.add(column);
            }
        }
        return results;
    }
}
