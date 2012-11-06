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

/**
 * Schnittstelle des Events für das applikationsweite EventBus Handling.
 * 
 * @param <H>
 *            der für die Verarbeitung dieses Events unterstützte
 *            {@link EventHandler}
 * 
 * @author markus.bonsch
 * 
 */
public interface Event<H extends EventHandler> extends Serializable {

	/**
	 * Aufruf der entsprechenden Handler Methoden.
	 * 
	 * @param eventHandler
	 *            entsprechender Handler für das Eventhandling.
	 * 
	 */
	void dispatch(H eventHandler);
}
