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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import java.io.Serializable;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;

/**
 * Repräsentiert die primären Id's einer Zeile.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptRowId implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ContainerRowId rowId;

	/**
	 * Konstruktor mit Parameter.
	 * @param rowId Primärschlüssel
	 */
	ScriptRowId(ContainerRowId rowId) {
		this.rowId = rowId;
	}

	/**
	 * Gibt die Primärschlüssel als Map zurück.
	 * @return Primärschlüssel als Map
	 */
	public Map<String, Object> asMap() {
		return rowId.asMap();
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
		ScriptRowId other = (ScriptRowId) obj;
		if (rowId == null) {
			if (other.rowId != null) {
				return false;
			}
		} else if (!rowId.equals(other.rowId)) {
			return false;
		}
		return true;
	}

	ContainerRowId getContainerRowId() {
		return rowId;
	}

}
