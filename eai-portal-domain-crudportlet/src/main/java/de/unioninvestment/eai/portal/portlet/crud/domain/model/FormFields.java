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

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.FormFieldChangeEventHandler;

/**
 * Aggregations-Objekt für Formularfelder, bietet Aggregatfunktionen.
 * 
 * @author carsten.mjartan
 */
public class FormFields implements Iterable<FormField>, Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, FormField> fields = new LinkedHashMap<String, FormField>();

	/**
	 * 
	 * @param fieldList
	 *            FormField
	 */
	public FormFields(FormField... fieldList) {
		for (FormField field : fieldList) {
			fields.put(field.getName(), field);
		}
	}

	@Override
	public Iterator<FormField> iterator() {
		return fields.values().iterator();
	}

	/**
	 * 
	 * @return Anzahl der Felder
	 */
	public int count() {
		return fields.size();
	}

	/**
	 * Holt ein FormField anhand des Namen.
	 * 
	 * @param fieldName
	 *            Feldname
	 * @return FormField
	 */
	public FormField get(String fieldName) {
		FormField field = fields.get(fieldName);
		if (field == null) {
			throw new NoSuchElementException("No such field: '" + fieldName
					+ "'");
		}
		return field;
	}

	/**
	 * 
	 * 
	 * @param fields
	 * @return Ob es min. ein Defaultwert gesetzt ist.
	 */
	public boolean hasDefaultValue() {
		for (FormField formField : fields.values()) {
			if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(formField
					.getDefault()))) {
				return true;
			}
		}
		return false;
	}

	public void addListener(FormFieldChangeEventHandler valueChangeListener) {
		for (FormField field : fields.values()) {
			field.addFormFieldChangeEventListener(valueChangeListener);
		}
	}
}
