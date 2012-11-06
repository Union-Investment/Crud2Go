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

import com.vaadin.ui.Table.CellStyleGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;

/**
 * Delegiert das CellStyle-Rendering an die Domain Klassen.
 * 
 * @author carsten.mjartan
 */
public class CrudCellStyleGenerator implements CellStyleGenerator {
	private static final long serialVersionUID = 1L;

	private Table model;
	private DataContainer container;

	/**
	 * Konstruktor.
	 * 
	 * @param model
	 *            das Tabellen-Model
	 * @param container
	 *            der Container zur ID-Konvertierung
	 */
	public CrudCellStyleGenerator(Table model, DataContainer container) {
		super();
		this.model = model;
		this.container = container;
	}

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		if (propertyId == null && model.getRowStyleRenderer() != null) {
			return model.getRowStyleRenderer().getStyle(
					container.convertInternalRowId(itemId));

		} else if (propertyId != null
				&& model.getColumnStyleRenderer((String) propertyId) != null) {
			return model.getColumnStyleRenderer((String) propertyId).getStyle(
					container.convertInternalRowId(itemId));
		}

		return "";
	}
}