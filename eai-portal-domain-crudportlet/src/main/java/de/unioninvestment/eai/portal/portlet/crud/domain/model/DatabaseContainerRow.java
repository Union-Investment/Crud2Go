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

import java.util.Collection;
import java.util.Map;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;

import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.TransformedKeyMap;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.ValueTransformer;

/**
 * Ergebniszeile eines SQL-Containers.
 * 
 * @author markus.bonsch
 * 
 */
public class DatabaseContainerRow extends ContainerRow {

	private final RowItem rowItem;

	private final DataContainer container;

	private boolean immutable;

	private final boolean transactional;

	private DatabaseContainerRowId id;

	private Map<String, ContainerField> fields;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param rowItem
	 *            Spalte
	 * @param id
	 *            SpaltenId
	 * @param container
	 *            DatabaseContainer
	 */
	public DatabaseContainerRow(RowItem rowItem, DatabaseContainerRowId id,
			DataContainer container, boolean transactional, boolean immutable) {
		this.rowItem = rowItem;
		this.container = container;
		this.transactional = transactional;
		this.immutable = immutable;

		if (id == null) {
			this.id = (DatabaseContainerRowId) container
					.convertInternalRowId(rowItem.getId());
		} else {
			this.id = id;
		}

	}

	@Override
	public ContainerRowId getId() {
		return id;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ContainerField> getFields() {
		if (fields == null) {
			fields = new TransformedKeyMap<String, ContainerField>(
					(Collection<String>) rowItem.getItemPropertyIds(),
					new ValueTransformer<String, ContainerField>() {
						@Override
						public ContainerField transform(String propId) {
							return new DatabaseContainerField(
									DatabaseContainerRow.this,
									(ColumnProperty) rowItem
											.getItemProperty(propId), container);
						}
					});
		}
		return fields;
	}

	@Override
	void setFields(Map<String, ContainerField> fields) {
		this.fields = fields;
	}

	@Override
	public RowItem getInternalRow() {
		return rowItem;
	}

	@Override
	public ContainerRow clone() throws CloneNotSupportedException {
		ContainerRow clone = container.addRow();

		for (Object propertyId : rowItem.getItemPropertyIds()) {
			clone.setValue((String) propertyId,
					rowItem.getItemProperty(propertyId).getValue());
		}

		return clone;
	}

	public boolean isReadonly() {
		return !(getId().getInternalId() instanceof TemporaryRowId)
				&& !container.isUpdateable();
	}

	public boolean isImmutable() {
		return immutable;
	}

	public boolean isTransactional() {
		return transactional;
	}

	@Override
	public boolean isNewItem() {
		return id.getInternalId() instanceof TemporaryRowId;
	}

	@Override
	public boolean isModified() {
		return rowItem.isModified();
	}

	@Override
	public boolean isDeleted() {
		// FIXME currently always 'false'
		return super.isDeleted();
	}
}
