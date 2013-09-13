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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

public class GenericVaadinContainerEventWrapperTest {

	private MetaData metaData;

	@Mock
	private GenericDelegate delegateMock;

	@Mock
	private EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouterMock;

	@Mock
	private EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouterMock;

	@Mock
	private EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouterMock;

	@Mock
	private EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouterMock;

	@Mock
	private DataContainer dataContainerMock;

	private GenericVaadinContainerEventWrapper container;

	@Captor
	private ArgumentCaptor<CreateEvent> createEventCaptor;
	@Captor
	private ArgumentCaptor<InsertEvent> insertEventCaptor;
	@Captor
	private ArgumentCaptor<UpdateEvent> updateEventCaptor;
	@Captor
	private ArgumentCaptor<DeleteEvent> deleteEventCaptor;
	@Mock
	private ContainerRow rowMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Column col1 = new Column("COL1", String.class, false, false, true, null);
		Column col2 = new Column("COL2", String.class, false, false, false,
				null);
		metaData = new MetaData(asList(col1, col2), false, false, false, false,
				false);
		when(delegateMock.getMetaData()).thenReturn(metaData);
		when(delegateMock.getRows()).thenReturn(
				asList(new Object[] { "1", "2" }, new Object[] { "3", "4" }));

		container = new GenericVaadinContainerEventWrapper(delegateMock,
				dataContainerMock, onCreateEventRouterMock,
				onInsertEventRouterMock, onUpdateEventRouterMock,
				onDeleteEventRouterMock);
	}

	@Test
	public void shouldFireCreateEventOnAdd() {
		when(
				dataContainerMock.convertItemToRow(any(Item.class), eq(false),
						eq(false))).thenReturn(rowMock);

		container.addItem();
		verify(onCreateEventRouterMock).fireEvent(createEventCaptor.capture());
		assertThat(createEventCaptor.getValue().getRow(), is(rowMock));
	}

	@Test
	public void shouldFireInsertEventOnCommit() {
		when(
				dataContainerMock.convertItemToRow(any(Item.class), eq(false),
						eq(true))).thenReturn(rowMock);

		container.addItem();
		container.commit();

		verify(onInsertEventRouterMock).fireEvent(insertEventCaptor.capture());
		assertThat(insertEventCaptor.getValue().getRow(), is(rowMock));
	}

	@Test
	public void shouldFireUpdateEventOnCommit() {
		when(
				dataContainerMock.convertItemToRow(any(Item.class), eq(false),
						eq(true))).thenReturn(rowMock);

		GenericItem item = container.getItem(new GenericItemId(
				new Object[] { "3" }));
		item.getItemProperty("COL2").setValue("5");
		container.commit();

		verify(onUpdateEventRouterMock).fireEvent(updateEventCaptor.capture());
		assertThat(updateEventCaptor.getValue().getRow(), is(rowMock));
	}

	@Test
	public void shouldFireDeleteEventOnCommit() {
		when(
				dataContainerMock.convertItemToRow(any(Item.class), eq(false),
						eq(true))).thenReturn(rowMock);

		container.removeItem(new GenericItemId(new Object[] { "3" }));
		container.commit();

		verify(onDeleteEventRouterMock).fireEvent(deleteEventCaptor.capture());
		assertThat(deleteEventCaptor.getValue().getRow(), is(rowMock));
	}
}
