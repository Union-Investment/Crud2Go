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
package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import java.text.Format;

import org.vaadin.tokenfield.TokenField;

import com.vaadin.ui.AbstractField;

/**
 * Schnittstelle, die die Unterstützung von Multiselects für einen Datentypen
 * ermöglicht.
 * 
 * @author carsten.mjartan
 */
public interface TokenFieldSupport {

	/**
	 * 
	 * @param type
	 *            Datentyp
	 * @param propertyId
	 *            der Name der aktuellen Spalte
	 * @param delimiter
	 *            for joining/splitting the values
	 * @return ein Formularfeld für die Eingabe von Daten
	 */
	TokenField createTokenField(Class<?> type, Object propertyId,
			String delimiter, Format format);

}
