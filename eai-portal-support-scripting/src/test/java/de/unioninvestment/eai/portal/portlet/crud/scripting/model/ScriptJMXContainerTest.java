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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.JmxDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.support.scripting.ScriptJMXWrapper;


public class ScriptJMXContainerTest {

	@Mock
	private ScriptJMXWrapper wrapperMock;
	@Mock
	private DataContainer containerMock;
	@Mock
	private JmxDelegate delegateMock;
	
	private ScriptJMXContainer scriptJMXContainer;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		scriptJMXContainer = new ScriptJMXContainer(wrapperMock, containerMock, delegateMock);
	}
	
	@Test
	public void shouldSetQuery() {
		scriptJMXContainer.setQuery("4711");
		verify(delegateMock).setQuery("4711");
	}

	@Test
	public void shouldGetServer() {
		assertThat(scriptJMXContainer.getServer(), is(wrapperMock));
	}

	@Test
	public void shouldConnect() {
		scriptJMXContainer.connect("4711");
		verify(wrapperMock).connect("4711");
	}
}
