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

import java.io.Reader;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerClob;

/**
 * Repräsentiert ein CLob Objekt.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptClob {

	private final ContainerClob containerClob;

	public ScriptClob(ContainerClob containerClob) {
		this.containerClob = containerClob;
	}

	/**
	 * @return den CLob Inhalt als String
	 */
	public String getValue() {
		return containerClob.getValue();
	}

	/**
	 * 
	 * @return - Zeichenlänge des CLobs
	 */
	public int getSize() {
		return containerClob.getSize();
	}

	/**
	 * Dem CLob ein neuen Wert setzten.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		containerClob.setValue(value);
	}

	/**
	 * @return dem CLob Inhalt als {@link Reader}
	 */
	public Reader getReader() {
		return containerClob.getReader();
	}

	/**
	 * Liefert ob sich der Feldinhalt des CLobs gändert hat
	 * 
	 * @return true falls sich der Wert sich geändert hat.
	 */
	public boolean isModified() {
		return containerClob.isModified();
	}

	/**
	 * Setzt den Modified Status zurück.
	 */
	public void commit() {
		containerClob.commit();

	}

	public boolean isEmpty() {
		return containerClob.getValue() == null;
	}

	ContainerClob getContainerClob() {
		return containerClob;
	}

}
