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
 * Ein Vaadin SQLContainer Filtertranslator für SQL-Where Filter.
 * 
 * @author markus.bonsch
 * 
 */
public class SQLFilterTranslator implements FilterTranslator {

	@Override
	public boolean translatesFilter(Filter filter) {
		return filter instanceof SQLFilter;
	}

	@Override
	public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
		SQLFilter sqlFilter = (SQLFilter) filter;

		for (Object param : sqlFilter.getValues()) {
			sh.addParameterValue(param);
		}

		String columnName = QueryBuilder.quote(sqlFilter.getColumn());
		return MessageFormat.format("{0} {1}", columnName,
				sqlFilter.getWhereString());
	}

}
