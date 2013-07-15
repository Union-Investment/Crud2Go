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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.vaadin.data.Property;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;

public class GenericContainerRowTest extends ContainerRowTest {

	@Mock
	private GenericItem rowItemMock;

	@Mock
	private GenericItemId itemIdMock;

	@Mock
	private GenericContainerRowId containerRowIdMock;

	@Test
	public void shouldCheckReadonly() {
		// given
		Mockito.when(dataContainerMock.isUpdateable()).thenReturn(false);
		Mockito.when(rowItemMock.getId()).thenReturn(itemIdMock);
		Mockito.when(dataContainerMock.convertInternalRowId(itemIdMock))
				.thenReturn(containerRowIdMock);
		GenericContainerRow row = createContainerRow();

		// when
		boolean isReadonly = row.isReadonly();

		// then
		Assert.assertTrue(isReadonly);
	}

	@Test
	public void shouldCheckReadonlyOnUpdateableContainer() {
		// given
		Mockito.when(dataContainerMock.isUpdateable()).thenReturn(true);
		Mockito.when(rowItemMock.getId()).thenReturn(itemIdMock);
		Mockito.when(dataContainerMock.convertInternalRowId(itemIdMock))
				.thenReturn(containerRowIdMock);
		GenericContainerRow row = createContainerRow();

		// when
		boolean isReadonly = row.isReadonly();

		// then
		Assert.assertFalse(isReadonly);
	}

	@Test
	public void shouldConstructGenericContainerRow() {
		// given
		boolean isTransactional = true;
		boolean isImmutable = true;

		@SuppressWarnings("rawtypes")
		HashSet ids = new HashSet();
		Mockito.when(rowItemMock.getItemPropertyIds()).thenReturn(ids);

		// when
		GenericContainerRow row = createContainerRow();

		// then
		Assert.assertEquals(isImmutable, row.isImmutable());
		Assert.assertEquals(isTransactional, row.isTransactional());
		Assert.assertEquals(rowItemMock, row.getInternalRow());
	}

	@Test
	public void shouldCloneRow() throws Exception {
		// given
		List ids = Arrays.asList("id-1");

		Mockito.when(rowItemMock.getItemPropertyIds()).thenReturn(ids);

		Property property1Mock = Mockito.mock(GenericProperty.class, Mockito
				.withSettings().extraInterfaces(Property.class));
		Mockito.when(property1Mock.getValue()).thenReturn("value-1");

		Mockito.when(rowItemMock.getItemProperty("id-1")).thenReturn(
				property1Mock);

		ContainerRow clone = Mockito.mock(ContainerRow.class);
		Mockito.when(dataContainerMock.addRow()).thenReturn(clone);
		GenericContainerRow row = createContainerRow();

		// when
		ContainerRow result = row.clone();

		// then
		Assert.assertNotNull(result);
		Assert.assertEquals(clone, result);

		Mockito.verify(dataContainerMock).addRow();

		ArgumentCaptor<String> valueCaptor = ArgumentCaptor
				.forClass(String.class);
		Mockito.verify(clone).setValue(valueCaptor.capture(),
				valueCaptor.capture());

		Assert.assertEquals("id-1", valueCaptor.getAllValues().get(0));
		Assert.assertEquals("value-1", valueCaptor.getAllValues().get(1));
	}

	@Override
	GenericContainerRow createContainerRow() {
		return new GenericContainerRow(rowItemMock, dataContainerMock, true,
				true);
	}
}
