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

import com.vaadin.data.Property;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.GenericVaadinContainerEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.TransactionCallback;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.support.vaadin.container.*;
import de.unioninvestment.eai.portal.support.vaadin.filter.AdvancedStringFilter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class GenericDataContainerTest
		extends
		AbstractDataContainerTest<GenericDataContainer, GenericVaadinContainerEventWrapper> {

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private GenericDelegate genericDelegateMock;

	@Mock
	private MetaData metaDataMock;

	@Mock
	private Property<Object> propertyMock;

	@Mock
	private CharBuffer clobDataMock;

	@Mock
	private GenericItemId genericItemIdMock;
	@Mock
	private GenericItem genericItemMock;

	@Mock
	private EventBus eventBus;

	@Override
	public GenericDataContainer createDataContainer() {

		GenericDataContainer genericDataContainer = new GenericDataContainer(
				eventBus, displayPatternMock, new ArrayList<ContainerOrder>(),
				null) {
            @Override
            protected GenericVaadinContainerEventWrapper createVaadinContainer() {
                setMetaData(metaDataMock);
                return vaadinContainerMock;
            }
        };

		when(genericDelegateMock.getMetaData()).thenReturn(metaDataMock);
		genericDataContainer.setDelegate(genericDelegateMock);

		return genericDataContainer;
	}

	@Override
	public GenericVaadinContainerEventWrapper createVaadinContainer() {
		GenericVaadinContainerEventWrapper eventWrapper = mock(
				GenericVaadinContainerEventWrapper.class, withSettings()
						.extraInterfaces(Filterable.class));
		return eventWrapper;
	}

	@Test
	public void shouldAllowOnlyQuerying() {
		when(metaDataMock.isInsertSupported()).thenReturn(false);
		when(metaDataMock.isRemoveSupported()).thenReturn(false);
		when(metaDataMock.isUpdateSupported()).thenReturn(false);
		assertThat(container.isInsertable(), is(false));
		assertThat(container.isDeleteable(), is(false));
		assertThat(container.isUpdateable(), is(false));
	}

	@Test
	public void shouldCheckReaderObjectsAsClobs() {
		when(vaadinContainerMock.getType(any(CharBuffer.class))).thenAnswer(
				new Answer<Class<CharBuffer>>() {

					@Override
					public Class<CharBuffer> answer(InvocationOnMock invocation)
							throws Throwable {
						return CharBuffer.class;
					}
				});

		when(
				vaadinContainerMock.getContainerProperty(
						any(ContainerRowId.class), any(String.class)))
				.thenReturn(propertyMock);

		assertThat(container.isCLob("CLob"), is(true));
	}

	@Test
	public void shouldGetCLobValueForCLobColumn() throws IOException {
		when(vaadinContainerMock.getType(any(CharBuffer.class))).thenAnswer(
				new Answer<Class<CharBuffer>>() {

					@Override
					public Class<CharBuffer> answer(InvocationOnMock invocation)
							throws Throwable {
						return CharBuffer.class;
					}
				});

		when(
				vaadinContainerMock.getContainerProperty(
						any(ContainerRowId.class), any(String.class)))
				.thenReturn(propertyMock);

		when(propertyMock.getValue()).thenReturn(clobDataMock);

		ContainerClob clob = container.getCLob(containerRowIdMock, "CLob");

		assertThat(clob.getValue(), is("clobDataMock"));
	}

	@Test
	public void shouldBuildContainerBLobForBLobDBColumn() {

		byte[] value = new byte[] { 1, 2, 4 };
		when(
				vaadinContainerMock.getContainerProperty(containerRowIdMock,
						"TestCol")).thenReturn(propertyMock);
		when(propertyMock.getValue()).thenReturn(value);

		ContainerBlob blob = container.getBLob(containerRowIdMock, "TestCol");

		assertThat(blob.getValue(), is(value));
	}

	@Test
	public void shouldReturnContainerBLobFromCache() {

		byte[] value = new byte[] { 1, 2, 4 };

		when(
				vaadinContainerMock.getContainerProperty(containerRowIdMock,
						"TestCol")).thenReturn(propertyMock);
		when(propertyMock.getValue()).thenReturn(value);

		ContainerBlob firstBlob = container.getBLob(containerRowIdMock,
				"TestCol");

		ContainerBlob secondBlob = container.getBLob(containerRowIdMock,
				"TestCol");

		assertThat(firstBlob, is(sameInstance(secondBlob)));
	}

	@Test
	public void shouldBuildContainerBlobForNewBlobEntry() {
		TemporaryItemId temporaryRowIdMock = mock(TemporaryItemId.class);
		when(containerRowIdMock.getInternalId()).thenReturn(temporaryRowIdMock);

		ContainerBlob newBlob = container
				.getBLob(containerRowIdMock, "TestCol");

		assertThat(container.isBLobModified(containerRowIdMock, "TestCol"),
				is(false));
		assertThat(newBlob.isEmpty(), is(true));
	}

	@Test
	public void shouldGetPrimaryKeyColumnsFromScriptBackend() {
		Collection<String> primeryKeyList = Arrays.asList("ID");
		when(metaDataMock.getPrimaryKeys()).thenReturn(primeryKeyList);
		List<String> resultIds = container.getPrimaryKeyColumns();
		assertThat(resultIds.get(0), is("ID"));
	}

	@Test
	public void shouldConvertGenericContainerRowIdFromGenericItemId() {
		Collection<String> primeryKeyList = Arrays.asList("ID");
		when(metaDataMock.getPrimaryKeys()).thenReturn(primeryKeyList);

		GenericContainerRowId containerRowId = (GenericContainerRowId) container
				.convertInternalRowId(genericItemIdMock);
		assertThat((GenericItemId) containerRowId.getInternalId(),
				is(genericItemIdMock));
	}

	@Test
	public void shouldConvertGenericContainerRowIdFromGenericItem() {
		Collection<String> primeryKeyList = Arrays.asList("ID");
		when(metaDataMock.getPrimaryKeys()).thenReturn(primeryKeyList);
		when(genericItemMock.getId()).thenReturn(genericItemIdMock);
		GenericContainerRowId containerRowId = (GenericContainerRowId) container
				.convertInternalRowId(genericItemIdMock);
		assertThat((GenericItemId) containerRowId.getInternalId(),
				is(genericItemIdMock));
	}

	@Test
	public void shouldRefreshTheDataBackend() {
		container.refresh();
		verify(vaadinContainerMock).refresh();
	}

	@Test
	public void shouldRollbackBackendTransaction() {
		container.rollback();
		verify(vaadinContainerMock).rollback();
	}

	@Test
	public void shouldFireCommitEventOnVaadinCommit() {
		container.addCommitEventHandler(commitEventHandlerMock);
		when(vaadinContainerMock.isModified()).thenReturn(true);
		container.commit();
		verify(commitEventHandlerMock).onCommit(new CommitEvent(container));
	}

	@Test
	public void shouldNotFireCommitEventOnUnmodifiedContainer() {
		container.addCommitEventHandler(commitEventHandlerMock);
		when(vaadinContainerMock.isModified()).thenReturn(false);
		container.commit();
		verify(commitEventHandlerMock, never()).onCommit(
				new CommitEvent(container));
	}

	@Test
	public void shouldNotCallVaadinContainerCommitIfUnmodified()
			throws UnsupportedOperationException, SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(false);
		container.commit();
		verify(vaadinContainerMock, never()).commit();
	}

	@Test
	public void shouldCallVaadinContainerCommitIfModified()
			throws UnsupportedOperationException, SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		container.commit();
		verify(vaadinContainerMock).commit();
	}

	@Test
	public void shouldRollbackOnExceptionDuringTransaction()
			throws SQLException {
		when(vaadinContainerMock.isModified()).thenReturn(true);
		try {
			container
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
			container
					.withTransaction(new DataContainer.TransactionCallback<Object>() {
						@Override
						public Object doInTransaction() {
							try {
								doThrow(
										new NullPointerException(
												"Commit failed")).when(
										vaadinContainerMock).commit();
							} catch (NullPointerException e) {
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
		container
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {

						container
								.withTransaction(new TransactionCallback<Object>() {

									@Override
									public Object doInTransaction() {

										verify(vaadinContainerMock).commit();

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
		container
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {

						verify(vaadinContainerMock).commit();

						return null;
					}
				});
	}

	@Test
	public void shouldAllowRequiringExistingTransaction() throws SQLException {
		final AtomicBoolean called = new AtomicBoolean();
		when(vaadinContainerMock.isModified()).thenReturn(true);
		container
				.withTransaction(new DataContainer.TransactionCallback<Object>() {

					@Override
					public Object doInTransaction() {
						container
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

	@Test
	public void shouldAddNewRow() {

		TemporaryItemId rowId = new TemporaryItemId(new Object[] { "ID" });
		GenericItem row1 = createRow(rowId);

		when(vaadinContainerMock.addItem()).thenReturn(rowId);
		when(vaadinContainerMock.getItem(rowId)).thenReturn(row1);

		ContainerRow containerRow = container
				.withTransaction(new TransactionCallback<ContainerRow>() {
					@Override
					public ContainerRow doInTransaction() {
						return container.addRow();
					}
				});

		assertThat((GenericItem) containerRow.getInternalRow(), is(row1));
	}

	@SuppressWarnings("rawtypes")
	private GenericItem createRow(GenericItemId rowId) {
		return new GenericItem(vaadinContainerMock, rowId,
				new HashSet<GenericProperty>());
	}

	@Test
	public void shouldAddAnyFilter() {
		super.shouldAddAnyFilter();
	}

	@Test
	public void shouldAddNotFilterWithAllSubfilter() {
		super.shouldAddNotFilterWithAllSubfilter();
	}

	@Test
	public void shouldAddContainsFilter() {
		shouldAddFilterType(new Contains("NAME", "abcdef", false),
				AdvancedStringFilter.class);
	}

	@Test
	public void shouldAddNotFilterWithSingleSubfilter() {
		super.shouldAddNotFilterWithSingleSubfilter();
	}

	@Test
	public void shouldAddAllFilter() {
		super.shouldAddAllFilter();
	}

	@Test
	public void shouldReturnContainerClobType() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType("test")).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return CharBuffer.class;
					}
				});

		Class<?> type = container.getType("test");

		assertTrue(type.isAssignableFrom(ContainerClob.class));
	}

	@Test
	public void shouldReturnContainerBlobType() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType("test")).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return byte[].class;
					}
				});

		Class<?> type = container.getType("test");

		assertTrue(type.isAssignableFrom(ContainerBlob.class));
	}

	@Test
	public void getTypeShouldReturnNullOnMissingTypeInformation() {
		assertNull(container.getType("UNKNOWN"));
	}

}
