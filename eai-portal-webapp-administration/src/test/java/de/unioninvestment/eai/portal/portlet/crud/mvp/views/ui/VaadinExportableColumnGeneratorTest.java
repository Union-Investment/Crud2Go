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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Property;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GeneratedValueGenerator;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;

public class VaadinExportableColumnGeneratorTest {

	@Mock
	private DataContainer dataContainerMock;
	@Mock
	private TableColumn columnMock;

	@Mock
	private GeneratedValueGenerator generatorMock;
	@Mock
	private ContainerRow rowMock;

	private VaadinExportableColumnGenerator generator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		generator = new VaadinExportableColumnGenerator(columnMock,
				dataContainerMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnTypeFromModel() {
		when(columnMock.getGeneratedType()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return String.class;
			}
		});
		assertThat((Class<String>) generator.getType(), equalTo(String.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnValueFromGeneratorInModel() {
		when(columnMock.getGeneratedValueGenerator()).thenReturn(generatorMock);
		when(dataContainerMock.getRowByInternalRowId("itemId", false, true))
				.thenReturn(rowMock);
		when(generatorMock.getValue(rowMock)).thenReturn("1234");
		when(columnMock.getGeneratedType()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return String.class;
			}
		});

		Property<?> property = generator.getGeneratedProperty("itemId", "col");

		assertThat(property.getValue(), is((Object) "1234"));
		assertThat((Class<String>) property.getType(), equalTo(String.class));
	}
}
