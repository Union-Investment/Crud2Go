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

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * A Vaadin SQLContainer Filter for Oracle REGEXP_LIKE
 * 
 * @author markus.bonsch
 * 
 */
public class OracleRegExpFilter implements Filter {

	private static final long serialVersionUID = 42L;

	private String column;
	private String pattern;
	private String matchParameter;

	/**
	 * Konstruktor.
	 * 
	 * @param column
	 *            Spalte
	 * @param pattern
	 *            The RegExp-Pattern formatted as expected by the Oracle
	 *            REGEXP_LIKE function
	 * @param matchParameter
	 *            'i': Case Insensitive, 'c': Case Sensitive, 'n': Dot matches
	 *            newline, 'm': '^' and '$' match line begin/end in multiline
	 *            string
	 */
	public OracleRegExpFilter(String column, String pattern,
			String matchParameter) {
		this.column = column;
		this.pattern = pattern;
		this.matchParameter = matchParameter;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		return true;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return this.column.equals(propertyId);
	}

	public String getColumn() {
		return this.column;
	}

	public String getPattern() {
		return pattern;
	}

	public String getMatchParameter() {
		return matchParameter;
	}

}
