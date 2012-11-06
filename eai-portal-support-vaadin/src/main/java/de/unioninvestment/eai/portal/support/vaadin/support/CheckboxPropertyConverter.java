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
package de.unioninvestment.eai.portal.support.vaadin.support;

import com.vaadin.data.Property;

/**
 * Frontend-TO-Backendkonverter für Check-Boxen.
 * 
 * @author max.hartmann
 * 
 */
public class CheckboxPropertyConverter extends PropertyConverter<Boolean> {

	private static final long serialVersionUID = 1L;
	private final String checkedValue;
	private final String uncheckedValue;


	/**
	 * 
	 * Konstruktor mit Parametern.
	 * 
	 * @param newDataSource
	 *            Property des Backends
	 * @param checkedValue Wert für Checked-Zustand (true)
	 * @param uncheckedValue Wert für Unchecked-Zustand (false)
	 */
	public CheckboxPropertyConverter(Property newDataSource,
			String checkedValue, String uncheckedValue) {
		super(newDataSource);
		this.checkedValue = checkedValue;
		this.uncheckedValue = uncheckedValue;

	}

	@Override
	public Object convertToBackend(Boolean input) {
		return input ? checkedValue : uncheckedValue;
	}

	@Override
	public Object convertToFrontend(String input) {
		if (input == null) {
			return false;
		}
		if (input.equals(checkedValue)) {
			return true;
		}
		return false;
	}
}
