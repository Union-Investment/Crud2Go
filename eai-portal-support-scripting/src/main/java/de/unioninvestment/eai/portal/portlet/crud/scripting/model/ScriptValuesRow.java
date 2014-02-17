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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ValuesRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.TransformedEntryMap;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.ScriptFieldValueTransformer;

/**
 * Repräsentiert eine Datenzeile einer Tabelle.
 * 
 * @author markus.bonsch
 * 
 */
public class ScriptValuesRow implements Serializable {

	private static final ScriptFieldValueTransformer VALUE_TRANSFORMER = new ScriptFieldValueTransformer();

	private final class ScriptFieldValueMap extends
			TransformedEntryMap<String, Object, Object> {
		private static final long serialVersionUID = 1L;

		private ScriptFieldValueMap(Map<String, Object> delegate,
				ScriptFieldValueTransformer transformer) {
			super(delegate, transformer);
		}
	}

	private static final long serialVersionUID = 1L;

	ValuesRow row;

	private Map<String,Object> values;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param row
	 *            Tabellenzeile
	 */
	public ScriptValuesRow(ValuesRow row) {
		this.row = row;
	}


	/**
	 * Alle Feldwerte einer Zeile.
	 * 
	 * @return Alle Spalten
	 */
	public Map<String, Object> getValues() {
		if (values == null) {
			values = new ScriptFieldValueMap(row.getValues(), VALUE_TRANSFORMER);
		}
		return values;
	}

	ValuesRow getRow() {
		return row;
	}
}
