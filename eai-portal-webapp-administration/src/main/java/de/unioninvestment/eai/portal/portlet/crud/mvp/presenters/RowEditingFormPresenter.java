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

import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DisplayMode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.RowEditingFormView;

/**
 * Presenter für das Formular zum editieren einer Tabellenzeile.
 * 
 * 
 * @author siva.selvarajah
 */
@SuppressWarnings("serial")
public class RowEditingFormPresenter extends DialogPresenter implements
		RowEditingFormView.Presenter {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory
			.getLogger(RowEditingFormView.class);

	private Panel parentPanel;
	private final String dialogId;
	private final Table table;
	private final TablePresenter tablePresenter;
	private ContainerRow currentContainerRow;
	private DataContainer container;

	/**
	 * Konstruktor.
	 * 
	 * @param view
	 *            View zum anzeigen des Forluars
	 * @param model
	 *            Formular-Dialog-Modelklasse
	 * @param parentPanel
	 *            Übergeordnetes Panle
	 * @param dialogId
	 *            Eindeutiger Id des Dialogs
	 * @param table
	 *            Tabellenmodel
	 * @param tablePresenter
	 *            Presenter der Tabelle
	 */
	public RowEditingFormPresenter(PanelContentView view, Dialog model,
			Panel parentPanel, String dialogId, Table table,
			TablePresenter tablePresenter) {

		super(view, model);
		this.parentPanel = parentPanel;
		this.dialogId = dialogId;
		this.table = table;
		this.tablePresenter = tablePresenter;
		this.container = table.getContainer();
		initialize();
	}

	@Override
	protected Button addBackButton(String caption) {
		Button button = super.addBackButton(caption);
		this.backButton = button;
		return button;
	}

	private void initialize() {

		getView().initialize(this, table);
		table.addModeChangeEventHandler(new ModeChangeEventHandler<Table, Table.Mode>() {
			@Override
			public void onModeChange(ModeChangeEvent<Table, Mode> event) {
				handleModeChange(event.getMode());
			}
		});

		table.addDisplayModeChangeEventHandler(new ModeChangeEventHandler<Table, Table.DisplayMode>() {
			@Override
			public void onModeChange(ModeChangeEvent<Table, DisplayMode> event) {
				handleDisplayModeChange(event.getSource().getDisplayMode());
			}
		});
		table.addSelectionEventHandler(new SelectionEventHandler() {
			@Override
			public void onSelectionChange(SelectionEvent selectionEvent) {
				handleSelectionChange();
			}
		});
		backButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				table.changeDisplayMode(DisplayMode.TABLE);
			}
		});
	}

	protected void handleDisplayModeChange(DisplayMode displayMode) {
		if (displayMode == DisplayMode.FORM) {
			parentPanel.attachDialog(dialogId);
			updateButtonsForMode(table.getMode());
			displayCurrentTableSelection();
		} else {
			if (isAttached()) {
				parentPanel.detachDialog();
			}
		}
	}

	protected void handleSelectionChange() {
		if (table.getDisplayMode() == DisplayMode.FORM) {
			displayCurrentTableSelection();
		}
	}

	protected void handleModeChange(Mode mode) {
		if (table.getDisplayMode() == DisplayMode.FORM) {
			updateButtonsForMode(mode);
			displayCurrentRow();
		}
	}

	private void displayCurrentTableSelection() {
		Set<ContainerRowId> selection = table.getSelection();
		if (selection.size() == 1) {
			ContainerRow selectedRow = table.getContainer().getRow(
					selection.iterator().next(), false, true);
			displayRow(selectedRow);
		}
	}

	private void updateButtonsForMode(Mode mode) {
		if (mode == Mode.VIEW) {
			getView().updateButtonsForViewMode();
		} else {
			getView().updateButtonsForEditMode();
		}
	}

	@Override
	public void changeMode() {
		boolean success = trySave();
		if (success) {
			table.changeMode();
		}
	}

	@Override
	public void addClobFields(Item item) {
		ContainerRow containerRow = table.getContainer().convertItemToRow(item,
				false, true);
		for (String fieldName : getVisibleFields()) {
			if (container.isCLob(fieldName)) {
				boolean isReadOnly = isReadOnly(containerRow, fieldName);
				getView().addClobField(table.getColumns().get(fieldName),
						isReadOnly);

			} else if (container.isBLob(fieldName)) {
				ContainerBlob blob = container.getBLob(containerRow.getId(),
						fieldName);
				boolean isReadOnly = isReadOnly(containerRow, fieldName);
				getView().addBlobField(table.getColumns().get(fieldName), blob,
						isReadOnly);
			}
		}
	}

	private boolean isReadOnly(ContainerRow containerRow, String fieldName) {
		boolean isReadOnly = containerRow.getFields().get(fieldName)
				.isReadonly()
				|| !isTableRowEditable(containerRow);

		return isReadOnly;
	}

	private boolean isTableRowEditable(ContainerRow containerRow) {
		return table.getMode() == Mode.EDIT
				&& table.isRowEditable(currentContainerRow);
	}

	private boolean isTableRowDeletable(ContainerRow containerRow) {
		return container.isDeleteable() && table.getMode() == Mode.EDIT
				&& table.isRowDeletable(containerRow.getId());
	}

	/**
	 * Öffnet das Formular zum editieren der Zeile.
	 * 
	 * @param currentContainerRow
	 *            Anzuzeigende Zeile
	 */
	public void displayRow(ContainerRow currentContainerRow) {
		if (currentContainerRow != null) {
			this.currentContainerRow = currentContainerRow;
			displayCurrentRow();
		}
	}

	private void displayCurrentRow() {
		try {
			getView().hideFormError();
			getView().displayRow(currentContainerRow,
					isTableRowEditable(currentContainerRow),
					isTableRowDeletable(currentContainerRow));
		} catch (RuntimeException e) {
			table.changeDisplayMode(DisplayMode.TABLE);
			throw e;
		}
	}

	@Override
	public RowEditingFormView getView() {
		if (super.getView() instanceof RowEditingFormView) {
			return (RowEditingFormView) super.getView();
		} else {
			throw new IllegalStateException("View ist kein "
					+ RowEditingFormView.class.getSimpleName());
		}
	}


	@Override
	public void save() {
		boolean success = trySave();
		if (success) {
			table.changeDisplayMode(DisplayMode.TABLE);
		}
	}
	
	private boolean trySave() {
		try {
			List<String> modifiedFieldNames = getModifiedFieldNames();
			Map<String, Object> modifiedColumnNames = getModifiedFieldValues(modifiedFieldNames);
			getView().commit();

			try {
				fireRowChangeEvent(modifiedColumnNames);

				container.commit();

				return true;

			} catch (Exception e) {
				LOG.error("Error during form commit: {}", e.getMessage());
				getView().showFormError(e.getMessage());
				return false;
			}

		} catch (Buffered.SourceException e) {
			String rootCauseMessage = ExceptionUtils.getRootCause(e)
					.getMessage();
			LOG.error("Error during form commit: {}", rootCauseMessage);
			getView().showFormError(rootCauseMessage);
			return false;

		} catch (CommitException e) {
			String rootCauseMessage = ExceptionUtils.getRootCause(e)
					.getMessage();
			LOG.error("Error during form commit: {}", rootCauseMessage);
			getView().showFormError(rootCauseMessage);
			return false;

		} catch (Exception e) {
			LOG.error("Error during form commit!", e);
			getView().showFormError(ExceptionUtils.getRootCause(e).toString());
			return false;
		}

	}

	private void fireRowChangeEvent(Map<String, Object> modifiedColumnNames) {
		if (modifiedColumnNames.size() > 0) {
			tablePresenter.rowChange(currentContainerRow.getInternalRow(),
					modifiedColumnNames);
		}
	}

	/**
	 * liefert eine Zuordnung zwischen Namen(key) der Felder mit Änderungen und
	 * neuen Werten(value).
	 * 
	 * @param modifiedFieldNames
	 *            Liste mit Namen der Felder, die geänderte Werte beinhalten.
	 */
	public Map<String, Object> getModifiedFieldValues(
			List<String> modifiedFieldNames) {
		Map<String, Object> fields = new HashMap<String, Object>();

		for (String fieldName : modifiedFieldNames) {
			if (container.isCLob(fieldName)) {
				fields.put(fieldName, null);
			} else {
				fields.put(fieldName,
						currentContainerRow.getFields().get(fieldName)
								.getValue());
			}
		}

		return fields;
	}

	/**
	 * liefert eine Liste mit Namen der Felder, in den Änderungen vorgenommen
	 * worden sind.
	 */
	public List<String> getModifiedFieldNames() {
		List<String> fields = new ArrayList<String>();

		for (String fieldName : getVisibleFields()) {
			if (getView().isFieldModifed(fieldName)) {
				if (container.isCLob(fieldName)) {
					fields.add(fieldName);
				} else {
					fields.add(fieldName);
				}
			}
		}
		return fields;
	}

	@Override
	public List<String> getVisibleFields() {
		return table.getColumns().getVisibleNamesForForm();
	}

	@Override
	public void delete() {
		ContainerRowId containerRowId = currentContainerRow.getId();
		container.removeRows(singleton(containerRowId));

		table.changeDisplayMode(DisplayMode.TABLE);
	}

	@Override
	public boolean hasPreviousRow() {
		return container.previousRowId(currentContainerRow.getId()) != null;
	}

	@Override
	public boolean hasNextRow() {
		return container.nextRowId(currentContainerRow.getId()) != null;
	}

	@Override
	public boolean nextRow() {
		ContainerRowId nextRowId = container.nextRowId(currentContainerRow
				.getId());
		if (nextRowId != null) {
			switchToRow(nextRowId);
			return true;
		}
		return false;
	}

	@Override
	public boolean previousRow() {
		ContainerRowId previousRowId = container
				.previousRowId(currentContainerRow.getId());

		if (previousRowId != null) {
			switchToRow(previousRowId);
			return true;
		}
		return false;
	}

	private void switchToRow(ContainerRowId otherRowId) {
		table.changeSelection(singleton(otherRowId));
	}

	@Override
	public void resetFields() {
		getView().hideFormError();
		getView().discard();
	}

	@Override
	public void cancel() {
		table.changeDisplayMode(DisplayMode.TABLE);
	}
}
