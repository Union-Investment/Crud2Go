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

import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;

/**
 * 
 * Interface für Drop-Down's.
 * 
 * @author max.hartmann
 * 
 */
public interface OptionList {

	/**
	 * 
	 * Liefert die Drop-Down-Optionen.
	 * 
	 * @param context
	 *            Context
	 * @return Drop-Down-Optionen
	 */
	Map<String, String> getOptions(SelectionContext context);

	/**
	 * Liefert den Title zum KEY.
	 * 
	 * @param key
	 *            KEY
	 * @param context
	 *            Selection-Context
	 * 
	 * @return Title zum KEY
	 */
	String getTitle(String key, SelectionContext context);
	
	void addValueChangeListener(OptionListChangeEventHandler handler);

	/**
	 * Entfernt die gepufferten Werte.
	 */
	void refresh();

	String getId();

}