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
package de.unioninvestment.eai.portal.support.scripting;

import groovy.lang.Closure;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.ColumnStyleRenderer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;

/**
 * Ist für das setzen der Styleklasse auf einer Tabellenzelle zuständig.
 * 
 * 
 * @author siva.selvarajah
 */
public class DynamicColumnStyleRenderer implements ColumnStyleRenderer {

	private final Closure<?> closure;
	private DataContainer container;
	private final String columnName;

	/**
	 * Konstruktor.
	 * 
	 * @param table
	 *            Tabelle
	 * @param rowStyleClosure
	 *            Closure
	 * @param columnName
	 *            Tabellenspaltenname
	 */
	public DynamicColumnStyleRenderer(Table table, Closure<?> rowStyleClosure,
			String columnName) {
		this.columnName = columnName;
		this.container = table.getContainer();
		this.closure = rowStyleClosure;
	}

	@Override
	public String getStyle(ContainerRowId rowId) {
		ContainerRow row = container.getCachedRow(rowId, false,
				true);
		if (row != null) {
			ScriptRow scriptRow = new ScriptRow(row);

			Object result = closure.call(scriptRow, columnName);

			if (result == null) {
				return null;

			} else if (result instanceof String) {

				return (String) result;

			} else {
				throw new IllegalArgumentException(
						"ColumnStyle only support String.");
			}
		} else {
			return null;
		}

	}

}
