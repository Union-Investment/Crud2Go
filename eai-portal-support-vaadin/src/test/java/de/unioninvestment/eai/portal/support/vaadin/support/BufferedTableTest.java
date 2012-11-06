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
package de.unioninvestment.eai.portal.support.vaadin.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;

public class BufferedTableTest {

	@Mock
	private TableFieldFactory fieldFactoryMock;

	@Mock
	private Container containerMock;

	@Mock
	private Field fieldMock;

	@Mock
	private PropertyFormatter propertyFormatterMock;

	// @Spy
	// private StringDataType stringDataTypeMock = new StringDataType();

	@InjectMocks
	private BufferedTable table = new BufferedTable();;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		table.setContainerDataSource(containerMock);
		table.setTableFieldFactory(fieldFactoryMock);
		table.setEditable(true);
	}

	@Test
	public void testConstructors() {
		BufferedTable bufferedTable = new BufferedTable("Caption");
		assertEquals("Caption", bufferedTable.getCaption());

		bufferedTable = new BufferedTable("Caption", containerMock);

		when(containerMock.getType("test")).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return String.class;
			}
		});
		Class<?> type = bufferedTable.getType("test");
		assertEquals(String.class, type);
	}

	@Test
	public void shouldRegisterNewFieldsOnlyIfWriteThrough() {

		when(fieldMock.isWriteThrough()).thenReturn(true);
		when(
				fieldFactoryMock.createField(table.getContainerDataSource(), 1,
						"col", table)).thenReturn(fieldMock);

		Field field = (Field) table.getPropertyValue(1, "col", null);
		assertEquals(fieldMock, field);
		assertThat(table.getRegisteredFields().size(), is(0));

	}

	@Test
	public void shouldRegisterNewFields() {

		when(fieldMock.isWriteThrough()).thenReturn(false);
		when(
				fieldFactoryMock.createField(table.getContainerDataSource(), 1,
						"col", table)).thenReturn(fieldMock);

		Field field = (Field) table.getPropertyValue(1, "col", null);
		assertEquals(fieldMock, field);
		assertThat(table.getRegisteredFields(), hasItem(field));
	}

	@Test
	public void shouldUnRegisterFields() {

		shouldRegisterNewFields();

		table.unregisterComponent(fieldMock);
		assertThat(table.getRegisteredFields(), not(hasItem(fieldMock)));
	}

	/**
	 * see CrudTableColumnGenerator
	 */
	@Test
	public void shouldUnRegisterFieldsWrappedInLayout() {

		shouldRegisterNewFields();
		CssLayout layout = new CssLayout();
		layout.addComponent(fieldMock);

		table.unregisterComponent(layout);
		assertThat(table.getRegisteredFields(), not(hasItem(fieldMock)));
	}

	@Test
	public void shouldCommitAllValidFields() {
		shouldRegisterNewFields();
		Field notValidFieldMock = mock(Field.class);
		when(fieldMock.isWriteThrough()).thenReturn(false);
		when(
				fieldFactoryMock.createField(table.getContainerDataSource(), 2,
						"col2", table)).thenReturn(notValidFieldMock);

		table.getPropertyValue(2, "col2", null);
		when(fieldMock.isValid()).thenReturn(true);
		when(fieldMock.isModified()).thenReturn(true);
		when(notValidFieldMock.isValid()).thenReturn(false);

		doThrow(new Validator.InvalidValueException("bla")).when(
				notValidFieldMock).validate();

		try {
			table.commitFieldValues();
			fail();
		} catch (SourceException e) {
			verify(fieldMock).commit();
			verify(notValidFieldMock, never()).commit();
		}

	}

	@Test
	public void shouldCommitRegisteredFields() {
		shouldRegisterNewFields();

		when(fieldMock.isValid()).thenReturn(true);
		when(fieldMock.isModified()).thenReturn(true);

		table.commitFieldValues();

		verify(fieldMock).commit();
	}

	@Test
	public void shouldNotCommitIfValidationFails() {
		shouldRegisterNewFields();

		doThrow(new Validator.InvalidValueException("bla")).when(fieldMock)
				.validate();

		try {
			table.commitFieldValues();
			fail("Exception expected");

		} catch (Exception e) {
			verify(fieldMock, never()).commit();
		}
	}

	@Test
	public void shouldDiscardFieldValues() {
		shouldRegisterNewFields();

		table.discardFieldValues();

		verify(fieldMock).discard();
	}

	@Test
	public void shouldReturnModifiedStateFalse() {
		shouldRegisterNewFields();

		when(fieldMock.isModified()).thenReturn(false);

		assertFalse(table.isFieldModified());

		verify(fieldMock).isModified();
	}

	@Test
	public void shouldReturnModifiedStateTrue() {
		shouldRegisterNewFields();

		when(fieldMock.isModified()).thenReturn(true);

		assertTrue(table.isFieldModified());

		verify(fieldMock).isModified();
	}

	@Test
	public void shouldGetModifiedColumnNames() {
		String propertyId = "id";
		String value = "value";

		Set<Field> registeredFields = new HashSet<Field>();
		registeredFields.add(fieldMock);
		when(fieldMock.isModified()).thenReturn(true);

		ColumnProperty columnProperty = new ColumnProperty(propertyId, false,
				true, true, value, String.class);

		when(fieldMock.getPropertyDataSource()).thenReturn(
				propertyFormatterMock);
		when(propertyFormatterMock.getPropertyDataSource()).thenReturn(
				columnProperty);

		table.getRegisteredFields().addAll(registeredFields);

		Map<String, Object> modifiedColumnNames = table
				.getModifiedColumnNames();

		assertThat(modifiedColumnNames, notNullValue());
		assertThat(modifiedColumnNames.size(), is(1));
		assertThat(modifiedColumnNames.get(propertyId), notNullValue());
		assertThat(modifiedColumnNames.get(propertyId), equalTo((Object) value));
	}
}
