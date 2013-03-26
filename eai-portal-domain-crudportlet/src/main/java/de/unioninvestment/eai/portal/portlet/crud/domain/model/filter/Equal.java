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

/**
 * Filterklasse für ein SQL Gleichoperator.
 * 
 * @author markus.bonsch
 * 
 */
public final class Equal extends Filter {

	private static final long serialVersionUID = 1L;

	private final String column;
	private final Object value;

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param value
	 *            - Vergleichswert
	 */
	public Equal(String column, Object value) {
		this(column, value, false);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param value
	 *            - Vergleichswert
	 * @param durable
	 *            Ob der Filter permanent gesetzt sein soll
	 */
	public Equal(String column, Object value, boolean durable) {
		super(durable);
		this.column = column;
		this.value = value;
	}

	public String getColumn() {
		return column;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Equal [column=" + column + ", value=" + value + ", durable="
				+ durable + "]";
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equal other = (Equal) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
