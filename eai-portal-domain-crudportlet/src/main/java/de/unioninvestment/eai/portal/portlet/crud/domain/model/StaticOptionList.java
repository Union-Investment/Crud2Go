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

import java.util.LinkedHashMap;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.config.OptionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;

/**
 * 
 * Modell-Klasse für Auswahl-Boxen. Die Auswahlmöglichkeiten werden statisch
 * übergeben.
 * 
 * @author max.hartmann
 * 
 */
public class StaticOptionList implements OptionList {

	private Map<String, String> options;

	private String id;

	/**
	 * Konstruktor mit Parametern. Wird verwendet, wenn die Optionen aus der
	 * Daten Datenbank gelesen werden sollen.
	 * 
	 * @param config
	 *            Konfiguration der Auswahl-Box
	 */
	public StaticOptionList(SelectConfig config) {
		this.options = new LinkedHashMap<String, String>();
		id = config.getId();
		for (OptionConfig option : config.getOption()) {
			options.put(option.getKey(), option.getValue());
		}
	}

	/**
	 * Konstruktor mit Parametern. Die Option werden direkt übergeben.
	 * 
	 * @param options
	 *            Auswahl-Optionen
	 */
	public StaticOptionList(Map<String, String> options) {
		this.options = options;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getOptions(SelectionContext context) {
		return options;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(String key, SelectionContext context) {
		if (options != null) {
			return options.get(key);
		}
		return null;
	}

	@Override
	public void addChangeListener(OptionListChangeEventHandler handler) {
		// tue nix
	}

	@Override
	public void removeChangeListener(OptionListChangeEventHandler handler) {
	}

	@Override
	public void refresh() {
		// tue nix
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isLazy() {
		return false;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}
}
