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

import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

public class DefaultCrudSQLGenerator extends DefaultSQLGenerator implements
		CrudSQLGenerator {

	private static final long serialVersionUID = 1L;

	private List<String> primaryKeyColumns;

	@Override
	public StatementHelper getIndexStatement(RowId rowId, String tableName,
			List<Filter> filters, List<OrderBy> orderBys) {
		StatementHelper sh = super.generateSelectQuery(tableName, filters,
				orderBys, 0, 0, null);
		StringBuilder query = new StringBuilder();
		query.append("SELECT c.`rownum` FROM (");
		query.append("SELECT @rn:=@rn+1 `rownum`, a.* FROM (");
		query.append(sh.getQueryString());
		query.append(") a, (SELECT @rn:=0) r");
		query.append(") c");
		query.append(getWhereStringForPrimaryKey(rowId, primaryKeyColumns, sh));

		sh.setQueryString(query.toString());
		return sh;
	}

	public static String getWhereStringForPrimaryKey(RowId rowId,
			List<String> primaryKeyColumns, StatementHelper sh) {
		int idx = 0;
		StringBuilder builder = new StringBuilder();
		for (String idName : primaryKeyColumns) {
			if (idx == 0) {
				builder.append(" WHERE ");
			} else {
				builder.append(" AND ");
			}
			builder.append(QueryBuilder.quote(idName)).append("=?");
			sh.addParameterValue(rowId.getId()[idx]);

			idx++;
		}
		return builder.toString();
	}

	@Override
	public void setPrimaryKeyColumns(List<String> primaryKeyColumns) {
		this.primaryKeyColumns = primaryKeyColumns;
	}
}
