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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * 
 * Modell-Klasse für Auswahl-Boxen.
 * 
 * @author max.hartmann
 * 
 */
public class QueryOptionList implements OptionList {

	private Map<String, String> options;
	private String query;
	private ConnectionPool connectionPool;
	private Object lock = new Object();
	private String id;
	private EventRouter<OptionListChangeEventHandler, OptionListChangeEvent> changeEventRouter = new EventRouter<OptionListChangeEventHandler, OptionListChangeEvent>();
	
	/**
	 * Konstruktor mit Parametern. Wird verwendet, wenn die Optionen aus der
	 * Daten Datenbank gelesen werden sollen.
	 * 
	 * @param connectionPool
	 *            Connection-Pool
	 * @param config
	 *            Konfiguration der Auswahl-Box
	 */
	public QueryOptionList(ConnectionPool connectionPool, SelectConfig config) {
		this.connectionPool = connectionPool;
		this.query = config.getQuery().getValue();

		id = config.getId();
	}

	/**
	 * Konstruktor mit Parametern. Die Option werden direkt übergeben.
	 * 
	 * @param options
	 *            Auswahl-Optionen
	 */
	public QueryOptionList(Map<String, String> options) {
		this.options = options;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getOptions(SelectionContext context) {
		synchronized (lock) {
			if (options == null) {
				loadOptions();
			}
			return options;
		}
	}

	/**
	 * 
	 * Nur für UnitTests Liefert die Einträge. Beachten, dass die Werte nicht
	 * aus der DB geladen werden.
	 * 
	 * @return Einträge im Objekt
	 */
	Map<String, String> getOptions() {
		return options;
	}

	private void loadOptions() {
		String nullSafeQuery = nullSafeQuery(query);
		options = new LinkedHashMap<String, String>();
		connectionPool.executeWithJdbcTemplate(nullSafeQuery,
				new RowMapper<Object>() {
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						options.put(rs.getString("key"), rs.getString("title"));
						return null;
					}
				});
	}

	/**
	 * Macht eine Abfrage NULL-Sicher.
	 * 
	 * @param query
	 *            Abfrage
	 * @return NULL-Sichere-Abfrage
	 */
	static String nullSafeQuery(String query) {
		StringBuilder sb = new StringBuilder();
		sb.append("select key, title from (");
		sb.append(query);
		sb.append(") where key is not null and title is not null");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(String key, SelectionContext context) {
		if (getOptions(context) != null) {
			return getOptions(context).get(key);
		}
		return null;
	}

	/**
	 * Entfernt die gepufferten Werte.
	 */
	@Override
	public void refresh() {
		options = null;
		changeEventRouter.fireEvent(new OptionListChangeEvent(this));
	}

	public String getId() {
		return id;
	}

	@Override
	public void addValueChangeListener(OptionListChangeEventHandler handler) {
		changeEventRouter.addHandler(handler);
	}
}
