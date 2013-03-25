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
 * Filterklasse für ein SQL Filter.
 * 
 * @author max.hartmann
 * 
 */
public final class RegExpFilter extends Filter {

	private static final long serialVersionUID = 1L;

	private final String column;
	private final String pattern;
	private final String modifiers;

	/**
	 * Creates non-durable filter.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param pattern
	 *            - Regular Expression
	 * @param modifiers
	 *            - Optional Modifiers
	 */
	public RegExpFilter(String column, String pattern, String modifiers) {
		this(column, pattern, modifiers, false);
	}

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            - Spaltenname
	 * @param pattern
	 *            - Regular Expression
	 * @param modifiers
	 *            - Optional Modifiers
	 * @param durable
	 *            Ob der Filter permanent gesetzt sein soll
	 */
	public RegExpFilter(String column, String pattern, String modifiers,
			boolean durable) {
		super(durable);
		this.column = column;
		this.pattern = pattern;
		this.modifiers = modifiers;
	}

	public String getColumn() {
		return column;
	}

	public String getPattern() {
		return pattern;
	}

	public String getModifiers() {
		return modifiers;
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((modifiers == null) ? 0 : modifiers.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		RegExpFilter other = (RegExpFilter) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (modifiers == null) {
			if (other.modifiers != null)
				return false;
		} else if (!modifiers.equals(other.modifiers))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

}
