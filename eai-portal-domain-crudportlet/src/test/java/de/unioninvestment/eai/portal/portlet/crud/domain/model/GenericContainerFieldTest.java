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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericProperty;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericVaadinContainer;

public class GenericContainerFieldTest extends ContainerFieldTest {

	@Mock
	private GenericContainerRow genericContainerRowMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private Column columnMock;

	@Mock
	private GenericItem itemMock;

	@Mock
	private GenericVaadinContainer vaadinContainerMock;

	private GenericProperty itemProperty;
	
	@Before
	public void setupGenericContainerFieldTest() {
		propertyId = "id";
		propertyName = "name";
	}

	@Test
	public void shouldReturnTrueIfPropertyIsReadonly() {
		GenericContainerField field = createContainerField();
		itemProperty.setReadOnly(true);

		Assert.assertTrue(field.isReadonly());
	}

	@Test
	public void shouldReturnTrueIfRowIsReadonly() {
		GenericContainerField field = createContainerField();
		Mockito.when(genericContainerRowMock.isReadonly()).thenReturn(true);
		
		Assert.assertTrue(field.isReadonly());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	GenericContainerField createContainerField() {
		Mockito.when(columnMock.getName()).thenReturn(propertyName);
		
		@SuppressWarnings("rawtypes")
		Class c = String.class;
		Mockito.when(columnMock.getType()).thenReturn(c);

		Mockito.when(itemMock.getContainer()).thenReturn(vaadinContainerMock);
		
		itemProperty = new GenericProperty(columnMock, propertyValue);
		itemProperty.setItem(itemMock);
		
		return new GenericContainerField(genericContainerRowMock, itemProperty, containerMock);
	}
}
