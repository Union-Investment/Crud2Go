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

import java.text.MessageFormat;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.FilterTranslator;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

/**
 * Diese Klasse übersetzt die Filterung über {@link AdvancedStringFilter} in
 * WHERE-Bedingungen für das SQLContainer-Backend.
 * 
 * @author carsten.mjartan
 */
public class AdvancedStringFilterTranslator implements FilterTranslator {

	private static final long serialVersionUID = 1L;

	static final String ESCAPE_CHARACTER = "~";

	@Override
	public boolean translatesFilter(Filter filter) {
		return filter instanceof AdvancedStringFilter;
	}

	@Override
	public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
		AdvancedStringFilter f = (AdvancedStringFilter) filter;
		String columnName = QueryBuilder.quote(f.getPropertyId());
		String comparisonString = createComparisonString(f);
		if (f.isIgnoreCase()) {
			sh.addParameterValue(comparisonString.toUpperCase());
			return MessageFormat.format("UPPER({0}) like ? escape ''{1}''",
					columnName, ESCAPE_CHARACTER);
		} else {
			sh.addParameterValue(comparisonString);
			return MessageFormat.format("{0} like ? escape ''{1}''",
					columnName, ESCAPE_CHARACTER);
		}
	}

	private String createComparisonString(AdvancedStringFilter f) {
		String comparisionString;
		if (f.isOnlyMatchPrefix()) {
			comparisionString = escape(f.getFilterString()) + "%";
		} else if (f.isOnlyMatchPostfix()) {
			comparisionString = "%" + escape(f.getFilterString());
		} else {
			comparisionString = "%" + escape(f.getFilterString()) + "%";
		}
		return comparisionString;
	}

	private String escape(String filterString) {
		return filterString
				.replaceAll(ESCAPE_CHARACTER,
						ESCAPE_CHARACTER + ESCAPE_CHARACTER)
				.replaceAll("%", ESCAPE_CHARACTER + "%")
				.replaceAll("_", ESCAPE_CHARACTER + "_");
	}

}
