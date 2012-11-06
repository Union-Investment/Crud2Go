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
package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import com.vaadin.addon.sqlcontainer.query.QueryDelegate;

/**
 * Abstrakte Oberklasse für QueryDelegates die ein Timeout unterstützen.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public abstract class AbstractTimeoutableQueryDelegate implements QueryDelegate {

	private static final long serialVersionUID = 1L;

	private int queryTimeout = 0;

	/**
	 * @return die Timeoutzeit in Sekunden.
	 */
	public int getQueryTimeout() {
		return this.queryTimeout;
	}

	/**
	 * Set a timeout time in seconds for the count query.
	 * 
	 * @param seconds
	 *            the timeout time or <code>0</code> to set no timeout. Values
	 *            below 0 will be ignored.
	 */
	public void setQueryTimeout(int seconds) {
		if (seconds >= 0) {
			this.queryTimeout = seconds;
		}
	}
}
