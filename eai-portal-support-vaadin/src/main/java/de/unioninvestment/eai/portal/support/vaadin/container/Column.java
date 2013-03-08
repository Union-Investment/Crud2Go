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

import java.io.Serializable;

/**
 * Splate in einem Vaadin-Container. Kapselt Information wie den Namen der
 * Spalte, den Datentyp des Inhaltes, ob der Inhalt verändert werden kann, ob
 * der Inhalt leer sein kann und ob der Inhalt Teil der ID des Datensatzen ist.
 */
public class Column implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final Class<?> type;
	private final boolean readOnly;
	private final boolean required;
	private final boolean partOfPrimaryKey;

	public Column(String name, Class<?> type, boolean readOnly,
			boolean required, boolean partOfPrimaryKey, String order) {
		super();
		this.name = name;
		this.type = type;
		this.readOnly = readOnly;
		this.required = required;
		this.partOfPrimaryKey = partOfPrimaryKey;

	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isPartOfPrimaryKey() {
		return partOfPrimaryKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (partOfPrimaryKey ? 1231 : 1237);
		result = prime * result + (readOnly ? 1231 : 1237);
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (partOfPrimaryKey != other.partOfPrimaryKey)
			return false;
		if (readOnly != other.readOnly)
			return false;
		if (required != other.required)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", readOnly="
				+ readOnly + ", required=" + required + ", partOfPrimaryKey="
				+ partOfPrimaryKey + "]";
	}

}
