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

/**
 * 
 * 
 * @author markus.bonsch
 * 
 */
public abstract class ContainerField {

	/**
	 * Gibt den Namen des Feldes zurück.
	 * 
	 * @return Names des Feldes
	 */
	public abstract String getName();

	/**
	 * Gibt den Wert des Feldes zurück.
	 * 
	 * @return Wert des Feldes
	 */
	public abstract Object getValue();

	/**
	 * Setzt den Feldwert.
	 * 
	 * @param value
	 *            Feldwert
	 */
	public abstract void setValue(final Object value);

	/**
	 * Setzt den Feldwert. Dabei findet ggf. eine Konvertierung zum Zieldatentyp
	 * statt.
	 * 
	 * @param value
	 *            Feldwert
	 */
	public abstract void setText(final String value);

	/**
	 * Gibt den Wert des Feldes als String zurück.
	 * 
	 * @return Wert des Feldes als String
	 */
	public abstract String getText();

	/**
	 * Gibt zurück ob das repräsentierte Feld verändert wurde.
	 * 
	 * @return true bei Feldänderung, sonst false
	 */
	public abstract boolean isModified();

	/**
	 * @return <code>true</code>, falls die Zeile oder das Feld nicht
	 *         beschreibbar sind
	 */
	public abstract boolean isReadonly();

	/**
	 * @return <code>true</code>, falls das Feld ein Pflichtfeld ist
	 */
	public abstract boolean isRequired();

	/**
	 * @return den Typ des Felds
	 */
	public abstract Class<?> getType();
}
