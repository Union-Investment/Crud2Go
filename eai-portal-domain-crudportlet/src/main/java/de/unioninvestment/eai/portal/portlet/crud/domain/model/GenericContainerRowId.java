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

import static java.util.Collections.unmodifiableMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.TemporaryItemId;

/**
 * ContainerRowId Model Klasse für den GeniericDataContainer
 * 
 * @author markus.bonsch
 * 
 */
public class GenericContainerRowId extends ContainerRowId {

	private GenericItemId itemId;

	private Map<String, Object> keyValueMap;

	/**
	 * 
	 * @param internalId
	 *            Primärschlüssel
	 * @param idNames
	 *            SpaltenIds
	 */
	public GenericContainerRowId(GenericItemId internalId, List<String> idNames) {
		this.itemId = internalId;
		if (!(itemId instanceof TemporaryItemId)) {
			keyValueMap = calculateKeyValueMap(internalId, idNames);
		}
	}

	@Override
	public Map<String, Object> asMap() {
		return keyValueMap;
	}

	@Override
	public Object getInternalId() {
		return itemId;
	}

	/**
	 * Gibt die Primärschlüssel als Map zurück.
	 * 
	 * @param internalId
	 *            Interne Ids
	 * @param primaryKeyColumns
	 *            Primärschlüsselspalten
	 * @return Zeile als Map
	 */
	static Map<String, Object> calculateKeyValueMap(GenericItemId internalId,
			List<String> primaryKeyColumns) {
		Object[] idArray = internalId.getId();

		if (idArray == null) {
			return null;
		}

		Map<String, Object> idMap = new LinkedHashMap<String, Object>();
		for (int i = 0; i < idArray.length; i++) {
			idMap.put(primaryKeyColumns.get(i), idArray[i]);
		}
		return unmodifiableMap(idMap);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenericContainerRowId other = (GenericContainerRowId) obj;
		if (itemId == null) {
			if (other.itemId != null) {
				return false;
			}
		} else if (!itemId.equals(other.itemId)) {
			return false;
		}
		return true;
	}

}
