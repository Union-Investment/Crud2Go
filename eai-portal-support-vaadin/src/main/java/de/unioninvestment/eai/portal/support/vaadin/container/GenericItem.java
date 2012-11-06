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

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Erweiterte Schnittstelle für {@link Item}, die für das CRUD-Portlet benötigt
 * wird.
 * 
 * @author carsten.mjartan
 */
public class GenericItem implements Item {

	private static final long serialVersionUID = 1L;

	private GenericVaadinContainer container;

	private final GenericItemId id;

	private final Collection<GenericProperty> properties;

	public GenericItem(GenericVaadinContainer container, GenericItemId id,
			Collection<GenericProperty> properties) {
		this.container = container;
		this.id = id;
		this.properties = properties;

		for (GenericProperty p : properties) {
			p.setItem(this);
		}
	}

	/**
	 * @return die ID der Zeile
	 */
	public GenericItemId getId() {
		return id;
	}

	/**
	 * @return <code>true</code>, falls eines der Properties Modifiziert wurde
	 */
	public boolean isModified() {
		for (GenericProperty p : properties) {
			if (p.isModified()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return <code>true</code>, falls das Item neu zu erstellen ist
	 */
	public boolean isNewItem() {
		return id.isNewId();
	}

	/**
	 * @return <code>true</code>, falls die Zeile zu löschen ist
	 */
	public boolean isDeleted() {
		return container.isDeleted(id);
	}

	/**
	 * Informiert das Item, dass die Feldinhalte in das Backend übernommen
	 * wurden. Der Aufruf wird an die Properties weitergereicht.
	 */
	public void commit() {
		for (GenericProperty p : properties) {
			p.commit();
		}
	}

	/**
	 * @return die Container-Instanz
	 */
	public GenericVaadinContainer getContainer() {
		return container;
	}

	@Override
	public Property getItemProperty(Object id) {
		for (GenericProperty p : properties) {
			if (p.getName().equals(id)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return container.getContainerPropertyIds();
	}

	@Override
	public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
