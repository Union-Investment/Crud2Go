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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerClob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.RowEditingFormView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.CrudFieldFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.ValidationFieldFactoryWrapper;

/**
 * Presenter für das Formular zum editieren einer Tabellenzeile.
 * 
 * 
 * @author siva.selvarajah
 */
public class RowEditingFormPresenter extends DialogPresenter implements
		TableDoubleClickEventHandler, RowEditingFormView.Presenter {

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
		CrudFieldFactory fac = new CrudFieldFactory(null, table);
		ValidationFieldFactoryWrapper validationWrapper = new ValidationFieldFactoryWrapper(
				table.getContainer(), fac, table.getColumns());

		getView().setFormFieldFactory(validationWrapper);

		table.addTableDoubleClickEventHandler(this);
		backButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				tablePresenter.revertChanges();
			}
		});
	}

	@Override
	public void addClobFields(Item item) {
		ContainerRow containerRow = table.getContainer().convertItemToRow(item,
				false, true);
		for (String fieldName : getVisibleFields()) {
			if (container.isCLob(fieldName)) {
				ContainerClob clob = container.getCLob(containerRow.getId(),
						fieldName);
				boolean isReadOnly = isReadOnly(containerRow, fieldName);

				Property property = clob.getPropertyValue();
				property.setReadOnly(isReadOnly);
				getView().addClobField(table.getColumns().get(fieldName),
						property);
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
				|| isRowReadonly(containerRow);

		return isReadOnly;
	}

	private boolean isRowReadonly(ContainerRow containerRow) {
		return !table.isRowEditable(containerRow);
	}

	@Override
	public void onDoubleClick(TableDoubleClickEvent event) {
		if (table.getMode() == Table.Mode.EDIT) {
			showDialog(event.getRow());
		}
	}

	/**
	 * Öffnet das Formular zum editieren der Zeile.
	 * 
	 * @param currentContainerRow
	 *            Aktuelle Zeile
	 */
	public void showDialog(ContainerRow currentContainerRow) {
		if (currentContainerRow != null) {
			this.currentContainerRow = currentContainerRow;

			parentPanel.attachDialog(dialogId);

			getView().displayRow(currentContainerRow.getInternalRow(),
					table.isRowEditable(currentContainerRow),
					tablePresenter.isDeleteable());

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
		try {
			List<String> modifiedFieldNames = getModifiedFieldNames();
			getView().getForm().commit();
			Map<String, Object> modifiedColumnNames = getModifiedFieldValues(modifiedFieldNames);

			try {
				fireRowChangeEvent(modifiedColumnNames);

				container.commit();

				parentPanel.detachDialog();

			} catch (Exception e) {
				getView().getForm().setComponentError(
						new UserError(e.getMessage()));
				throw e;
			}

		} catch (Buffered.SourceException e) {
			Throwable[] causes = e.getCauses();
			int i = 1;
			for (Throwable throwable : causes) {
				LOG.error("******* " + i + ") Error during form commit!",
						throwable);
			}

		} catch (Exception e) {
			LOG.error("Error during form commit!", e);
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

		parentPanel.detachDialog();
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
			currentContainerRow = container.getRow(nextRowId, false, true);
			table.changeSelection(singleton(nextRowId));
			getView().displayRow(currentContainerRow.getInternalRow(),
					!isRowReadonly(currentContainerRow),
					container.isDeleteable());
			return true;
		}
		return false;
	}

	@Override
	public boolean previousRow() {
		ContainerRowId previousRowId = container
				.previousRowId(currentContainerRow.getId());

		if (previousRowId != null) {
			currentContainerRow = container.getRow(previousRowId, false, true);
			table.changeSelection(singleton(previousRowId));
			getView().displayRow(currentContainerRow.getInternalRow(),
					!isRowReadonly(currentContainerRow),
					container.isDeleteable());
			return true;
		}
		return false;
	}

	@Override
	public void resetFields() {
		getView().getForm().discard();
	}

	@Override
	public void cancel() {
		parentPanel.detachDialog();
		tablePresenter.revertChanges();
	}
}
