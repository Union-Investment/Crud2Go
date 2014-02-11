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

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DisplayMode;
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
		TableView.Presenter, ShowEventHandler<Tab>, Table.Presenter,
		SelectionEventHandler, ModeChangeEventHandler<Table, Mode> {

	private static final long serialVersionUID = 2L;

	private static final Logger LOG = LoggerFactory
			.getLogger(TablePresenter.class);

	private DataContainer container;
	private boolean isInitializeView = false;

	private Set<String> generatedColumIds = new HashSet<String>();

	private boolean ignoreSelectionEvent = false;

	/**
	 * @param view
	 *            das Anzeigeobjekt
	 * @param model
	 *            Tabellen-Modell-Klasse
	 * 
	 * 
	 */
	@SuppressWarnings("serial")
	public TablePresenter(TableView view, Table model) {
		super(view, model);
		container = getModel().getContainer();
		getModel().addSelectionEventHandler(this);
		getModel().addModeChangeEventHandler(this);
		getModel().addDisplayModeChangeEventHandler(
				new ModeChangeEventHandler<Table, Table.DisplayMode>() {
					@Override
					public void onModeChange(
							ModeChangeEvent<Table, DisplayMode> event) {
						handleDisplayModeChange(event.getMode());
					}
				});
	}

	protected void handleDisplayModeChange(DisplayMode mode) {
		if (mode == DisplayMode.TABLE) {
			revertChanges();
			updateViewViewMode(getModel().getMode());
		}
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

	@Override
	public boolean isRowDeletable(Object itemId) {
		ContainerRowId containerRowId = container.convertInternalRowId(itemId);
		return getModel().isRowDeletable(containerRowId);
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

	@Override
	public boolean isCSVExport() {
		return (getModel().isExport() && getModel().getExportType().equals(
				"csv"));
	}

	@Override
	public void onShow(ShowEvent<Tab> event) {
		if (!isInitializeView) {
			initializeView();
		}
	}

	@Override
	public void onModeChange(ModeChangeEvent<Table, Mode> event) {
		// TODO optimize: only change something if in DisplayMode.TABLE
		if (getModel().getDisplayMode() == DisplayMode.TABLE) {
			updateViewViewMode(event.getMode());
		}
	}

	private void updateViewViewMode(Mode mode) {
		if (mode == Mode.VIEW) {
			getView().switchToViewMode();
		} else {
			getView().switchToEditMode();
		}
	}

	@Override
	public void changeSelection(Set<Object> selection) {
		try {
			ignoreSelectionEvent = true;

			Set<ContainerRowId> selectionIds = convertItemIdsToContainerRowIds(selection);
			getModel().changeSelection(selectionIds);

		} finally {
			ignoreSelectionEvent = false;
		}
	}

	private Set<ContainerRowId> convertItemIdsToContainerRowIds(
			Set<Object> selection) {
		Set<ContainerRowId> selectionIds = new LinkedHashSet<ContainerRowId>();
		for (Object s : selection) {
			if (s != null) {
				ContainerRowId rowId = container.convertInternalRowId(s);
				selectionIds.add(rowId);
			}
		}
		return selectionIds;
	}

	@Override
	public void onSelectionChange(SelectionEvent event) {
		if (!ignoreSelectionEvent) {
			Set<Object> selection = convertContainerRowIdsToItemIds(event
					.getSelection());
			getView().selectionUpdatedExternally(selection);
		}
	}

	private Set<Object> convertContainerRowIdsToItemIds(
			Set<ContainerRowId> selection) {
		HashSet<Object> results = new HashSet<Object>(selection.size() * 2);
		for (ContainerRowId id : selection) {
			results.add(id.getInternalId());
		}
		return results;
	}

	@Override
	public void doubleClick(Item item) {
		ContainerRow row = container.convertItemToRow(item, false, true);
		getModel().doubleClick(row);
	}

	@Override
	public void rowChange(Item containerRow, Map<String, Object> changedValues) {
		getModel().rowChange(containerRow, changedValues);

	}

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
		getModel().changeMode(mode);
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
		return getModel().getMode();
	}

	@Override
	public void openRowEditingForm() {
		getModel().changeDisplayMode(DisplayMode.FORM);
	}

	/**
	 * Verwirft die Änderungen auf der Tabelle.
	 */
	public void revertChanges() {
		getView().onRevertChanges();
	}

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

	public void addGeneratedColumn(String columnName, String columnTitle,
			ColumnGenerator columnGenerator) {
		LOG.debug("Adding column [" + columnName + "].");
		getView().addGeneratedColumn(columnName, columnTitle, columnGenerator);
		generatedColumIds.add(columnName);
		LOG.debug("Column [" + columnName + "] added.");
	}

	@Override
	public void removeGeneratedColumn(String columnId) {
		LOG.debug("Removing column [" + columnId + "].");
		getView().removeGeneratedColumn(columnId);
		generatedColumIds.remove(columnId);
		LOG.debug("Column [" + columnId + "] removed.");
	}

	@Override
	public void download(Download download) {
		getView().download(download);
	}

	@Override
	public void renderOnce(DynamicColumnChanges changes) {
		boolean tableContentRefreshWasEnabled = getView()
				.disableContentRefreshing();
		changes.apply();
		if (tableContentRefreshWasEnabled) {
			getView().enableContentRefreshing(true);
		}
	}

	@Override
	public boolean hasGeneratedColumn(String id) {
		return generatedColumIds.contains(id);
	}

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

	@Override
	public List<String> getVisibleColumns() {
		return getView().getVisibleColumns();
	}

	@Override
	public void setVisibleColumns(List<String> visibleColumns) {
		getView().setVisibleColumns(visibleColumns);
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
