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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import java.util.LinkedHashSet;
import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;

/**
 * Informationsobjekt zu einer Selektion. Liefert die Liste der Zeilen-IDs und
 * bietet Methoden zur Manipulation der Zeilen.
 * 
 * @author carsten.mjartan
 */
public class ScriptTableSelection {

	private final Set<ScriptRowId> selectedRowIds;

	private DataContainer container;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param container
	 *            Der Tabelle hinterlegte DatabaseContainer
	 * @param selectedRowIds
	 *            Liste der markierten Zeilen-IDs
	 */
	public ScriptTableSelection(DataContainer container,
			Set<ScriptRowId> selectedRowIds) {
		this.container = container;
		this.selectedRowIds = selectedRowIds;
	}

	/**
	 * Überprüft, ob etwas selektiert ist.
	 * 
	 * @return {@code true} genau dann, wenn keine Zeile ausgewählt ist.
	 */
	public boolean isEmpty() {
		return selectedRowIds.isEmpty();
	}

	/**
	 * Gibt die Anzahl der selektierten Zeilen zurück.
	 * 
	 * @return die Anzahl der selektierten Zeilen
	 */
	public int size() {
		return selectedRowIds.size();
	}

	/**
	 * Gibt die IDs der selektierten Zeile zurück.
	 * 
	 * @return die Liste der markierten Zeilen-IDs
	 */
	public Set<ScriptRowId> getIds() {
		return selectedRowIds;
	}

	/**
	 * Löscht alle selektierten Zeilen.
	 */
	public void removeAllRows() {
		container.removeRows(collectContainerRowIds());
	}

	private Set<ContainerRowId> collectContainerRowIds() {
		Set<ContainerRowId> containerRowIds = new LinkedHashSet<ContainerRowId>();
		for (ScriptRowId id : selectedRowIds) {
			containerRowIds.add(id.getContainerRowId());
		}
		return containerRowIds;
	}

	/**
	 * Iteriert durch alle Zeilen und führt für jede Zeile den Cosure aus.
	 * 
	 * Achtung: Bitte nicht mit dem SQL-Backend benutzen, da diese Methode
	 * zusammen mit Vaadin-Versionen < 6.8.5 ernsthafte Performanceprobleme
	 * verursacht.
	 * 
	 * @param c
	 *            Closure mit einem Parameter { ScriptRow row -> ... }, die für
	 *            jede selektierte Zeile aufgerufen wird
	 */
	public void eachRow(final Closure<?> c) {
		container.eachRow(collectContainerRowIds(),
				new DataContainer.EachRowCallback() {
					@Override
					public void doWithRow(ContainerRow row) {
						c.call(new ScriptRow(row));
					}
				});
	}

	/**
	 * Führt das übergebene Closure in einer neuen Transaktion aus.
	 * 
	 * @param c
	 *            Closure
	 */
	public void withTransaction(final Closure<?> c) {
		container
				.withTransaction(new DataContainer.TransactionCallback<Object>() {
					@Override
					public Object doInTransaction() {
						c.call();
						return null;
					}
				});
	}

	/**
	 * Führt das übergebene Closure in einer bestehenden Transaktion aus.
	 * 
	 * @param c
	 *            Closure
	 */
	public void withExistingTransaction(final Closure<?> c) {
		container
				.withExistingTransaction(new DataContainer.TransactionCallback<Object>() {
					@Override
					public Object doInTransaction() {
						c.call();
						return null;
					}
				});
	}

}
