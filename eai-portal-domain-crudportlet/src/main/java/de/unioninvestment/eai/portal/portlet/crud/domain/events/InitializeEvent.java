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
package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event, der bei der Initialisierung von Komponenten (z. B. Tabellen) erzeugt
 * wird.
 * 
 * @param <T>
 *            der Ursprung des Events, in der Regel eine Komponente (z.B.
 *            Tabelle)
 * @author Bastian Krol
 */
public class InitializeEvent<T> implements
		SourceEvent<InitializeEventHandler<T>, T> {
	private static final long serialVersionUID = 42L;

	private final T source;

	/**
	 * Konstruktor.
	 * 
	 * @param source
	 *            Source
	 */
	public InitializeEvent(T source) {
		this.source = source;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.support.vaadin.mvp.Event#dispatch(de.unioninvestment.eai.portal.support.vaadin.mvp.EventHandler)
	 */
	@Override
	public void dispatch(InitializeEventHandler<T> eventHandler) {
		eventHandler.onInitialize(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent#getSource()
	 */
	public T getSource() {
		return source;
	}
}
