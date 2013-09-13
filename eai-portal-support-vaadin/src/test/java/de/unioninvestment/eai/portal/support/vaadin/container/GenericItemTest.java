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
package de.unioninvestment.eai.portal.support.vaadin.container;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GenericItemTest {

	@Mock
	private GenericVaadinContainer containerMock;

	@Mock
	private GenericItemId idMock;

	private GenericItem item;

	@Mock
	private GenericProperty<Object> propertyMock;

	@Mock
	private GenericProperty<Object> propertyMock2;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		item = new GenericItem(containerMock, idMock,
				asList((GenericProperty) propertyMock));
	}

	@Test
	public void shouldBeModifiedIfPropertyIsModified() {

		when(propertyMock.isModified()).thenReturn(true);
		assertThat(item.isModified(), is(true));
	}

	@Test
	public void shouldNotBeModifiedIfNoPropertyIsModified() {
		assertThat(item.isModified(), is(false));
	}

	@Test
	public void shouldBeNewItem() {

		when(idMock.isNewId()).thenReturn(true);

		assertThat(item.isNewItem(), is(true));
	}

	@Test
	public void shouldNotBeNewItem() {
		assertThat(item.isNewItem(), is(false));
	}

	@Test
	public void shouldBeDeleted() {
		when(containerMock.isDeleted(idMock)).thenReturn(true);
		assertThat(item.isDeleted(), is(true));
	}

	@Test
	public void shouldNotBeDeleted() {
		when(containerMock.isDeleted(idMock)).thenReturn(false);
		assertThat(item.isDeleted(), is(false));
	}

	@Test
	public void shouldPropagateCommitToProperties() {
		item.commit();
		verify(propertyMock).commit();
	}

	@Test
	public void shouldReturnPropertyByName() {
		when(propertyMock.getName()).thenReturn("TEST");
		assertThat((GenericProperty) item.getItemProperty("TEST"),
				is(propertyMock));
	}

	@Test
	public void shouldReturnNullIfPropertyNotExists() {
		when(propertyMock.getName()).thenReturn("TEST");
		assertThat((GenericProperty) item.getItemProperty("WRONGNAME"),
				nullValue());
	}

	@Test
	public void shouldSetItemOnProperties() {
		item = new GenericItem(containerMock, idMock, asList(
				(GenericProperty) propertyMock, propertyMock2));
		verify(propertyMock).setItem(item);
		verify(propertyMock2).setItem(item);
	}

	@Test
	public void shouldReturnFirstMatchingProperty() {
		item = new GenericItem(containerMock, idMock, asList(
				(GenericProperty) propertyMock, propertyMock2));

		when(propertyMock.getName()).thenReturn("TEST1");
		when(propertyMock2.getName()).thenReturn("TEST2");

		assertThat((GenericProperty) item.getItemProperty("TEST2"),
				is(propertyMock2));
	}

	@Test
	public void shouldReturnPropertyIdsFromMetaData() {
		when(containerMock.getContainerPropertyIds()).thenAnswer(
				new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return asList("COLUMN1");
					}
				});
		assertThat((List<String>) item.getItemPropertyIds(),
				is(asList("COLUMN1")));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportAddItemProperty() {
		item.addItemProperty(null, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportRemoveItemProperty() {
		item.removeItemProperty(null);
	}

}
