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
package de.unioninvestment.eai.portal.support.vaadin.filter;

import static java.util.Collections.unmodifiableList;

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * Ein Vaadin SQLContainer Filter für eine dynamische SQL-Where Abfragen.
 * 
 * @author markus.bonsch
 * 
 */
public class SQLFilter implements Filter {

	private static final long serialVersionUID = 42L;

	private String column;
	private String where;
	private List<Object> values;

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            Spalte
	 * @param where
	 *            Bedingung
	 * @param values
	 *            Werte
	 */
	public SQLFilter(String column, String where, List<Object> values) {
		this.column = column;
		this.where = where;
		this.values = values;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		return true;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return column == null || column.equals(propertyId);
	}

	public String getWhereString() {
		return this.where;
	}

	public String getColumn() {
		return this.column;
	}

	public List<Object> getValues() {
		return unmodifiableList(values);
	}

}
