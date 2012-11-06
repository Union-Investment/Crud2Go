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
package de.unioninvestment.eai.portal.support.vaadin.mvp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Der EventBus dient einem zentralen und applikationsweitem Eventhandling, an
 * ihm registrieren sich unterschiedliche EventHandler.
 * 
 * @author markus.bonsch
 * 
 */
public class EventBus implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<String, Set<EventHandler>> handler = new HashMap<String, Set<EventHandler>>();

	/**
	 * 
	 * Handler an den EventBus registieren.
	 * 
	 * @param <T>
	 *            EventHandler Klasse
	 * @param eventClass
	 *            Eventtype zu dem der Handler registriert wird.
	 * @param eventHandler
	 *            der zu registrierende Handler.
	 */
	public <T extends EventHandler> void addHandler(
			Class<? extends Event<T>> eventClass, T eventHandler) {
		if (!handler.containsKey(eventClass.getName())) {
			handler.put(eventClass.getName(), new HashSet<EventHandler>());
		}
		handler.get(eventClass.getName()).add(eventHandler);
	}

	/**
	 * Handler vom EventBus entfernen.
	 * 
	 * @param eventClass
	 *            Typ des Eventhandlers.
	 * @param eventHandler
	 *            der vom Bus deregistriert werden soll.
	 */
	public void removeHandler(Class<?> eventClass, EventHandler eventHandler) {
		if (handler.containsKey(eventClass.getName())) {
			handler.get(eventClass.getName()).remove(eventHandler);
		}
	}

	/**
	 * Ein Event an alle für diesen Typ registierten Eventhandler abfeuern.
	 * 
	 * @param <H>
	 *            Handler Typ
	 * @param <E>
	 *            Event Typ
	 * @param event
	 *            welches an die registrierten Handler übergeben wird.
	 */
	@SuppressWarnings("unchecked")
	public <H extends EventHandler, E extends Event<H>> void fireEvent(E event) {
		if (handler.containsKey(event.getClass().getName())) {
			Set<EventHandler> handlers = handler
					.get(event.getClass().getName());
			for (EventHandler h : handlers) {
				event.dispatch((H) h);
			}
		}
	}

	/**
	 * Liefert die Anzahl der registrierten Handler einer Klasse.
	 * 
	 * @param clazz
	 *            event Typ
	 * @return Anzahl der Handler
	 */
	public int getRegisteredHandlerSize(Class<?> clazz) {
		if (!handler.containsKey(clazz.getName())) {
			return 0;
		} else {
			return handler.get(clazz.getName()).size();
		}
	}
}
