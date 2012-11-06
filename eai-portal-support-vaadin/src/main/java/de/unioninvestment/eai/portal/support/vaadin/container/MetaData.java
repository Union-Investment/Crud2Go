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
package de.unioninvestment.eai.portal.support.vaadin.container;

import static java.util.Collections.unmodifiableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Information über den Container (Spaltenname, Datentypen der Splateninhalte,
 * etc.).
 */
public class MetaData implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Column> columns;
	private final Collection<String> columnNames;
	private Collection<String> primaryKeys;
	private final Map<String, Class<?>> columnTypes;

	private final boolean insertSupported;
	private final boolean updateSupported;
	private final boolean removeSupported;
	private final boolean transactional;
	private final boolean filterSupported;

	public MetaData(List<Column> columns, boolean insertSupported,
			boolean updateSupported, boolean removeSupported,
			boolean transactional, boolean filterSupported) {
		this.columns = columns;
		this.filterSupported = filterSupported;
		this.columnNames = collectColumnNames();
		this.columnTypes = collectColumnTypes();
		this.primaryKeys = collectPrimaryKeys();
		this.insertSupported = insertSupported;
		this.updateSupported = updateSupported;
		this.removeSupported = removeSupported;
		this.transactional = transactional;
	}

	private Collection<String> collectPrimaryKeys() {
		List<String> keys = new LinkedList<String>();
		for (Column column : columns) {
			if (column.isPartOfPrimaryKey()) {
				keys.add(column.getName());
			}
		}
		return unmodifiableList(keys);
	}

	private Map<String, Class<?>> collectColumnTypes() {
		Map<String, Class<?>> types = new HashMap<String, Class<?>>();
		for (Column column : columns) {
			types.put(column.getName(), column.getType());
		}
		return types;
	}

	private Collection<String> collectColumnNames() {
		ArrayList<String> list = new ArrayList<String>(columns.size());
		for (Column column : columns) {
			list.add(column.getName());
		}
		return unmodifiableList(list);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public boolean isInsertSupported() {
		return insertSupported;
	}

	public boolean isUpdateSupported() {
		return updateSupported;
	}

	public boolean isRemoveSupported() {
		return removeSupported;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public Collection<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnName
	 *            der Name der Spalte
	 * @return den Spaltentyp
	 */
	public Class<?> getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}

	public Collection<String> getPrimaryKeys() {
		return primaryKeys;
	}

	/**
	 * @param columnName
	 *            der Spaltenname
	 * @return der Index der Spalte oder -1, falls keine passende Spalte
	 *         gefunden wird
	 */
	public int getIndex(String columnName) {
		int idx = 0;
		for (Column column : columns) {
			if (column.getName().equals(columnName)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public boolean isFilterSupported() {
		return filterSupported;
	}
}
