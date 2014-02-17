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

import static java.util.Arrays.asList;

import java.util.List;

import com.vaadin.data.util.converter.Converter;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ValuesRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItemValuesRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ExportColumnSelectionContext;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Wrapper class that enriches the {@link StreamItem} information by additional
 * layout information.
 * <p/>
 * The class is designed so that it can be reused with changing items.
 * 
 * @author cmj
 */
public class StreamItemInfoWrapper implements ItemInfo {

	private Table table;
	private StreamItem item;
	private DataContainer container;
	private List<String> columnNames;
	private TableColumns columns;

	public StreamItemInfoWrapper(Table table, String[] columnNames) {
		this.table = table;
		this.columns = table.getColumns();
		this.columnNames = asList(columnNames);
		this.container = table.getContainer();
	}

	/**
	 * @param item
	 *            delegate item replacement
	 */
	public void setItem(StreamItem item) {
		this.item = item;
	}

	@Override
	public Object getValue(String columnName) {
		return item.getValue(columnName);
	}

	@Override
	public boolean isComboBox(String columnName) {
		return columns != null && table.getColumns().isComboBox(columnName);
	}

	@Override
	public String getTitle(String columnName) {
		Object value = getValue(columnName);
		String text = getText(columnName, value);
		return getDropdownTitle(columnName, text);
	}

	private String getText(String columnName, Object value) {
		String text = "";

		if (value != null) {
			DisplaySupport displayer = container.findDisplayer(columnName);
			if (displayer != null) {
				Converter<String, Object> nf = (Converter<String, Object>) displayer
						.createFormatter(container.getType(columnName),
								container.getFormat(columnName));
				if (nf == null) {
					text = value.toString();
				} else {
					text = nf.convertToPresentation(value, String.class,
							Context.getLocale());
				}
			} else {
				text = value.toString();
			}
		}
		return text;
	}

	private String getDropdownTitle(String columnName, String text) {
		if (columns == null) {
			return text;
		}
		ValuesRow row = new StreamItemValuesRow(columnNames, item);
		ExportColumnSelectionContext context = new ExportColumnSelectionContext(
				row, columnName);
		String title = table.getColumns().getDropdownSelections(columnName)
				.getTitle(text, context);
		return title != null ? title : text;
	}
}
