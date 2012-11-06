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

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event für im Container gelöschte Zeilen. Wird z. Zt. nicht über den
 * {@link EventBus} gesendet.
 * 
 * @author carsten.mjartan
 */
public class DeleteEvent implements
		SourceEvent<DeleteEventHandler, DataContainer> {

	private static final long serialVersionUID = 1L;

	private final DataContainer source;

	private final ContainerRow row;

	/**
	 * @param container
	 *            die auslösende Quelle
	 * @param row
	 *            die gelöschte Zeile
	 */
	public DeleteEvent(DataContainer container, ContainerRow row) {
		this.source = container;
		this.row = row;
	}

	@Override
	public DataContainer getSource() {
		return source;
	}

	/**
	 * @return die betroffene Zeile
	 */
	public ContainerRow getRow() {
		return row;
	}

	@Override
	public void dispatch(DeleteEventHandler eventHandler) {
		eventHandler.onDelete(this);
	}

}
