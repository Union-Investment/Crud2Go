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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView;

/**
 * Repräsentiert eine Tabelle.
 * 
 * @author carsten.mjartan
 */
public class TablePresenter extends
		AbstractComponentPresenter<Table, TableView> implements
		TableView.Presenter, ShowEventHandler<Tab>, Table.Presenter {

	private static final long serialVersionUID = 2L;

	private static final Logger LOG = LoggerFactory
			.getLogger(TablePresenter.class);

	private DataContainer container;
	private boolean isInitializeView = false;

	private Mode currentMode = Table.Mode.VIEW;

	private RowEditingFormPresenter rowEditingFormPresenter;

	private Set<String> generatedColumIds = new HashSet<String>();

	/**
	 * @param view
	 *            das Anzeigeobjekt
	 * @param model
	 *            Tabellen-Modell-Klasse
	 * 
	 * 
	 */
	public TablePresenter(TableView view, Table model) {
		super(view, model);
		container = getModel().getContainer();
	}

	public void setRowEditingFormPresenter(
			RowEditingFormPresenter rowEditingFormPresenter) {
		this.rowEditingFormPresenter = rowEditingFormPresenter;
	}

	/**
	 * Initialisiert die View.
	 */
	void initializeView() {
		getView().initialize(this, container, getModel(),
				getModel().getPageLength(), getModel().getCacheRate());
		isInitializeView = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isReadonly()
	 */
	@Override
	public boolean isReadonly() {
		return !getModel().isEditable()
				|| (!container.isInsertable() && !container.isUpdateable() && !container
						.isDeleteable());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isInsertable()
	 */
	@Override
	public boolean isInsertable() {
		return container.isInsertable();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isUpdateable()
	 */
	@Override
	public boolean isUpdateable() {
		return container.isUpdateable();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isDeleteable()
	 */
	@Override
	public boolean isDeleteable() {
		return container.isDeleteable();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isExcelExport()
	 */
	@Override
	public boolean isExcelExport() {
		return (getModel().isExport() && getModel().getExportType().equals(
				"xls"));

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isCSVExport()
	 */
	@Override
	public boolean isCSVExport() {
		return (getModel().isExport() && getModel().getExportType().equals(
				"csv"));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler#onShow(de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent)
	 */
	@Override
	public void onShow(ShowEvent<Tab> event) {
		if (!isInitializeView) {
			initializeView();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#selectionChange(java.util.Set)
	 */
	@Override
	public void selectionChange(Set<Object> selection) {
		Set<ContainerRowId> selectionIds = new LinkedHashSet<ContainerRowId>();
		for (Object s : selection) {
			if (s != null) {
				ContainerRowId rowId = container.convertInternalRowId(s);
				selectionIds.add(rowId);
			}
		}
		getModel().selectionChange(selectionIds);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#doubleClick(com.vaadin.data.Item)
	 */
	@Override
	public void doubleClick(Item item) {
		ContainerRow row = container.convertItemToRow(item, false, true);
		getModel().doubleClick(row);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#rowChange(com.vaadin.data.Item,
	 *      java.util.Map)
	 */
	@Override
	public void rowChange(Item containerRow, Map<String, Object> changedValues) {
		getModel().rowChange(containerRow, changedValues);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#doInitialize()
	 */
	@Override
	public void doInitialize() {
		getModel().doInitialize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#callClosure(de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction)
	 */
	@Override
	public void callClosure(TableAction action) {
		action.execute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#switchMode(de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode)
	 */
	@Override
	public void switchMode(Mode mode) {
		this.currentMode = mode;
		getModel().changeMode(currentMode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#isFormEditEnabled()
	 */
	@Override
	public boolean isFormEditEnabled() {
		return getModel().isFormEditEnabled();
	}

	public Mode getCurrentMode() {
		return this.currentMode;
	}

	public Item getNextItem() {
		return getView().getNextItem();
	}

	public Item getPreviousItem() {
		return getView().getPreviousItem();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView.Presenter#openRowEditingForm()
	 */
	@Override
	public void openRowEditingForm() {
		if (rowEditingFormPresenter != null) {
			Set<ContainerRowId> selection = getModel().getSelection();
			if (selection.size() > 0) {
				ContainerRowId containerRowId = selection.iterator().next();

				ContainerRow containerRow = getModel().getContainer().getRow(
						containerRowId, false, false);

				rowEditingFormPresenter.showDialog(containerRow);

			}
		}
	}

	/**
	 * Verwirft die Änderungen auf der Tabelle.
	 */
	public void revertChanges() {
		getView().onRevertChanges();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#createNewRow(java.util.Map)
	 */
	@Override
	public ContainerRow createNewRow(Map<String, Object> values) {
		Object itemId = getView().addItemToTable();
		ContainerRow newRow = container.getRowByInternalRowId(itemId, false,
				false);
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			newRow.setValue(entry.getKey(), entry.getValue());
		}
		getView().selectItemForEditing(itemId, true);
		return newRow;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#addGeneratedColumn(java.lang.String,
	 *      com.vaadin.ui.Table.ColumnGenerator)
	 */
	@Override
	public void addGeneratedColumn(String columnName, String columnTitle,
			ColumnGenerator columnGenerator) {
		LOG.debug("Adding column [" + columnName + "].");
		getView().addGeneratedColumn(columnName, columnTitle, columnGenerator);
		generatedColumIds.add(columnName);
		LOG.debug("Column [" + columnName + "] added.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#removeGeneratedColumn(java.lang.String)
	 */
	@Override
	public void removeGeneratedColumn(String columnId) {
		LOG.debug("Removing column [" + columnId + "].");
		getView().removeGeneratedColumn(columnId);
		generatedColumIds.remove(columnId);
		getView().recalculateColumnWidths();
		LOG.debug("Column [" + columnId + "] removed.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#renderOnce(de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges)
	 */
	@Override
	public void renderOnce(DynamicColumnChanges changes) {
		boolean tableContentRefreshWasEnabled = getView()
				.disableContentRefreshing();
		changes.apply();
		if (tableContentRefreshWasEnabled) {
			getView().enableContentRefreshing(true);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#hasGeneratedColumn(java.lang.String)
	 */
	@Override
	public boolean hasGeneratedColumn(String id) {
		return generatedColumIds.contains(id);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#clearAllGeneratedColumns()
	 */
	@Override
	public void clearAllGeneratedColumns() {
		renderOnce(new DynamicColumnChanges() {
			@Override
			public void apply() {
				for (String columnId : generatedColumIds) {
					LOG.debug("Removing column [" + columnId + "].");
					getView().removeGeneratedColumn(columnId);
					LOG.debug("Column [" + columnId + "] removed.");
				}
				generatedColumIds.clear();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#getVisibleColumns()
	 */
	@Override
	public List<String> getVisibleColumns() {
		return getView().getVisibleColumns();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Presenter#setVisibleColumns(java.util.List)
	 */
	@Override
	public void setVisibleColumns(List<String> visibleColumns) {
		getView().setVisibleColumns(visibleColumns);
		getView().recalculateColumnWidths();
	}

	/**
	 * Setzt die Sichtbarkeit der TableAction (Button) mit der ID {@code id}.
	 * 
	 * @param id
	 *            die ID der TableAction, so wie sie im Attribut <tt>id</tt> des
	 *            Tags <tt>action</tt> definiert ist.
	 * 
	 * @param visible
	 *            {@code true}: sichtbar, {@code false}: unsichtbar,
	 */
	public void setTableActionVisibility(String id, boolean visible) {
		getView().setTableActionVisibility(id, visible);
	}
}
