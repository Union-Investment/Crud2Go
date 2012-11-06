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

import groovy.lang.GString;

import java.util.List;

import javax.sql.DataSource;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.portlet.crud.scripting.database.ExtendedSql;

/**
 * SQLWhereFactory Implementation zum erstellen von script-basierten SQL-Where
 * Filtern.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptSQLWhereFactory extends ExtendedSql {

	/**
	 * Konstruktor.
	 */
	public ScriptSQLWhereFactory() {
		super((DataSource) null);
	}

	/**
	 * Erzeugt ein SQL-Filter.
	 * 
	 * @param column
	 *            Spalte
	 * @param where
	 *            where-Bedingung als GString
	 * @param durable
	 *            ob es ein durable-Filter werden soll
	 * @return SQL-Filter
	 */
	public SQLFilter createFilter(String column, GString where, boolean durable) {
		List<Object> values = getParameters(where);

		if (values.contains(null)) {
			return null;
		}

		SQLFilter filter = new SQLFilter(column, asSql(where, values), values,
				durable);

		return filter;
	}

}
