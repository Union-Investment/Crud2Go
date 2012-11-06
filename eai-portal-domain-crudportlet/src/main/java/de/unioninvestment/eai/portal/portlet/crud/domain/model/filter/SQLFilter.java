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
package de.unioninvestment.eai.portal.portlet.crud.domain.model.filter;

import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * Filterklasse für ein SQL Filter.
 * 
 * @author max.hartmann
 * 
 */
public final class SQLFilter extends Filter {

	private static final long serialVersionUID = 1L;

	private final String column;
	private final String whereString;
	private final List<Object> values;

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param whereString
	 *            - Where-String
	 * @param values
	 *            - Werteliste
	 */
	public SQLFilter(String column, String whereString, List<Object> values) {
		this(column, whereString, values, false);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param whereString
	 *            - Where-String
	 * @param values
	 *            - Werteliste
	 * @param durable
	 *            Ob der Filter permanent gesetzt sein soll
	 */
	public SQLFilter(String column, String whereString, List<Object> values,
			boolean durable) {
		super(durable);
		this.column = column;
		this.whereString = whereString;
		this.values = values;
	}

	public String getColumn() {
		return column;
	}

	public String getWhereString() {
		return whereString;
	}

	public List<Object> getValues() {
		return unmodifiableList(values);
	}

	@SuppressWarnings("all")
	@Override
	public String toString() {
		return "SQLFilter [column=" + column + ", whereString=" + whereString
				+ ", values=" + values + ", durable=" + durable + "]";
	}

	@SuppressWarnings("all")
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		result = prime * result
				+ ((whereString == null) ? 0 : whereString.hashCode());
		result = prime * result + (durable ? 1231 : 1237);
		return result;
	}

	@SuppressWarnings("all")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SQLFilter other = (SQLFilter) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		if (whereString == null) {
			if (other.whereString != null)
				return false;
		} else if (!whereString.equals(other.whereString))
			return false;
		if (durable != other.durable)
			return false;
		return true;
	}

}
