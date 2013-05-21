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
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.portlet.crud.config.ColumnsConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportCallback;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;

public class TableTest {

	@Mock
	private Panel panelMock;

	@Captor
	private ArgumentCaptor<ModeChangeEvent<Table, Mode>> modeChangeEventCaptor;

	@Captor
	private ArgumentCaptor<SelectionEvent> selectionChangeEventCaptor;

	@Captor
	private ArgumentCaptor<TableDoubleClickEvent> tableDoubleClickEventCaptor;

	private TableColumns tableColumns = new TableColumns(
			new ArrayList<TableColumn>());
	private TableConfig config = new TableConfig();
	private Table table;

	@Mock
	private ModeChangeEventHandler<Table, Mode> modeChangeListenerMock;

	@Mock
	private SelectionEventHandler selectionChangeListenerMock;

	@Mock
	private TableDoubleClickEventHandler doubleClickEventHandler;

	private Set<ContainerRowId> selectionRowId = new HashSet<ContainerRowId>();

	@Mock
	private RowId rowIdMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private RowChangeEventHandler rowChangeEventHandlerMock;

	@Mock
	private ContainerRow containerRowMock;

	@Mock
	private Item itemMock;

	@Mock
	private RowEditableChecker rowEditableCheckerMock;

	@Mock
	private Table.Presenter presenterMock;

	@Mock
	private ExportCallback exportMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		config.setRowHeight(20);

		table = new Table(config, tableColumns, true);
		table.setContainer(containerMock);
		table.setPresenter(presenterMock);
	}

	@Test
	public void shouldReturnEditableCheckerResult() {
		when(rowEditableCheckerMock.isEditable(containerRowMock)).thenReturn(
				false);
		table.setRowEditableChecker(rowEditableCheckerMock);

		boolean result = table.isRowEditable(containerRowMock);

		verify(rowEditableCheckerMock).isEditable(containerRowMock);

		assertThat(result, is(false));
	}

	@Test
	public void shouldReturnRowIsEditableAsDefault() {
		assertThat(table.isRowEditable(containerRowMock), is(true));

		table = new Table(config, tableColumns, false);

		assertThat(table.isRowEditable(containerRowMock), is(false));
	}

	@Test
	public void shouldProvidePageReference() {
		table.setPanel(panelMock);
		assertThat(table.getPanel(), is(panelMock));
	}

	@Test
	public void shouldIgnoreConfiguredRowHeightOnMissingColumns() {
		assertThat(table.getRowHeight(), is(nullValue()));
	}

	@Test
	public void shouldReturnConfiguredColumnHeight() {
		config.setColumns(new ColumnsConfig());
		assertThat(table.getRowHeight(), is(20));
	}

	@Test
	public void shouldFireModeChangeEvent() {

		table.addModeChangeEventHandler(modeChangeListenerMock);

		table.changeMode(Mode.EDIT);

		verify(modeChangeListenerMock).onModeChange(
				modeChangeEventCaptor.capture());

		assertThat(modeChangeEventCaptor.getValue().getSource(),
				is((Object) table));
		assertThat(modeChangeEventCaptor.getValue().getMode(),
				is((Object) Mode.EDIT));
	}

	@Test
	public void shouldFireSelectionChangeEvent() {

		table.addSelectionEventHandler(selectionChangeListenerMock);

		when(rowIdMock.getId()).thenReturn(new Object[] { 1, 2 });
		selectionRowId.add(new DatabaseContainerRowId(rowIdMock, asList("ID",
				"INDEX")));
		table.changeSelection(selectionRowId);

		verify(selectionChangeListenerMock).onSelectionChange(
				selectionChangeEventCaptor.capture());

		assertThat(selectionChangeEventCaptor.getValue().getSource(),
				is((Object) table));
	}

	@Test
	public void shouldFireDoubleClickEvent() {
		table.addTableDoubleClickEventHandler(doubleClickEventHandler);
		table.doubleClick(containerRowMock);

		verify(doubleClickEventHandler).onDoubleClick(
				tableDoubleClickEventCaptor.capture());
		assertThat(tableDoubleClickEventCaptor.getValue().getSource(),
				is(table));
		assertThat(tableDoubleClickEventCaptor.getValue().getRow(),
				is(containerRowMock));
	}

	@Test
	public void shouldCallRefreshOnDBContainer() {
		table.refresh();

		verify(containerMock).refresh();
	}

	@Test
	public void shouldFireRowChangeEvent() {
		table.addRowChangeEventHandler(rowChangeEventHandlerMock);

		Map<String, Object> changedValues = singletonMap("NAME",
				(Object) "VALUE");

		when(containerMock.convertItemToRow(itemMock, false, false))
				.thenReturn(containerRowMock);

		table.rowChange(itemMock, changedValues);

		ArgumentCaptor<RowChangeEvent> eventCaptor = ArgumentCaptor
				.forClass(RowChangeEvent.class);
		verify(rowChangeEventHandlerMock).rowChange(eventCaptor.capture());

		assertThat(eventCaptor.getValue().getSource(), is(containerRowMock));
		assertThat(eventCaptor.getValue().getChangedValues(), is(changedValues));
	}

	@Test
	public void shouldDelegateAddGeneratedColumn() {
		com.vaadin.ui.Table.ColumnGenerator columnGenerator = mock(com.vaadin.ui.Table.ColumnGenerator.class);
		table.addGeneratedColumn("id", "name", columnGenerator);
		verify(presenterMock).addGeneratedColumn("id", "name", columnGenerator);
	}

	@Test
	public void shouldDelegateRemoveGeneratedColumn() {
		table.removeGeneratedColumn("id");
		verify(presenterMock).removeGeneratedColumn("id");
	}

	@Test
	public void shouldDelegateGetRowByItemId() {
		ContainerRow rowMock = mock(ContainerRow.class);
		when(table.getRowByItemId("id")).thenReturn(rowMock);
		ContainerRow rowResult = table.getRowByItemId("id");
		assertThat(rowResult, is(sameInstance(rowMock)));
	}

	@Test
	public void shouldDelegateRenderOnce() {
		DynamicColumnChanges changesMock = mock(DynamicColumnChanges.class);
		table.renderOnce(changesMock);
		verify(presenterMock).renderOnce(changesMock);
	}

	@Test
	public void shouldDelegateHasGeneratedColumn() {
		table.hasGeneratedColumn("id");
		verify(presenterMock).hasGeneratedColumn("id");
	}

	@Test
	public void shouldDelegateClearAllGeneratedColumns() {
		table.clearAllGeneratedColumns();
		verify(presenterMock).clearAllGeneratedColumns();
	}

	@Test
	public void shouldDelegateGetVisibleColumns() {
		table.getVisibleColumns();
		verify(presenterMock).getVisibleColumns();
	}

	@Test
	public void shouldDelegateSetVisibleColumns() {
		List<String> visibleColumns = Arrays.asList(new String[] { "1", "2" });
		table.setVisibleColumns(visibleColumns);
		verify(presenterMock).setVisibleColumns(visibleColumns);
	}

	@Test
	public void shouldDelegateCreateNewRow() {
		Map<String, Object> values = Collections.<String, Object> emptyMap();
		table.createNewRow(values);
		verify(presenterMock).createNewRow(values);
	}

	@Test
	public void shouldDelegateWithExportSettings() {
		table.withExportSettings(exportMock);
		verify(containerMock).withExportSettings(exportMock);
	}
}
