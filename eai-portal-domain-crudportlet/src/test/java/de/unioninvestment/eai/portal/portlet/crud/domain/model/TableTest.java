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
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;

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
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DisplayMode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.EmptyColumnGenerator;

public class TableTest {

	@Mock
	private Panel panelMock;

	@Captor
	private ArgumentCaptor<ModeChangeEvent<Table, Mode>> modeChangeEventCaptor;

	@Captor
	private ArgumentCaptor<ModeChangeEvent<Table, DisplayMode>> displayModeChangeEventCaptor;

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
	private ContainerRowId containerRowIdMock;

	@Mock
	private Item itemMock;

	@Mock
	private RowEditableChecker rowEditableCheckerMock;

	@Mock
	private RowDeletableChecker rowDeletableCheckerMock;

	@Mock
	private Table.Presenter presenterMock;

	@Mock
	private ExportWithExportSettings exportMock;

	@Mock
	private Portlet portletMock;

	@Mock
	private ModeChangeEventHandler<Table, DisplayMode> displayModeChangeEventHandlerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		config.setRowHeight(20);

		table = new Table(config, tableColumns, true);
		table.setContainer(containerMock);
		table.setPresenter(presenterMock);
		table.setPanel(panelMock);

		when(panelMock.getPortlet()).thenReturn(portletMock);
	}

	@Test
	public void shouldReturnEditableCheckerResult() {
		when(rowEditableCheckerMock.isEditable(containerRowMock)).thenReturn(
				false);
		table.setRowEditableChecker(rowEditableCheckerMock);
		table.setRowDeletableChecker(rowDeletableCheckerMock);

		boolean result = table.isRowEditable(containerRowMock);

		verify(rowEditableCheckerMock).isEditable(containerRowMock);

		assertThat(result, is(false));
	}

	@Test
	public void shouldReturnRowIsNotEditableAsDefault() {
		assertThat(table.isRowEditable(containerRowMock), is(true));

		table = new Table(config, tableColumns, false);

		assertThat(table.isRowEditable(containerRowMock), is(false));
	}

	@Test
	public void shouldReturnDeletableCheckerResult() {
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(
				containerRowMock);
		when(rowDeletableCheckerMock.isDeletable(containerRowMock)).thenReturn(
				false);
		table.setRowDeletableChecker(rowDeletableCheckerMock);

		boolean result = table.isRowDeletable(containerRowIdMock);

		verify(rowDeletableCheckerMock).isDeletable(containerRowMock);

		assertThat(result, is(false));
	}

	@Test
	public void shouldReturnRowIsDeletableAsDefault() {
		table = new Table(config, tableColumns, false);
		assertThat(table.isRowDeletable(containerRowIdMock), is(true));
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
	public void shouldNotFireModeChangeEventIfNothingChanges() {
		table.addModeChangeEventHandler(modeChangeListenerMock);

		table.changeMode(Mode.VIEW);

		verifyZeroInteractions(modeChangeListenerMock);
	}

	@Test
	public void shouldChangeModes() {
		assertThat(table.getMode(), is(Mode.VIEW));
		table.changeMode();
		assertThat(table.getMode(), is(Mode.EDIT));
		table.changeMode();
		assertThat(table.getMode(), is(Mode.VIEW));
	}

	@Test
	public void shouldChangeDisplayModes() {
		assertThat(table.getDisplayMode(), is(DisplayMode.TABLE));
		table.changeDisplayMode();
		assertThat(table.getDisplayMode(), is(DisplayMode.FORM));
		table.changeDisplayMode();
		assertThat(table.getDisplayMode(), is(DisplayMode.TABLE));
	}

	@Test
	public void shouldFireEventOnDisplayModeChange() {
		table.addDisplayModeChangeEventHandler(displayModeChangeEventHandlerMock);
		table.changeDisplayMode(DisplayMode.FORM);
		verifyDisplayModeChangeEvent(table, DisplayMode.FORM);
	}

	@Test
	public void shouldNotFireEventWhenDisplayModeAlreadySet() {
		table.addDisplayModeChangeEventHandler(displayModeChangeEventHandlerMock);
		table.changeDisplayMode(DisplayMode.TABLE);
		verifyZeroInteractions(displayModeChangeEventHandlerMock);
	}

	@Test
	public void shouldFireSelectionChangeEvent() {

		table.addSelectionEventHandler(selectionChangeListenerMock);

		when(rowIdMock.getId()).thenReturn(new Object[] { 1, 2 });
		selectionRowId.add(new DatabaseContainerRowId(rowIdMock, asList("ID",
				"INDEX")));
		table.changeSelection(selectionRowId);

		verifySelectionChangeEvent(table, selectionRowId);
	}

	private void verifySelectionChangeEvent(Table expectedTable,
			Set<ContainerRowId> expectedSelection) {
		verify(selectionChangeListenerMock).onSelectionChange(
				selectionChangeEventCaptor.capture());
		assertThat(selectionChangeEventCaptor.getValue().getSource(),
				is((Object) expectedTable));
		assertThat(selectionChangeEventCaptor.getValue().getSelection(),
				equalTo(expectedSelection));
	}

	@Test
	public void shouldFireDoubleClickEventIfHandlerIsRegistered() {
		table.addDoubleClickEventHandler(doubleClickEventHandler);
		table.doubleClick(containerRowMock);
		verifyDoubleClickEvent(table, containerRowMock);
	}

	private void verifyDoubleClickEvent(Table expectedTable,
			ContainerRow expectedRow) {
		verify(doubleClickEventHandler).onDoubleClick(
				tableDoubleClickEventCaptor.capture());
		assertThat(tableDoubleClickEventCaptor.getValue().getSource(),
				is(expectedTable));
		assertThat(tableDoubleClickEventCaptor.getValue().getRow(),
				is(expectedRow));
	}

	@Test
	public void shouldSwitchToFormViewAndChangeSelectionOnDoubleClickWithFormEditing() {
		table.addDisplayModeChangeEventHandler(displayModeChangeEventHandlerMock);
		table.addSelectionEventHandler(selectionChangeListenerMock);
		when(containerRowMock.getId()).thenReturn(containerRowIdMock);

		verifySwitchToFormViewAndSelectionChangeOnDoubleClick(Mode.VIEW, false,
				false, false, false, false);
		verifySwitchToFormViewAndSelectionChangeOnDoubleClick(Mode.VIEW, false,
				false, false, false, false);
		verifySwitchToFormViewAndSelectionChangeOnDoubleClick(Mode.VIEW, true,
				true, true, false, false);
		verifySwitchToFormViewAndSelectionChangeOnDoubleClick(Mode.VIEW, false,
				true, false, true, true);
		verifySwitchToFormViewAndSelectionChangeOnDoubleClick(Mode.EDIT, true,
				true, false, true, false); // selection already changed
	}

	private void verifySwitchToFormViewAndSelectionChangeOnDoubleClick(
			Mode mode, boolean hasDoubleClickHandler, boolean editForm,
			boolean shouldFireDoubleClickEvent, boolean shouldSwitch,
			boolean shouldFireSelectionChange) {

		table.removeDoubleClickEventHandler(doubleClickEventHandler);
		if (hasDoubleClickHandler) {
			table.addDoubleClickEventHandler(doubleClickEventHandler);
		}
		config.setEditForm(editForm);
		table.mode = mode;
		table.displayMode = DisplayMode.TABLE;
		reset(doubleClickEventHandler, displayModeChangeEventHandlerMock,
				selectionChangeListenerMock);

		table.doubleClick(containerRowMock);

		if (hasDoubleClickHandler && shouldFireDoubleClickEvent) {
			verifyDoubleClickEvent(table, containerRowMock);
		} else if (hasDoubleClickHandler) {
			verifyZeroInteractions(doubleClickEventHandler);
		}
		if (shouldSwitch) {
			verifyDisplayModeChangeEvent(table, DisplayMode.FORM);
		} else {
			verifyZeroInteractions(displayModeChangeEventHandlerMock);
		}
		if (shouldFireSelectionChange) {
			verifySelectionChangeEvent(table, singleton(containerRowIdMock));
		} else {
			verifyZeroInteractions(selectionChangeListenerMock);
		}
	}

	private void verifyDisplayModeChangeEvent(Table expectedTable,
			DisplayMode expectedDisplayMode) {
		verify(displayModeChangeEventHandlerMock).onModeChange(
				displayModeChangeEventCaptor.capture());
		assertThat(displayModeChangeEventCaptor.getValue().getSource(),
				is(expectedTable));
		assertThat(displayModeChangeEventCaptor.getValue().getMode(),
				is(expectedDisplayMode));
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
		when(portletMock.allowsDisplayGeneratedContent()).thenReturn(true);

		com.vaadin.ui.Table.ColumnGenerator columnGenerator = mock(com.vaadin.ui.Table.ColumnGenerator.class);
		table.addGeneratedColumn("id", "name", columnGenerator);
		verify(presenterMock).addGeneratedColumn("id", "name", columnGenerator);
	}

	@Test
	public void shouldAddEmptyGeneratedColumnIfPermissionIsMissing() {
		when(portletMock.allowsDisplayGeneratedContent()).thenReturn(false);

		com.vaadin.ui.Table.ColumnGenerator columnGenerator = mock(com.vaadin.ui.Table.ColumnGenerator.class);
		table.addGeneratedColumn("id", "name", columnGenerator);

		verify(presenterMock).addGeneratedColumn(eq("id"), eq("name"),
				isA(EmptyColumnGenerator.class));
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
