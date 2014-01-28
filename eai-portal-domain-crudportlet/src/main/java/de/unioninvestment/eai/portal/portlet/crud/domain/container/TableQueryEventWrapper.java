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
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;
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
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.AuditLogger;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.table.CrudSQLGenerator;

/**
 * Unterklasse von {@link TableQuery}, die bei INSERT, UPDATE, DELETE und COMMIT
 * ensprechende Events auslöst.
 * 
 * @author carsten.mjartan
 */
public class TableQueryEventWrapper extends CrudTableQuery implements
		IndexResolver {

	private static final long serialVersionUID = 42L;

	private AuditLogger auditLogger;

	private final EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter;
	private final EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter;
	private final EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter;

	private DataContainer container;

	private List<Filter> filters;

	private List<OrderBy> orderBys;

	private final ConnectionPool connectionPool;

	private SQLGenerator sqlGenerator;

	private String tableName;

	/**
	 * @param container
	 *            der DatabaseContainer der den Events mitgegeben wird
	 * @param tableName
	 *            die zu verwendende Tabelle
	 * @param connectionPool
	 *            der zu verwendende {@link JDBCConnectionPool}
	 * @param sqlGenerator
	 *            Utility-Klasse für SQL-Statements
	 * @param onInsertEventRouter
	 *            der Router für INSERT Events
	 * @param onUpdateEventRouter
	 *            der Router für UPDATE Events
	 * @param onDeleteEventRouter
	 *            der Router für DELETE Events
	 * @param currentUserName
	 *            Aktueller Benutzername
	 */
	public TableQueryEventWrapper(DataContainer container, String tableName,
			ConnectionPool connectionPool, SQLGenerator sqlGenerator,
			EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter,
			EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter,
			EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter,
			CurrentUser currentUser) {

		super(tableName, connectionPool, sqlGenerator);
		this.container = container;
		this.connectionPool = connectionPool;
		this.sqlGenerator = sqlGenerator;
		this.tableName = tableName;

		this.onInsertEventRouter = onInsertEventRouter;
		this.onUpdateEventRouter = onUpdateEventRouter;
		this.onDeleteEventRouter = onDeleteEventRouter;
		this.auditLogger = new AuditLogger(currentUser);
	}

	/**
	 * @param container
	 *            der DatabaseContainer der den Events mitgegeben wird
	 * @param tableName
	 *            die zu verwendende Tabelle
	 * @param connectionPool
	 *            der zu verwendende {@link JDBCConnectionPool}
	 * @param onInsertEventRouter
	 *            der Router für INSERT Events
	 * @param onUpdateEventRouter
	 *            der Router für UPDATE Events
	 * @param onDeleteEventRouter
	 *            der Router für DELETE Events
	 * @param currentUserName
	 *            Aktueller Benutzername
	 */
	public TableQueryEventWrapper(DataContainer container, String tableName,
			ConnectionPool connectionPool,
			EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter,
			EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter,
			EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter,
			CurrentUser currentUser) {

		super(tableName, connectionPool);
		this.container = container;
		this.connectionPool = connectionPool;

		this.onInsertEventRouter = onInsertEventRouter;
		this.onUpdateEventRouter = onUpdateEventRouter;
		this.onDeleteEventRouter = onDeleteEventRouter;
		this.auditLogger = new AuditLogger(currentUser);
	}

	@Override
	public int storeRow(RowItem row) throws SQLException {
		boolean newRow = row.getId() instanceof TemporaryRowId;

		int storeRow = super.storeRow(row);

		if (storeRow == 1) {
			DatabaseContainerRow containerRow = (DatabaseContainerRow) container
					.convertItemToRow(row, false, true);
			if (newRow) {
				onInsertEventRouter.fireEvent(new InsertEvent(container,
						containerRow));

				StatementHelper sh = sqlGenerator.generateInsertQuery(
						tableName, row);
				logSQLStatement(sh, row);

			} else {
				onUpdateEventRouter.fireEvent(new UpdateEvent(container,
						containerRow));

				StatementHelper sh = sqlGenerator.generateUpdateQuery(
						tableName, row);
				logSQLStatement(sh, row);
			}
		}

		return storeRow;
	}

	@Override
	public RowId storeRowImmediately(RowItem row) throws SQLException {
		boolean newRow = row.getId() instanceof TemporaryRowId;
		RowId rowId = super.storeRowImmediately(row);

		DatabaseContainerRowId containerRowId = (DatabaseContainerRowId) container
				.convertInternalRowId(rowId);
		DatabaseContainerRow containerRow = new DatabaseContainerRow(row,
				containerRowId, container, false, true);
		if (newRow) {
			onInsertEventRouter.fireEvent(new InsertEvent(container,
					containerRow));

			StatementHelper sh = sqlGenerator.generateInsertQuery(tableName,
					row);
			logSQLStatement(sh, row);

		} else {
			onUpdateEventRouter.fireEvent(new UpdateEvent(container,
					containerRow));

			StatementHelper sh = sqlGenerator.generateUpdateQuery(tableName,
					row);
			logSQLStatement(sh, row);
		}

		return rowId;
	}

	@Override
	public boolean removeRow(RowItem row) throws SQLException {
		boolean removeRow = super.removeRow(row);
		if (removeRow) {
			DatabaseContainerRow containerRow = (DatabaseContainerRow) container
					.convertItemToRow(row, false, true);
			onDeleteEventRouter.fireEvent(new DeleteEvent(container,
					containerRow));

			StatementHelper sh = sqlGenerator.generateDeleteQuery(
					super.getTableName(), super.getPrimaryKeyColumns(),
					super.getVersionColumn(), row);
			logSQLStatement(sh, row);
		}
		return removeRow;
	}

	@Override
	public void setFilters(List<Filter> filters)
			throws UnsupportedOperationException {
		super.setFilters(filters);
		this.filters = filters;
	}

	@Override
	public void setOrderBy(List<OrderBy> orderBys)
			throws UnsupportedOperationException {
		super.setOrderBy(orderBys);
		this.orderBys = orderBys;
	}

	@Override
	public int getIndexById(RowId rowId) {
		CrudSQLGenerator sqlGenerator = (CrudSQLGenerator) getSqlGenerator();
		final StatementHelper sh = sqlGenerator.getIndexStatement(rowId,
				getTableName(), filters, orderBys);

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

	private void logSQLStatement(StatementHelper sh, RowItem row) {
		StringBuilder sb = new StringBuilder();
		sb.append(sh.getQueryString());
		sb.append(" -> Attribute <");

		Collection<?> itemPropertyIds = row.getItemPropertyIds();
		for (Object id : itemPropertyIds) {
			sb.append(id);
			sb.append(" : ");
			sb.append(row.getItemProperty(id));
			sb.append(", ");
		}
		sb.append(">");

		auditLogger.audit(sb.toString());
	}

	void setAuditLogger(AuditLogger auditLogger) {
		this.auditLogger = auditLogger;
	}
}
