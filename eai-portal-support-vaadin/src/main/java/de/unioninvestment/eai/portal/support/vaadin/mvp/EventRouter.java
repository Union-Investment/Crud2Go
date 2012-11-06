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
import java.util.HashSet;
import java.util.Set;

/**
 * Der EventRouter dient dem lokalen Eventhandling, an ihm registrieren sich
 * unterschiedliche EventHandler.
 * 
 * 
 * @author markus.bonsch
 * @param <H>
 *            EventHandler Klasse
 * @param <E>
 *            Event Klasse
 */
public class EventRouter<H extends EventHandler, E extends Event<H>> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private Set<H> handler;

	/**
	 * 
	 * Handler an den EventBus registieren.
	 * 
	 * @param eventHandler
	 *            der zu registrierende Handler.
	 */
	public void addHandler(H eventHandler) {
		if (handler == null) {
			handler = new HashSet<H>();
		}
		handler.add(eventHandler);
	}

	/**
	 * Handler aus Liste entfernen.
	 * 
	 * @param eventHandler
	 *            der deregistriert werden soll.
	 */
	public void removeHandler(EventHandler eventHandler) {
		if (handler != null) {
			handler.remove(eventHandler);
		}
	}

	/**
	 * Ein Event an alle registierten Eventhandler abfeuern.
	 * 
	 * @param event
	 *            welches an die registrierten Handler übergeben wird.
	 */
	public void fireEvent(E event) {
		if (handler != null) {
			for (H h : handler) {
				event.dispatch(h);
			}
		}
	}

	/**
	 * Liefert die Anzahl der registrierten Handler.
	 * 
	 * @return Anzahl der Handler
	 */
	public int getRegisteredHandlerSize() {
		if (handler == null) {
			return 0;
		} else {
			return handler.size();
		}
	}

	/**
	 * @param eventHandler
	 *            ein EventHandler
	 * @return <code>true</code>, falls der Handler registriert ist
	 */
	public boolean contains(H eventHandler) {
		if (this.handler == null) {
			return false;
		}

		return this.handler.contains(eventHandler);
	}
}
