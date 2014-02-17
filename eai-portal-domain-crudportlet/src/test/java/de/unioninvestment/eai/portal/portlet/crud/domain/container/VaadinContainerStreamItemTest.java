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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.VaadinContainerStreamItem;


public class VaadinContainerStreamItemTest {

	@Mock
	private Item itemMock;
	@Mock
	private Property propMock;
	private VaadinContainerStreamItem streamItem;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		streamItem = new VaadinContainerStreamItem(itemMock);
	}
	
	@Test
	public void shouldGetValueFromItemProperty() {
		when(itemMock.getItemProperty("A")).thenReturn(propMock);
		when(propMock.getValue()).thenReturn("AValue");
		assertThat(streamItem.getValue("A"), is((Object)"AValue"));
	}
	
	@Test
	public void shouldReturnNullForNonExistentProperties() {
		when(itemMock.getItemProperty("A")).thenReturn(null);
		assertThat(streamItem.getValue("A"), is(nullValue()));
	}
	
}
