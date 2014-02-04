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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DisplayMode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.DefaultRowEditingFormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;

public class RowEditingFormPresenterTest {

	private RowEditingFormPresenter presenter;

	@Mock
	private DefaultRowEditingFormView viewMock;

	@Mock
	private PanelContentView panelContentViewMock;

	@Mock
	private Dialog modelMock;

	@Mock
	private Panel parentPanelMock;

	@Mock
	private Table tableMock;

	@Mock
	private TablePresenter tablePresenterMock;

	@Mock
	private DataContainer containerMock;

	@Mock
	private TableColumns tableColumnsMock;

	@Mock
	private TableDoubleClickEvent tableDoubleClickEventMock;

	@Mock
	private ContainerRow containerRowMock;

	@Mock
	private ContainerRow otherRowMock;

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private ContainerField containerFieldMock;

	@Mock
	private Button backButtonMock;

	@Mock
	private ContainerRowId otherRowIdMock;

	@Mock
	private PanelPresenter parentPanelPresenterMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(containerMock);
		when(tableMock.getColumns()).thenReturn(tableColumnsMock);
		when(viewMock.addBackButton(anyString(), any(ClickListener.class)))
				.thenReturn(backButtonMock);
		when(containerRowMock.getId()).thenReturn(containerRowIdMock);

		// default
		presenter = new RowEditingFormPresenter(viewMock, modelMock,
				parentPanelMock, "id1", tableMock, tablePresenterMock);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailWithWrongView() {
		presenter = new RowEditingFormPresenter(panelContentViewMock,
				modelMock, parentPanelMock, "id1", tableMock,
				tablePresenterMock);
	}

	@Test
	public void shouldReturnView() {
		assertEquals(presenter.getView(), viewMock);
	}

	@Test
	public void shouldAttachItselfAsDisplayModeChangeEventHandler() {
		verify(tableMock).addDisplayModeChangeEventHandler(
				any(ModeChangeEventHandler.class));
	}

	@Test
	public void shouldAttachDialogOnDoubleClick() {
		presenter.handleDisplayModeChange(DisplayMode.FORM);

		verify(parentPanelMock).attachDialog("id1");
	}

	@Test
	public void shouldCommitFormOnSave() throws CommitException {
		presenter.save();

		verify(viewMock).commit();
	}

	@Test
	public void shouldDisplayExceptionsFromStorageAsFormError() {
		doThrow(new RuntimeException("Bla")).when(containerMock).commit();

		presenter.save();

		verify(viewMock).showFormError("Bla");
	}

	@Test
	public void shouldDisplayRootCauseExceptionOfCommitAsFormError()
			throws CommitException {
		CommitException exception = new CommitException("Commit failed",
				new RuntimeException("Bla"));
		doThrow(exception).when(viewMock).commit();

		presenter.save();

		verify(viewMock).showFormError("Bla");
	}

	@Test
	public void shouldDisplayUnexpectedExceptionsIncludingClassAsFormError()
			throws CommitException {
		RuntimeException exception = new RuntimeException("Unexpected error",
				new RuntimeException("Bla"));
		doThrow(exception).when(viewMock).commit();

		presenter.save();

		verify(viewMock).showFormError("java.lang.RuntimeException: Bla");
	}

	@Test
	public void shouldDisplayRowOnRequest() {
		when(tableMock.isRowEditable(containerRowMock)).thenReturn(true);
		when(tableMock.getMode()).thenReturn(Mode.EDIT);

		when(containerRowMock.getId()).thenReturn(containerRowIdMock);
		when(containerMock.isDeleteable()).thenReturn(true);
		when(tableMock.isRowDeletable(containerRowIdMock)).thenReturn(true);

		presenter.displayRow(containerRowMock);

		verify(viewMock).displayRow(containerRowMock, true, true);
	}

	@Test
	public void shouldCommitContainerOnSave() {
		presenter.save();

		verify(containerMock).commit();
	}

	@Test
	public void shouldReturnToTableOnSave() {
		presenter.save();

		verify(tableMock).changeDisplayMode(DisplayMode.TABLE);
	}

	@Test
	public void shouldDiscardFormFieldsOnReset() {
		presenter.resetFields();

		verify(viewMock).discard();
	}

	@Test
	public void shouldDetachAttachedDialogOnDisplayModeChange() {
		presenter.notifyAboutBeingAttached(parentPanelPresenterMock);
		presenter.handleDisplayModeChange(DisplayMode.TABLE);
		verify(parentPanelMock).detachDialog();
	}

	/**
	 * This happens if backButton was pressed
	 */
	@Test
	public void shouldNotDetachAlreadyDetachedDialogOnDisplayModeChange() {
		presenter.handleDisplayModeChange(DisplayMode.TABLE);

		verify(parentPanelMock, never()).detachDialog();
	}

	@Test
	public void shouldSwitchDisplayModeOnCancel() {
		presenter.cancel();

		verify(tableMock).changeDisplayMode(DisplayMode.TABLE);
	}

	@Test
	public void shouldRemoveRowOnDelete() {
		presenter.displayRow(containerRowMock);

		presenter.delete();

		verify(containerMock).removeRows(
				Collections.singleton(containerRowIdMock));
	}

	@Test
	public void shouldReturnToTableOnDelete() {
		presenter.displayRow(containerRowMock);

		presenter.delete();

		verify(tableMock).changeDisplayMode(DisplayMode.TABLE);
	}

	@Test
	public void shouldReturnNullAsTitle() {
		assertEquals(presenter.getTitle(), null);
	}

	@Test
	public void shouldReturnModifiedFieldNamesAndValues() {
		when(tableColumnsMock.getVisibleNamesForForm()).thenReturn(
				asList("A", "B", "C"));
		when(viewMock.isFieldModifed("B")).thenReturn(true);

		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		when(tableMock.getMode()).thenReturn(Mode.EDIT);
		presenter.displayRow(containerRowMock);

		when(containerRowMock.getFields()).thenReturn(
				singletonMap("B", containerFieldMock));
		when(containerFieldMock.getValue()).thenReturn("D");

		List<String> modifiedFieldNames = presenter.getModifiedFieldNames();
		Map<String, Object> modifiedFields = presenter
				.getModifiedFieldValues(modifiedFieldNames);

		assertThat(modifiedFields, is(singletonMap("B", (Object) "D")));
	}

	@Test
	public void shouldReturnNullForModifiedClob() {
		when(tableColumnsMock.getVisibleNamesForForm()).thenReturn(
				asList("A", "B", "C"));
		when(viewMock.isFieldModifed("B")).thenReturn(true);

		when(tableMock.getMode()).thenReturn(Mode.EDIT);
		when(tableMock.getSelection())
				.thenReturn(singleton(containerRowIdMock));
		presenter.handleSelectionChange();

		when(containerMock.isCLob("B")).thenReturn(true);

		List<String> modifiedFieldNames = presenter.getModifiedFieldNames();
		Map<String, Object> modifiedFields = presenter
				.getModifiedFieldValues(modifiedFieldNames);

		assertThat(modifiedFields, is(singletonMap("B", null)));
	}

	@Test
	public void shouldHideErrorsOnSelectionChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		when(tableMock.getSelection()).thenReturn(singleton(containerRowIdMock));
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(containerRowMock);

		presenter.handleSelectionChange();
		
		verify(viewMock).hideFormError();
	}

	@Test
	public void shouldDisplayTheNewRowOnSelectionChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		when(tableMock.getSelection()).thenReturn(singleton(containerRowIdMock));
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(containerRowMock);

		presenter.handleSelectionChange();
		
		verify(viewMock).displayRow(Mockito.eq(containerRowMock), any(Boolean.class), any(Boolean.class));
	}
	
	@Test
	public void shouldPassEditableAndDeletableStatusForNewRow() {
		presenter.displayRow(containerRowMock);

		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		when(tableMock.getSelection()).thenReturn(singleton(otherRowIdMock));
		when(containerMock.getRow(otherRowIdMock, false, true)).thenReturn(
				otherRowMock);
		when(tableMock.isRowEditable(otherRowMock)).thenReturn(true);
		when(tableMock.getMode()).thenReturn(Mode.EDIT);
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);

		when(containerMock.isDeleteable()).thenReturn(true);
		when(otherRowMock.getId()).thenReturn(otherRowIdMock);
		when(tableMock.isRowDeletable(otherRowIdMock)).thenReturn(true);

		reset(viewMock);

		presenter.handleSelectionChange();

		verify(viewMock).displayRow(otherRowMock, true, true);
	}



	@Test
	public void shouldReDisplayTheCurrentRowOnModeChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		when(tableMock.getSelection()).thenReturn(singleton(containerRowIdMock));
		when(containerMock.getRow(containerRowIdMock, false, true)).thenReturn(containerRowMock);

		presenter.handleSelectionChange();
		reset(viewMock);
		
		presenter.handleModeChange(Mode.EDIT);
		
		verify(viewMock).displayRow(Mockito.eq(containerRowMock), any(Boolean.class), any(Boolean.class));
	}


	@Test
	public void shouldTellThatNextRowDoesNotExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.nextRowId(containerRowIdMock)).thenReturn(null);

		boolean result = presenter.hasNextRow();

		assertFalse(result);
	}

	@Test
	public void shouldTellThatNextRowExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.nextRowId(containerRowIdMock)).thenReturn(
				otherRowIdMock);

		boolean result = presenter.hasNextRow();

		assertTrue(result);
	}

	@Test
	public void shouldTellThatPreviousRowDoesNotExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.previousRowId(containerRowIdMock)).thenReturn(null);

		boolean result = presenter.hasPreviousRow();

		assertFalse(result);
	}

	@Test
	public void shouldTellThatPreviousRowExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.previousRowId(containerRowIdMock)).thenReturn(
				otherRowIdMock);

		boolean result = presenter.hasPreviousRow();

		assertTrue(result);
	}

	@Test
	public void shouldDoNothingIfNextRowDoesNotExist() {
		presenter.displayRow(containerRowMock);
		when(containerMock.nextRowId(containerRowIdMock)).thenReturn(null);
		reset(viewMock, tableMock);

		boolean switched = presenter.nextRow();

		verifyNoMoreInteractions(viewMock, tableMock);
		assertFalse(switched);
	}

	@Test
	public void shouldDoNothingIfPreviousRowDoesNotExist() {
		presenter.displayRow(containerRowMock);
		when(containerMock.previousRowId(containerRowIdMock)).thenReturn(null);
		reset(viewMock, tableMock);

		boolean switched = presenter.previousRow();

		verifyNoMoreInteractions(viewMock, tableMock);
		assertFalse(switched);
	}

	@Test
	public void shouldSwitchToPreviousRowIfItExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.previousRowId(containerRowIdMock)).thenReturn(
				otherRowIdMock);
		when(containerMock.getRow(otherRowIdMock, false, true)).thenReturn(
				otherRowMock);
		reset(viewMock, tableMock);

		boolean switched = presenter.previousRow();

		verify(tableMock).changeSelection(singleton(otherRowIdMock));
		assertTrue(switched);
	}

	@Test
	public void shouldSwitchToNextRowIfItExists() {
		presenter.displayRow(containerRowMock);
		when(containerMock.nextRowId(containerRowIdMock)).thenReturn(
				otherRowIdMock);
		when(containerMock.getRow(otherRowIdMock, false, true)).thenReturn(
				otherRowMock);
		reset(viewMock, tableMock);

		boolean switched = presenter.nextRow();

		verify(tableMock).changeSelection(singleton(otherRowIdMock));
		assertTrue(switched);
	}

	@Test
	public void shouldUpdateButtonsForViewModeOnModeChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		presenter.handleModeChange(Mode.EDIT);
		verify(viewMock).updateButtonsForEditMode();
	}

	@Test
	public void shouldUpdateButtonsForEditModeOnModeChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		presenter.handleModeChange(Mode.VIEW);
		verify(viewMock).updateButtonsForViewMode();
	}

	@Test
	public void shouldDisplayCurrentRowOnModeChange() {
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.FORM);
		presenter.handleModeChange(Mode.EDIT);
		verify(viewMock).displayRow(any(ContainerRow.class), any(Boolean.class), any(Boolean.class));
	}

	@Test
	public void shouldIgnoreModeChangesForDisplayModeTable() {
		reset(viewMock);
		when(tableMock.getDisplayMode()).thenReturn(DisplayMode.TABLE);

		presenter.handleModeChange(Mode.EDIT);

		verifyZeroInteractions(viewMock);
	}
	
	@Test
	public void shouldSaveOnModeChange() {
		presenter.changeMode();
		verify(containerMock).commit();
		verify(tableMock).changeMode();
	}
	
	@Test
	public void shouldNotChangeModeOnFailureDuringSave() {
		doThrow(new RuntimeException()).when(containerMock).commit();
		presenter.changeMode();
		verify(tableMock, never()).changeMode();
	}
}
