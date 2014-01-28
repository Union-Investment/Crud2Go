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
 * A Vaadin SQLContainer filter translator for {@link DatabaseRegExpFilter}.
 * 
 * @author carsten.mjartan
 * 
 */
public class MySQLRegExpFilterTranslator implements FilterTranslator {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean translatesFilter(Filter filter) {
		return filter instanceof DatabaseRegExpFilter;
	}

	@Override
	public String getWhereStringForFilter(Filter filter, StatementHelper sh) {

		DatabaseRegExpFilter regexpFilter = (DatabaseRegExpFilter) filter;
		if (regexpFilter.getMatchParameter() != null) {
			throw new IllegalArgumentException("Modifiers are not supported by MySQL REGEXP");
		}
		
		String columnName = QueryBuilder.quote(regexpFilter.getColumn());
		sh.addParameterValue(regexpFilter.getPattern());

		return MessageFormat.format("{0} REGEXP ?", columnName);
	}

}
