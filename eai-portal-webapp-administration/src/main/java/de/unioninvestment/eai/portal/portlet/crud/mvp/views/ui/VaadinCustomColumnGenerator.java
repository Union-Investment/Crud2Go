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

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;

/**
 * ColumnGenerator für generierte Vaadin Komponenten
 * 
 * @author markus.bonsch
 * 
 */
public class VaadinCustomColumnGenerator implements ColumnGenerator {

	private static final long serialVersionUID = 1L;

	private final TableColumn column;

	private final DataContainer dataContainer;

	/**
	 * @param column
	 *            die zu exportierende Tabellenspalte
	 * @param dataContainer
	 *            Container für die Transformation der Vaadin-Objekte ins Modell
	 */
	public VaadinCustomColumnGenerator(TableColumn column,
			DataContainer dataContainer) {
		this.column = column;
		this.dataContainer = dataContainer;
	}

	@Override
	public Component generateCell(Table source, Object itemId, Object columnId) {
		Item item = source.getItem(itemId);
		ContainerRow row = dataContainer.convertItemToRow(item, false, true);
		return column.getCustomColumnGenerator().generate(row);

	}

	protected TableColumn getColumn() {
		return column;
	}

	protected DataContainer getDataContainer() {
		return dataContainer;
	}

}
