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

import java.util.Arrays;

import com.vaadin.data.Property;

/**
 * Erweiterte Schnittstelle für {@link Property}, die für das CRUD-Portlet
 * benötigt wird.
 * 
 * @author carsten.mjartan
 */
public class GenericProperty implements Property {

	/**
	 * wird geworfen beim Versuch {@code null} als Wert von
	 * {@link GenericProperty} zu setzen.
	 */
	public static class RequiredException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public RequiredException(String message) {
			super(message);
		}
	}

	private static final long serialVersionUID = 1L;
	private final Column metaData;
	private boolean readOnly;
	private GenericItem item;

	private Object value;
	private Object newValue;

	public GenericProperty(Column column, Object value) {
		this.metaData = column;
		this.readOnly = metaData.isReadOnly();
		this.value = value;
		this.newValue = value;
	}

	/**
	 * @return <code>true</code>, falls das Property nicht NULLable ist
	 */
	public boolean isRequired() {
		return metaData.isRequired();
	}

	/**
	 * @return <code>true</code>, falls das Property seit dem letzten commit()
	 *         geändert wurde
	 */
	public boolean isModified() {
		return (this.value == null && newValue != null)
				|| (this.value != null && !this.value.equals(newValue));
	}

	/**
	 * Informiert das Property, dass der Feldinhalt in das Backend übernommen
	 * wurde. Der Modified-Status wird zurückgesetzt.
	 */
	public void commit() {
		if (isModified()) {
			this.value = newValue;
		}
	}

	@Override
	public Object getValue() {
		return isModified() ? newValue : value;
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException,
			ConversionException {
		if (isReadOnly()) {
			throw new ReadOnlyException("Field " + metaData.getName()
					+ " is readOnly");
		}
		if (newValue != null
				&& !getType().isAssignableFrom(newValue.getClass())) {
			throw new ConversionException("Cannot convert value of type '"
					+ newValue.getClass().getName() + "' to type '"
					+ getType().getName() + "'");
		}
		if (isRequired() && newValue == null) {
			throw new RequiredException("Field " + metaData.getName()
					+ " is required");
		}

		this.newValue = newValue;
		item.getContainer().itemChangeNotification(item);
	}

	@Override
	public Class<?> getType() {
		return metaData.getType();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		this.readOnly = metaData.isReadOnly() || newStatus;
	}

	public void setItem(GenericItem item) {
		this.item = item;
	}

	public String getName() {
		return metaData.getName();
	}

	@Override
	public String toString() {
		Object val = getValue();
		if (val == null) {
			return null;
		}
		if (val.getClass().isArray()) {
			return Arrays.toString((Object[]) val);
		}
		return val.toString();
	}
}
