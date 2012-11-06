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
package de.unioninvestment.eai.portal.support.vaadin.table;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;

/**
 * Erweiteres Interface des @see
 * com.vaadin.addon.sqlcontainer.query.FreeformStatementDelegate
 * 
 * @author markus.bonsch
 * 
 */
public interface DatabaseQueryDelegate extends FreeformStatementDelegate {

	/**
	 * Erzeugt ein Statement, gefilter auf die Hauptquery, um auf eine bestimmte
	 * Zeile zugreifen zu können.
	 * 
	 * @param rowId
	 *            Primärschlüssel
	 * @return StatementHelper
	 */
	public abstract StatementHelper getIndexStatement(RowId rowId);

	/**
	 * Erzeugt ein ungefiltertes Statement, um auf eine bestimmte Zeile
	 * zugreifen zu können.
	 * 
	 * @param rowId
	 * @return
	 */
	public abstract StatementHelper getRowByIdStatement(RowId rowId);

}