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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;

/**
 * Abstrakte Klasse für Frondend-TO-Backendkonverter.
 * 
 * @author max.hartmann
 * 
 * @param <T>
 *            Typ des Frondend-Datentyps
 */
public abstract class PropertyConverter<T> extends PropertyFormatter {

	private static final long serialVersionUID = 42L;

	/**
	 * Default-Konstruktor.
	 */
	public PropertyConverter() {
		super();
	}

	/**
	 * Konstruktor mit {@link Property} als PropertyDatasource.
	 * 
	 * @param propertyDataSource
	 *            Backend-Property
	 */
	public PropertyConverter(Property propertyDataSource) {
		super(propertyDataSource);
	}

	/**
	 * Konvertiere zum Backend.
	 * 
	 * @param input
	 *            Frondend-Wert
	 * 
	 * @return Backend-Wert
	 */
	public abstract Object convertToBackend(T input);

	/**
	 * Konvertiere zum Frondend.
	 * 
	 * @param input
	 *            Backend-Wert
	 * 
	 * @return Frondend-Wert
	 */
	public abstract Object convertToFrontend(String input);

	@Override
	public Object getValue() {
		Object value = getPropertyDataSource() == null ? null
				: getPropertyDataSource().getValue();
		if (value == null) {
			return convertToFrontend(null);
		}
		return convertToFrontend(value.toString());
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			return (Class<T>) ((ParameterizedType) type)
					.getActualTypeArguments()[0];
		}
		return (Class<T>) Object.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object newValue) throws ReadOnlyException,
			ConversionException {
		if (newValue == null) {
			super.setValue(null);
		}
		super.setValue(convertToBackend((T) newValue));
	}

	@Override
	public final Object parse(String formattedValue) throws Exception {
		return formattedValue;
	}

	@Override
	public String format(Object value) {
		return value.toString();
	}
}