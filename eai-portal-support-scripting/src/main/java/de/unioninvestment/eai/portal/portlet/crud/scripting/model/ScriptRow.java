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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.TransformedEntryMap;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.ValueTransformer;

/**
 * Repräsentiert eine Datenzeile einer Tabelle.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptRow implements Serializable, Cloneable {

	private static final FieldToValueTransformer VALUE_TRANSFORMER = new FieldToValueTransformer();
	private static final FieldToFieldTransformer FIELD_TRANSFORMER = new FieldToFieldTransformer();

	private final class FieldToValueMap extends
			TransformedEntryMap<String, ContainerField, Object> {
		private static final long serialVersionUID = 1L;

		private FieldToValueMap(Map<String, ContainerField> delegate,
				ValueTransformer<ContainerField, Object> transformer) {
			super(delegate, transformer);
		}

		@Override
		public Object put(String key, Object value) {
			ContainerField field = row.getFields().get(key);
			ScriptField scriptField = new ScriptField(field);
			Object oldValue = scriptField.getValue();
			scriptField.setValue(value);
			return oldValue;
		}
	}

	private static final class FieldToValueTransformer implements
			ValueTransformer<ContainerField, Object> {
		@Override
		public Object transform(ContainerField a) {
			return new ScriptField(a).getValue();
		}
	}

	private static final class FieldToFieldTransformer implements
			ValueTransformer<ContainerField, ScriptField> {
		@Override
		public ScriptField transform(ContainerField containerField) {
			return new ScriptField(containerField);
		}
	}

	private static final long serialVersionUID = 1L;

	private ScriptRowId id;

	private ContainerRow row;

	private Map<String, ScriptField> fields;
	private FieldToValueMap values;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param row
	 *            Tabellenzeile
	 */
	public ScriptRow(ContainerRow row) {
		init(row);
	}

	private void init(ContainerRow row) {
		id = new ScriptRowId(row.getId());
		this.row = row;
	}

	/**
	 * 
	 * @return Primärschlüssel der Zeile
	 */
	public ScriptRowId getId() {
		return id;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ScriptRow clone() throws CloneNotSupportedException {
		return new ScriptRow(row.clone());
	}

	/**
	 * Alle Feldwerte einer Zeile.
	 * 
	 * @return Alle Spalten
	 */
	public Map<String, Object> getValues() {
		if (values == null) {
			values = new FieldToValueMap(row.getFields(), VALUE_TRANSFORMER);
		}
		return values;
	}

	/**
	 * Alle Felder einer Row.
	 * 
	 * @return key:Feldname - value:Feldobjekt
	 */
	public Map<String, ScriptField> getFields() {
		if (fields == null) {
			fields = new TransformedEntryMap<String, ContainerField, ScriptField>(
					row.getFields(), FIELD_TRANSFORMER);
		}
		return fields;
	}

	public boolean isModified() {
		return row.isDeleted() ? false : row.isModified();
	}

	/**
	 * @return <code>true</code>, falls das Item neu zu erstellen ist
	 */
	public boolean isNewItem() {
		return row.isNewItem();
	}

	/**
	 * @return <code>true</code>, falls die Zeile zu löschen ist
	 */
	public boolean isDeleted() {
		return row.isDeleted();
	}

	ContainerRow getRow() {
		return row;
	}
}
