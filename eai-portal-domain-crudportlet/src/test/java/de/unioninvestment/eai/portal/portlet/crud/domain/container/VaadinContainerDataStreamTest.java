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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container.Ordered;
import com.vaadin.data.Item;

public class VaadinContainerDataStreamTest {

	private VaadinContainerDataStream stream;
	@Mock
	private Ordered containerMock;
	@Mock
	private Item itemMock1, itemMock2, itemMock3;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		stream = new VaadinContainerDataStream(containerMock);
	}

	@Test
	public void shouldReturnTheSizeGivenByTheVaadinContainer() {
		when(containerMock.size()).thenReturn(23);
		assertThat(stream.open(true), is(23));
	}

	@Test
	public void shouldDeliverOrderedContainerStreamItems() {
		when(containerMock.firstItemId()).thenReturn(1);
		when(containerMock.nextItemId(1)).thenReturn(2);
		when(containerMock.nextItemId(2)).thenReturn(3);
		when(containerMock.nextItemId(3)).thenReturn(null);
		when(containerMock.getItem(1)).thenReturn(itemMock1);
		when(containerMock.getItem(2)).thenReturn(itemMock2);
		when(containerMock.getItem(3)).thenReturn(itemMock3);
		
		stream.open(false);
		assertThat(stream.hasNext(), is(true));
		assertThat(stream.next(), instanceOf(VaadinContainerStreamItem.class));
		assertThat(stream.hasNext(), is(true));
		assertThat(stream.next(), instanceOf(VaadinContainerStreamItem.class));
		assertThat(stream.hasNext(), is(true));
		assertThat(stream.next(), instanceOf(VaadinContainerStreamItem.class));
		assertThat(stream.hasNext(), is(false));
		verify(containerMock, times(3)).getItem(Mockito.anyInt());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldFailOnRemove() {
		stream.remove();
	}
}
