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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder;

public class ScriptTableTest {

	@Mock
	private Table tableMock;

	private Set<ContainerRowId> selectionRowId = new HashSet<ContainerRowId>();

	private ScriptTable scriptTable;

	@Mock
	private Closure<?> onSelectionChangeMock;

	@Mock
	private Closure<?> onModeMock;

	@Mock
	private Closure<?> onRowChangeClosureMock;

	@Mock
	private Closure<?> onDoubleClickClosureMock;

	@Captor
	private ArgumentCaptor<ModeChangeEventHandler<Table, Mode>> modeChangeEventCaptor;

	@Captor
	private ArgumentCaptor<RowChangeEventHandler> rowChangeEventCaptor;

	@Mock
	private DataContainer databaseContainerMock;

	@Mock
	private ScriptContainer scriptContainerMock;

	@Mock
	private RowId rowIdMock;

	private ContainerRowId containerRowId;

	@Mock
	private ContainerRow containerRowMock;

	@Captor
	private ArgumentCaptor<TableDoubleClickEventHandler> doubleClickEventCaptor;

	@Mock
	private PortletApplication applicationMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		scriptTable = new ScriptTable(applicationMock, tableMock);
		scriptTable.setContainer(scriptContainerMock);
		when(tableMock.getContainer()).thenReturn(databaseContainerMock);

		when(rowIdMock.getId()).thenReturn(new Object[] { 1, 2 });
		containerRowId = new DatabaseContainerRowId(rowIdMock, asList("A", "B"));
	}

	@Test
	public void shouldFireSelectionChangeEvent() {

		selectionRowId.add(containerRowId);

		ArgumentCaptor<SelectionEventHandler> selectionEventCaptor = ArgumentCaptor
				.forClass(SelectionEventHandler.class);
		verify(tableMock).addSelectionEventHandler(
				selectionEventCaptor.capture());

		scriptTable.setOnSelectionChange(onSelectionChangeMock);

		SelectionEvent selectionEvent = new SelectionEvent(tableMock,
				selectionRowId);
		selectionEventCaptor.getValue().onSelectionChange(selectionEvent);

		verify(onSelectionChangeMock).call(eq(scriptTable),
				any(ScriptTableSelection.class));
	}

	@Test
	public void shouldFireSelectionChangeEventClosureNull() {

		selectionRowId.add(containerRowId);

		ArgumentCaptor<SelectionEventHandler> selectionEventCaptor = ArgumentCaptor
				.forClass(SelectionEventHandler.class);
		verify(tableMock).addSelectionEventHandler(
				selectionEventCaptor.capture());

		SelectionEvent selectionEvent = new SelectionEvent(tableMock,
				selectionRowId);
		selectionEventCaptor.getValue().onSelectionChange(selectionEvent);

		verify(onSelectionChangeMock, never()).call(eq(scriptTable),
				any(ScriptTableSelection.class));
	}

	@Test
	public void shouldFireModeChangeEvent() {

		verify(tableMock).addModeChangeEventHandler(
				modeChangeEventCaptor.capture());

		scriptTable.setOnModeChange(onModeMock);

		ModeChangeEvent<Table, Mode> modeChangeEvent = new ModeChangeEvent<Table, Mode>(
				tableMock, Mode.EDIT);
		modeChangeEventCaptor.getValue().onModeChange(modeChangeEvent);

		verify(onModeMock).call(scriptTable, "EDIT");
	}

	@Test
	public void shouldFireRowChangeEvent() {

		verify(tableMock).addRowChangeEventHandler(
				rowChangeEventCaptor.capture());

		scriptTable.setOnRowChange(onRowChangeClosureMock);

		Map<String, Object> changedValues = singletonMap("FIELDNAME",
				(Object) "OLDVALUE");
		RowChangeEvent rowChangeEvent = new RowChangeEvent(containerRowMock,
				changedValues);

		rowChangeEventCaptor.getValue().rowChange(rowChangeEvent);

		verify(onRowChangeClosureMock).call(any(ScriptTable.class),
				any(ScriptRow.class), eq(changedValues));
	}

	@Test
	public void shouldFireModeChangeEventClosureNull() {

		verify(tableMock).addModeChangeEventHandler(
				modeChangeEventCaptor.capture());

		ModeChangeEvent<Table, Mode> modeChangeEvent = new ModeChangeEvent<Table, Mode>(
				tableMock, Mode.EDIT);
		modeChangeEventCaptor.getValue().onModeChange(modeChangeEvent);

		verify(onModeMock, never()).call(scriptTable, Mode.EDIT);
	}

	@Test
	public void shouldDoubleClickEvent() {

		scriptTable.setOnDoubleClick(onDoubleClickClosureMock);

		verify(tableMock).addTableDoubleClickEventHandler(
				doubleClickEventCaptor.capture());

		TableDoubleClickEvent doubleClickEvent = new TableDoubleClickEvent(
				tableMock, containerRowMock);
		doubleClickEventCaptor.getValue().onDoubleClick(doubleClickEvent);

		verify(onDoubleClickClosureMock).call(any(ScriptTable.class),
				any(ScriptRow.class));
	}

	@Test
	public void shouldDoubleClickEventNull() {

		verify(tableMock).addTableDoubleClickEventHandler(
				doubleClickEventCaptor.capture());

		TableDoubleClickEvent doubleClickEvent = new TableDoubleClickEvent(
				tableMock, containerRowMock);
		doubleClickEventCaptor.getValue().onDoubleClick(doubleClickEvent);

		verify(onDoubleClickClosureMock, never()).call(any(ScriptTable.class),
				any(ScriptRow.class));
	}

	@Test
	public void shouldCallRefreshOnTable() {
		scriptTable.refresh();

		verify(tableMock).refresh();
	}

	@Test
	public void addGeneratedColumnShouldConvertClosureToColumnGenerator() {
		// gegeben eine Closure zur Generierung der Zellen
		String columnId = "columnId";
		@SuppressWarnings("unchecked")
		Closure<Component> generatorClosure = mock(Closure.class);

		// wenn addGeneratedColumn mit dieser Closure aufgerufen wird
		scriptTable.addGeneratedColumn(columnId, generatorClosure);

		// dann wird diese Closure in einen Table.ColumnGenerator gewrappt
		verify(tableMock).addGeneratedColumn(eq(columnId), eq(columnId),
				isA(com.vaadin.ui.Table.ColumnGenerator.class));
	}

	@Test
	public void columnGeneratorShouldCallClosure() {
		// gegeben eine Closure zur Generierung der Zellen
		String columnId = "columnId";
		@SuppressWarnings("unchecked")
		Closure<Component> generatorClosure = mock(Closure.class);

		com.vaadin.ui.Table source = new com.vaadin.ui.Table();
		Object itemId = "itemId";
		ContainerRow rowMock = mock(ContainerRow.class);
		when(tableMock.getRowByItemId(itemId)).thenReturn(rowMock);

		// wenn addGeneratedColumn mit dieser Closure aufgerufen wird
		scriptTable.addGeneratedColumn(columnId, generatorClosure);

		// und wenn dann der Table.ColumnGenerator-Wrapper aufgerufen wird
		ArgumentCaptor<com.vaadin.ui.Table.ColumnGenerator> captor = ArgumentCaptor
				.forClass(com.vaadin.ui.Table.ColumnGenerator.class);
		verify(tableMock).addGeneratedColumn(eq(columnId), eq(columnId),
				captor.capture());
		ColumnGenerator generator = captor.getValue();
		generator.generateCell(source, itemId, columnId);

		// dann wird intern die Closure zur Generierung der Zellen benutzt
		verify(generatorClosure).call(isA(ScriptRow.class),
				isA(VaadinBuilder.class));
	}

	@Test
	public void shouldDelegateRemoveGeneratedColumn() {
		scriptTable.removeGeneratedColumn("columnId");
		verify(tableMock).removeGeneratedColumn("columnId");
	}

	@Test
	public void shouldDelegateRenderOnce() {
		@SuppressWarnings("unchecked")
		Closure<Component> changes = mock(Closure.class);
		scriptTable.renderOnce(changes);
		verify(tableMock).renderOnce(isA(DynamicColumnChanges.class));
	}

	@Test
	public void shouldDelegateHasGeneratedColumn() {
		scriptTable.hasGeneratedColumn("columnId");
		verify(tableMock).hasGeneratedColumn("columnId");
	}

	@Test
	public void shouldDelegateClearAllGeneratedColumns() {
		scriptTable.clearAllGeneratedColumns();
		verify(tableMock).clearAllGeneratedColumns();
	}

	@Test
	public void shouldDelegateGetVisibleColumns() {
		scriptTable.getVisibleColumns();
		verify(tableMock).getVisibleColumns();
	}

	@Test
	public void shouldDelegateSetVisibleColumns() {
		List<String> columnIds = Arrays.asList(new String[] { "1", "2", "3" });
		scriptTable.setVisibleColumns(columnIds);
		verify(tableMock).setVisibleColumns(columnIds);
	}

	@Test
	public void shouldDelegateCreateNewRow() {
		// Gegeben einige Werte für eine neue Zeile
		Map<String, Object> values = createValuesForNewRow();
		when(tableMock.createNewRow(values)).thenReturn(containerRowMock);

		// wenn per createNewRow eine neue Zeile hinzugefügt wird
		scriptTable.createNewRow(values);

		// dann sollte dies an die Modell-Table delegiert werden
		verify(tableMock).createNewRow(values);
	}

	private Map<String, Object> createValuesForNewRow() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("1", "Fitze");
		values.put("2", "Fatze");
		values.put("3", "Foo");
		return values;
	}

	@Test
	public void getRowByIdShouldReturnRow() {
		when(tableMock.getRow(containerRowId)).thenReturn(containerRowMock);
		ScriptRow scriptRow = scriptTable.getRowById(new ScriptRowId(
				containerRowId));
		assertThat(scriptRow.getRow(), is(sameInstance(containerRowMock)));
	}

	@Test
	public void getRowByIdShouldReturnNullWhenNoSuchRowExists() {
		when(tableMock.getRow(containerRowId)).thenReturn(null);
		assertNull(scriptTable.getRowById(new ScriptRowId(containerRowId)));
	}

	@Test
	public void getSelectedRows() {
		Set<ScriptRowId> selectedRowIds = new HashSet<ScriptRowId>();
		addScriptRowId(selectedRowIds);
		addScriptRowId(selectedRowIds);
		addScriptRowId(selectedRowIds);
		ScriptTableSelection selection = new ScriptTableSelection(
				databaseContainerMock, selectedRowIds);
		ScriptTable spy = Mockito.spy(scriptTable);
		when(spy.getSelection()).thenReturn(selection);

		assertThat(spy.getSelectedRows().size(), is(equalTo(3)));
	}

	private void addScriptRowId(Set<ScriptRowId> selectedRowIds) {
		selectedRowIds.add(createScriptRowId());
	}

	private ScriptRowId createScriptRowId() {
		return new ScriptRowId(new DatabaseContainerRowId(mock(RowId.class),
				asList("A", "B")));
	}

	@Test
	public void shouldDelegateSetTableActionVisibility() {
		scriptTable.setTableActionVisibility("ID", false);
		verify(tableMock).setTableActionVisibility("ID", false);
	}
}
