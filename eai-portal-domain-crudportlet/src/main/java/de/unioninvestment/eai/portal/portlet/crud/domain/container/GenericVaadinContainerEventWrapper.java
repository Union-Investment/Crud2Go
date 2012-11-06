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

import java.util.ArrayList;

import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Filterable;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericVaadinContainer;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Unterklasse von {@link GenericVaadinContainer}, die bei CREATE, INSERT,
 * UPDATE und DELETE ensprechende Events auslöst.
 * 
 * @author carsten.mjartan
 */
public class GenericVaadinContainerEventWrapper extends GenericVaadinContainer
		implements Filterable {

	private static final long serialVersionUID = 42L;

	private EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter;
	private EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter;
	private EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter;
	private EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter;

	private final DataContainer container;

	/**
	 * @param container
	 *            der DatabaseContainer der den Events mitgegeben wird
	 * @param onInsertEventRouter
	 *            der Router für INSERT Events
	 * @param onInsertEventRouter
	 *            der Router für INSERT Events
	 * @param onUpdateEventRouter
	 *            der Router für UPDATE Events
	 * @param onDeleteEventRouter
	 *            der Router für DELETE Events
	 */
	public GenericVaadinContainerEventWrapper(GenericDelegate delegate,
			DataContainer container,
			EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter,
			EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter,
			EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter,
			EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter) {
		super(delegate);
		this.container = container;
		this.onCreateEventRouter = onCreateEventRouter;
		this.onInsertEventRouter = onInsertEventRouter;
		this.onUpdateEventRouter = onUpdateEventRouter;
		this.onDeleteEventRouter = onDeleteEventRouter;
	}

	@Override
	protected void created(GenericItem item) {
		ContainerRow row = container.convertItemToRow(item, false, false);
		onCreateEventRouter.fireEvent(new CreateEvent(container, row));
	}

	@Override
	protected void committed(ArrayList<GenericItem> items) {
		for (GenericItem item : items) {
			ContainerRow row = container.convertItemToRow(item, false, true);
			if (item.isNewItem()) {
				onInsertEventRouter.fireEvent(new InsertEvent(container, row));
			} else if (item.isDeleted()) {
				onDeleteEventRouter.fireEvent(new DeleteEvent(container, row));
			} else if (item.isModified()) {
				onUpdateEventRouter.fireEvent(new UpdateEvent(container, row));
			}
		}
	}

	@Override
	public Item getItemUnfiltered(Object itemId) {
		return getItem(itemId);
	}
}
