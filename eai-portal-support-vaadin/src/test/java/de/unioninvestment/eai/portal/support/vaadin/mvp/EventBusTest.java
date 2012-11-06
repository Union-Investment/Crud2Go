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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class EventBusTest {

	class TestEventHandler implements EventHandler {

		private static final long serialVersionUID = 1L;

		boolean eventIsFired = false;

		public boolean isEventIsFired() {
			return eventIsFired;
		}

		private void onHandleTest(TestEvent event) {
			eventIsFired = true;
		}
	}

	class TestEvent implements Event<TestEventHandler> {

		private static final long serialVersionUID = 1L;

		@Override
		public void dispatch(TestEventHandler eventHandler) {
			eventHandler.onHandleTest(this);

		}
	}

	private EventBus eventBus;

	private TestEvent event;

	private TestEventHandler eventHandler;

	@Before
	public void setUp() throws Exception {
		event = new TestEvent();
		eventHandler = new TestEventHandler();

		eventBus = new EventBus();
	}

	@Test
	public void shouldRegisterEventHandler() {
		eventBus.addHandler(TestEvent.class, eventHandler);

		int size = eventBus.getRegisteredHandlerSize(TestEvent.class);

		assertEquals(1, size);
	}

	@Test
	public void shouldRemoveEventHandler() {
		eventBus.addHandler(TestEvent.class, eventHandler);
		eventBus.removeHandler(TestEvent.class, eventHandler);

		int size = eventBus.getRegisteredHandlerSize(Event.class);

		assertEquals(0, size);
	}

	@Test
	public void shouldHandleReciveEvent() {
		eventBus.addHandler(TestEvent.class, eventHandler);
		eventBus.fireEvent(event);
		assertTrue(eventHandler.isEventIsFired());
	}
}
