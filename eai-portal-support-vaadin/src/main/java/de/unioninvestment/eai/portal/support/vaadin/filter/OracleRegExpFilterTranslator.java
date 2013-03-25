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

import com.vaadin.addon.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.addon.sqlcontainer.query.generator.filter.FilterTranslator;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

/**
 * A Vaadin SQLContainer filter translator for {@link OracleRegExpFilter}.
 * 
 * @author carsten.mjartan
 * 
 */
public class OracleRegExpFilterTranslator implements FilterTranslator {

	@Override
	public boolean translatesFilter(Filter filter) {
		return filter instanceof OracleRegExpFilter;
	}

	@Override
	public String getWhereStringForFilter(Filter filter, StatementHelper sh) {

		OracleRegExpFilter regexpFilter = (OracleRegExpFilter) filter;

		String columnName = QueryBuilder.quote(regexpFilter.getColumn());
		sh.addParameterValue(regexpFilter.getPattern());

		if (regexpFilter.getMatchParameter() != null) {
			sh.addParameterValue(regexpFilter.getMatchParameter());
			return MessageFormat.format("REGEXP_LIKE({0},?,?)", columnName);
		} else {
			return MessageFormat.format("REGEXP_LIKE({0},?)", columnName);
		}
	}

}
