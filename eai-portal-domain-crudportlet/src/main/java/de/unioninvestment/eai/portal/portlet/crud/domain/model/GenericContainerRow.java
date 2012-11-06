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

import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.TransformedKeyMap;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.ValueTransformer;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;
import de.unioninvestment.eai.portal.support.vaadin.container.TemporaryItemId;

/**
 * ContainerRow Model Klasse für den GeniericDataContainer
 * 
 * @author markus.bonsch
 * 
 */
public class GenericContainerRow extends ContainerRow {

	private final GenericItem rowItem;

	private final DataContainer container;
	private final boolean transactional;
	private final boolean immutable;

	private GenericContainerRowId id;

	private Map<String, ContainerField> fields;

	public GenericContainerRow(GenericItem rowItem, DataContainer container,
			boolean transactional, boolean immutable) {
		this(null, rowItem, container, transactional, immutable);
	}

	@Override
	public ContainerRowId getId() {
		return id;
	}

	public GenericContainerRow(GenericContainerRowId id, GenericItem rowItem,
			DataContainer container, boolean transactional, boolean immutable) {

		if (id == null) {
			this.id = (GenericContainerRowId) container
					.convertInternalRowId(rowItem.getId());
		} else {
			this.id = id;
		}

		this.rowItem = rowItem;
		this.container = container;
		this.transactional = transactional;
		this.immutable = immutable;
	}

	@Override
	public GenericItem getInternalRow() {
		return rowItem;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public boolean isImmutable() {
		return immutable;
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

	@Override
	public boolean isReadonly() {
		return !(getId().getInternalId() instanceof TemporaryItemId)
				&& !container.isUpdateable();
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
							return new GenericContainerField(
									GenericContainerRow.this,
									(GenericProperty) rowItem
											.getItemProperty(propId), container);
						}
					});
		}
		return fields;
	}

	@Override
	public boolean isDeleted() {
		return rowItem.isDeleted();
	}

	@Override
	public boolean isNewItem() {
		return rowItem.isNewItem();
	}

	@Override
	public boolean isModified() {
		return rowItem.isModified();
	}

	/**
	 * @param fields
	 *            for Testing
	 */
	void setFields(Map<String, ContainerField> fields) {
		this.fields = fields;
	}

}
