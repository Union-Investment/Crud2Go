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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Proxy-Implementierung, die es ermöglicht die Ausführung einer Action zu
 * unterbinden.<br/>
 * Dies ist insbesondere dann notwendig, wenn z.B. alle Felder eines Formulars,
 * das über einer ValueChangeTriggerAction verfügt auf einmal geändert werden,
 * man aber die Action nicht bei jeder Feldänderung ausführen lassen möchte. Für
 * diesen Zeitraum kann man dann die Ausführung der Action verhindern, indem man
 * den Proxy via {@link #setActivated(boolean)} deaktiviert.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DeferredExecutionActionProxy {

	private static final Logger LOG = LoggerFactory
			.getLogger(DeferredExecutionActionProxy.class);

	private final FormAction action;

	private boolean activated = true;

	/**
	 * Ezeugt eine neue Instanz dieses Proxys.
	 * 
	 * @param actionToProxy
	 *            die FormAction die geproxied werden soll.
	 */
	public DeferredExecutionActionProxy(FormAction actionToProxy) {
		if (actionToProxy == null) {
			throw new IllegalArgumentException("Undefined form action!");
		}
		this.action = actionToProxy;
	}

	/**
	 * @return <code>true</code> wenn dieser proxy aktiviert ist, was heißt,
	 *         dass ein Aufruf auf {@link #execute()} weiter auf die interne
	 *         Action delegiert. <code>false</code> wenn deaktivert, was heißt,
	 *         das ein Aufruf auf {@link #execute()} nichts macht.
	 */
	public boolean isActivated() {
		return this.activated;
	}

	/**
	 * @param activated
	 *            <code>true</code> um diesen proxy zu aktivieren.
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	/**
	 * Führt die Action aus
	 */
	public void execute() {
		if (this.activated) {
			this.action.execute();
			LOG.debug("Trigger value change action: " + action.getId());
		}
	}
}
