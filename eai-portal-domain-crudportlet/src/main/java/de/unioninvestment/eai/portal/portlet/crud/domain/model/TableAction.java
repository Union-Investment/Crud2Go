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

import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;

/**
 * Tabellen-Aktion (Skript-Action).
 * 
 * @author max.hartmann
 * 
 */
public class TableAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private TableActionConfig config;
	private Table table;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param portlet
	 *            Portletmodel
	 * @param config
	 *            Konfiguration
	 * @param table
	 *            Tabelle
	 * @param trigger
	 *            Triggers
	 */
	public TableAction(Portlet portlet, TableActionConfig config, Table table,
			Triggers trigger) {
		super(portlet, config, trigger);
		this.config = config;
		this.table = table;
	}

	public String getId() {
		return config.getId();
	}

	public String getTitle() {
		return config.getTitle();
	}

	public Table getTable() {
		return table;
	}

	@Override
	public void execute() {
		fireExecutionEvent();

		super.execute();
	}
}
