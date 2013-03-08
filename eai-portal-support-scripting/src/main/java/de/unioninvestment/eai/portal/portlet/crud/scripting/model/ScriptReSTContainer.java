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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;

/**
 * Container used for ReST operations.
 * 
 * @author carsten.mjartan
 */
public class ScriptReSTContainer extends ScriptContainer {

	private ReSTContainer container;

	/**
	 * @param container
	 *            the backing container
	 */
	public ScriptReSTContainer(ReSTContainer container) {
		super(container);
		this.container = container;
	}

	/**
	 * Updates the base url and refreshes the container.
	 * 
	 * @param newBaseUrl
	 *            the new base url
	 */
	public void setBaseUrl(String newBaseUrl) {
		container.setBaseUrl(newBaseUrl);
	}

	/**
	 * Updates the query url and refreshes the container.
	 * 
	 * @param newQueryUrl
	 *            the new query url
	 */
	public void setQueryUrl(String newQueryUrl) {
		container.setQueryUrl(newQueryUrl);
	}

}
