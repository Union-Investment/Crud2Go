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


/**
 * Konvertiert Java-Klassennamen zu Java-Klassen und vice versa.
 * 
 * @author carsten.mjartan
 */
public class JavaClassConverter {

	/**
	 * @param value
	 *            der vollständige Name der Klasse
	 * @return eine Java-Klasse
	 */
	public static Class<?> parseStringToJavaClass(String value) {
		try {
			return JavaClassConverter.class.getClassLoader().loadClass(value);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param clazz
	 *            eine Java-Klasse
	 * @return der vollständige Name der Klasse
	 */
	public static String printJavaClass(Class<?> clazz) {
		return clazz.getName();
	}
}
