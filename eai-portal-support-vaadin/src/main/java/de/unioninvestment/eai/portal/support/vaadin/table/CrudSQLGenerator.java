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

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public interface CrudSQLGenerator extends SQLGenerator {

	/**
	 * Erzeugt ein Statement, um auf eine bestimmte Zeile zugreifen zu können.
	 * 
	 * @param rowId
	 *            Primärschlüssel
	 * @param tableName
	 *            Tabellenname
	 * @param filters
	 *            Filterliste
	 * @param orderBys
	 *            Sortierung
	 * @return StatementHelper
	 */
	public abstract StatementHelper getIndexStatement(RowId rowId,
			String tableName, List<Filter> filters, List<OrderBy> orderBys);

	public abstract void setPrimaryKeyColumns(List<String> primaryKeyColumns);

}