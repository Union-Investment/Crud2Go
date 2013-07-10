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
package de.unioninvestment.eai.portal.support.vaadin.table;

import static java.util.Arrays.asList;

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.OracleGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

/**
 * Spezielle Ableitung der Klasse OracleGenerator, um den Zugriff auf die
 * Zeilennummer über den Primärschlüssel zu beschleunigen.
 * 
 * 
 * @author siva.selvarajah
 */
public class CrudOracleGenerator extends OracleGenerator {

	private static final long serialVersionUID = 42L;

	private List<String> primaryKeyColumns;

	/**
	 * Erzeugt ein Statement, um auf eine bestimmte Zeile zugreifen zu können.
	 * 
	 * @param rowId
	 *            Primärschlüssel
	 * @param tableName
	 *            Tabellenname
	 * @param filters
	 *            Filterliste
	 * @param orderBys
	 *            Sortierung
	 * @return StatementHelper
	 */
	public StatementHelper getIndexStatement(RowId rowId, String tableName,
			List<Filter> filters, List<OrderBy> orderBys) {
		StatementHelper sh = generateSelectQuery(tableName, filters, orderBys,
				0, 0, null);
		StringBuffer query = new StringBuffer();

		query.append("SELECT * FROM (");
		query.append("SELECT x.*, ROWNUM AS \"rownum\" FROM (");
		query.append("SELECT * FROM ");
		query.append(tableName);
		if (orderBys != null && orderBys.size() > 0) {
			appendOrderBy(orderBys, query);
		} else {
			appendOrderBy(
					asList(new OrderBy[] { new OrderBy(
							primaryKeyColumns.get(0), true) }), query);
		}
		query.append(") x ")
				.append(QueryBuilder.getWhereStringForFilters(filters, sh))
				.append(")");

		int idx = 0;
		for (String idName : primaryKeyColumns) {
			if (idx == 0) {
				query.append(" WHERE ");
			} else {
				query.append(" AND ");
			}
			query.append(idName).append("=?");
			sh.addParameterValue(rowId.getId()[idx]);

			idx++;
		}

		sh.setQueryString(query.toString());
		return sh;
	}

	public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
		this.primaryKeyColumns = primaryKeyColumns;
	}

	private void appendOrderBy(List<OrderBy> orderBys, StringBuffer query) {
		if (orderBys != null) {
			for (OrderBy o : orderBys) {
				generateOrderBy(query, o, orderBys.indexOf(o) == 0);
			}
		}
	}

}
