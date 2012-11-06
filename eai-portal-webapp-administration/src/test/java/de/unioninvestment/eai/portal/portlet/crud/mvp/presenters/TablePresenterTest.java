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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Table;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;

public class TablePresenterTest extends AbstractTablePresenterTest {

	@Before
	public void setUp() {
		super.setUp();
		presenter = new TablePresenter(viewMock, modelMock);
	}

	@Test
	public void shouldInitializeViewEager() {
		presenter.onShow(eventMock);

		verify(viewMock, times(1)).initialize(presenter, containerMock,
				modelMock, 15, 2.0);
	}

	@Test
	public void shouldReturnTableIsReadonlyIfContainerIsNotEditable() {
		when(modelMock.isEditable()).thenReturn(true);
		when(containerMock.isDeleteable()).thenReturn(false);
		when(containerMock.isUpdateable()).thenReturn(false);
		when(containerMock.isInsertable()).thenReturn(false);

		boolean result = presenter.isReadonly();
		assertThat(result, is(true));
	}

	@Test
	public void shouldReturnTableIsReadonlyIfModelIsReadonly() {
		when(modelMock.isEditable()).thenReturn(false);
		when(containerMock.isDeleteable()).thenReturn(true);
		when(containerMock.isUpdateable()).thenReturn(false);
		when(containerMock.isInsertable()).thenReturn(false);

		boolean result = presenter.isReadonly();
		assertThat(result, is(true));
	}

	@Test
	public void shouldReturnTableIsInsertable() {
		when(containerMock.isInsertable()).thenReturn(true);

		boolean result = presenter.isInsertable();
		assertThat(result, is(true));
	}

	@Test
	public void shouldReturnTableIsUpdateabale() {
		when(containerMock.isUpdateable()).thenReturn(true);

		boolean result = presenter.isUpdateable();
		assertThat(result, is(true));
	}

	@Test
	public void shouldReturnTableIsDeleteable() {
		when(containerMock.isDeleteable()).thenReturn(true);

		boolean result = presenter.isDeleteable();
		assertThat(result, is(true));
	}

	@Test
	public void shouldCallRowChange() {
		Map<String, Object> changedValues = singletonMap("NAME",
				(Object) "VALUE");
		presenter.rowChange(itemMock, changedValues);

		verify(modelMock).rowChange(itemMock, changedValues);
	}

	@Test
	public void shouldDelegateAddGeneratedColumn() {
		Table.ColumnGenerator columnGeneratorMock = mock(Table.ColumnGenerator.class);
		presenter.addGeneratedColumn("id", "name", columnGeneratorMock);
		verify(viewMock).addGeneratedColumn("id", "name", columnGeneratorMock);
	}

	@Test
	public void shouldRememberAddedColumn() {
		Table.ColumnGenerator columnGeneratorMock = mock(Table.ColumnGenerator.class);
		presenter.addGeneratedColumn("id", "name", columnGeneratorMock);
		assertTrue(presenter.hasGeneratedColumn("id"));
	}

	@Test
	public void shouldDelegateRemoveGeneratedColumn() {
		presenter.removeGeneratedColumn("id");
		verify(viewMock).removeGeneratedColumn("id");
	}

	@Test
	public void shouldRecalculateColumndWidthAfterRemovingAColumn() {
		presenter.removeGeneratedColumn("id");
		verify(viewMock).recalculateColumnWidths();
	}

	@Test
	public void shouldForgetRemovedColumn() {
		presenter.addGeneratedColumn("id", "name", null);
		presenter.removeGeneratedColumn("id");
		assertFalse(presenter.hasGeneratedColumn("id"));
	}

	@Test
	public void shouldApplyChangesInRenderOnce() {
		DynamicColumnChanges changes = mock(DynamicColumnChanges.class);
		presenter.renderOnce(changes);
		verify(changes).apply();
	}

	@Test
	public void shouldRefreshAtTheEndOfRenderOnceIfRefreshWasEnabledBefore() {
		when(viewMock.disableContentRefreshing()).thenReturn(true);
		DynamicColumnChanges changes = mock(DynamicColumnChanges.class);
		presenter.renderOnce(changes);
		verify(viewMock).enableContentRefreshing(true);
	}

	@Test
	public void shouldNotRefreshAtTheEndOfRenderOnceIfRefreshWasNotEnabledBefore() {
		when(viewMock.disableContentRefreshing()).thenReturn(false);
		DynamicColumnChanges changes = mock(DynamicColumnChanges.class);
		presenter.renderOnce(changes);
		verify(viewMock, never()).enableContentRefreshing(anyBoolean());
	}

	@Test
	public void shouldClearAllGeneratedColumns() {
		presenter.addGeneratedColumn("id1", "name1", null);
		presenter.addGeneratedColumn("id2", "name2", null);
		presenter.addGeneratedColumn("id3", "name3", null);
		presenter.clearAllGeneratedColumns();
		verify(viewMock).removeGeneratedColumn("id1");
		verify(viewMock).removeGeneratedColumn("id2");
		verify(viewMock).removeGeneratedColumn("id3");
	}

	@Test
	public void shouldForgetAllColumnsWhenClearAllGeneratedColumnsIsCalled() {
		presenter.addGeneratedColumn("id1", "name1", null);
		presenter.addGeneratedColumn("id2", "name2", null);
		presenter.addGeneratedColumn("id3", "name3", null);
		presenter.clearAllGeneratedColumns();
		assertFalse(presenter.hasGeneratedColumn("id1"));
		assertFalse(presenter.hasGeneratedColumn("id2"));
		assertFalse(presenter.hasGeneratedColumn("id3"));
	}

	@Test
	public void shouldDelegateGetVisibleColumns() {
		presenter.getVisibleColumns();
		verify(viewMock).getVisibleColumns();
	}

	@Test
	public void shouldDelegateSetVisibleColumns() {
		presenter.setVisibleColumns(null);
		verify(viewMock).setVisibleColumns(null);
	}

	@Test
	public void shouldRecalculateColumnWidthAfterSetVisibleColumns() {
		presenter.setVisibleColumns(null);
		verify(viewMock).recalculateColumnWidths();
	}

	@Test
	public void shouldCreateNewRowInView() {
		// Object itemId = new Object();
		// when(viewMock.addItemToTable()).thenReturn(itemId);

		// wenn per createNewRow eine neue Zeile hinzugefügt wird
		presenter.createNewRow(Collections.<String, Object> emptyMap());

		// dann sollte diese über die View hinzugefügt werden
		verify(viewMock).addItemToTable();
	}

	@Test
	public void shouldSelectNewRow() {
		Object itemId = new Object();
		when(viewMock.addItemToTable()).thenReturn(itemId);

		// wenn per createNewRow eine neue Zeile hinzugefügt wird
		presenter.createNewRow(Collections.<String, Object> emptyMap());

		// dann sollte diese nach dem Erstellen editierbar und ausgewählt sein
		verify(viewMock).selectItemForEditing(itemId, true);
	}

	@Test
	public void shouldFillCreatedRowWithValues() {
		// Gegeben einige Werte für eine neue Zeile
		Map<String, Object> values = createValuesForNewRow();
		when(
				containerMock.getRowByInternalRowId(any(), eq(false),
						eq(false))).thenReturn(containerRowMock);

		// wenn per createNewRow eine neue Zeile hinzugefügt wird
		presenter.createNewRow(values);

		// dann sollte diese mit den übergebenen Werten befüllt werden
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			verify(containerRowMock).setValue(entry.getKey(), entry.getValue());
		}
	}

	private Map<String, Object> createValuesForNewRow() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("1", "Fitze");
		values.put("2", "Fatze");
		values.put("3", "Foo");
		return values;
	}

	@Test
	public void shouldDelegateSetTableActionVisibility() {
		presenter.setTableActionVisibility("ID", false);
		verify(viewMock).setTableActionVisibility("ID", false);
	}
}
