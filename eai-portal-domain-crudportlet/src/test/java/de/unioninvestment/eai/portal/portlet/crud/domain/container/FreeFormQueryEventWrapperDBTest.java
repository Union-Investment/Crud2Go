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
package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import org.mockito.Mock;

import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

public class FreeFormQueryEventWrapperDBTest extends EventWrapperTest {

	@Mock
	private DatabaseQueryDelegate queryDelegate;

	@Override
	protected QueryDelegate getWrapper() throws SQLException {
		StatementHelper sh = new StatementHelper();
		sh.setQueryString("SELECT * FROM TABLEQUERY_WRAPPER");
		when(queryDelegate.getQueryStatement(anyInt(), anyInt()))
				.thenReturn(sh);
		StatementHelper shCount = new StatementHelper();
		shCount.setQueryString("SELECT count(*) FROM TABLEQUERY_WRAPPER");
		when(queryDelegate.getCountStatement()).thenReturn(shCount);
		when(queryDelegate.removeRow(any(Connection.class), any(RowItem.class)))
				.thenReturn(true);

		FreeformQueryEventWrapper freeformQueryEventWrapper = new FreeformQueryEventWrapper(
				container, "SELECT * FROM TABLEQUERY_WRAPPER",
				getConnectionPool(), onInsertEventRouter, onUpdateEventRouter,
				onDeleteEventRouter, "ID");
		freeformQueryEventWrapper.setDelegate(queryDelegate);
		return freeformQueryEventWrapper;
	}
}
