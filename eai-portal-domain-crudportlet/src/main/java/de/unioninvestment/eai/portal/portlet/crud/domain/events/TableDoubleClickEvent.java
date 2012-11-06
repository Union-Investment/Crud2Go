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
package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Wird bei einem Doppelklick auf eine Tabellenzeile ausgelöst.
 * 
 * @author eugen.melnichuk
 */
public class TableDoubleClickEvent implements
		SourceEvent<TableDoubleClickEventHandler, Table> {
	private static final long serialVersionUID = 1L;

	private Table source;

	private final ContainerRow row;

	/**
	 * Konstruktor.
	 * 
	 * @param source
	 *            Tabelle
	 * @param row
	 *            Zeile
	 */
	public TableDoubleClickEvent(Table source, ContainerRow row) {
		super();
		this.source = source;
		this.row = row;
	}

	@Override
	public void dispatch(TableDoubleClickEventHandler eventHandler) {
		eventHandler.onDoubleClick(this);
	}

	@Override
	public Table getSource() {
		return source;
	}

	public ContainerRow getRow() {
		return row;
	}
}
