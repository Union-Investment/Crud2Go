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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRow;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

/**
 * Unterklasse von {@link FreeformQuery}, die bei INSERT, UPDATE, DELETE und
 * COMMIT ensprechende Events auslöst.
 * 
 * @author carsten.mjartan
 */
public class FreeformQueryEventWrapper extends CrudFreeformQuery implements
		IndexResolver {

	private static final long serialVersionUID = 42L;

	private EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter;
	private EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter;
	private EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter;

	private final DataContainer container;

	private final ConnectionPool connectionPool;

	private boolean orderByPrimaryKeys;

	/**
	 * @param container
	 *            der DatabaseContainer der den Events mitgegeben wird
	 * @param queryString
	 *            die Datenbank-Query
	 * @param connectionPool
	 *            der zu verwendende {@link JDBCConnectionPool}
	 * @param onInsertEventRouter
	 *            der Router für INSERT Events
	 * @param onUpdateEventRouter
	 *            der Router für UPDATE Events
	 * @param onDeleteEventRouter
	 *            der Router für DELETE Events
	 * @param primaryKeyColumns
	 *            die konfigurierten Primärschlüsselspalten
	 * @param orderByPrimaryKeys
	 */
	public FreeformQueryEventWrapper(DataContainer container,
			String queryString, ConnectionPool connectionPool,
			EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter,
			EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter,
			EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter,
			boolean orderByPrimaryKeys, String... primaryKeyColumns) {
		super(queryString, connectionPool, primaryKeyColumns);
		this.container = container;
		this.connectionPool = connectionPool;
		this.onInsertEventRouter = onInsertEventRouter;
		this.onUpdateEventRouter = onUpdateEventRouter;
		this.onDeleteEventRouter = onDeleteEventRouter;
		this.orderByPrimaryKeys = orderByPrimaryKeys;
	}

	@Override
	public int storeRow(RowItem row) throws SQLException {
		boolean newRow = row.getId() instanceof TemporaryRowId;
		int storeRow = super.storeRow(row);

		DatabaseContainerRow containerRow = (DatabaseContainerRow) container
				.convertItemToRow(row, false, true);

		if (newRow) {
			onInsertEventRouter.fireEvent(new InsertEvent(container,
					containerRow));
		} else {
			onUpdateEventRouter.fireEvent(new UpdateEvent(container,
					containerRow));
		}

		return storeRow;
	}

	@Override
	public boolean removeRow(RowItem row) throws SQLException {
		boolean removeRow = super.removeRow(row);
		if (removeRow) {
			DatabaseContainerRow containerRow = (DatabaseContainerRow) container
					.convertItemToRow(row, false, true);
			onDeleteEventRouter.fireEvent(new DeleteEvent(container,
					containerRow));
		}
		return removeRow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexById(RowId rowId) {
		DatabaseQueryDelegate delegate = (DatabaseQueryDelegate) getDelegate();
		StatementHelper sh = delegate.getIndexStatement(rowId);

		int rownum = connectionPool.querySingleResultWithJdbcTemplate(sh,
				new RowMapper<Integer>() {

					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt("rownum");
					}
				});

		return rownum - 1;
	}

	public byte[] getBLob(RowId rowId, final String columnName) {
		DatabaseQueryDelegate delegate = (DatabaseQueryDelegate) getDelegate();
		StatementHelper sh = delegate.getRowByIdStatement(rowId);
		final LobHandler lobHandler = createLobHandler();

		byte[] blobBytes = connectionPool.querySingleResultWithJdbcTemplate(sh,
				new RowMapper<byte[]>() {
					@Override
					public byte[] mapRow(ResultSet rs, int i)
							throws SQLException {
						return lobHandler.getBlobAsBytes(rs, columnName);
					}
				});
		return blobBytes;
	}

	private LobHandler createLobHandler() {
		DefaultLobHandler lobHandler = new DefaultLobHandler();
		lobHandler.setStreamAsLob(true);
		lobHandler.setCreateTemporaryLob(true);
		return lobHandler;
	}

	public boolean hasBlobData(RowId rowId, final String columnName) {
		DatabaseQueryDelegate delegate = (DatabaseQueryDelegate) getDelegate();
		StatementHelper sh = delegate.getRowByIdStatement(rowId);
		boolean result = connectionPool.querySingleResultWithJdbcTemplate(sh,
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getBlob(columnName) != null;
					}
				});
		return result;
	}

	public String getCLob(RowId rowId, final String columnName) {
		DatabaseQueryDelegate delegate = (DatabaseQueryDelegate) getDelegate();
		StatementHelper sh = delegate.getRowByIdStatement(rowId);
		final LobHandler lobHandler = createLobHandler();

		String clobString = connectionPool.querySingleResultWithJdbcTemplate(
				sh, new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int i)
							throws SQLException {
						return lobHandler.getClobAsString(rs, columnName);
					}
				});
		return clobString;
	}

	@Override
	public void setOrderBy(List<OrderBy> orderBys)
			throws UnsupportedOperationException {
		if (orderByPrimaryKeys) {
			List<OrderBy> newOrderBys = createOrderBysIncludingPrimaryKeys(orderBys, getPrimaryKeyColumns());
			super.setOrderBy(newOrderBys);
		} else {
			super.setOrderBy(orderBys);
		}
	}

	static List<OrderBy> createOrderBysIncludingPrimaryKeys(List<OrderBy> orderBys, List<String> primaryKeyColumns) {
		if (primaryKeyColumns == null) {
			return orderBys;
		}
		
		List<String> remainingPrimaryKeyColumns = new ArrayList<String>(primaryKeyColumns);
		List<OrderBy> newOrderBys = new ArrayList<OrderBy>(primaryKeyColumns.size());
		if (orderBys != null) {
			for (OrderBy orderBy : orderBys) {
				newOrderBys.add(orderBy);
				remainingPrimaryKeyColumns.remove(orderBy.getColumn());
			}
		}
		for (String primaryKeyColumn : remainingPrimaryKeyColumns) {
			newOrderBys.add(new OrderBy(primaryKeyColumn, true));
		}
		return newOrderBys;
	}
}
