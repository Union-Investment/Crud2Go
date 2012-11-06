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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;
import com.vaadin.addon.sqlcontainer.query.QueryDelegate;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.TestUser;

public class TableQueryEventWrapperDBTest extends EventWrapperTest {

	@Override
	protected QueryDelegate getWrapper() throws SQLException {
		TableQueryEventWrapper tableQueryEventWrapper = new TableQueryEventWrapper(
				container, "TABLEQUERY_WRAPPER", getConnectionPool(),
				new DerbySQLGenerator(), onInsertEventRouter,
				onUpdateEventRouter, onDeleteEventRouter, new TestUser("Anonymous"));
		return tableQueryEventWrapper;
	}

	@Test
	public void shouldFireInsertEventStoreRowImmediately()
			throws UnsupportedOperationException, SQLException {
		// StoreRowImmediately muss ohne Transaktion ausgeführt werden
		wrapper.rollback();

		rowItem = new RowItem(sqlContainer, new TemporaryRowId(
				new Object[] { 1 }), Arrays.asList(new ColumnProperty(
				"TESTSTRING", false, true, false, "TESTVAL", String.class)));

		((TableQueryEventWrapper) wrapper).storeRowImmediately(rowItem);

		ArgumentCaptor<InsertEvent> eventCaptor = ArgumentCaptor
				.forClass(InsertEvent.class);
		verify(onInsertEventRouter).fireEvent(eventCaptor.capture());

		assertThat(eventCaptor.getValue(), instanceOf(InsertEvent.class));
	}
}
