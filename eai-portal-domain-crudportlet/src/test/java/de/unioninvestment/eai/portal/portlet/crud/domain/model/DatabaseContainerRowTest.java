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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

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
import com.vaadin.addon.sqlcontainer.TemporaryRowId;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;

public class DatabaseContainerRowTest extends ContainerRowTest {

	private DatabaseContainerRow containerRow;

	private RowItem rowItem;

	@Mock
	private DataContainer containerMock;

	@Mock
	private DatabaseContainerRowId containerRowId;

	@Mock
	private RowId rowIdMock;

	@Mock
	private SQLContainer sqlContainerMock;

	private ColumnProperty columnProperty;

	@Mock
	private EditorSupport editorSupportMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		columnProperty = new ColumnProperty("id", true, true, true, "1",
				String.class);
		rowItem = new RowItem(sqlContainerMock, rowIdMock,
				Arrays.asList(columnProperty));

		containerRow = createContainerRow();

		// doAnswer(new Answer<Object>() {
		//
		// @Override
		// public Object answer(InvocationOnMock invocation) throws Throwable {
		// @SuppressWarnings("rawtypes")
		// DatabaseContainer.TransactionCallback callback =
		// (DatabaseContainer.TransactionCallback) invocation
		// .getArguments()[0];
		// callback.doInTransaction();
		// return null;
		// }
		// }).when(containerMock).withExistingTransaction(
		// any(DatabaseContainer.TransactionCallback.class));
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
		when(containerMock.isUpdateable()).thenReturn(true);
	}

	@Test
	public void shouldSetTransactionalPropertyValue() {
		columnProperty.setReadOnly(false);

		containerRow.setValue("id", "2");
		assertThat((String) columnProperty.getValue(), is("2"));
	}

	@Test
	public void shouldSetNonTransactionalPropertyValue() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, false);
		columnProperty.setReadOnly(false);
		containerRow.setValue("id", "2");
		assertThat((String) columnProperty.getValue(), is("2"));
	}

	@Test(expected = ContainerException.class)
	public void shouldFailWithExceptionToSetAValueOfAReadonlyPropery() {

		containerRow.setValue("id", "2");
	}

	@Test(expected = ContainerException.class)
	public void shouldFailWithExceptionToSetAValueOfAnImmutableRow() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, true);

		columnProperty.setReadOnly(false);
		containerRow.setValue("id", "2");
	}

	@Test
	public void shouldGetNonTransactionalPropertyValue() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, false);
		columnProperty.setReadOnly(false);
		assertThat((String) containerRow.getValue("id"), is("1"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetTransactionalPropertyValue() {
		columnProperty.setReadOnly(false);
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("rawtypes")
				DataContainer.TransactionCallback callback = (DataContainer.TransactionCallback) invocation
						.getArguments()[0];
				return callback.doInTransaction();
			}
		}).when(containerMock).withExistingTransaction(
				any(DataContainer.TransactionCallback.class));

		assertThat((String) containerRow.getValue("id"), is("1"));
	}

	@Test(expected = ContainerException.class)
	public void shouldFailWithExceptionToGetAValueOfAnNotExistingPropery() {
		columnProperty.setReadOnly(false);
		containerRow.getValue("name");
	}


	@Test
	public void shouldCloneRow() throws CloneNotSupportedException {
		ContainerRow cloneMock = mock(DatabaseContainerRow.class);
		when(containerMock.addRow()).thenReturn(cloneMock);
		DatabaseContainerRow clone = (DatabaseContainerRow) containerRow
				.clone();

		assertThat(clone.getId(), nullValue());
	}

	@Test
	public void shouldSetText() {
		when(containerMock.findEditor(anyString())).thenReturn(
				editorSupportMock);

		columnProperty.setReadOnly(false);
		containerRow.setText("id", "2");

		assertThat((String) columnProperty.getValue(), is("2"));
	}

	@Test
	public void shouldSetValue() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, false);
		columnProperty.setReadOnly(false);

		containerRow.setValue("id", "newValue");

		Map<String, ContainerField> fields = containerRow.getFields();
		assertThat(fields.get("id").getValue(), is((Object) "newValue"));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldNotSetValueOnNonExistingField() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, false);

		containerRow.setValue("keinFeld", "newValue");
	}

	@Test
	public void shouldCreateFields() {
		containerRow = new DatabaseContainerRow(rowItem, containerRowId,
				containerMock, false, false);

		Map<String, ContainerField> fields = containerRow.getFields();

		assertThat(fields, is(notNullValue()));
		assertThat(fields.size(), is(1));
		assertThat(fields.containsKey("id"), is(true));
		assertThat(fields.get("id").getValue(), is((Object) "1"));
	}

	@Test
	public void shouldBeReadonlyIfUpdatesAreNotSupported() {
		when(containerMock.isUpdateable()).thenReturn(false);
		assertThat(containerRow.isReadonly(), is(true));
	}

	@Test
	public void shouldBeWritableForExistingRowsIfUpdatesAreAllowed() {
		when(containerMock.isUpdateable()).thenReturn(true);
		assertThat(containerRow.isReadonly(), is(false));
	}

	@Test
	public void shouldBeWritableForNewRows() {
		when(containerMock.isUpdateable()).thenReturn(false);
		when(containerRowId.getInternalId()).thenReturn(
				new TemporaryRowId(new Object[] { 1 }));
		assertThat(containerRow.isReadonly(), is(false));
	}

	@Override
	DatabaseContainerRow createContainerRow() {
		return new DatabaseContainerRow(rowItem, containerRowId, containerMock, true, false);
	}

}
