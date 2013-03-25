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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.derby.iapi.types.SQLTimestamp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.addon.sqlcontainer.ColumnProperty;
import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.SQLContainer;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;
import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.AbstractTimeoutableQueryDelegate;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.BeforeCommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.EachRowCallback;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.TransactionCallback;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.RegExpFilter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.SQLFilter;
import de.unioninvestment.eai.portal.support.vaadin.filter.AdvancedStringFilter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

public abstract class AbstractDatabaseContainerTest<T extends AbstractDatabaseContainer, V extends SQLContainerEventWrapper>
		extends AbstractDataContainerTest<T, V> {

	private TestContainer testContainer;

	@Mock
	private RowId rowId1Mock;

	@Mock
	private RowId rowId2Mock;

	@Mock
	private EachRowCallback eachRowCallbackMock;

	@Captor
	private ArgumentCaptor<ContainerRow> rowCaptor;

	@Mock
	private EventBus eventBus;

	@Before
	public void databaseSetUp() {
		testContainer = new TestContainer(eventBus, editors,
				vaadinContainerMock,
				displayPatternMock);
	}

	private class TestContainer extends AbstractDatabaseContainer {

		private static final long serialVersionUID = 42L;

		private final String[] primaryKeys;

		public TestContainer(EventBus eventBus, List<EditorSupport> editors,
				SQLContainerEventWrapper container,
				Map<String, String> displayPattern) {
			super(eventBus, displayPattern, null, null);
			primaryKeys = null;
			super.editors = editors;
			super.container = container;
		}

		public TestContainer(EventBus eventBus, String[] primaryKeys,
				List<EditorSupport> editors,
				Map<String, String> displayPattern,
				List<ContainerOrder> defaultOrder) {
			super(eventBus, displayPattern, defaultOrder, null);
			this.primaryKeys = primaryKeys;
			super.editors = editors;
		}

		@Override
		public String getDatasource() {
			return "eai";
		}

		@Override
		public boolean isInsertable() {
			return false;
		}

		@Override
		public boolean isUpdateable() {
			return false;
		}

		@Override
		public boolean isDeleteable() {
			return false;
		}

		@Override
		public List<String> getPrimaryKeyColumns() {
			return asList(primaryKeys);
		}

		@Override
		protected SQLContainerEventWrapper createVaadinContainer() {
			return vaadinContainerMock;
		}

		@Override
		public EventRouter<UpdateEventHandler, UpdateEvent> getOnUpdateEventRouter() {
			return super.getOnUpdateEventRouter();
		}

		@Override
		protected EventRouter<InsertEventHandler, InsertEvent> getOnInsertEventRouter() {
			return super.getOnInsertEventRouter();
		}

		@Override
		protected EventRouter<DeleteEventHandler, DeleteEvent> getOnDeleteEventRouter() {
			return super.getOnDeleteEventRouter();
		}

		@Override
		protected EventRouter<CreateEventHandler, CreateEvent> getOnCreateEventRouter() {
			return super.getOnCreateEventRouter();
		}

		@Override
		public EventRouter<CommitEventHandler, CommitEvent> getOnCommitEventRouter() {
			return super.getOnCommitEventRouter();
		}

		@Override
		public DisplaySupport findDisplayer(String columnName) {
			return null;
		}

		@Override
		public ContainerClob getCLob(ContainerRowId rowId, String columnName) {
			return null;
		}

		@Override
		public boolean isCLobModified(ContainerRowId containerRowId,
				String columnName) {
			return false;
		}

		@Override
		public ContainerBlob getBLob(ContainerRowId rowId, String columnName) {
			return null;
		}

		@Override
		public boolean isBLobModified(ContainerRowId containerRowId,
				String columnName) {
			return false;
		}

		@Override
		public boolean isBLobEmpty(ContainerRowId rowId, String columnName) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@Test
	public void shouldReturnContainerItemIds() {
		testContainer.getRowIds();
		verify(vaadinContainerMock, times(1)).getItemIds();
	}

	@Test
	public void shouldFireBeforeCommitEventOnCommit() {
		testContainer.addBeforeCommitEventHandler(beforeCommitEventHandlerMock);
		testContainer.commit();
		verify(beforeCommitEventHandlerMock).beforeCommit(
				new BeforeCommitEvent(testContainer));
	}

	@Test
	public void shouldFireCommitEventOnVaadinCommit() {
		testContainer.addCommitEventHandler(commitEventHandlerMock);
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer.commit();
		verify(commitEventHandlerMock).onCommit(new CommitEvent(testContainer));
	}

	@Test
	public void shouldNotFireCommitEventOnUnmodifiedContainer() {
		testContainer.addCommitEventHandler(commitEventHandlerMock);
		when(vaadinContainerMock.isModified()).thenReturn(false);
		testContainer.commit();
		verify(commitEventHandlerMock, never()).onCommit(
				new CommitEvent(testContainer));
	}

	@Test
	public void shouldCallSortOnContainerWithDefaultOrder() {
		ContainerOrder order1 = new ContainerOrder("ID", true);
		ContainerOrder order2 = new ContainerOrder("NAME", false);
		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, asList(order1,
				order2));

		container.getVaadinContainer();

		verify(vaadinContainerMock).sort(new Object[] { "ID", "NAME" },
				new boolean[] { true, false });
	}

	@Test
	public void shouldNotCallSortOnContainerWithoutDefaultOrder() {

		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, null);

		container.getVaadinContainer();

		verify(vaadinContainerMock, never()).sort(any(Object[].class),
				any(boolean[].class));
	}

	@Test
	public void shouldNotCallVaadinContainerCommitIfUnmodified()
			throws UnsupportedOperationException, SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(false);
		testContainer.commit();
		verify(vaadinContainerMock, never()).commit();
	}

	@Test
	public void shouldCallVaadinContainerCommitIfModified()
			throws UnsupportedOperationException, SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer.commit();
		verify(vaadinContainerMock).commit();
	}

	@Test
	public void shouldConvertRowItemToDatabaseContainerRow() {
		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, null);
		RowId rowId = new RowId(new Object[] { 1, 2, "MY_NAME" });
		ColumnProperty idProperty = new ColumnProperty("ID", false, false,
				true, "ID", String.class);
		ColumnProperty indexProperty = new ColumnProperty("INDEX", false,
				false, true, "ID", String.class);
		ColumnProperty nameProperty = new ColumnProperty("NAME", false, false,
				true, "ID", String.class);
		Item item = new RowItem(vaadinContainerMock, rowId, Arrays.asList(
				idProperty, indexProperty, nameProperty));

		ContainerRow row = container.convertItemToRow(item, true, false);
		assertThat(row.getInternalRow(), is(item));
	}

	@Test
	public void shouldConvertInternalRowIds() {
		RowId rowId = new RowId(new Object[] { 1, 2, "MY_NAME" });
		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, null);

		ContainerRowId id = container.convertInternalRowId(rowId);

		assertThat(id.asMap().size(), is(3));
		assertThat(id.asMap().get("ID"), is((Object) 1));
		assertThat(id.asMap().get("INDEX"), is((Object) 2));
		assertThat(id.asMap().get("NAME"), is((Object) "MY_NAME"));
	}

	@Test
	public void shouldAllowTransactionalCallbackOperations()
			throws UnsupportedOperationException, SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer
				.withTransaction(new DataContainer.TransactionCallback<Object>() {
					@Override
					public Object doInTransaction() {
						return null;
					}
				});
		verify(vaadinContainerMock, times(2)).commit();
	}

	@Test(expected = ContainerException.class)
	public void shouldNotThrowExceptionOnRollback() throws Exception {
		doThrow(new SQLException()).when(vaadinContainerMock).rollback();

		testContainer.rollback();
	}

	@Test
	public void shouldRollbackOnExceptionDuringTransaction()
			throws SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		try {
			testContainer
					.withTransaction(new DataContainer.TransactionCallback<Object>() {

						@Override
						public Object doInTransaction() {
							throw new IllegalArgumentException("Bla");
						}

					});
			fail();

		} catch (IllegalArgumentException e) {
			// ok
		}
		verify(vaadinContainerMock).commit(); // user transaction
		verify(vaadinContainerMock).rollback();
	}

	@Test
	public void shouldRollbackOnExceptionDuringCommit() throws SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		try {
			testContainer
					.withTransaction(new DataContainer.TransactionCallback<Object>() {
						@Override
						public Object doInTransaction() {
							try {
								doThrow(new SQLException("Commit failed"))
										.when(vaadinContainerMock).commit();
							} catch (SQLException e) {
								fail();
							}
							return null;
						}

					});
			fail();

		} catch (ContainerException e) {
			// ok
		}
		verify(vaadinContainerMock, times(2)).commit(); // user transaction
		verify(vaadinContainerMock).rollback();
	}

	@Test
	public void shouldJoinExistingTransaction() throws SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {

						testContainer
								.withTransaction(new TransactionCallback<Object>() {

									@Override
									public Object doInTransaction() {
										try {
											verify(vaadinContainerMock)
													.commit();

										} catch (SQLException e) {
											fail();
										}
										return null;
									}
								});
						return null;
					}
				});
		verify(vaadinContainerMock, times(2)).commit();
	}

	@Test
	public void shouldCommitPossibleUserTransactionBeforeStartingATransaction()
			throws SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {
						try {
							verify(vaadinContainerMock).commit();

						} catch (UnsupportedOperationException e) {
							fail();
						} catch (SQLException e) {
							fail();
						}
						return null;
					}
				});
	}

	@Test
	public void shouldAddSqlFilter() {
		shouldAddFilterType(
				new SQLFilter("NAME", "bla", Arrays.asList((Object) "Test")),
				de.unioninvestment.eai.portal.support.vaadin.filter.SQLFilter.class);
	}

	@Test
	public void shouldAddRegExpFilter() {
		shouldAddFilterType(
				new RegExpFilter("NAME", "bla", "i"),
				de.unioninvestment.eai.portal.support.vaadin.filter.OracleRegExpFilter.class);
	}

	@Test
	public void shouldAllowRequiringExistingTransaction() throws SQLException {
		final AtomicBoolean called = new AtomicBoolean();
		when(vaadinContainerMock.isModified()).thenReturn(true);
		testContainer
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {
						testContainer
								.withExistingTransaction(new TransactionCallback<Object>() {

									@Override
									public Object doInTransaction() {
										called.set(true);
										return null;
									}

								});
						return null;
					}
				});
		assertThat(called.get(), is(true));
		verify(vaadinContainerMock, times(2)).commit();
	}

	@Test(expected = IllegalStateException.class)
	public void shouldHandleMissingExistingTransaction() throws SQLException {
		final TestContainer container = new TestContainer(eventBus, editors,
				vaadinContainerMock, displayPatternMock);
		container.withExistingTransaction(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				return null;
			}
		});
	}

	@Test
	public void shouldRemoveGivenRows() {
		HashSet<ContainerRowId> ids = new HashSet<ContainerRowId>();
		when(rowId1Mock.getId()).thenReturn(new Object[] { 1, 2 });
		when(rowId2Mock.getId()).thenReturn(new Object[] { 1, 3 });
		ids.add(new DatabaseContainerRowId(rowId1Mock, asList("A", "B")));
		ids.add(new DatabaseContainerRowId(rowId2Mock, asList("A", "B")));

		testContainer.removeRows(ids);

		verify(vaadinContainerMock).removeItem(rowId1Mock);
		verify(vaadinContainerMock).removeItem(rowId2Mock);
	}

	@Test
	public void shouldAddNewRow() {

		container = (T) new TestContainer(eventBus, new String[] { "ID" },
				editors,
				displayPatternMock, null);

		TemporaryRowId rowId = new TemporaryRowId(new Object[] { "ID" });
		Item row1 = createRow(rowId);

		when(vaadinContainerMock.addItem()).thenReturn(rowId);
		when(vaadinContainerMock.getItem(rowId)).thenReturn(row1);

		ContainerRow containerRow = container
				.withTransaction(new TransactionCallback<ContainerRow>() {
					@Override
					public ContainerRow doInTransaction() {
						return container.addRow();
					}
				});

		assertThat(containerRow.getInternalRow(), is(row1));
	}

	protected RowItem createRow(RowId rowId) {
		return new RowItem(vaadinContainerMock, rowId,
				new HashSet<ColumnProperty>());
	}

	@Test
	public void shouldReturnCachedRowById() {
		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, null);

		when(rowId1Mock.getId()).thenReturn(new Object[] { 1 });
		Item row1 = createRow(rowId1Mock);
		DatabaseContainerRowId containerRowId1 = new DatabaseContainerRowId(
				rowId1Mock, asList("ID"));

		when(vaadinContainerMock.getItemUnfiltered(rowId1Mock))
				.thenReturn(row1);

		ContainerRow cachedRow = container.getCachedRow(containerRowId1, true,
				false);
		assertThat(cachedRow.getInternalRow(), is(row1));
	}

	@Test
	public void shouldReturnRowById() {
		TestContainer container = new TestContainer(eventBus, new String[] {
				"ID",
				"INDEX", "NAME" }, editors, displayPatternMock, null);

		when(rowId1Mock.getId()).thenReturn(new Object[] { 1 });
		Item row1 = createRow(rowId1Mock);
		DatabaseContainerRowId containerRowId1 = new DatabaseContainerRowId(
				rowId1Mock, asList("ID"));

		when(vaadinContainerMock.getItem(rowId1Mock)).thenReturn(row1);

		ContainerRow cachedRow = container.getRow(containerRowId1, true, false);
		assertThat(cachedRow.getInternalRow(), is(row1));
	}

	@Test
	public void shouldReturnNullWhenAskingForNonExistentRows() {
		when(rowId1Mock.getId()).thenReturn(new Object[] { 1 });
		DatabaseContainerRowId containerRowId1 = new DatabaseContainerRowId(
				rowId1Mock, asList("ID"));

		when(vaadinContainerMock.getItem(rowId1Mock)).thenReturn(null);

		assertThat(testContainer.getRow(containerRowId1, true, false),
				nullValue());
	}

	@Test
	public void shouldReturnNullWhenAskingForUncachedRows() {
		when(rowId1Mock.getId()).thenReturn(new Object[] { 1 });
		DatabaseContainerRowId containerRowId1 = new DatabaseContainerRowId(
				rowId1Mock, asList("ID"));

		when(vaadinContainerMock.getItemUnfiltered(rowId1Mock))
				.thenReturn(null);

		assertThat(testContainer.getCachedRow(containerRowId1, true, false),
				nullValue());
	}

	@Test
	public void shouldCallRefreshOnDBContainer() {
		testContainer.refresh();
		verify(vaadinContainerMock).refresh();
	}

	@Test
	public void shouldFindEditor() {
		returnVaadinColumnType("Test", SQLTimestamp.class);

		when(editorSupportMock.supportsEditing(SQLTimestamp.class)).thenReturn(
				true);

		EditorSupport editor = testContainer.findEditor("Test");

		assertThat(editor, is(editorSupportMock));
	}

	@Test
	public void shouldNotFindEditor() {
		returnVaadinColumnType("Test", SQLTimestamp.class);

		when(editorSupportMock.supportsEditing(SQLTimestamp.class)).thenReturn(
				false);

		EditorSupport editor = testContainer.findEditor("Test");

		assertThat(editor, is(nullValue()));
	}

	@Test
	public void shouldCheckClobColumnType() {
		returnVaadinColumnType("TestCLob", Clob.class);
		assertThat(testContainer.isCLob("TestCLob"), is(true));

	}

	private void returnVaadinColumnType(String columnName, final Class<?> clazz) {
		when(vaadinContainerMock.getType(any())).thenAnswer(
				new Answer<Class<?>>() {
					@Override
					public Class<?> answer(InvocationOnMock invocation)
							throws Throwable {
						return clazz;
					}
				});
	}

	@Test
	public void shouldReturnFalseForClobIfBackendGivesNullType() {
		returnVaadinColumnType("TestCLob", null);
		assertThat(testContainer.isCLob("TestCLob"), is(false));
	}

	@Test
	public void shouldReturnFalseForBlobIfBackendGivesNullType() {
		returnVaadinColumnType("TestBLob", null);
		assertThat(testContainer.isBLob("TestBLob"), is(false));

	}

	@Test
	public void getTypeShouldReturnNullOnMissingTypeInformation() {
		assertNull(testContainer.getType("UNKNOWN"));
	}

	@Test
	public void shouldAddContainsFilter() {
		shouldAddFilterType(new Contains("NAME", "abcdef", false),
				AdvancedStringFilter.class);
	}

	@Test
	public void shouldSetTimeoutAtQueryDelegateAndRestoreOldTimeout() {
		this.testContainer.queryDelegate = Mockito
				.mock(AbstractTimeoutableQueryDelegate.class);

		when(this.testContainer.queryDelegate.getQueryTimeout()).thenReturn(42);

		this.testContainer.replaceFilters(new ArrayList<Filter>(), false, 9);

		verify(this.testContainer.queryDelegate).setQueryTimeout(9);

		assertThat(this.testContainer.queryDelegate.getQueryTimeout(), is(42));
	}

	@Test
	public void shouldAllowTraversalOfEachContainerRow() {
		container.queryDelegate = mock(AbstractTimeoutableQueryDelegate.class);
		container.setVaadinContainer(vaadinContainerMock);

		when(vaadinContainerMock.firstItemId()).thenReturn(rowId1Mock);
		ColumnProperty property = new ColumnProperty("id", false, false, true,
				1, Integer.class);
		RowItem rowItem1 = new RowItem((SQLContainer) vaadinContainerMock,
				rowId1Mock, asList(property));
		when(vaadinContainerMock.getItem(rowId1Mock)).thenReturn(rowItem1);

		when(vaadinContainerMock.nextItemId(rowId1Mock)).thenReturn(rowId2Mock);
		ColumnProperty property2 = new ColumnProperty("id", false, false, true,
				1, Integer.class);
		RowItem rowItem2 = new RowItem((SQLContainer) vaadinContainerMock,
				rowId2Mock, asList(property2));
		when(vaadinContainerMock.getItem(rowId2Mock)).thenReturn(rowItem2);

		when(vaadinContainerMock.nextItemId(rowId2Mock)).thenReturn(null);

		container.eachRow(eachRowCallbackMock);

		verify(vaadinContainerMock).firstItemId();
		verify(vaadinContainerMock).nextItemId(rowId1Mock);
		verify(vaadinContainerMock).nextItemId(rowId2Mock);

		verify(eachRowCallbackMock, times(2)).doWithRow(rowCaptor.capture());
		List<ContainerRow> allRows = rowCaptor.getAllValues();
		assertThat(allRows.get(0).getInternalRow(),
				sameInstance((Item) rowItem1));
		assertThat(allRows.get(1).getInternalRow(),
				sameInstance((Item) rowItem2));

	}

	@Test
	public void shouldDoNothingOnTraversalOfEmptyList() {
		container.setVaadinContainer(vaadinContainerMock);

		when(vaadinContainerMock.firstItemId()).thenReturn(null);

		container.eachRow(eachRowCallbackMock);

		verify(vaadinContainerMock).firstItemId();
		verifyZeroInteractions(eachRowCallbackMock);
	}

}
