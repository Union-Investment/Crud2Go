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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.EachRowCallback;

public class ScriptTableSelectionTest {

	@Mock
	private DataContainer containerMock;

	@Mock
	private ContainerRowId rowId1Mock;

	@Mock
	private ContainerRowId rowId2Mock;

	@Mock
	private Closure<?> closureMock;

	private ScriptTableSelection selection;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Set<ScriptRowId> ids = new HashSet<ScriptRowId>();
		ids.add(new ScriptRowId(rowId1Mock));
		ids.add(new ScriptRowId(rowId2Mock));
		selection = new ScriptTableSelection(containerMock, ids);
	}

	@Test
	public void shouldAllowRemovingAllSelectedRows() {

		selection.removeAllRows();

		HashSet<ContainerRowId> containerIds = new HashSet<ContainerRowId>();
		containerIds.add(rowId1Mock);
		containerIds.add(rowId2Mock);
		verify(containerMock).removeRows(containerIds);
	}

	@Test
	public void shouldCallClosureWithEachRow() {
		Set<ContainerRowId> ids = new HashSet<ContainerRowId>(Arrays.asList(
				rowId1Mock, rowId2Mock));
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				EachRowCallback callback = (EachRowCallback) invocation
						.getArguments()[1];
				ContainerRow rowMock = mock(ContainerRow.class);
				callback.doWithRow(rowMock);
				return null;
			}
		}).when(containerMock).eachRow(eq(ids), any(EachRowCallback.class));

		selection.eachRow(closureMock);

		verify(closureMock).call(any(ScriptRow.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallClosureWithNewTransaction() {

		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("rawtypes")
				DataContainer.TransactionCallback callback = (DataContainer.TransactionCallback) invocation
						.getArguments()[0];
				callback.doInTransaction();
				return null;
			}
		}).when(containerMock).withTransaction(
				any(DataContainer.TransactionCallback.class));

		selection.withTransaction(closureMock);

		verify(closureMock).call();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCallClosureWithExistingTransaction() {

		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("rawtypes")
				DataContainer.TransactionCallback callback = (DataContainer.TransactionCallback) invocation
						.getArguments()[0];
				callback.doInTransaction();
				return null;
			}
		}).when(containerMock).withExistingTransaction(
				any(DataContainer.TransactionCallback.class));

		selection.withExistingTransaction(closureMock);

		verify(closureMock).call();
	}

	@Test
	public void emptySelectionShouldReturnTrueOnIsEmpty() {
		Set<ScriptRowId> ids = new HashSet<ScriptRowId>();
		selection = new ScriptTableSelection(containerMock, ids);
		assertTrue(selection.isEmpty());
	}

	@Test
	public void nonEmptySelectionShouldReturnFalseOnIsEmpty() {
		assertFalse(selection.isEmpty());
	}

	@Test
	public void shouldReturnSize() {
		assertThat(selection.size(), is(2));
	}
}
