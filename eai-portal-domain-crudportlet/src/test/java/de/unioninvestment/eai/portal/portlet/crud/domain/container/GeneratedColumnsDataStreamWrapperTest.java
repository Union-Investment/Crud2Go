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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;


public class GeneratedColumnsDataStreamWrapperTest {

	@Mock
	private DataStream delegateMock;
	
	@Mock
	private TableColumns columnsMock;

	private GeneratedColumnsDataStreamWrapper wrapper;

	@Mock
	private StreamItem itemMock;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		wrapper = new GeneratedColumnsDataStreamWrapper(delegateMock, asList("A", "B"), columnsMock);
	}
	
	@Test
	public void shouldDelegateOpen() {
		when(delegateMock.open(true)).thenReturn(4711);
		assertThat(wrapper.open(true), is(4711));
	}
	
	@Test
	public void shouldDelegateHasNext() {
		when(delegateMock.hasNext()).thenReturn(true);
		assertThat(wrapper.hasNext(), is(true));
	}
	
	@Test
	public void shouldDelegateClose() {
		wrapper.close();
		verify(delegateMock).close();
	}
	
	@Test
	public void shouldDelegateRemove() {
		wrapper.remove();
		verify(delegateMock).remove();
	}
	
	@Test
	public void shouldWrapNext() {
		when(delegateMock.next()).thenReturn(itemMock);
		StreamItem wrapppedItem = wrapper.next();
		assertThat(wrapppedItem, instanceOf(GeneratedColumnsStreamItemWrapper.class));
	}
}
