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

import java.util.ArrayList;
import java.util.List;

/**
 * Filterklasse für ein SQL Oder-Operator.
 * 
 * @author max.hartmann
 * 
 */
public final class Any extends Filter {

	private static final long serialVersionUID = 1L;
	private final List<Filter> filters = new ArrayList<Filter>();

	/**
	 * Konstuktor mit Parameter.
	 * 
	 * @param filters
	 *            Liste und-verknüpfter Filter
	 * @param durable
	 *            Ob der Filter permanent gesetzt sein soll
	 */
	public Any(List<Filter> filters, boolean durable) {
		super(durable);
		if (filters != null) {
			this.filters.addAll(filters);
		}
	}

	/**
	 * Konstuktor mit Parameter.
	 * 
	 * @param filters
	 *            Liste und-verknüpfter Filter
	 */
	public Any(List<Filter> filters) {
		this(filters, false);
	}

	public List<Filter> getFilters() {
		return filters;
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + (durable ? 1231 : 1237);
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Any other = (Any) obj;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (durable != other.durable)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Any [filters=" + filters + ", durable=" + durable + "]";
	}
}
