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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class DeferredExecutionActionProxyTest {

	@Test
	public void shouldBeActivatedByDefault() {
		DeferredExecutionActionProxy proxy = new DeferredExecutionActionProxy(
				mock(FormAction.class));

		assertTrue(proxy.isActivated());
	}

	@Test
	public void canBeActivatedAndDeactivated() {
		DeferredExecutionActionProxy proxy = new DeferredExecutionActionProxy(
				mock(FormAction.class));

		assertTrue(proxy.isActivated());
		proxy.setActivated(false);
		assertFalse(proxy.isActivated());
		proxy.setActivated(true);
		assertTrue(proxy.isActivated());
	}

	@Test
	public void shouldDelegateToProxiedActionIfActivated() {
		FormAction actionMock = mock(FormAction.class);
		DeferredExecutionActionProxy proxy = new DeferredExecutionActionProxy(
				actionMock);
		assertTrue(proxy.isActivated());

		proxy.execute();

		verify(actionMock).execute();
	}

	@Test
	public void shouldSkipProxiedActionIfDeactivated() {
		FormAction actionMock = mock(FormAction.class);
		DeferredExecutionActionProxy proxy = new DeferredExecutionActionProxy(
				actionMock);
		assertTrue(proxy.isActivated());

		proxy.setActivated(false);
		proxy.execute();

		verify(actionMock, times(0)).execute();
	}
}
