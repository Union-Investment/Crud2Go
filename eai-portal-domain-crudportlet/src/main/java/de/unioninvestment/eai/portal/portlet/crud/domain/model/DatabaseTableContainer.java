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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseTableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.TableQueryEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.table.CrudSQLGenerator;
import de.unioninvestment.eai.portal.support.vaadin.table.DefaultCrudSQLGenerator;
import de.unioninvestment.eai.portal.support.vaadin.table.OracleCrudSQLGenerator;

/**
 * Repräsentation eines Datenbanktabellen-basierten Vaadin Containers als
 * Backend für die Tabellenansicht.
 * 
 * @author carsten.mjartan
 */
public class DatabaseTableContainer extends AbstractDatabaseContainer {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseTableContainer.class);

	private final String tablename;

	private final ConnectionPool connectionPool;

	private final boolean insertable;

	private final boolean updateable;

	private final boolean deleteable;

	private CurrentUser currentUser;

	private final int pageLength;

	private final int sizeValidTimeout;

	private DatabaseDialect dialect;

	/**
	 * Konstruktor.
	 * 
	 * @param config
	 *            die Container-Konfiguration
	 * @param connectionPool
	 *            der zu verwendende ConnectionPool
	 * @param deleteable
	 *            ob delete-Statements erlaubt sind
	 * @param updateable
	 *            ob update-Statements erlaubt sind
	 * @param insertable
	 *            ob insert-Statements erlaubt sind
	 * @param filterPolicy
	 * @param sizeValidTimeout
	 *            Cachttimeout für die Anzahl aller selektierten Einträge
	 * @param pageLength
	 *            Anzahl der Einträge pro Seite
	 */
	public DatabaseTableContainer(EventBus eventBus, DatabaseTableConfig config,
                                  ConnectionPool connectionPool,
			boolean insertable, boolean updateable, boolean deleteable,
			CurrentUser currentUser, Map<String, String> formatPattern,
			List<ContainerOrder> containerOrders, FilterPolicy filterPolicy,
			int pageLength, int exportPageLength, int sizeValidTimeout,
			DatabaseDialect dialect) {
		super(eventBus, formatPattern, containerOrders, filterPolicy);
		this.datasource = config.getDatasource();
		this.tablename = config.getTablename();
		this.connectionPool = connectionPool;
		this.insertable = insertable;
		this.updateable = updateable;
		this.deleteable = deleteable;
		this.currentUser = currentUser;
		this.pageLength = pageLength;
		this.sizeValidTimeout = sizeValidTimeout;
		this.dialect = dialect;
	}

	@Override
	@SuppressWarnings("all")
	protected SQLContainerEventWrapper createVaadinContainer() {
		try {
			CrudSQLGenerator sqlGenerator = createSqlGenerator(dialect);

			queryDelegate = new TableQueryEventWrapper(this, tablename,
					connectionPool, sqlGenerator, getOnInsertEventRouter(),
					getOnUpdateEventRouter(), getOnDeleteEventRouter(),
					currentUser);
			sqlGenerator.setPrimaryKeyColumns(queryDelegate
					.getPrimaryKeyColumns());

			SQLContainerEventWrapper sqlContainerEventWrapper = new SQLContainerEventWrapper(
					queryDelegate, this, getOnCreateEventRouter());

			sqlContainerEventWrapper.setPageLength(pageLength);
			sqlContainerEventWrapper
					.setSizeValidMilliSeconds((sizeValidTimeout * 1000));

			return sqlContainerEventWrapper;

		} catch (BusinessException e) {
			LOG.debug(e.getMessage(), e);
			throw e;

		} catch (IllegalArgumentException e) {
			LOG.warn(e.getMessage(), e);
			throw new BusinessException("portlet.crud.error.tableNotFound",
					datasource, tablename);

		} catch (RuntimeException e) {
			LOG.warn(e.getMessage(), e);
			throw new BusinessException(
					"portlet.crud.error.dataSourceNotFound", datasource);

		} catch (SQLException e) {
			LOG.warn(e.getMessage(), e);
			throw new BusinessException("portlet.crud.error.tableNotFound",
					datasource, tablename);
		}
	}

	private CrudSQLGenerator createSqlGenerator(DatabaseDialect dialect) {
		CrudSQLGenerator sqlGenerator;
		switch (dialect) {
		case ORACLE:
			sqlGenerator = new OracleCrudSQLGenerator();
			break;
		case MYSQL:
			sqlGenerator = new DefaultCrudSQLGenerator();
			break;
		default:
			throw new UnsupportedOperationException("Dialect " + dialect
					+ " is not supported in DatabaseTableContainer");
		}
		return sqlGenerator;
	}

	@Override
	public boolean isInsertable() {
		return insertable;
	}

	@Override
	public boolean isUpdateable() {
		return updateable;
	}

	@Override
	public boolean isDeleteable() {
		return deleteable;
	}

	@Override
	public void removeAllFilters() {
		getVaadinContainer().removeAllContainerFilters();
	}

	@Override
	public List<String> getPrimaryKeyColumns() {
		return queryDelegate.getPrimaryKeyColumns();
	}

	@Override
	public ContainerClob getCLob(ContainerRowId containerRowId,
			String columnName) {
		throw new UnsupportedOperationException(
				"No CLob support for TableContainer.");
	}

	@Override
	public boolean isCLobModified(ContainerRowId containerRowId,
			String columnName) {
		throw new UnsupportedOperationException(
				"No CLob support for TableContainer.");
	}

	@Override
	public ContainerBlob getBLob(ContainerRowId rowId, String columnName) {
		throw new UnsupportedOperationException(
				"No BLob support for TableContainer.");
	}

	@Override
	public boolean isBLobEmpty(ContainerRowId rowId, String columnName) {
		throw new UnsupportedOperationException(
				"No BLob support for TableContainer.");
	}

	@Override
	public boolean isBLobModified(ContainerRowId containerRowId,
			String columnName) {
		throw new UnsupportedOperationException(
				"No BLob support for TableContainer.");
	}

    @Override
    protected String getNoTypeInformationForColumnMessage(String name) {
        return MessageFormat.format("Could not retrieve type information for column ''{0}'' from table ''{1}''. Does it exist in the backend?",
                name, tablename);
    }


}
