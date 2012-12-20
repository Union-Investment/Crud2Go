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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;

/**
 * Repräsentiert eine Datenquelle einer Tabelle.
 * 
 * @author carsten.mjartan
 */
public class ScriptDatabaseContainer extends ScriptContainer {

	/**
	 * @param container
	 *            die zugehörige Modell-Instanz
	 */
	ScriptDatabaseContainer(DataContainer container) {
		super(container);
		initializeEventHandler();
	}

	/**
	 * liefert {@code true} zurück, wenn es sich bei {@code columName} um einen
	 * CLOB handelt. Sonst liefert die Methode {@code false}
	 * 
	 * @param columnName
	 *            der Spaltenname
	 * @return <code>true</code>, falls die Spalte CLobs enthält
	 */
	public boolean isClob(String columnName) {
		return container.isCLob(columnName);
	}

	/**
	 * @param row
	 *            die betroffene Zeile
	 * @param columnName
	 *            der Spaltenname
	 * @return die Clob-Instanz
	 */
	public ScriptClob getCLob(ScriptRow row, String columnName) {
		return new ScriptClob(container.getCLob(
				row.getId().getContainerRowId(), columnName));
	}

	/**
	 * liefert {@code true} zurück, wenn es sich bei {@code columName} um einen
	 * BLOB handelt. Sonst liefert die Methode {@code false}
	 * 
	 * @param columnName
	 *            der Spaltenname
	 * @return <code>true</code>, falls die Spalte eine Blob-Spalte ist
	 */
	public boolean isBLob(String columnName) {
		return container.isBLob(columnName);
	}

	/**
	 * 
	 * @param row
	 *            die Tabellenzeile
	 * @param columnName
	 *            der Spaltenname
	 * @return ein BLob Objekt
	 */
	public ScriptBlob getBLob(ScriptRow row, String columnName) {
		return new ScriptBlob(container.getBLob(
				row.getId().getContainerRowId(), columnName));
	}
}
