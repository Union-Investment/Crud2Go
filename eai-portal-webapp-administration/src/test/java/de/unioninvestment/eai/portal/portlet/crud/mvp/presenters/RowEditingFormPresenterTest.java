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
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.ui.Form;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
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
	private Form formMock;

	@Mock
	private ContainerRowId containerRowIdMock;

	@Mock
	private Item itemMock;

	@Mock
	private ContainerField containerFieldMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(containerMock);
		when(tableMock.getColumns()).thenReturn(tableColumnsMock);
		when(viewMock.getForm()).thenReturn(formMock);
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
	public void shouldAttachPresenterAsDoubleClickHandler() {
		verify(tableMock).addTableDoubleClickEventHandler(presenter);
	}

	@Test
	public void shouldAttachDialogOnDoubleClick() {
		when(tablePresenterMock.getCurrentMode()).thenReturn(Mode.EDIT);
		when(tableDoubleClickEventMock.getRow()).thenReturn(containerRowMock);

		presenter.onDoubleClick(tableDoubleClickEventMock);

		verify(parentPanelMock).attachDialog("id1");
	}

	@Test
	public void shouldCommitFormOnSave() {
		presenter.save();

		verify(formMock).commit();
	}

	@Test
	public void shouldCommitContainerOnSave() {
		presenter.save();

		verify(containerMock).commit();
	}

	@Test
	public void shouldDetachDialogOnSave() {
		presenter.save();

		verify(parentPanelMock).detachDialog();
	}

	@Test
	public void shouldDiscardFormFieldsOnReset() {
		presenter.resetFields();

		verify(formMock).discard();
	}

	@Test
	public void shouldDetachDialogOnCancel() {
		presenter.cancel();

		verify(parentPanelMock).detachDialog();
	}

	@Test
	public void shouldRevertChangesOnCancel() {
		presenter.cancel();

		verify(tablePresenterMock).revertChanges();
	}

	@Test
	public void shouldRemoveRowOnDelete() {
		presenter.showDialog(containerRowMock);

		presenter.delete();

		verify(containerMock).removeRows(
				Collections.singleton(containerRowIdMock));
	}

	@Test
	public void shouldDetachDialogOnDelete() {
		presenter.showDialog(containerRowMock);

		presenter.delete();

		verify(parentPanelMock).detachDialog();
	}

	@Test
	public void shouldDisplayPreviousItem() {
		when(tablePresenterMock.getPreviousItem()).thenReturn(itemMock);

		presenter.previousRow();

		verify(viewMock).displayRow(itemMock);
	}

	@Test
	public void shouldDisplayNextItem() {
		when(tablePresenterMock.getNextItem()).thenReturn(itemMock);

		presenter.nextRow();

		verify(viewMock).displayRow(itemMock);
	}

	@Test
	public void shouldReturnFalseIfNoNextRow() {
		when(tablePresenterMock.getNextItem()).thenReturn(null);

		assertEquals(presenter.nextRow(), false);
	}

	@Test
	public void shouldReturnFalseIfNoPreviousRow() {
		when(tablePresenterMock.getPreviousItem()).thenReturn(null);

		assertEquals(presenter.previousRow(), false);
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

		when(tablePresenterMock.getCurrentMode()).thenReturn(Mode.EDIT);
		when(tableDoubleClickEventMock.getRow()).thenReturn(containerRowMock);
		presenter.onDoubleClick(tableDoubleClickEventMock);

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

		when(tablePresenterMock.getCurrentMode()).thenReturn(Mode.EDIT);
		when(tableDoubleClickEventMock.getRow()).thenReturn(containerRowMock);
		presenter.onDoubleClick(tableDoubleClickEventMock);

		when(containerMock.isCLob("B")).thenReturn(true);

		List<String> modifiedFieldNames = presenter.getModifiedFieldNames();
		Map<String, Object> modifiedFields = presenter
				.getModifiedFieldValues(modifiedFieldNames);

		assertThat(modifiedFields, is(singletonMap("B", null)));
	}
}