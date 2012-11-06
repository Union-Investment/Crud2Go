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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.SQLContainer;
import com.vaadin.data.util.PropertyFormatter;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

public class DatabaseContainerFieldTest extends ContainerFieldTest {
	private DatabaseContainerField databaseContainerField;

	private ColumnProperty columnProperty;

	@SuppressWarnings("unused")
	private RowItem rowItem;
	@Mock
	private RowId rowIdMock;

	@Mock
	private SQLContainer sqlContainerMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private EditorSupport editorSupportMock;

	@Mock
	private PropertyFormatter propertyFormatterMock;

	@Mock
	private DatabaseContainerRow rowMock;

	@Mock
	private DisplaySupport displaySupportMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		propertyName = propertyId;

		when(
				containerMock
						.withExistingTransaction(any(DataContainer.TransactionCallback.class)))
				.thenAnswer(new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						@SuppressWarnings("rawtypes")
						DataContainer.TransactionCallback callback = (DataContainer.TransactionCallback) invocation
								.getArguments()[0];
						return callback.doInTransaction();
					}
				});

		columnProperty = new ColumnProperty(propertyId, false, true, true,
				propertyValue, String.class);

		rowItem = new RowItem(sqlContainerMock, rowIdMock,
				Arrays.asList(columnProperty));

		databaseContainerField = createContainerField();
	}

	@Test
	public void shouldSetProperty() {
		assertThat(databaseContainerField.getProperty(), is(columnProperty));
	}

	@Test
	public void shouldSetContainer() {
		assertThat(databaseContainerField.getContainer(), is(containerMock));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnTypeFromDataContainer() {
		AbstractDatabaseContainerTest.returnFollowingColumnType(containerMock,
				propertyId, ContainerClob.class);
		assertThat((Class<ContainerClob>) databaseContainerField.getType(),
				equalTo(ContainerClob.class));
	}

	@Test
	public void shouldSetValueWithoutTransaction() {
		databaseContainerField.setValue("newValue");
		assertThat(databaseContainerField.getValue(), is((Object) "newValue"));
	}

	@Test
	public void shouldSetValueWithTransaction() {
		when(rowMock.isTransactional()).thenReturn(true);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);

		databaseContainerField.setValue("newValue");

		assertThat(databaseContainerField.getValue(), is((Object) "newValue"));
	}

	@Test(expected = ContainerException.class)
	public void shouldNotSetImmutableValue() {
		when(rowMock.isImmutable()).thenReturn(true);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);

		databaseContainerField.setValue("newValue");
	}

	@Test(expected = ContainerException.class)
	public void shouldNotSetReadonlyValue() {
		columnProperty.setReadOnly(true);
		databaseContainerField.setValue("newValue");
	}

	@Test
	public void shouldSetText() {
		when(containerMock.findEditor(propertyId))
				.thenReturn(editorSupportMock);
		when(editorSupportMock.createFormatter(String.class, null)).thenReturn(
				null);

		databaseContainerField.setText("NewValue");

		assertThat(databaseContainerField.getValue(), is((Object) "NewValue"));
	}

	@Test
	public void shouldSetTextWithTransaction() {
		when(rowMock.isTransactional()).thenReturn(true);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);

		when(containerMock.findEditor(propertyId))
				.thenReturn(editorSupportMock);
		when(editorSupportMock.createFormatter(String.class, null)).thenReturn(
				null);

		databaseContainerField.setText("NewValue");

		assertThat(databaseContainerField.getValue(), is((Object) "NewValue"));
	}

	@Test
	public void shouldSetFormatedText() {
		when(containerMock.findEditor(propertyId))
				.thenReturn(editorSupportMock);

		extracted();
		when(editorSupportMock.createFormatter(String.class, null)).thenReturn(
				propertyFormatterMock);

		databaseContainerField.setText("NewValue");

		verify(propertyFormatterMock).setPropertyDataSource(columnProperty);
		verify(propertyFormatterMock).setValue("NewValue");
	}

	private void extracted() {
		when(containerMock.getType(propertyId)).thenAnswer(
				new Answer<Class<String>>() {
					@Override
					public Class<String> answer(InvocationOnMock invocation)
							throws Throwable {
						return String.class;
					}
				});
	}

	@Test(expected = ContainerException.class)
	public void shouldNotSetText() {
		databaseContainerField.setText("NewValue");
	}

	@Test(expected = ContainerException.class)
	public void shouldNotSetReadonlyText() {
		when(rowMock.isTransactional()).thenReturn(true);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);

		columnProperty.setReadOnly(true);

		databaseContainerField.setText("NewValue");
	}

	@Test
	public void shouldNotBeRequiredIfPropertyIsNullable() {
		assertThat(databaseContainerField.isRequired(), is(false));
	}

	@Test
	public void shouldBeRequiredIfPropertyIsNotNullable() {
		columnProperty = new ColumnProperty(propertyId, false, true, false,
				propertyValue, String.class);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);

		assertThat(databaseContainerField.isRequired(), is(true));
	}

	@Test
	public void shouldReturnIsReadonlyIfPropertyIsReadonly() {
		columnProperty.setReadOnly(true);
		assertThat(databaseContainerField.isReadonly(), is(true));
	}

	@Test
	public void shouldReturnIsReadonlyIfRowIsReadonly() {
		when(rowMock.isReadonly()).thenReturn(true);
		assertThat(databaseContainerField.isReadonly(), is(true));
	}

	@Test
	public void shouldReturnFormattedTextUsingDisplaySupport() {
		columnProperty = new ColumnProperty(propertyId, false, true, false,
				propertyValue, String.class);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);
		when(containerMock.findDisplayer(propertyId)).thenReturn(
				displaySupportMock);
		when(displaySupportMock.formatPropertyValue(columnProperty, null))
				.thenReturn("formatted text");
		assertThat(databaseContainerField.getText(), is("formatted text"));
	}

	@Test
	public void shouldReturnNullIfDisplaySupportIsMissing() {
		columnProperty = new ColumnProperty(propertyId, false, true, false,
				propertyValue, String.class);
		databaseContainerField = new DatabaseContainerField(rowMock,
				columnProperty, containerMock);
		assertThat(databaseContainerField.getText(), nullValue());
	}

	@Override
	DatabaseContainerField createContainerField() {
		return new DatabaseContainerField(rowMock, columnProperty,
				containerMock);
	}
}
