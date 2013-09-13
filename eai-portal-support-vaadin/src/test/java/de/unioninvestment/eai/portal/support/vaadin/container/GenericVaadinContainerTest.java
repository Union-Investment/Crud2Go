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
package de.unioninvestment.eai.portal.support.vaadin.container;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.filter.Compare;

@SuppressWarnings("unchecked")
public class GenericVaadinContainerTest {

	@Mock
	private GenericDelegate delegateMock;

	@Mock
	private MetaData metaDataMock;

	private GenericVaadinContainer container;

	@Mock
	private ItemSetChangeListener listenerMock;

	@Captor
	private ArgumentCaptor<ItemSetChangeEvent> itemSetChangeEventCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Column col1 = new Column("COL1", String.class, false, false, true, null);
		Column col2 = new Column("COL2", String.class, false, false, false,
				null);
		metaDataMock = new MetaData(asList(col1, col2), false, false, false,
				false, false);
		when(delegateMock.getMetaData()).thenReturn(metaDataMock);
		when(delegateMock.getRows()).thenReturn(
				asList(new Object[] { "1", "2" }, new Object[] { "3", "4" }));

		container = new GenericVaadinContainer(delegateMock);
	}

	@Test
	public void shouldReturnPropertyIdsFromMetaData() {
		when(delegateMock.getMetaData()).thenReturn(metaDataMock);
		container = new GenericVaadinContainer(delegateMock);

		List<String> ids = asList("COL1", "COL2");
		when(metaDataMock.getColumnNames()).thenReturn(ids);
		assertThat((List<String>) container.getContainerPropertyIds(), is(ids));
	}

	@Test
	public void shouldReturnTypeFromMetaData() {
		when(delegateMock.getMetaData()).thenReturn(metaDataMock);
		container = new GenericVaadinContainer(delegateMock);

		MetaDataTest.mockType(metaDataMock, "COL1", String.class);
		assertThat((Class<String>) container.getType("COL1"),
				equalTo(String.class));
	}

	@Test
	public void shouldCreateItemIdFromBackendRow() {
		GenericItemId itemId = container.createItemId(new Object[] { "abc",
				"def" });
		assertThat(itemId.getId(), is(new Object[] { "abc" }));
	}

	@Test
	public void shouldCreateItemFromBackendRow() {
		GenericItem item = container.createItem(new GenericItemId(
				new Object[] { "def" }), new Object[] { "abc", "def" });

		assertThat((String) item.getItemProperty("COL1").getValue(), is("abc"));
		assertThat((String) item.getItemProperty("COL2").getValue(), is("def"));
	}

	@Test
	public void shouldStoreItem() {
		assertThat(
				(String) container
						.getUnfilteredItem(
								new GenericItemId(new Object[] { "3" }))
						.getItemProperty("COL2").getValue(), is("4"));
	}

	@Test
	public void shouldAddNewItem() {
		GenericItemId newItemId = (GenericItemId) container.addItem();
		assertThat(newItemId.isNewId(), is(true));
	}

	@Test
	public void shouldNotBeModifiedAfterConstruction() {
		assertThat(container.isModified(), is(false));
	}

	@Test
	public void shouldBeModifiedAfterAddingNewItem() {
		container.addItem();
		assertThat(container.isModified(), is(true));
	}

	@Test
	public void shouldBeModifiedAfterDeletingItem() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		container.removeItem(deletedItemId);

		assertThat(container.isModified(), is(true));
	}

	@Test
	public void shouldBeModifiedAfterChangingItem() {
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");

		assertThat(container.isModified(), is(true));
	}

	@Test
	public void shouldDeleteExistingItem() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		container.removeItem(deletedItemId);

		assertThat(container.getItem(deletedItemId), nullValue());
	}

	@Test
	public void shouldDeleteAddedItem() {
		Object newItem = container.addItem();
		container.removeItem(newItem);

		container.commit();
		verify(delegateMock, never()).update(anyList(),
				any(UpdateContext.class));
	}

	@Test
	public void shouldReturnDeletedFlag() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		container.removeItem(deletedItemId);

		assertThat(container.isDeleted(deletedItemId), is(true));
	}

	@Test
	public void shouldCallDelegateOnDeletedItem() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		GenericItem deletedItem = container.getItem(deletedItemId);
		container.removeItem(deletedItemId);

		container.commit();

		verify(delegateMock).update(eq(asList(deletedItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldSortDescending() {
		container.sort(new Object[] { "COL2" }, new boolean[] { false });

		container.commit();

		GenericItemId firstItemId = container.firstItemId();
		assertThat(
				(String) container.getItem(firstItemId).getItemProperty("COL2")
						.getValue(), is("4"));
		assertThat(
				(String) container.getItem(container.nextItemId(firstItemId))
						.getItemProperty("COL2").getValue(), is("2"));
	}

	
	@Test
	public void shouldKeepModifiedItemsOnRefresh() {
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");

		container.refresh();

		GenericItem refreshedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		assertThat((String) refreshedItem.getItemProperty("COL2").getValue(),
				is("5"));

	}

	@Test
	public void shouldDropOrphanedModifiedItemsOnRefresh() {
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");

		when(delegateMock.getRows()).thenReturn(
				singletonList(new Object[] { "1", "2" }));

		container.refresh();

		assertThat(container.getUnfilteredItem(new GenericItemId(
				new Object[] { "3" })), nullValue());

		container.commit();
		verify(delegateMock, never()).update(anyList(),
				any(UpdateContext.class));
	}

	@Test
	public void shouldCallDelegateUpdateWithNewItemsOnCommit() {
		GenericItemId newItemId = (GenericItemId) container.addItem();
		GenericItem newItem = container.getItem(newItemId);

		container.commit();

		verify(delegateMock).update(eq(asList(newItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldFireContentChangeEventOnCommit() {
		container.addItemSetChangeListener(listenerMock);

		container.commit();

		verify(listenerMock).containerItemSetChange(
				itemSetChangeEventCaptor.capture());
		assertThat(itemSetChangeEventCaptor.getValue().getContainer(),
				is((Container) container));
	}

	@Test
	public void shouldRefreshTheContainerAfterUpdateIfRequestedByDelegate() {
		reset(delegateMock);

		container.addItem();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				UpdateContext context = (UpdateContext) invocation
						.getArguments()[1];
				context.requireRefresh();
				return null;
			}
		}).when(delegateMock).update(any(List.class), any(UpdateContext.class));

		container.commit();

		verify(delegateMock).getRows();
	}

	@Test
	public void shouldCallDelegateUpdateWithModifiedItemsOnCommit() {
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");

		container.commit();

		verify(delegateMock).update(eq(asList(changedItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldFilterRows() {
		container.addContainerFilter(new Compare.Equal("COL1", "1"));

		assertThat(container.firstItemId(), is(new GenericItemId(
				new Object[] { "1" })));
		assertThat(container.nextItemId(container.firstItemId()), nullValue());
	}

	@Test
	public void shouldNotFilterNewRows() {
		GenericItemId newItemId = (GenericItemId) container.addItem();
		container.addContainerFilter(new Compare.Equal("COL1", "OTHER"));

		assertThat(container.firstItemId(), is(newItemId));
	}

	@Test
	public void shouldNotCommitAddedItemsTwice() {
		GenericItemId newItemId = (GenericItemId) container.addItem();
		GenericItem newItem = container.getItem(newItemId);

		container.commit();
		container.commit();

		verify(delegateMock, times(1)).update(eq(asList(newItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldNotCommitModifiedItemsTwice() {
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");

		container.commit();
		container.commit();

		verify(delegateMock, times(1)).update(eq(asList(changedItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldNotCommitDeletedItemsTwice() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		GenericItem deletedItem = container.getItem(deletedItemId);
		container.removeItem(deletedItemId);

		container.commit();
		container.commit();

		verify(delegateMock, times(1)).update(eq(asList(deletedItem)),
				any(UpdateContext.class));
	}

	@Test
	public void shouldClearModificationsOnRollback() {
		GenericItemId deletedItemId = new GenericItemId(new Object[] { "3" });
		container.getItem(deletedItemId);
		GenericItem changedItem = container
				.getUnfilteredItem(new GenericItemId(new Object[] { "3" }));
		changedItem.getItemProperty("COL2").setValue("5");
		GenericItemId newItemId = (GenericItemId) container.addItem();
		container.getItem(newItemId);

		container.rollback();
		container.commit();

		assertThat(container.isModified(), is(false));
		verify(delegateMock, never()).update(anyList(),
				any(UpdateContext.class));

		changedItem = container.getUnfilteredItem(new GenericItemId(
				new Object[] { "3" }));
		assertThat((String) changedItem.getItemProperty("COL2").getValue(),
				is("4"));
	}
}
