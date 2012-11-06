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
package de.unioninvestment.eai.portal.portlet.crud.validation;

import com.vaadin.data.Validator;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Field;

/**
 * Führt eine Validierung durch mit Hilfe des {@link PropertyFormatter}, der an
 * der Property {@code field} hängt. Wirft der {@link PropertyFormatter} eine
 * Exception, gilt die Validierung als fehlgeschlagen.
 */
public class FormattingValidator implements Validator {

	private static final long serialVersionUID = 1L;

	private final Field field;

	public FormattingValidator(Field field) {
		this.field = field;

	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			if (value instanceof String
					&& field.getPropertyDataSource() instanceof PropertyFormatter) {

				PropertyFormatter formatter = (PropertyFormatter) field
						.getPropertyDataSource();
				formatter.parse((String) value);
			}
		} catch (Exception e) {
			throw new InvalidValueException(e.getMessage());
		}
	}

	@Override
	public boolean isValid(Object value) {
		try {
			validate(value);
			return true;

		} catch (InvalidValueException e) {
			return false;
		}
	}

}
