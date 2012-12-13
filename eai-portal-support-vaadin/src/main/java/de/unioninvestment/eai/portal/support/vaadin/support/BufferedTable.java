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
package de.unioninvestment.eai.portal.support.vaadin.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;

/**
 * UI Tabelle zur Zwischenspeicherung von Usereingaben für eine Validierung vor
 * dem DB Commit.
 * 
 * @author carsten.mjartan
 * 
 */
public class BufferedTable extends Table {

	private static final long serialVersionUID = 1L;

	private final Set<Field> registeredFields = new HashSet<Field>();

	/**
	 * Leerer Konstruktor.
	 */
	public BufferedTable() {
		super();
	}

	/**
	 * @param caption
	 *            Beschreibung
	 * @param dataSource
	 *            Datenquelle für Selection
	 */
	public BufferedTable(String caption, Container dataSource) {
		super(caption, dataSource);
	}

	/**
	 * @param caption
	 *            Beschreibung
	 */
	public BufferedTable(String caption) {
		super(caption);
	}

	@Override
	protected void unregisterComponent(Component component) {
		super.unregisterComponent(component);
		if (component instanceof Field) {
			Field field = (Field) component;
			unregisterField(field);
		} else if (component instanceof Layout) {
			Field field = getFieldFromLayout((Layout) component);
			if (field != null) {
				unregisterField(field);
			}
		}
	}

	private void unregisterField(Field field) {
		registeredFields.remove(field);
		handleUnregisteredField(field);
	}

	protected void handleUnregisteredField(Field field) {
		// default: nothing to do
	}

	private Field getFieldFromLayout(Layout layout) {
		Field field = null;
		Iterator<Component> it = layout.getComponentIterator();
		if (it.hasNext()) {
			Component c = it.next();
			if (c instanceof Field) {
				field = (Field) c;
			}
		}
		return field;
	}

	/**
	 * Buffered Fields werden in zwischengespeichert.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object getPropertyValue(Object rowId, Object colId,
			com.vaadin.data.Property property) {
		Object value = super.getPropertyValue(rowId, colId, property);
		if (value instanceof Field) {
			Field field = (Field) value;
			if (!field.isWriteThrough() && !field.isReadOnly()) {
				registeredFields.add(field);
			}
		}
		return value;
	};

	/**
	 * Prüft ob eines der uebergebenden Felder modifiziert wurde.
	 * 
	 * @return true, falls ein Feld seit dem letzten Commit modifiert wurde.
	 */
	public boolean isFieldModified() {
		for (Field field : registeredFields) {
			if (field.isModified()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Setzt alle Änderungen auf den Wert vor dem letzten Commit zurueck.
	 */
	public void discardFieldValues() {
		for (Field field : registeredFields) {
			field.discard();
		}
	}

	/**
	 * Führt einen Commit durch. Falls {@link #isInvalidCommitted()} auf
	 * <code>false</code> gesetzt wurde, wird nur nach erfolgreicher Validierung
	 * committed.
	 * 
	 * @throws InvalidValueException
	 *             falls ein Commit nicht erfolgreich durchgeführt wurde
	 */
	public void commitFieldValues() {
		if (!isInvalidCommitted()) {
			Field invalidField = null;
			for (Field field : registeredFields) {
				if (field.isValid()) {
					if (field.isModified()) {
						field.commit();
					}
				} else {
					if (invalidField == null) {
						invalidField = field;
					}
				}
			}
			if (invalidField != null) {
				try {
					invalidField.validate();
				} catch (InvalidValueException e) {
					throw new SourceException(invalidField, e);
				}
			}
		}
	}

	Set<Field> getRegisteredFields() {
		return registeredFields;
	}

	/**
	 * Liefert eine Map der geänderten Spalten Key->Spaltename, Value->alter
	 * Wert.
	 * 
	 * @return Alle Spalten, die eine Änderung beinhalten
	 */
	public Map<String, Object> getModifiedColumnNames() {
		Map<String, Object> changedFieldNames = new HashMap<String, Object>();
		if (isFieldModified()) {
			for (Field field : registeredFields) {
				if (field.isModified()) {
					Property prop = getColumnPropertyOfField(field);
					changedFieldNames.put(getPropertyName(prop),
							prop.getValue());
				}
			}
		}
		return changedFieldNames;
	}

	private String getPropertyName(Property prop) {
		String propertyName;
		if (prop instanceof GenericProperty) {
			propertyName = ((GenericProperty) prop).getName();
		} else {
			propertyName = ((ColumnProperty) prop).getPropertyId();
		}
		return propertyName;
	}

	/**
	 * Liefert das ColumnProperty eines Feldes und geht gegenfalls vorhandenden
	 * Formatter durch.
	 * 
	 * @param field
	 * @return
	 */
	private Property getColumnPropertyOfField(Field field) {
		// TODO Entkopplung des Codes von SQLContainer-Klassen
		Property property = field.getPropertyDataSource();
		while (property instanceof PropertyFormatter) {
			property = ((PropertyFormatter) property).getPropertyDataSource();
		}
		return property;
	}
}
