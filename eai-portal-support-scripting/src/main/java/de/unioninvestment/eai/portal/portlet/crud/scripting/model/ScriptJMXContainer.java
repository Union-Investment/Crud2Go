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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.JmxDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.support.scripting.ScriptJMXWrapper;

/**
 * Container, der seine Datensätze über JMX bezieht.
 */
public class ScriptJMXContainer extends ScriptContainer {

	private final ScriptJMXWrapper scriptJMXWrapper;
	private JmxDelegate delegate;
	private boolean changedServerOrQuery = false;

	/**
	 * Konstruktor.
	 * 
	 * @param wrapper
	 *            Wrapper für den Zugriff auf JMX
	 * @param container
	 *            Datenkontainer der Tabelle
	 */
	public ScriptJMXContainer(ScriptJMXWrapper wrapper,
			DataContainer container, JmxDelegate delegate) {
		super(container);
		this.scriptJMXWrapper = wrapper;
		this.delegate = delegate;
	}

	/**
	 * Gibt den Wrapper für den Zugriff auf JMX zurück.
	 * 
	 * @return Wrapper für den Zugriff auf JMX
	 */
	public ScriptJMXWrapper getServer() {
		return scriptJMXWrapper;
	}

	/**
	 * Setzt den Suchstring neu.
	 * 
	 * @param query
	 *            Suchstring
	 */
	public void setQuery(String query) {
		this.changedServerOrQuery = true;
		this.delegate.setQuery(query);
	}

	/**
	 * Baut eine neue Verbindung auf.
	 * 
	 * @param connectionString
	 *            URL zum Server oder <server>:<port>
	 */
	public void connect(String connectionString) {
		this.changedServerOrQuery = true;
		scriptJMXWrapper.connect(connectionString);
		delegate.setServer(connectionString);
	}

	@Override
	public void refresh() {
		super.refresh();
		applyDefaultOrderIfBackendChanged();
	}

	private void applyDefaultOrderIfBackendChanged() {
		if (this.changedServerOrQuery) {
			getContainer().applyDefaultOrder();
			this.changedServerOrQuery = false;
		}
	}
}