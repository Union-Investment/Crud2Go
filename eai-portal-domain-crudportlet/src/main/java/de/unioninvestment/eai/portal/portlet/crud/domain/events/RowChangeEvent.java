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

import static java.util.Collections.synchronizedMap;

import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * 
 * @author markus.bonsch
 * 
 */
public class RowChangeEvent implements
		SourceEvent<RowChangeEventHandler, ContainerRow> {

	private static final long serialVersionUID = 1L;

	private final ContainerRow source;
	private final Map<String, Object> changedValues;

	/**
	 * Konstruktor.
	 * 
	 * @param source
	 *            Zeile
	 * @param changedValues
	 *            Map mit geänderten Werten
	 * 
	 */
	public RowChangeEvent(ContainerRow source, Map<String, Object> changedValues) {
		this.source = source;
		this.changedValues = synchronizedMap(changedValues);
	}

	@Override
	public void dispatch(RowChangeEventHandler eventHandler) {
		eventHandler.rowChange(this);
	}

	@Override
	public ContainerRow getSource() {
		return source;
	}

	public Map<String, Object> getChangedValues() {
		return changedValues;
	}
}
