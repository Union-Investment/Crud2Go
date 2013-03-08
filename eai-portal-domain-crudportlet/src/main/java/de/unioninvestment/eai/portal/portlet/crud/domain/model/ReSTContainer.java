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

import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * Implementation of GenericContainer for ReST-Operations.
 * 
 * @author carsten.mjartan
 */
public class ReSTContainer extends GenericDataContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * @param eventBus
	 *            the Event Bus needed by {@link GenericDataContainer}
	 * @param formatPattern
	 *            the format patterns needed by {@link GenericDataContainer}
	 * @param defaultOrder
	 *            the default order configuration needed by
	 *            {@link GenericDataContainer}
	 * @param filterPolicy
	 *            the filter policy configuration needed by
	 *            {@link GenericDataContainer}
	 */
	public ReSTContainer(EventBus eventBus, Map<String, String> formatPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy) {
		super(eventBus, formatPattern, defaultOrder, filterPolicy);
	}

	/**
	 * @param newBaseUrl
	 *            the new base url for future ReST operations
	 */
	public void setBaseUrl(String newBaseUrl) {
		getReSTDelegate().setBaseUrl(newBaseUrl);
		refresh();
	}

	/**
	 * @param newQueryUrl
	 *            the new query url for future ReST operations
	 */
	public void setQueryUrl(String newQueryUrl) {
		getReSTDelegate().setQueryUrl(newQueryUrl);
		refresh();
	}

	/**
	 * @return the current delegate
	 */
	private ReSTDelegate getReSTDelegate() {
		return (ReSTDelegate) getDelegate();
	}

}
