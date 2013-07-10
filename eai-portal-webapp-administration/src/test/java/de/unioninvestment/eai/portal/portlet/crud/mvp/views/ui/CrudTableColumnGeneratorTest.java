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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;

import de.unioninvestment.eai.portal.portlet.crud.datatypes.StringDataType;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.support.vaadin.junit.Answers;

public class CrudTableColumnGeneratorTest {

	private CrudTableColumnGenerator columnGenerator;

	private String otherColumnId = "otherColumnId";

	private Object itemId = "itemId";
	private String columnId = "columnId";
	private Integer columnHeight = 25;

	@Mock
	private CrudTable tableMock;

	@Mock
	Item itemMock;

	@Mock
	private Property propertyMock;

	@Mock
	private TableColumns tableColumnsMock;

	@Mock
	private CheckBoxTableColumn checkBoxModelMock;

	@Mock
	private Container vaadinContainerMock;

	@Mock
	private DataContainer containerMock;

	private StringDataType editorSupportMock = new StringDataType();

	@Mock
	private com.vaadin.ui.CheckBox checkBoxMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(containerMock.getDataSourceContainer()).thenReturn(
				vaadinContainerMock);

		when(containerMock.getFormat("columnId")).thenReturn(null);
		when(vaadinContainerMock.getItem(itemId)).thenReturn(itemMock);
		when(tableMock.getItem(itemId)).thenReturn(itemMock);

		setupColumnGenerator();
	}

	private void setupColumnGenerator() {
		columnGenerator = new CrudTableColumnGenerator("columnId",
				String.class, columnHeight, tableColumnsMock, null, tableMock,
				containerMock, editorSupportMock);
	}

	@Test
	public void shouldReturnNullOnEmptyItemProperty() {
		when(itemMock.getItemProperty(columnId)).thenReturn(null);
		assertNull(columnGenerator.generateCell(tableMock, itemId, columnId));
	}

	@Test
	public void shouldReturnStringForSimpleProperty() {
		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn("formattedValue");
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);

		assertThat((String) columnGenerator.generateCell(tableMock, itemId,
				columnId), is("formattedValue"));
	}

	// @Test
	// public void shouldReturnStringFromDisplayerForSimpleProperty() {
	// when(propertyMock.getValue()).thenReturn("value");
	// when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
	//
	// assertThat((String) columnGenerator.generateCell(tableMock, itemId,
	// columnId), is("formattedValue"));
	// }

	@Test
	public void shouldReturnTextAreaWithCorrectHeight() {
		when(tableColumnsMock.getMultilineNames()).thenReturn(asList(columnId));
		columnGenerator = new CrudTableColumnGenerator("columnId",
				String.class, columnHeight, tableColumnsMock, null, tableMock,
				containerMock, editorSupportMock);

		when(tableMock.getItem(itemId)).thenReturn(itemMock);
		when(propertyMock.getValue()).thenReturn("value");
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn(new TextArea());

		Object component = columnGenerator.generateCell(tableMock, itemId,
				columnId);
		assertTrue(component instanceof TextArea);
		assertEquals(100.0f, ((TextArea) component).getHeight(), 0);
	}

	@Test
	public void shouldReturnLabelWithFullHeightForMultilineColumns() {
		when(tableColumnsMock.getMultilineNames()).thenReturn(asList(columnId));
		columnGenerator = new CrudTableColumnGenerator("columnId",
				String.class, columnHeight, tableColumnsMock, null, tableMock,
				containerMock, editorSupportMock);
		when(tableMock.getItem(itemId)).thenReturn(itemMock);

		when(propertyMock.getValue()).thenReturn("value");
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn(new Object());
		Label label = (Label) columnGenerator.generateCell(tableMock, itemId,
				columnId);
		assertEquals(100.0f, label.getHeight(), 0);

	}

	@Test
	public void shouldReturnStringForFirstRowIfThereAreMultilineColumns() {
		when(tableColumnsMock.getMultilineNames()).thenReturn(
				asList(otherColumnId));
		columnGenerator = new CrudTableColumnGenerator("columnId",
				String.class, columnHeight, tableColumnsMock, columnId,
				tableMock, containerMock, editorSupportMock);
		when(tableMock.getItem(itemId)).thenReturn(itemMock);

		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn("formattedValue");
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		assertThat((String) columnGenerator.generateCell(tableMock, itemId,
				columnId), is("formattedValue"));
	}

	@Test
	public void shouldReturnReadOnlyCheckBoxComponent() {
		when(tableColumnsMock.isCheckbox(anyString())).thenReturn(true);

		when(tableColumnsMock.getCheckBox(anyString())).thenReturn(
				checkBoxModelMock);
		when(propertyMock.getValue()).thenReturn("true");
		when(propertyMock.getType()).thenAnswer(Answers.object(String.class));
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn(new Object());

		when(tableMock.getType(any())).thenAnswer(new Answer<Class<String>>() {
			@Override
			public Class<String> answer(InvocationOnMock invocation)
					throws Throwable {
				return String.class;
			}
		});
		when(checkBoxModelMock.getCheckedValue()).thenReturn("true", "true");
		when(checkBoxModelMock.getUncheckedValue())
				.thenReturn("false", "false");
		when(containerMock.getFormat(columnId)).thenReturn(null);

		setupColumnGenerator();
		Object component = columnGenerator.generateCell(tableMock, itemId,
				columnId);

		assertThat(component instanceof com.vaadin.ui.CheckBox, is(true));
		assertThat(((com.vaadin.ui.CheckBox) component).isReadOnly(), is(true));

	}

	@Test
	public void shouldReturnLabelIfCheckBoxValueIsNotValid() {
		when(tableColumnsMock.isCheckbox(anyString())).thenReturn(true);

		when(tableColumnsMock.getCheckBox(anyString())).thenReturn(
				checkBoxModelMock);
		when(propertyMock.getValue()).thenReturn("horst");
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		when(tableMock.getPropertyValue(itemId, columnId, propertyMock))
				.thenReturn(new Object());

		when(tableMock.getType(any())).thenAnswer(new Answer<Class<String>>() {
			@Override
			public Class<String> answer(InvocationOnMock invocation)
					throws Throwable {
				return String.class;
			}
		});
		when(checkBoxModelMock.getCheckedValue()).thenReturn("true", "true");
		when(checkBoxModelMock.getUncheckedValue())
				.thenReturn("false", "false");

		setupColumnGenerator();
		Object component = columnGenerator.generateCell(tableMock, itemId,
				columnId);

		assertThat(component instanceof Label, is(true));
		assertThat(((Label) component).getValue().toString(), is("horst"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetGeneratedPropertyForExport() {
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		propertyMockReturnsType(propertyMock, BigDecimal.class);
		when(propertyMock.getValue()).thenReturn(new BigDecimal("100.00"));
		Property property = columnGenerator.getGeneratedProperty(itemId,
				columnId);

		assertThat((Class<BigDecimal>) property.getType(),
				equalTo(BigDecimal.class));
		assertThat(property.getValue(), is((Object) new BigDecimal("100.00")));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGeneratedTitlePropertyForExportOfDropdowns() {
		when(tableColumnsMock.isDropdown(columnId)).thenReturn(true);
		setupColumnGenerator();
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		propertyMockReturnsType(propertyMock, Integer.class);
		when(tableMock.formatPropertyValue(itemId, columnId, propertyMock))
				.thenReturn("einsZweiDreiVier");

		Property property = columnGenerator.getGeneratedProperty(itemId,
				columnId);

		assertThat((Class<String>) property.getType(), equalTo(String.class));
		assertThat(property.getValue(), is((Object) "einsZweiDreiVier"));
	}

	@SuppressWarnings("unchecked")
	private void propertyMockReturnsType(Property propertyMock,
			final Class<?> clazz) {
		when(propertyMock.getType()).thenAnswer(new Answer<Class<Object>>() {
			@Override
			public Class<Object> answer(InvocationOnMock invocation)
					throws Throwable {
				return (Class<Object>) clazz;
			}
		});
	}

	@Test
	public void shouldGetGeneratedPropertyWhenValueIsNull() {
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		propertyMockReturnsType(propertyMock, BigDecimal.class);
		when(propertyMock.getValue()).thenReturn(null);
		Property property = columnGenerator.getGeneratedProperty(itemId,
				columnId);

		assertThat(property.getValue(), nullValue());

	}

	@Test
	public void shouldGetPropertyTypeForExport() {
		columnGenerator = new CrudTableColumnGenerator("Test", Date.class,
				columnHeight, tableColumnsMock, columnId, tableMock,
				containerMock, editorSupportMock);
		when(itemMock.getItemProperty(columnId)).thenReturn(propertyMock);
		propertyMockReturnsType(propertyMock, Date.class);
		when(propertyMock.getValue()).thenReturn(new Date());

		Property property = columnGenerator.getGeneratedProperty(itemId,
				columnId);
		Class<?> result = columnGenerator.getType();
		assertThat(result.equals(Date.class), is(true));
	}

	@Test
	public void shouldGetGeneratedPropertyWithNullValueWhenItemIsNull() {
		when(tableMock.getItem(itemId)).thenReturn(null);
		Property property = columnGenerator.getGeneratedProperty(itemId,
				columnId);

		assertThat(null, is(property));
	}
}
