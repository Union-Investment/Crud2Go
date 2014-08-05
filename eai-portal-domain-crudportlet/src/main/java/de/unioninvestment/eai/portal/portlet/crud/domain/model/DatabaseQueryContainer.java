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

import static java.util.Collections.unmodifiableList;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import de.unioninvestment.eai.portal.portlet.crud.config.DatabaseQueryConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.StatementConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.QueryCursorDataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.table.DatabaseQueryDelegate;

/**
 * Repräsentation eines auf SQL Anweisungen basierten Vaadin Containers als
 * Backend für die Tabellenansicht.
 * 
 * @author markus.bonsch
 * 
 */
public class DatabaseQueryContainer extends AbstractDatabaseContainer {
	private static final List<OrderBy> EMPTY_ORDER_BY = unmodifiableList(new LinkedList<OrderBy>());

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseQueryContainer.class);

	private final String query;
    private final DatabaseQueryConfig config;

    private boolean insertable;
	private boolean updateable;
	private boolean deleteable;

	private final List<String> primaryKeys;

	private final ConnectionPool connectionPool;

	private DatabaseQueryDelegate databaseQueryDelegate;

	@SuppressWarnings("unused")
	private String currentUsername;

	private final Integer pageLength;

	private final Integer sizeValidTimeout;

	private final Integer exportPageLength;

	private boolean orderByPrimaryKeys;

	/**
	 * Erzeugt bei der Initialisierung eine neue {@link SQLContainer} Instanz
	 * auf Basis der übergebenen SQLQuery.
	 * 
	 * @param datasource
	 *            Das DataSource-Kürzel
	 * @param sqlQuery
	 *            Das select für die Query-Ansicht
	 * @param insertable
	 *            ob insert-Statements erlaubt sind
	 * @param updateable
	 *            ob update-Statements erlaubt sind
	 * @param deleteable
	 *            ob delete-Statements erlaubt sind
	 * @param primaryKeys
	 *            Liste der Primarykeys
	 * @param connectionPool
	 *            Der zu verwendende ConnectionPool
	 * @param currentUsername
	 *            Aktueller Benutzername
	 * @param filterPolicy
	 * @param sizeValidTimeout
	 *            Cachttimeout für die Anzahl aller selektierten Einträge
	 * @param pageLength
	 *            Anzahl der Einträge pro Seite
	 * @param orderByPrimaryKeys
	 *            Es wird unabhängig von der aktuellen Sortierung immer auch
	 *            nach den Primärschlüsselspalten sortiert. Dies ist nur bei
	 *            Queries notwendig, die Daten nicht in einer konsistenten
	 *            Reihenfolge liefern.
	 */
	public DatabaseQueryContainer(EventBus eventBus, DatabaseQueryConfig config,
			String sqlQuery, boolean insertable, boolean updateable,
			boolean deleteable, List<String> primaryKeys,
			ConnectionPool connectionPool, String currentUsername,
			Map<String, String> displayPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy,
			int pageLength, int exportPageLength, Integer sizeValidTimeout,
			boolean orderByPrimaryKeys) {
		super(eventBus, displayPattern, defaultOrder, filterPolicy);

		this.insertable = insertable;
		this.updateable = updateable;
		this.deleteable = deleteable;
		this.pageLength = pageLength;
		this.exportPageLength = exportPageLength;
		this.sizeValidTimeout = sizeValidTimeout;
		this.orderByPrimaryKeys = orderByPrimaryKeys;
		Assert.notNull(config.getDatasource(), "DataSource is required");
		Assert.notNull(sqlQuery, "SQL-Query is required");

        this.config = config;
		this.datasource = config.getDatasource();
        this.tablename = config.getTablename();

		this.query = sqlQuery;
		this.primaryKeys = primaryKeys;
		this.connectionPool = connectionPool;
		this.currentUsername = currentUsername;

		checkThatPrimaryKeysExistForEditing();
	}

	private void checkThatPrimaryKeysExistForEditing() {
		if (isEditable()) {
			if (primaryKeys == null || primaryKeys.isEmpty()) {
				throw new BusinessException(
						"portlet.crud.error.primaryKeysRequired");
			}
		}
	}

	private boolean isEditable() {
		return insertable || updateable || deleteable;
	}

	private FreeformQueryEventWrapper getFreeformQueryEventWrapper() {
		return (FreeformQueryEventWrapper) this.queryDelegate;
	}

	@Override
	protected SQLContainerEventWrapper createVaadinContainer() {
		try {
			queryDelegate = new FreeformQueryEventWrapper(this, query,
					connectionPool, getOnInsertEventRouter(),
					getOnUpdateEventRouter(), getOnDeleteEventRouter(),
					orderByPrimaryKeys,
					primaryKeys.toArray(new String[primaryKeys.size()]));
			this.getFreeformQueryEventWrapper().setDelegate(
					databaseQueryDelegate);
			SQLContainerEventWrapper sqlContainerEventWrapper = new SQLContainerEventWrapper(
					queryDelegate, this, getOnCreateEventRouter());

			sqlContainerEventWrapper.setPageLength(pageLength);
			sqlContainerEventWrapper
					.setSizeValidMilliSeconds((sizeValidTimeout * 1000));

			// sqlContainerEventWrapper.setDebugMode(false);
			return sqlContainerEventWrapper;

		} catch (SQLException e) {
			LOG.warn(e.getLocalizedMessage(), e);
			throw new BusinessException("portlet.crud.error.wrongQuery",
					datasource, query);
		} catch (RuntimeException e) {
			LOG.warn(e.getLocalizedMessage(), e);
			throw new BusinessException("portlet.crud.error.wrongQuery",
					datasource, query);
		}
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
	public List<String> getPrimaryKeyColumns() {
		return primaryKeys;
	}

	/**
	 * @param databaseQueryDelegate
	 *            wird aus Scripting erstellt
	 */
	public void setDatabaseQueryDelegate(
			DatabaseQueryDelegate databaseQueryDelegate) {
		this.databaseQueryDelegate = databaseQueryDelegate;
	}

	public DatabaseQueryDelegate getDatabaseQueryDelegate() {
		return databaseQueryDelegate;
	}

	@Override
	public void commit() {
		markRowsWithModifiedLobsAsModified();
		super.commit();
		clearAdditionalFields();
	}

	private void markRowsWithModifiedLobsAsModified() {
		for (Entry<ContainerRowId, Map<String, ContainerBlob>> rowBlobs : blobFields
				.entrySet()) {
			for (ContainerBlob blob : rowBlobs.getValue().values()) {
				if (blob.isModified()) {
					getVaadinContainer().markRowAsModified(
							rowBlobs.getKey().getInternalId());
				}
			}
		}
		for (Entry<ContainerRowId, Map<String, ContainerClob>> rowClobs : clobFields
				.entrySet()) {
			for (ContainerClob clob : rowClobs.getValue().values()) {
				if (clob.isModified()) {
					getVaadinContainer().markRowAsModified(
							rowClobs.getKey().getInternalId());
				}
			}
		}
	}

	@Override
	public void rollback() {
		try {
			super.rollback();
		} finally {
			clearAdditionalFields();
		}
	}

	@Override
	public ContainerClob getCLob(ContainerRowId containerRowId,
			String columnName) {

		if (clobFields.containsKey(containerRowId)) {
			if (clobFields.get(containerRowId).containsKey(columnName)) {
				return clobFields.get(containerRowId).get(columnName);
			}
		}

		if (containerRowId.getInternalId() instanceof TemporaryRowId) {
			ContainerClob clob = new ContainerClob();
			setCLob(containerRowId, columnName, clob);
			return clob;
		}

		ContainerClob clob = new ContainerClob(
				this.getFreeformQueryEventWrapper(), containerRowId, columnName);
		setCLob(containerRowId, columnName, clob);
		return clob;
	}

	@Override
	public boolean isBLobEmpty(ContainerRowId rowId, String columnName) {
		return this.getFreeformQueryEventWrapper().hasBlobData(
				(RowId) rowId.getInternalId(), columnName);
	}

	@Override
	public ContainerBlob getBLob(ContainerRowId containerRowId,
			String columnName) {
		if (blobFields.containsKey(containerRowId)) {
			if (blobFields.get(containerRowId).containsKey(columnName)) {
				return blobFields.get(containerRowId).get(columnName);
			}
		}

		if (containerRowId.getInternalId() instanceof TemporaryRowId) {
			ContainerBlob blob = new ContainerBlob();
			setBLob(containerRowId, columnName, blob);
			return blob;
		}

		ContainerBlob blob = new ContainerBlob(
				this.getFreeformQueryEventWrapper(), containerRowId, columnName);
		setBLob(containerRowId, columnName, blob);
		return blob;
	}

	/**
	 * For unit test
	 * 
	 * @param queryDelegate
	 */
	void setQueryDelegate(FreeformQueryEventWrapper queryDelegate) {
		this.queryDelegate = queryDelegate;
	}

	@Override
	public void withExportSettings(ExportWithExportSettings exportCallback) {
		try {
			getVaadinContainer().setPageLength(exportPageLength);
			super.withExportSettings(exportCallback);
		} finally {
			getVaadinContainer().setPageLength(pageLength);
		}
	}

	public StatementHelper getCurrentQuery(boolean preserveOrder) {
		// just to initialize if not already done
		getVaadinContainer();

		if (preserveOrder) {
			return databaseQueryDelegate.getQueryStatement(0, 0);
		} else {
			List<OrderBy> previousOrder = databaseQueryDelegate.getOrderBy();
			try {
				databaseQueryDelegate.setOrderBy(EMPTY_ORDER_BY);
				return databaseQueryDelegate.getQueryStatement(0, 0);

			} finally {
				databaseQueryDelegate.setOrderBy(previousOrder);
			}
		}
	}

	@Override
	public DataStream getStream() {
		return new QueryCursorDataStream(getVaadinContainer(), connectionPool, getCurrentQuery(true));
	}
}
