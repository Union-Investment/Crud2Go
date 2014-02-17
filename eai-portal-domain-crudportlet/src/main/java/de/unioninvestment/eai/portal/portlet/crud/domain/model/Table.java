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

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import groovy.lang.Closure;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.config.TableConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.GeneratedColumnsDataStreamWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InitializeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InitializeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ModeChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.RowChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.SelectionEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.TableDoubleClickEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.EmptyColumnGenerator;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Modell-Klasse für Tabellen.
 * 
 * @author max.hartmann
 * @author Jan Malcomess (codecentric AG)
 * 
 */
public class Table extends Component implements Component.ExpandableComponent,
		Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Permission Actions.
	 */
	public enum Permission {
		BUILD, EDIT
	}

	/**
	 * Renderer-Klasse für Zeilen-Styles.
	 */
	public static interface RowStyleRenderer {
		/**
		 * @param rowId
		 *            ZeilenId
		 * @return den Style der Zeile.
		 */
		public String getStyle(ContainerRowId rowId);
	}

	/**
	 * Renderer-Klasse für Spalten-Styles.
	 */
	public static interface ColumnStyleRenderer {
		/**
		 * @param rowId
		 *            ZeilenId
		 * @return den Style der Spalte.
		 */
		public String getStyle(ContainerRowId rowId);
	}

	/**
	 * Definiert die Erwartungen des Models an den Presenter.
	 * 
	 * @author eugen.melnichuk
	 */
	public interface Presenter {

		/**
		 * Fügt der Tabelle dynamisch eine neue Spalte hinzu.
		 * 
		 * @param columnName
		 *            die ID der Spalte
		 * @param columnTitle
		 *            die Überschrift der Spalte
		 * @param columnGenerator
		 *            der Generator, der den Inhalt der einzelnen Zellen
		 *            generiert
		 */
		void addGeneratedColumn(String columnName, String columnTitle,
				ColumnGenerator columnGenerator);

		/**
		 * Entfernt eine zuvor per
		 * {@link #addGeneratedColumn(String, ColumnGenerator)} hinzugefügte
		 * Spalte aus der Tabelle.
		 * 
		 * @param columnName
		 *            der Name der Spalte, die bei
		 *            {@link #addGeneratedColumn(String, ColumnGenerator)}
		 *            verwendet wurde
		 */
		void removeGeneratedColumn(String columnName);

		/**
		 * Führt Änderungen an der Tabelle zu wie beispielsweise dynamisches
		 * Hinzufügen und Entfernen von Spalten. Das Content-Refreshing
		 * (Rerendering) wird vor dem Ausführen von {@code change} ausgeschaltet
		 * und (falls es vorher eingeschaltet war) danach wieder eingeschaltet.
		 * 
		 * @param changes
		 *            die durchzuführenden Änderungen
		 */
		public void renderOnce(DynamicColumnChanges changes);

		/**
		 * @param columnName
		 *            der Spaltenname
		 * @return {@code true} wenn die Tabelle eine per
		 *         {@link #addGeneratedColumn(String, ColumnGenerator)}
		 *         hinzugefügte Spalte hat
		 */
		public boolean hasGeneratedColumn(String columnName);

		/**
		 * Entfernt alle per
		 * {@link #addGeneratedColumn(String, ColumnGenerator)} hinzugefügten
		 * Spalten.
		 */
		public void clearAllGeneratedColumns();

		/**
		 * @return die Namen der sichtbaren Spalten in der Reihenfolge, in der
		 *         sie angezeigt werden.
		 * @see #setVisibleColumns(List)
		 */
		public List<String> getVisibleColumns();

		/**
		 * Setzt gleichzeitig die Reihenfolge und die Sichtbarkeit der Spalten.
		 * Alle Spalten, deren ID in der übergebenen Liste
		 * {@code visibleColumns} enthalten sind, werden angezeigt, alle anderen
		 * nicht. Das bezieht sich nicht nur auf per {@code addGeneratedColumn}
		 * hinzugefügten Spalten sondern auf alle Spalten inkl. der fest in der
		 * Konfiguration deklarierten. Die Spalten werden in der Reihenfolge
		 * angeordnet, wie sie in {@code visibleColumns} stehen.
		 * 
		 * @param visibleColumns
		 *            die Liste der ColumnNames. Für eine per
		 *            {@link #addGeneratedColumn(String, String, Closure)}
		 *            hinzugefügte Spalten ist das der String, der dabei als
		 *            Argument {@code columnName} verwendet wurde, für eine
		 *            deklarierte Spalte das Attribut {@code name}
		 */
		public void setVisibleColumns(List<String> visibleColumns);

		/**
		 * Setzt die Sichtbarkeit der TableAction (Button) mit der ID {@code id}
		 * .
		 * 
		 * @param id
		 *            die ID der TableAction, so wie sie im Attribut <tt>id</tt>
		 *            des Tags <tt>action</tt> definiert ist.
		 * 
		 * @param visible
		 *            {@code true}: sichtbar, {@code false}: unsichtbar,
		 */
		public void setTableActionVisibility(String id, boolean visible);

		/**
		 * Fügt der Tabelle eine neue Zeile hinzu. Werte der Zeile können über
		 * {@code values} vorbelegt werden.
		 * 
		 * @param values
		 *            die Werte, die in der neuen Zeile bereits gesetzt sein
		 *            sollen. Die Keys der Map entsprechen den Column Names, die
		 *            dazugehörigen Values sind die Werte in der Zeile.
		 */
		public ContainerRow createNewRow(Map<String, Object> values);

		void download(Download download);
	}

	/**
	 * Ein Wrapper für eine Reihe von
	 * {@link Table.Presenter#addGeneratedColumn(String, com.vaadin.ui.Table.ColumnGenerator)}
	 * and {@link Table.Presenter#removeGeneratedColumn(String)} Anweisungen.
	 * 
	 * @author Bastian Krol
	 */
	public interface DynamicColumnChanges {

		/**
		 * Wendet die Änderungen an. {@code addGeneratedColum} und
		 * {@code removeGeneratedColumn} Anweisungen sollten in dieser Methode
		 * ausgeführt werden.
		 */
		public void apply();
	}

	/**
	 * Ansichtsmodus der Tabellen-Komponente.
	 */
	public enum Mode {
		VIEW, EDIT
	};

	public enum DisplayMode {
		TABLE, FORM
	}

	private final TableConfig config;
	private final TableColumns columns;
	private DataContainer container;
	private List<TableAction> actions;

	private RowStyleRenderer rowStyleRenderer;

	private EventRouter<SelectionEventHandler, SelectionEvent> selectionEventRouter = new EventRouter<SelectionEventHandler, SelectionEvent>();
	private EventRouter<TableDoubleClickEventHandler, TableDoubleClickEvent> doubleClickEventRouter = new EventRouter<TableDoubleClickEventHandler, TableDoubleClickEvent>();
	private EventRouter<ModeChangeEventHandler<Table, Mode>, ModeChangeEvent<Table, Mode>> editModeChangeEventRouter = new EventRouter<ModeChangeEventHandler<Table, Mode>, ModeChangeEvent<Table, Mode>>();
	private EventRouter<ModeChangeEventHandler<Table, DisplayMode>, ModeChangeEvent<Table, DisplayMode>> displayModeChangeEventRouter = new EventRouter<ModeChangeEventHandler<Table, DisplayMode>, ModeChangeEvent<Table, DisplayMode>>();
	private EventRouter<RowChangeEventHandler, RowChangeEvent> rowChangeEventRouter = new EventRouter<RowChangeEventHandler, RowChangeEvent>();
	private EventRouter<InitializeEventHandler<Table>, InitializeEvent<Table>> initializeEventRouter = new EventRouter<InitializeEventHandler<Table>, InitializeEvent<Table>>();

	Mode mode;
	DisplayMode displayMode = DisplayMode.TABLE;

	private Set<ContainerRowId> selection = new LinkedHashSet<ContainerRowId>();
	private final boolean editable;
	private final boolean directEdit;

	private RowEditableChecker editableChecker;
	private RowDeletableChecker deletableChecker;

	private Table.Presenter presenter;

	/**
	 * Konstruktor mit Parametern.
	 * 
	 * @param config
	 *            Konfiguration der Tabelle
	 * @param tableColumns
	 *            Tabellen-Spalten
	 * @param editable
	 *            Ob die Tabelle editierbar ist
	 */
	public Table(TableConfig config, TableColumns tableColumns,
			boolean editable, boolean directEdit) {
		this.config = config;
		this.columns = tableColumns;
		this.editable = editable;
		this.directEdit = directEdit;
		this.mode = directEdit && editable ? Mode.EDIT : Mode.VIEW;
	}

	/**
	 * Setzt den Presenter im Modell für den Fall, dass Ereignisse im Modell
	 * Auswirkungen auf die Präsentation der Tabelle haben. Der Presenter darf
	 * in der Table nur über das Interface {@link Table.Presenter} angesprochen
	 * werden.
	 * 
	 * @param presenter
	 *            der Presenter
	 */
	public void setPresenter(Table.Presenter presenter) {
		this.presenter = presenter;
	}

	public String getId() {
		return config.getId();
	}

	/**
	 * @since 1.45
	 */
	public String getWidth() {
		return this.config.getWidth();
	}

	/**
	 * @since 1.45
	 */
	public String getHeight() {
		return this.config.getHeight();
	}

	public RowStyleRenderer getRowStyleRenderer() {
		return rowStyleRenderer;
	}

	public void setRowStyleRenderer(RowStyleRenderer rowStyleRenderer) {
		this.rowStyleRenderer = rowStyleRenderer;
	}

	public void setRowEditableChecker(RowEditableChecker checker) {
		this.editableChecker = checker;
	}

	public void setRowDeletableChecker(RowDeletableChecker checker) {
		this.deletableChecker = checker;
	}

	/**
	 * Holt für jedes Column den jeweiligen ColumnStyleRenderer.
	 * 
	 * @param columnName
	 *            Tabellenspaltenname
	 * @return ColumnStyleRenderer
	 */
	public ColumnStyleRenderer getColumnStyleRenderer(String columnName) {
		if (columns != null) {
			if (columns.contains(columnName)) {
				TableColumn tableColumn = columns.get(columnName);
				if (tableColumn != null) {
					return tableColumn.getColumnStyleRenderer();
				}
			}
		}
		return null;
	}

	/**
	 * Liefert die Zeilenhöhe zurück.
	 * 
	 * @return Zeilenhöhe
	 */
	public Integer getRowHeight() {
		if (config.getColumns() == null) {
			// no further configuration of columns (multiline), so always use
			// default height
			return null;
		} else {
			return config.getRowHeight();
		}
	}

	public boolean isSortingEnabled() {
		return config.isSortable();
	}

	void setContainer(DataContainer container) {
		this.container = container;
	}

	public DataContainer getContainer() {
		return container;
	}

	public TableColumns getColumns() {
		return columns;
	}

	public List<TableAction> getActions() {
		return Collections.unmodifiableList(actions);
	}

	void setActions(List<TableAction> buildTableActions) {
		this.actions = buildTableActions;
	}

	/**
	 * @param selection
	 *            die im View geänderte Selektion.
	 */
	public void changeSelection(Set<ContainerRowId> selection) {
		LinkedHashSet<ContainerRowId> newSelection = new LinkedHashSet<ContainerRowId>(
				selection);
		if (!newSelection.equals(this.selection)) {
			this.selection = newSelection;
			selectionEventRouter.fireEvent(new SelectionEvent(this,
					unmodifiableSet(this.selection)));
		}
	}

	/**
	 * Verarbeitet den Doppelklick auf eine Zeile.
	 * 
	 * @param row
	 *            Zeile
	 */
	public void doubleClick(ContainerRow row) {
		if (config.isEditForm()
				&& (doubleClickEventRouter.getRegisteredHandlerSize() == 0 || mode == Mode.EDIT)) {

			changeDisplayMode(DisplayMode.FORM);
			changeSelection(singleton(row.getId()));
		} else {
			doubleClickEventRouter.fireEvent(new TableDoubleClickEvent(this,
					row));
		}
	}

	public boolean isDirectEdit() {
		return directEdit;
	}

	public void changeMode() {
		Mode newMode = mode == Mode.VIEW ? Mode.EDIT : Mode.VIEW;
		changeMode(newMode);
	}

	/**
	 * @param mode
	 *            der im View gesetzte Modus
	 */
	public void changeMode(Mode mode) {
		if (directEdit) {
			throw new IllegalStateException(
					"Cannot change mode if direct editing is enabled");
		} else if (this.mode != mode) {
			this.mode = mode;
			editModeChangeEventRouter
					.fireEvent(new ModeChangeEvent<Table, Mode>(this, mode));
		}
	}

	public void changeDisplayMode() {
		DisplayMode newDisplayMode = displayMode == DisplayMode.TABLE ? DisplayMode.FORM
				: DisplayMode.TABLE;
		changeDisplayMode(newDisplayMode);
	}

	/**
	 * @param mode
	 *            der im View gesetzte Modus
	 */
	public void changeDisplayMode(DisplayMode newDisplayMode) {
		if (newDisplayMode == DisplayMode.FORM && !isFormEditEnabled()) {
			throw new IllegalStateException(
					"Cannot change displayMode if form editing is disabled");
		} else if (this.displayMode != newDisplayMode) {
			this.displayMode = newDisplayMode;
			displayModeChangeEventRouter
					.fireEvent(new ModeChangeEvent<Table, DisplayMode>(this,
							newDisplayMode));
		}
	}

	/**
	 * Fügt einen Event Handler für die Änderung des Anzeigemodus hinzu.
	 * 
	 * @param handler
	 *            Event Handler
	 */
	public void addDisplayModeChangeEventHandler(
			ModeChangeEventHandler<Table, DisplayMode> handler) {
		displayModeChangeEventRouter.addHandler(handler);
	}

	/**
	 * Fügt ein SelectionEventHandler hinzu.
	 * 
	 * @param selectionEventHandler
	 *            EventHandler
	 */
	public void addSelectionEventHandler(
			SelectionEventHandler selectionEventHandler) {
		selectionEventRouter.addHandler(selectionEventHandler);
	}

	/**
	 * Fügt einen TableDoubleClickEventHandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	public void addDoubleClickEventHandler(TableDoubleClickEventHandler handler) {
		doubleClickEventRouter.addHandler(handler);
	}

	public void removeDoubleClickEventHandler(
			TableDoubleClickEventHandler doubleClickEventHandler) {
		doubleClickEventRouter.removeHandler(doubleClickEventHandler);
	}

	/**
	 * Fügt ein ModeChangeEventHandler hinzu.
	 * 
	 * @param onEditEventHandler
	 *            EventHandler
	 */
	public void addModeChangeEventHandler(
			ModeChangeEventHandler<Table, Mode> onEditEventHandler) {
		editModeChangeEventRouter.addHandler(onEditEventHandler);
	}

	/**
	 * Wird durch die Table-UI-Komponente nach Änderung einer Zeile im Container
	 * aufgerufen.
	 * 
	 * @param item
	 *            die gerade im UI geänderte Zeile
	 * @param changedValues
	 *            Geänderten Werte als Map
	 */
	public void rowChange(Item item, Map<String, Object> changedValues) {
		ContainerRow containerRow = container.convertItemToRow(item, false,
				false);

		rowChangeEventRouter.fireEvent(new RowChangeEvent(containerRow,
				changedValues));

	}

	public void doInitialize() {
		initializeEventRouter.fireEvent(new InitializeEvent<Table>(this));
	}

	public Set<ContainerRowId> getSelection() {
		return unmodifiableSet(selection);
	}

	/**
	 * Prüft, ob die Tabelle generell editierbar oder schreibgeschützt ist. Dies
	 * sagt allerdings noch nichts darüber aus, ob eine bestimmte Zeile
	 * editierbar ist, dazu muss {@link #isRowEditable(ContainerRow)} aufgerufen
	 * werden.
	 * 
	 * @return ob die Tabelle generell editiert werden kann
	 */
	public boolean isEditable() {
		return editable
				&& (container.isInsertable() || container.isUpdateable() || container
						.isDeleteable());
	}

	public boolean isModeChangeable() {
		return isEditable() && !isDirectEdit();
	}

	/**
	 * Prüft, ob die aktuelle Zeile editiert werden darf.
	 * 
	 * @param row
	 *            die Zeile
	 * @return ob die aktuelle Zeile editiert werden darf
	 */
	public boolean isRowEditable(ContainerRow row) {
		if (!isEditable()) {
			return false;
		}
		if (editableChecker != null && !editableChecker.isEditable(row)) {
			return false;
		}
		return true;
	}

	/**
	 * Prüft, ob die aktuelle Zeile gelöscht werden darf.
	 * 
	 * @param row
	 *            die Zeile
	 * @return ob die aktuelle Zeile gelöscht werden darf
	 */
	public boolean isRowDeletable(ContainerRowId rowId) {
		boolean rowIsDeletable = true;
		if (deletableChecker != null) {
			ContainerRow row = container.getRow(rowId, false, true);
			rowIsDeletable = deletableChecker.isDeletable(row);
		}
		return rowIsDeletable;
	}

	/**
	 * Aktualisiert die Daten im Container aus der DB.
	 */
	public void refresh() {
		container.refresh();
	}

	/**
	 * Fügt einen rowChange-Eventhandler hinzu.
	 * 
	 * @param rowChangeEventHandler
	 *            EventHandler
	 */
	public void addRowChangeEventHandler(
			RowChangeEventHandler rowChangeEventHandler) {

		rowChangeEventRouter.addHandler(rowChangeEventHandler);
	}

	public void addInitializeEventHandler(
			InitializeEventHandler<Table> eventHandler) {
		initializeEventRouter.addHandler(eventHandler);
	}

	public boolean isFormEditEnabled() {
		return config.isEditForm();
	}

	/**
	 * @return
	 * @deprecated Export should no longer be configured globally, but instead
	 *             use special export-actions.
	 * @see TableAction#isExportAction()
	 */
	public boolean isExport() {
		return config.getExport() != null;
	}

	/**
	 * @return
	 * @deprecated Export should no longer be configured globally, but instead
	 *             use special export-actions.
	 * @see TableAction#getExportType()
	 */
	public String getExportType() {
		return config.getExport().value();
	}

	/**
	 * @return Anzahl angezeigter Zeilen
	 */
	public int getPageLength() {
		return config.getPageLength();
	}

	/**
	 * @return Fakrot gerenderter Zeilen im Verhältnis zur page-length
	 */
	public double getCacheRate() {
		return config.getCacheRate();
	}

	/**
	 * Fügt der Tabelle dynamisch eine neue Spalte hinzu. Wenn die notwendigen
	 * Berechtigungen fehlen, werden leere Spalten generiert.
	 * 
	 * @param columnName
	 *            der Name der Spalte
	 * @param columnTitle
	 *            die Überschrift der Spalte
	 * @param columnGenerator
	 *            der Generator, der den Inhalt der einzelnen Zellen generiert
	 */
	public void addGeneratedColumn(String columnName, String columnTitle,
			ColumnGenerator columnGenerator) {
		if (getPortlet().allowsDisplayGeneratedContent()) {
			presenter.addGeneratedColumn(columnName, columnTitle,
					columnGenerator);
		} else {
			presenter.addGeneratedColumn(columnName, columnTitle,
					new EmptyColumnGenerator());
		}
	}

	private Portlet getPortlet() {
		return getPanel().getPortlet();
	}

	/**
	 * Entfernt eine zuvor per
	 * {@link #addGeneratedColumn(String, ColumnGenerator)} hinzugefügte Spalte
	 * aus der Tabelle.
	 * 
	 * @param columnId
	 *            die ID der Spalte, die bei
	 *            {@link #addGeneratedColumn(String, ColumnGenerator)} verwendet
	 *            wurde
	 */
	public void removeGeneratedColumn(String columnId) {
		presenter.removeGeneratedColumn(columnId);
	}

	/**
	 * Gibt die ContainerRow zur übergebenen {@code itemId} aus der
	 * Vaadin-Table/dem Vaadin-Container zurück.
	 * 
	 * @param itemId
	 *            die Vaadin-ID der Zeile
	 * @return die Modell-Zeile
	 */
	public ContainerRow getRowByItemId(Object itemId) {
		ContainerRowId rowId = container.convertInternalRowId(itemId);
		return getRow(rowId);
	}

	/**
	 * Gibt die ContainerRow zur übergebenen {@code rowId} zurück.
	 * 
	 * @param rowId
	 *            die ID der Zeile
	 * @return die Zeile
	 */
	public ContainerRow getRow(ContainerRowId rowId) {
		return container.getRow(rowId, false, true);
	}

	/**
	 * Führt Änderungen an der Tabelle zu wie beispielsweise dynamisches
	 * Hinzufügen und Entfernen von Spalten. Das Content-Refreshing
	 * (Rerendering) wird vor dem Ausführen von {@code change} ausgeschaltet und
	 * (falls es vorher eingeschaltet war) danach wieder eingeschaltet.
	 * 
	 * @param changes
	 *            die durchzuführenden Änderungen
	 */
	public void renderOnce(DynamicColumnChanges changes) {
		presenter.renderOnce(changes);
	}

	/**
	 * @param columnId
	 *            die Spalten-ID/Spalten-Überschrift.
	 * @return {@code true} wenn die Tabelle eine per
	 *         {@link #addGeneratedColumn(String, ColumnGenerator)} hinzugefügte
	 *         Spalte hat.
	 */
	public boolean hasGeneratedColumn(String columnId) {
		return presenter.hasGeneratedColumn(columnId);
	}

	/**
	 * Entfernt alle per {@link #addGeneratedColumn(String, ColumnGenerator)}
	 * hinzugefügten Spalten.
	 */
	public void clearAllGeneratedColumns() {
		presenter.clearAllGeneratedColumns();
	}

	/**
	 * @return die IDs der sichtbaren Spalten in der Reihenfolge, in der sie
	 *         angezeigt werden.
	 * @see #setVisibleColumns(List)
	 */
	public List<String> getVisibleColumns() {
		return presenter.getVisibleColumns();
	}

	/**
	 * Setzt gleichzeitig die Reihenfolge und die Sichtbarkeit der Spalten. Alle
	 * Spalten, deren ID in der übergebenen Liste {@code visibleColumns}
	 * enthalten sind, werden angezeigt, alle anderen nicht. Das bezieht sich
	 * nicht nur auf per {@code addGeneratedColumn} hinzugefügten Spalten
	 * sondern auf alle Spalten inkl. der fest in der Konfiguration
	 * deklarierten. Die Spalten werden in der Reihenfolge angeordnet, wie sie
	 * in {@code visibleColumns} stehen.
	 * 
	 * @param visibleColumns
	 *            die Liste der ColumnNames. Für eine per
	 *            {@link #addGeneratedColumn(String, String, Closure)}
	 *            hinzugefügte Spalten ist das der String, der dabei als
	 *            Argument {@code columnName} verwendet wurde, für eine
	 *            deklarierte Spalte das Attribut {@code name}
	 */
	public void setVisibleColumns(List<String> visibleColumns) {
		presenter.setVisibleColumns(visibleColumns);
	}

	/**
	 * Fügt der Tabelle eine neue Zeile hinzu. Werte der Zeile können über
	 * {@code values} vorbelegt werden.
	 * 
	 * @param values
	 *            die Werte, die in der neuen Zeile bereits gesetzt sein sollen.
	 *            Die Keys der Map entsprechen den Column Names, die
	 *            dazugehörigen Values sind die Werte in der Zeile.
	 * @return die neu angelegte Zeile
	 */
	public ContainerRow createNewRow(Map<String, Object> values) {
		// Ggf. vorhandene Änderungen in der alten Zeile vorher committen
		container.commit();
		return presenter.createNewRow(values);
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
		presenter.setTableActionVisibility(id, visible);
	}

	public void download(Download download) {
		presenter.download(download);
	}

	/**
	 * @param exportCallback
	 *            Callback im Kontext bestimmter Container-Anpassungen.
	 */
	public void withExportSettings(ExportWithExportSettings exportCallback) {
		container.withExportSettings(exportCallback);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.45
	 */
	@Override
	public int getExpandRatio() {
		return config.getExpandRatio();
	}

	/**
	 * @return the current edit mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @return the current display mode
	 */
	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public DataStream getStream() {
		DataStream stream = container.getStream();
		if (columns != null) {
			stream = new GeneratedColumnsDataStreamWrapper(stream, container.getColumns(), columns);
		}
		return stream;
	}
}
