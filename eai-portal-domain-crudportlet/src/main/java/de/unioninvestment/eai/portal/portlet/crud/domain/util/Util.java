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
package de.unioninvestment.eai.portal.portlet.crud.domain.util;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;

/**
 * Heimat kleiner, unschuldiger statischer Hilfsmethoden.
 * 
 * @author Bastian Krol
 */
public abstract class Util {

	/**
	 * Helper um zu checken ob ein Closure-Script nur {@code true} oder
	 * {@code false enthält}
	 * 
	 * @param closure
	 *            das zu checkende Closure-Script
	 * @return {@code true} wenn das Script nur {@code true} oder
	 *         {@code false enthält}
	 */
	public static boolean isPlainBoolean(GroovyScript closure) {
		if (closure != null) {
			return isPlainBoolean(closure.getSource());
		} else {
			return false;
		}
	}

	/**
	 * Helper um zu checken ob ein String nur {@code true} oder
	 * {@code false enthält}
	 * 
	 * @param string
	 *            der zu überprüfende String
	 * @return {@code true} wenn der String nur {@code true} oder
	 *         {@code false enthält}
	 */
	public static boolean isPlainBoolean(String string) {
		if (string != null) {
			string = string.trim();
			return string.equalsIgnoreCase(Boolean.TRUE.toString())
					|| string.equalsIgnoreCase(Boolean.FALSE.toString());
		} else {
			return false;
		}
	}
}
