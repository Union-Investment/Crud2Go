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

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.SQLContainer;
import com.vaadin.addon.sqlcontainer.query.QueryDelegate;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.IndexResolver;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Wrapper zum Feuern von {@link CreateEvent}'s. Er wird nur die
 * {@link SQLContainer#addItem()}-Methode überschrieben.
 * 
 * @author max.hartmann
 * 
 * @see {@link SQLContainer}
 */
public class SQLContainerEventWrapper extends SQLContainer implements
		Filterable {

	private static final long serialVersionUID = 42L;

	private EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter;

	private final DataContainer databaseContainer;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param delegate
	 *            QueryDelegate
	 * @param databaseContainer
	 *            DatabaseContainer
	 * @param onCreateEventRouter
	 *            EventRouter
	 * 
	 * @throws SQLException
	 *             SQL-Fehler
	 */
	public SQLContainerEventWrapper(QueryDelegate delegate,
			DataContainer databaseContainer,
			EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter)
			throws SQLException {
		super(delegate);
		this.databaseContainer = databaseContainer;
		this.onCreateEventRouter = onCreateEventRouter;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		Object rowId = super.addItem();

		RowItem rowItem = (RowItem) this.getItem(rowId);
		onCreateEventRouter.fireEvent(new CreateEvent(databaseContainer,
				databaseContainer.convertItemToRow(rowItem, false, false)));

		return rowId;
	}

	/**
	 * Sichtbarkeit erhöhen. Erlaubt das Anstoßen des Rerenderings bei Table ohne dass die Datenbank gepollt wird.
	 */
	@Override
	public void fireContentsChange() {

		super.fireContentsChange();
	}
	
	@Override
	public int indexOfId(Object itemId) {
		if (getItemUnfiltered(itemId) != null
				|| !(getQueryDelegate() instanceof IndexResolver)) {
			return super.indexOfId(itemId);
		}
		if (itemId != null) {
			IndexResolver tableQuery = (IndexResolver) getQueryDelegate();
			int index = tableQuery.getIndexById((RowId) itemId);

			getIdByIndex(index);

			return index;
		}
		return -1;
	}

	public void replaceContainerFilter(Filter filter) {
		clearFiltersWithoutRefresh();
		addContainerFilter(filter);
	}

	@SuppressWarnings("unchecked")
	private void clearFiltersWithoutRefresh() {
		try {
			Field filtersField = SQLContainer.class.getDeclaredField("filters");
			filtersField.setAccessible(true);
			List<Filter> filters = (List<Filter>) filtersField.get(this);
			filters.clear();

		} catch (Exception e) {
			throw new TechnicalCrudPortletException(
					"'filters' field not accessible", e);
		}
	}
}
