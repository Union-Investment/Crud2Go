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

import com.vaadin.addon.tableexport.ExportableColumnGenerator;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;

/**
 * Für Export erweiterter {@link ColumnGenerator}.
 * 
 * @author carsten.mjartan
 */
public class VaadinExportableColumnGenerator extends
		VaadinCustomColumnGenerator implements ExportableColumnGenerator {

	private static final long serialVersionUID = 1L;

	/**
	 * @param column
	 *            die zu exportierende Tabellenspalte
	 * @param dataContainer
	 *            Container für die Transformation der Vaadin-Objekte ins Modell
	 */
	public VaadinExportableColumnGenerator(TableColumn column,
			DataContainer dataContainer) {
		super(column, dataContainer);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Property getGeneratedProperty(Object itemId, Object columnId) {
		ContainerRow row = getDataContainer().getRowByInternalRowId(itemId,
				false, true);
		Object value = getColumn().getGeneratedValueGenerator().getValue(row);
		return new ObjectProperty(value, getType());
	}

	@Override
	public Class<?> getType() {
		return getColumn().getGeneratedType();
	}

}
