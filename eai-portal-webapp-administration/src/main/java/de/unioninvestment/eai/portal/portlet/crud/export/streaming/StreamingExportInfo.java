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

package de.unioninvestment.eai.portal.portlet.crud.export.streaming;

import java.util.ArrayList;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;

/**
 * Container for export table metadata obtained from various sources.
 * 
 * @author cmj
 */
public class StreamingExportInfo implements ExportInfo {

	private final DataContainer container;
	private final Table table;
	private TableColumns columns;

	private final String[] columnNames;
	private final String[] columnTitles;
	private final Class[] columnTypes;
	private final String[] displayFormats;
	private final String[] excelFormats;
    private boolean[] multilineFlags;

    public StreamingExportInfo(DataContainer container, Table table) {
		this.container = container;
		this.table = table;
		this.columns = table.getColumns();
		this.columnNames = generateColumnNames();
		this.columnTitles = generateColumnTitles(columnNames);
		this.columnTypes = generateColumnTypes(columnNames);
		this.displayFormats = generateDisplayFormats(columnNames);
		this.excelFormats = generateExcelFormats(columnNames);
        this.multilineFlags = generateMultilineFlags(columnNames);
	}

    private String[] generateColumnNames() {
		List<String> visibleColumns = table.getVisibleColumns();
		List<String> results = visibleColumns;
		if (columns != null) {
			results = new ArrayList<String>(visibleColumns.size());
			for (String name : visibleColumns) {
				TableColumn column = columns.get(name);
				if (column.isGenerated()) {
					if (column.getGeneratedValueGenerator() != null) {
						results.add(name);
					}
				} else {
					results.add(name);
				}
			}
		}
		return results.toArray(new String[results.size()]);
	}

	private String[] generateColumnTitles(String[] columnNames) {
		TableColumns columns = table.getColumns();
		if (columns == null) {
			return columnNames;
		}

		String[] titles = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			String title = columnName;
			TableColumn column = columns.get(columnName);
			if (column.getTitle() != null) {
				title = column.getTitle();
			}
			titles[i] = title;
		}
		return titles;
	}

	@SuppressWarnings("rawtypes")
	private Class[] generateColumnTypes(String[] columnNames) {
		Class<?>[] types = new Class[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = getExportableGeneratedColumn(columnNames[i]);
			if (column != null) {
				types[i] = column.getGeneratedType();
			} else {
				types[i] = container.getType(columnNames[i]);
			}
		}
		return types;
	}

    private boolean[] generateMultilineFlags(String[] columnNames) {
        boolean[] multilineFlags = new boolean[columnNames.length];
        if (columns != null) {
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn tableColumn = columns.get(columnNames[i]);
                multilineFlags[i] = tableColumn.isMultiline();
            }
        }
        return multilineFlags;
    }


    private TableColumn getExportableGeneratedColumn(String columnName) {
		if (columns != null) {
			TableColumn column = columns.get(columnName);
			if (column.isGenerated() && column.getGeneratedType() != null) {
				return column;
			}
		}
		return null;
	}

	private String[] generateDisplayFormats(String[] columnNames) {
		String[] displayFormats = new String[columnNames.length];
		if (columns != null) {
			for (int i = 0; i < columnNames.length; i++) {
				TableColumn tableColumn = columns.get(columnNames[i]);
				displayFormats[i] = tableColumn.getDisplayFormat();
			}
		}
		return displayFormats;
	}

	private String[] generateExcelFormats(String[] columnNames) {
		String[] excelFormats = new String[columnNames.length];
		if (columns != null) {
			for (int i = 0; i < columnNames.length; i++) {
				TableColumn tableColumn = columns.get(columnNames[i]);
				excelFormats[i] = tableColumn.getExcelFormat();
			}
		}
		return excelFormats;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String[] getColumnTitles() {
		return columnTitles;
	}

	public Class[] getColumnTypes() {
		return columnTypes;
	}

	public String[] getDisplayFormats() {
		return displayFormats;
	}

	public String[] getExcelFormats() {
		return excelFormats;
	}

    @Override
    public boolean[] getMultilineFlags() {
        return multilineFlags;
    }
}
