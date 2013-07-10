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
package de.unioninvestment.eai.portal.support.vaadin.table;

import java.text.Format;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Field;

/**
 * Schnittstelle zur Steuerung der Formatierung von Tabellenzellen, abhängig vom
 * Datentyp.
 * 
 * @author carsten.mjartan
 */
public interface DisplaySupport {

	/**
	 * @param clazz
	 *            ein Java-Datentyp
	 * @return <code>true</code>, falls die Formatierung dieses Datentyps von
	 *         der Implementierung unterstützt wird
	 */
	boolean supportsDisplaying(Class<?> clazz);

	/**
	 * @param type
	 *            Typ
	 * @param propertyId
	 *            der Name der aktuellen Spalte
	 * @param multiline
	 *            ob das Feld mehzeilig ist.
	 * @param inputPrompt
	 *            Promt
	 * 
	 * @return ein Formularfeld für die Anzeige (und ggf. Eingabe) von Daten
	 */
	Field<?> createField(Class<?> type, Object propertyId, boolean multiline,
			String inputPrompt, Format format);

	/**
	 * Erzeugt eine Hilfsklasse zum Formatieren der Eingabe.
	 * 
	 * @param type
	 *            Datentyp
	 * @return Hilfsklasse zum Formatieren der Eingabe
	 */
	Converter<String, ?> createFormatter(Class<?> type, Format format);

	/**
	 * @param property
	 *            das zu formatierende Datenfeld
	 * @return der formatierte String zum Wert
	 */
	@Deprecated
	String formatPropertyValue(Property property, Format format);
}
