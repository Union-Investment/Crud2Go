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
package de.unioninvestment.eai.portal.support.vaadin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * Generische Implementierung des {@link Container}, erweitert um
 * Transaktionalität, Event-Handling usw.
 * 
 * @author carsten.mjartan
 */
public class GenericVaadinContainer extends
		AbstractInMemoryContainer<GenericItemId, String, GenericItem> implements
		Container, Container.Filterable, Container.Sortable,
		Container.ItemSetChangeNotifier {

	private static final long serialVersionUID = 1L;
	private final GenericDelegate delegate;
	private final MetaData metaData;

	private Map<GenericItemId, GenericItem> items = new HashMap<GenericItemId, GenericItem>();
	private Map<GenericItemId, GenericItem> modifiedItems = new HashMap<GenericItemId, GenericItem>();
	private Map<GenericItemId, GenericItem> addedItems = new HashMap<GenericItemId, GenericItem>();
	private Map<GenericItemId, GenericItem> deletedItems = new HashMap<GenericItemId, GenericItem>();
	private boolean filteringByDelegate;

	/**
	 * @param delegate
	 *            die Backend-Klasse für CRUD-Operationen
	 */
	public GenericVaadinContainer(GenericDelegate delegate) {
		this.delegate = delegate;
		this.metaData = delegate.getMetaData();
		filteringByDelegate = false;
		refresh();
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return metaData.getColumnNames();
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return metaData.getColumnType(propertyId.toString());
	}

	/**
	 * Verwendung der Default-Implementierung der Superklasse (Spalten mit
	 * primitiven Datentypen oder Comparable Interface)
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		return super.getSortablePropertyIds();
	}

	/**
	 * Verwendung des Standardsortieralgorithmus der Superklasse
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		super.sortContainer(propertyId, ascending);
	}

	@Override
	public void addContainerFilter(Filter filter)
			throws UnsupportedFilterException {
		super.addFilter(filter);
	}

	@Override
	public void removeContainerFilter(Filter filter) {
		super.removeFilter(filter);

	}

	@Override
	public void removeAllContainerFilters() {
		super.removeAllFilters();

	}

	@Override
	public Collection<Filter> getContainerFilters() {
		return super.getContainerFilters();
	}

	/**
	 * @param filter
	 *            die Liste der Filter als Ersatz
	 */
	public void replaceContainerFilter(Filter filter) {
		super.removeAllFilters();
		super.addFilter(filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		return getItem(itemId).getItemProperty(propertyId);
	}

	@Override
	protected void registerNewItem(int position, GenericItemId itemId,
			GenericItem item) {
		items.put(itemId, item);
	}

	@Override
	protected GenericItem getUnfilteredItem(Object itemId) {
		return items.get(itemId);
	}

	/**
	 * @return <code>true</code>, falls Inhalte seit dem letzten commit/rollback
	 *         geändert wurden
	 */
	public boolean isModified() {
		return numberOfModifiedItems() > 0;
	}

	/**
	 * Lädt die Container-Inhalte neu aus dem Backend.
	 */
	public void refresh() {
		internalRemoveAllItems();
		items.clear();
		for (Object[] cells : delegate.getRows()) {
			GenericItemId newItemId = createItemId(cells);
			if (modifiedItems.containsKey(newItemId)) {
				GenericItem item = modifiedItems.get(newItemId);
				internalAddItemAtEnd(newItemId, item, false);
			} else {
				GenericItem item = createItem(newItemId, cells);
				internalAddItemAtEnd(newItemId, item, false);
			}
		}
		if (!filteringByDelegate) {
			removeDroppedModifiedItems();
		}

		filterAll();

		fireContentsChange();
	}

	private void removeDroppedModifiedItems() {
		for (Iterator<Entry<GenericItemId, GenericItem>> it = modifiedItems
				.entrySet().iterator(); it.hasNext();) {
			Entry<GenericItemId, GenericItem> entry = it.next();
			if (!items.containsKey(entry.getKey())) {
				it.remove();
			}
		}
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {

		GenericItem item = items.remove(itemId);
		if (item == null) {
			return false;
		}
		internalRemoveItem(itemId);
		if (!item.isNewItem() || addedItems.remove(itemId) == null) {
			deletedItems.put((GenericItemId) itemId, item);
		}
		return true;
	};

	/**
	 * @param cells
	 *            Liste der Spaltenwerte des {@link Item} in Reihenfolge der
	 *            MetaDaten
	 * @return die ID
	 */
	GenericItemId createItemId(Object[] cells) {
		Object[] id = new Object[metaData.getPrimaryKeys().size()];
		int idx = 0;
		for (String pk : metaData.getPrimaryKeys()) {
			id[idx++] = cells[metaData.getIndex(pk)];
		}
		return new GenericItemId(id);
	}

	/**
	 * @param id
	 *            die ID der Zeile
	 * @param cells
	 *            die Spaltenwerte
	 * @return die generierte Zeile
	 */
	public GenericItem createItem(GenericItemId id, Object[] cells) {
		@SuppressWarnings("rawtypes")
		Collection<GenericProperty> properties = new ArrayList<GenericProperty>(
				cells.length);
		int idx = 0;
		for (Column column : metaData.getColumns()) {
			properties.add(new GenericProperty<Object>(column, cells[idx++]));
		}
		return new GenericItem(this, id, properties);
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		GenericItemId newItemId = new TemporaryItemId(new Object[metaData
				.getPrimaryKeys().size()]);
		GenericItem item = createItem(newItemId, new Object[metaData
				.getColumns().size()]);
		created(item);
		internalAddItemAtEnd(newItemId, item, true);
		addedItems.put(newItemId, item);
		return newItemId;
	}

	/**
	 * Methode, die bei der Erstellung einer neuen Zeile per addItem()
	 * aufgerufen wird.
	 * 
	 * @param item
	 *            eine neue Zeile
	 */
	protected void created(GenericItem item) {
		// to be overwritten
	}

	/**
	 * Erlaubt das Anstoßen des Rerenderings bei Table ohne dass die Datenbank
	 * gepollt wird.
	 */
	public void fireContentsChange() {
		super.fireItemSetChange();
	}

	/**
	 * Rollback der seit dem letzten commit() gemachten Änderungen.
	 */
	public void rollback() {
		addedItems.clear();
		modifiedItems.clear();
		deletedItems.clear();
		refresh();
	}

	/**
	 * This is called every time an item property has changed.
	 * 
	 * @param item
	 */
	void itemChangeNotification(GenericItem item) {
		if (!item.isNewItem()) {
			modifiedItems.put(item.getId(), item);
		}
	}

	/**
	 * Committed gemachte Änderungen in das Backend.
	 */
	public void commit() {
		int size = numberOfModifiedItems();
		if (size > 0) {
			ArrayList<GenericItem> items = new ArrayList<GenericItem>(size);
			items.addAll(addedItems.values());
			items.addAll(modifiedItems.values());
			items.addAll(deletedItems.values());

			UpdateContext context = new UpdateContext();
			delegate.update(items, context);
			committed(items);

			addedItems.clear();
			modifiedItems.clear();
			deletedItems.clear();

			if (context.isRefreshRequired()) {
				refresh();
			} else {
				fireContentsChange();
			}
		} else {
			// der Content hat sich hier beim generischen Container nicht
			// geändert, aber Table benötigt es für das Re-Rendering der
			// Anzeige
			fireContentsChange();
		}
	}

	/**
	 * Methode die nach eineme erfolgreichem Commit aufgerufen wird
	 * 
	 * @param items
	 *            die neuen/geänderten/gelöschten Zeilen
	 */
	protected void committed(ArrayList<GenericItem> items) {
		// to be overridden
	}

	private int numberOfModifiedItems() {
		return addedItems.size() + modifiedItems.size() + deletedItems.size();
	}

	boolean isDeleted(GenericItemId itemId) {
		return deletedItems.containsKey(itemId);
	}

	@Override
	protected boolean passesFilters(Object itemId) {
		if (itemId instanceof TemporaryItemId) {
			return true;
		}
		return super.passesFilters(itemId);
	}
}
