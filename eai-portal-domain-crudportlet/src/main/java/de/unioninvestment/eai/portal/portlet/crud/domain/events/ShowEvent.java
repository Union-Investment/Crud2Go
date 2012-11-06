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

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event für die Sichtbarwerden einer Komponente, wird z. Zt. nicht über den
 * {@link EventBus} gesendet.
 * 
 * @param <T>
 *            der Typ der Event-Quelle
 * 
 * @author carsten.mjartan
 */
public class ShowEvent<T> implements SourceEvent<ShowEventHandler<T>, T> {

	private static final long serialVersionUID = 42L;

	private final T source;

	/**
	 * 
	 * @param source
	 *            Source
	 */
	public ShowEvent(T source) {
		this.source = source;
	}

	@Override
	public void dispatch(ShowEventHandler<T> eventHandler) {
		eventHandler.onShow(this);
	}

	public T getSource() {
		return source;
	}

}
