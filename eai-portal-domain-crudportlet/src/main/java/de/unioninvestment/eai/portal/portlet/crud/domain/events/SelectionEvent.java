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

import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event wenn sich die Benutzerauswahl auf der der Table ändert. Wird z. Zt.
 * nicht über den {@link EventBus} gesendet.
 * 
 * @author markus.bonsch
 * 
 */
public class SelectionEvent implements
		SourceEvent<SelectionEventHandler, Table> {

	private static final long serialVersionUID = 1L;

	private Table source;

	private final Set<ContainerRowId> selection;

	/**
	 * Konstruktor.
	 * 
	 * @param source
	 *            Die auslösende Tabelle
	 * @param selection
	 *            ein Set von Zeilen Id's der aktuellen Auswahl.
	 */
	public SelectionEvent(Table source, Set<ContainerRowId> selection) {
		this.source = source;
		this.selection = selection;
	}

	@Override
	public void dispatch(SelectionEventHandler eventHandler) {
		eventHandler.onSelectionChange(this);
	}

	public Table getSource() {
		return source;
	}

	public Set<ContainerRowId> getSelection() {
		return selection;
	}
}
