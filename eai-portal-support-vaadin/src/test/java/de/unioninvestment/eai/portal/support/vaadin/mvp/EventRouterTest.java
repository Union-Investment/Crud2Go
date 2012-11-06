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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EventRouterTest {

	private EventRouter<EventHandler, Event<EventHandler>> eventRouter;

	@Mock
	private EventHandler firstEventHandlerMock;

	@Before
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setup() {
		MockitoAnnotations.initMocks(this);
		eventRouter = new EventRouter();
	}

	@Test
	public void shouldAddHandler() {
		int sizeBefore = eventRouter.getRegisteredHandlerSize();

		eventRouter.addHandler(firstEventHandlerMock);
		int sizeAfter = eventRouter.getRegisteredHandlerSize();

		Assert.assertEquals(sizeBefore + 1, sizeAfter);
	}

	@Test
	public void shouldRemoveHandler() {
		eventRouter.addHandler(firstEventHandlerMock);
		int sizeBefore = eventRouter.getRegisteredHandlerSize();

		eventRouter.removeHandler(firstEventHandlerMock);
		int sizeAfter = eventRouter.getRegisteredHandlerSize();

		Assert.assertEquals(sizeBefore - 1, sizeAfter);
	}

	@Test
	public void shouldReturnTrueOnContains() {
		eventRouter.addHandler(firstEventHandlerMock);
		boolean result = eventRouter.contains(firstEventHandlerMock);
		Assert.assertTrue(result);
	}

	@Test
	public void shouldReturnFalseOnContains() {
		boolean result = eventRouter.contains(firstEventHandlerMock);
		Assert.assertFalse(result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldCallHandlerOnFireEvent() {
		Event<EventHandler> eventMock = Mockito.mock(Event.class);

		eventRouter.addHandler(firstEventHandlerMock);
		eventRouter.fireEvent(eventMock);

		Mockito.verify(eventMock).dispatch(firstEventHandlerMock);
	}
}
