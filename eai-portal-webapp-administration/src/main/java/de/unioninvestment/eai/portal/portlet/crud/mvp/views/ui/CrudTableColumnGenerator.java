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

import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.addon.tableexport.ExportableColumnGenerator;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.CheckBoxSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.support.vaadin.support.BufferedTable;

/**
 * Generator für Tabellenzellen, benötigt für die Unterstützung von
 * konfigurierbaren Zeilenhöhen.
 * 
 * @author markus.bonsch
 */
@Configurable
public class CrudTableColumnGenerator implements ExportableColumnGenerator {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(CrudTableColumnGenerator.class);

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_COLUMN_HEIGHT = 15;

	private final TableColumns columns;

	private final List<String> multilineCols;

	private Object firstColumnId;

	private final Class<?> type;

	private final CrudTable table;
	private final DataContainer dataContainer;

	private boolean returnsComponent;

	private final EditorSupport editor;

	private final String columnName;

	private boolean multilineColumn;

	boolean dropdownColumn;

	/**
	 * @param columnName
	 *            der Spaltenname
	 * @param table
	 *            Tabelle
	 * @param columnHeight
	 *            die optionale Zeilenhöhe in Pixeln
	 * @param columns
	 *            Spalten
	 */
	public CrudTableColumnGenerator(String columnName, Class<?> type,
			Integer columnHeight, TableColumns columns, Object firstColumnId,
			CrudTable table, DataContainer dataContainer, EditorSupport editor) {
		this.columnName = columnName;
		this.type = type;
		this.columns = columns;
		this.firstColumnId = firstColumnId;
		this.table = table;
		this.dataContainer = dataContainer;
		this.editor = editor;

		if (columns != null) {
			this.multilineCols = columns.getMultilineNames();
		} else {
			multilineCols = Collections.emptyList();
		}
		this.multilineColumn = multilineCols.contains(columnName);
		this.returnsComponent = isReturningComponent(columnName);
		this.dropdownColumn = columns != null && columns.isComboBox(columnName);
	}

	private boolean isReturningComponent(String columnName) {
		if (columns.getMultilineNames().contains(columnName)) {
			return true;
		} else if (columns.isCheckbox(columnName)) {
			return true;
		} else if (columns.getMultilineNames().isEmpty()
				&& columnName.equals(firstColumnId)) {
			return true;
		}
		return false;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Property prop = source.getItem(itemId).getItemProperty(columnId);
		if (prop == null) {
			return null;
		}

		Object value = ((BufferedTable) source).getPropertyValue(itemId,
				columnId, prop);
		if (returnsComponent) {
			if (value instanceof Component) {
				return wrapForCustomHeight((Component) value);
			} else if (columns != null
					&& columns.isCheckbox(columnId.toString())) {
				return buildReadOnlyCheckBox(prop);
			} else {
				return buildLabel(columnId,
						value == null ? "" : value.toString());
			}
		} else {
			return value;
		}
	}

	private Component wrapForCustomHeight(Component component) {
		if (component == null) {
			return null;
		} else if (multilineColumn) {
			component.setHeight("100%");
			return component;
		} else {
			return component;
		}
	}

	/**
	 * Erstellt ein Label. Die Spaltenhöhe wird bei Multiline-Spalten oder bei
	 * der ersten Spalte gesetzt, so dass mindestens eine Spalte die Zeilehöhe
	 * vorgibt.
	 * 
	 * @param columnId
	 * @param value
	 * @return
	 */
	private Component buildLabel(Object columnId, String value) {
		Component component = new Label(value);
		if (multilineCols.contains(columnId)) {
			component.addStyleName("wordwrap");
		}
		return wrapForCustomHeight(component);
	}

	private Component buildReadOnlyCheckBox(Property prop) {
		de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxTableColumn checkBoxModel = columns
				.getCheckBox(columnName);

		if (prop.getValue() == null) {
			return wrapForCustomHeight(new Label("", Label.CONTENT_XHTML));
		}

		String propertyValue = prop.getValue().toString();
		// only display a CheckBox if the value of the cell is either the
		// checked or unchecked value, otherwise simply return a Label
		if (propertyValue.equals(checkBoxModel.getCheckedValue())
				|| propertyValue.equals(checkBoxModel.getUncheckedValue())) {
			CheckBox checkBox = ((CheckBoxSupport) editor).createCheckBox(type,
					checkBoxModel.getCheckedValue(),
					checkBoxModel.getUncheckedValue(),
					dataContainer.getFormat(columnName));
			checkBox.setPropertyDataSource(prop);
			checkBox.setReadOnly(true);
			return wrapForCustomHeight(checkBox);
		} else {
			return wrapForCustomHeight(new Label(propertyValue,
					Label.CONTENT_XHTML));
		}
	}

	@Override
	public Property getGeneratedProperty(Object itemId, Object columnId) {
		Item item = dataContainer.getDataSourceContainer().getItem(itemId);
		if (item == null) {
			LOG.warn("Das Item mit der ID {} wurde nicht gefunden.", itemId);
			return null;
		}
		Property property = item.getItemProperty(columnId);

		if (dropdownColumn) {
			return new ObjectProperty<String>(table.formatPropertyValue(itemId,
					columnId, property), String.class);
		} else {
			return property;
		}
	}

	@Override
	public Class<?> getType() {
		return type;
	}

}
