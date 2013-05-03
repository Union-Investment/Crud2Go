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
package de.unioninvestment.eai.portal.portlet.crud.config.converter;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;

/**
 * Konvertiert Script-Strings im XML-File in ein {@link GroovyScript} Objekt,
 * das auch die kompilierten Klassen dazu aufnehmen kann.
 * 
 * @author carsten.mjartan
 */
public class GroovyScriptConverter {

	/**
	 * @param value
	 *            ein Script als String
	 * @return ein {@link GroovyScript} mit gefülltem Source-Feld
	 */
	public static GroovyScript parseStringToGroovyScript(String value) {
		return new GroovyScript(value);
	}

	/**
	 * @param script
	 *            ein {@link GroovyScript} Objekt
	 * @return die Script-Quelle als String
	 */
	public static String printGroovyScript(GroovyScript script) {
		if (script != null && script.getSource() != null) {
			return script.getSource();
		} else {
			return null;
		}
	}
}
