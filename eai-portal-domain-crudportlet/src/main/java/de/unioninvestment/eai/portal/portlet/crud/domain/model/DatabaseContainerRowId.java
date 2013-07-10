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

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;

/**
 * Die primären IDs einer Datenzeile.
 * 
 * @author markus.bonsch
 * 
 */
public class DatabaseContainerRowId extends ContainerRowId {

	private RowId rowId;

	private Map<String, Object> keyValueMap;

	/**
	 * 
	 * @param internalId
	 *            Primärschlüssel
	 * @param idNames
	 *            SpaltenIds
	 */
	public DatabaseContainerRowId(RowId internalId, List<String> idNames) {
		this.rowId = internalId;
		if (!(internalId instanceof TemporaryRowId)) {
			keyValueMap = calculateKeyValueMap(internalId, idNames);
		}
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
	static Map<String, Object> calculateKeyValueMap(RowId internalId,
			List<String> primaryKeyColumns) {
		Object[] idArray = internalId.getId();
		if (idArray != null) {
			Map<String, Object> idMap = new LinkedHashMap<String, Object>();
			for (int i = 0; i < idArray.length; i++) {
				idMap.put(primaryKeyColumns.get(i), idArray[i]);
			}

			return unmodifiableMap(idMap);
		} else {
			return null;
		}
	}

	@Override
	public Map<String, Object> asMap() {
		return keyValueMap;
	}

	@Override
	public RowId getInternalId() {
		return rowId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowId == null) ? 0 : rowId.hashCode());
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
		DatabaseContainerRowId other = (DatabaseContainerRowId) obj;
		if (rowId == null) {
			if (other.rowId != null) {
				return false;
			}
		} else if (!rowId.equals(other.rowId)) {
			return false;
		}
		return true;
	}

}
