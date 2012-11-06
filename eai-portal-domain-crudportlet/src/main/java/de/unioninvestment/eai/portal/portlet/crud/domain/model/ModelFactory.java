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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory;
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory;

/**
 * Spring-Factory-Klasse für Model-Komponenten.
 * 
 * @author carsten.mjartan
 * 
 */
@Component
public class ModelFactory {

	@Autowired
	private ConnectionPoolFactory connectionPoolFactory;

	@Autowired
	private ResetFormAction resetFormAction;

	@Autowired
	private FieldValidatorFactory fieldValidatorFactory;

	@Value("${portlet.crud.table.select.default-width}")
	private int defaultSelectWidth;

	/**
	 * Konstruktor.
	 */
	ModelFactory() {
		// for Spring
	}

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param connectionPoolFactory
	 *            ConnectionPoolFactory
	 * @param resetFormAction
	 *            ResetFormAction
	 * @param fieldValidatorFactory
	 *            FieldValidatorFactory
	 * @param defaultSelectWidth
	 *            Breite der Selectboxen
	 */
	public ModelFactory(ConnectionPoolFactory connectionPoolFactory,
			ResetFormAction resetFormAction,
			FieldValidatorFactory fieldValidatorFactory, int defaultSelectWidth) {
		this.connectionPoolFactory = connectionPoolFactory;
		this.resetFormAction = resetFormAction;
		this.fieldValidatorFactory = fieldValidatorFactory;
		this.defaultSelectWidth = defaultSelectWidth;
	}

	/**
	 * Gibt einen Model-Builder zurück.
	 * 
	 * @param config
	 *            Portletkonfiguration
	 * @return Model-Builder
	 */
	public ModelBuilder getBuilder(Config config) {
		return new ModelBuilder(this, connectionPoolFactory, resetFormAction,
				fieldValidatorFactory, defaultSelectWidth, config);
	}

	/**
	 * Gibt den Datenbankcontainer zurück.
	 * 
	 * @param datasource
	 *            die DataSource
	 * @param tablename
	 *            der Tabellenname
	 * @param deleteable
	 *            ob delete-Statements erlaubt sind
	 * @param updateable
	 *            ob update-Statements erlaubt sind
	 * @param insertable
	 *            ob insert-Statements erlaubt sind
	 * @param currentUser
	 *            Aktueller Benutzer
	 * @param formatPattern
	 *            Formatierungen nach Spaltennamen.
	 * @param orderBys
	 *            Default-Sortierung
	 * @param filterPolicy
	 *            Art des Filterhandlings
	 * @param sizeValidTimeout
	 *            Cachttimeout für die Anzahl aller selektierten Einträge
	 * @param pageLength
	 *            Anzahl der Einträge pro Seite
	 * @return eine neue Instanz des {@link DatabaseTableContainer}
	 */
	public DatabaseTableContainer getDatabaseTableContainer(String datasource,
			String tablename, boolean insertable, boolean updateable,
			boolean deleteable, CurrentUser currentUser,
			Map<String, String> formatPattern, List<ContainerOrder> orderBys,
			FilterPolicy filterPolicy, int pagelength, int exportPagelength,
			int sizeValidTimeout) {
		ConnectionPool pool = connectionPoolFactory.getPool(datasource);
		return new DatabaseTableContainer(datasource, tablename, pool,
				insertable, updateable, deleteable, currentUser, formatPattern,
				orderBys, filterPolicy, pagelength, exportPagelength,
				sizeValidTimeout);
	}

	/**
	 * 
	 * @param datasource
	 *            die DataSource
	 * @param query
	 *            die SQL-Select Abfrage
	 * @param deleteable
	 *            ob delete-Statements erlaubt sind
	 * @param updateable
	 *            ob update-Statements erlaubt sind
	 * @param insertable
	 *            ob insert-Statements erlaubt sind
	 * @param primaryKeys
	 *            eine Liste mit den Primärschlüssel der Abfrage
	 * @param currentUsername
	 *            Aktueller Benutzer
	 * @param displayPattern
	 *            Formatierungen nach Spaltennamen.
	 * @param orderBys
	 *            Default-Sortierung
	 * @param filterPolicy
	 *            Art des Filterhandlings
	 * @param sizeValidTimeout
	 *            Cachttimeout für die Anzahl aller selektierten Einträge
	 * @param pageLength
	 *            Anzahl der Einträge pro Seite
	 * @return DatabaseQueryContainer
	 */
	public DatabaseQueryContainer getDatabaseQueryContainer(String datasource,
			String query, boolean insertable, boolean updateable,
			boolean deleteable, List<String> primaryKeys,
			String currentUsername, Map<String, String> displayPattern,
			List<ContainerOrder> orderBys, FilterPolicy filterPolicy,
			int pagelength, int exportPagelength, Integer sizeValidTimeout) {
		ConnectionPool pool = connectionPoolFactory.getPool(datasource);
		return new DatabaseQueryContainer(datasource, query, insertable,
				updateable, deleteable, primaryKeys, pool, currentUsername,
				displayPattern, orderBys, filterPolicy, pagelength,
				exportPagelength, sizeValidTimeout);
	}

	/**
	 * @param formatPattern
	 *            Formatierungen nach Spaltennamen.
	 * @param defaultOrder
	 *            Default-Sortierung
	 * @param filterPolicy
	 *            Art des Filterhandlings
	 * @return den Container
	 */
	public GenericDataContainer getGenericDataContainer(
			Map<String, String> formatPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy) {
		return new GenericDataContainer(formatPattern, defaultOrder,
				filterPolicy);
	}

}
