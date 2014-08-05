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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table.ColumnGenerator;

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
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DisplayMode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.DynamicColumnChanges;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.domain.support.map.TransformedEntryMap;
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder;

/**
 * 
 * Repräsentiert eine Tabelle.
 * 
 * @author siva.selvarajah
 */
@SuppressWarnings("serial")
public class ScriptTable extends ScriptComponent {

	private Table table;
	private ScriptContainer container;
	private List<ScriptTableAction> scriptActions = new ArrayList<ScriptTableAction>();

	private Closure<?> onSelectionChange;
	private Closure<?> onModeChange;
	private Closure<?> onRowChange;
	private Closure<?> onDoubleClick;
	private Closure<?> onInitialize;

	private Map<String, Closure<?>> generatedColumnClosures = new HashMap<String, Closure<?>>();

	private ScriptTableSelection currentSelection;

	TableDoubleClickEventHandler doubleClickEventHandler = null;

	/**
	 * Konstruktor mit Parameter.
	 * 
	 * @param table
	 *            Tabelle
	 */
	ScriptTable(Table table) {
		this.table = table;
		currentSelection = new ScriptTableSelection(table.getContainer(),
				new HashSet<ScriptRowId>());
		initializeEventHandler(table);

	}

	/**
	 * Fügt ein Button hinzu.
	 * 
	 * @param scriptTableAction
	 *            ScriptTableAction
	 */
	void addAction(ScriptTableAction scriptTableAction) {
		this.scriptActions.add(scriptTableAction);
		scriptTableAction.setTable(this);
	}

	/**
	 * 
	 * @return Ob die Sortierung aktiviert ist
	 */
	public boolean isSortingEnabled() {
		return table.isSortingEnabled();
	}

	/**
	 * @return die aktuelle Zeilenauswahl der Tabelle
	 */
	public ScriptTableSelection getSelection() {
		return currentSelection;
	}

	/**
	 * Liefert die Zeile mit der übergebenen ID.
	 * 
	 * @param rowId
	 *            die ID der Zeile
	 * @return die Zeile mit der ID {@code rowId}
	 */
	public ScriptRow getRowById(ScriptRowId rowId) {
		ContainerRow row = table.getRow(rowId.getContainerRowId());
		if (row != null) {
			return new ScriptRow(row);
		} else {
			return null;
		}
	}

	/**
	 * Liefert alle momentan selektierten Zeilen.
	 * 
	 * @return alle momentan selektierten Zeilen
	 */
	public List<ScriptRow> getSelectedRows() {
		List<ScriptRow> selectedRows = new ArrayList<ScriptRow>();
		for (ScriptRowId rowId : getSelection().getIds()) {
			selectedRows.add(getRowById(rowId));
		}
		return selectedRows;
	}

	/**
	 * 
	 * @return Alle Tabellenbuttons, die der Tabelle zugeordnet sind.
	 */
	public List<ScriptTableAction> getActions() {
		return unmodifiableList(scriptActions);
	}

	/**
	 * 
	 * @return Closure mit zwei Parametern { ScriptTable it,
	 *         ScriptTableSelection selection -> ... }, die bei einer Änderung
	 *         der Selektion ausgeführt wird.
	 */
	public Closure<?> getOnSelectionChange() {
		return onSelectionChange;
	}

	/**
	 * 
	 * @param onSelectionChange
	 *            Closure mit zwei Parametern { ScriptTable it,
	 *            ScriptTableSelection selection -> ... }, die bei einer
	 *            Änderung der Selektion ausgeführt werden soll.
	 */
	public void setOnSelectionChange(Closure<?> onSelectionChange) {
		this.onSelectionChange = onSelectionChange;
	}

	/**
	 * @return Closure die bei einem Doppelklick ausgeführt wird
	 */
	public Closure<?> getOnDoubleClick() {
		return onDoubleClick;
	}

	/**
	 * Setzt die Closure die bei einem Doppelklick ausgeführt wird.
	 * 
	 * @param onDoubleClick
	 *            die Closure
	 */
	public void setOnDoubleClick(Closure<?> onDoubleClick) {
		if (onDoubleClick == null) {
			removeExistingDoubleClickEventHandler();
		} else {
			addMissingDoubleClickEventHandler();
		}
		this.onDoubleClick = onDoubleClick;
	}

	private void addMissingDoubleClickEventHandler() {
		if (doubleClickEventHandler == null) {
			doubleClickEventHandler = new TableDoubleClickEventHandler() {
				@Override
				public void onDoubleClick(TableDoubleClickEvent event) {
					ScriptRow scriptRow = new ScriptRow(event.getRow());
					ScriptTable.this.onDoubleClick.call(ScriptTable.this,
							scriptRow);
				}
			};
			table.addDoubleClickEventHandler(doubleClickEventHandler);
		}
	}

	private void removeExistingDoubleClickEventHandler() {
		if (doubleClickEventHandler != null) {
			table.removeDoubleClickEventHandler(doubleClickEventHandler);
			doubleClickEventHandler = null;
		}
	}

	/**
	 * @return <code>true</code>, falls direct edit für die Tabelle aktiv ist
	 */
	public boolean isDirectEdit() {
		return table.isDirectEdit();
	}

	/**
	 * @return liefert den aktuellen Editiermodus 'VIEW' oder 'EDIT' zurück.
	 */
	public String getMode() {
		return table.getMode().name();
	}

	/**
	 * @param mode
	 *            der Zielmodus. Kann nur geändert werden, wenn direct editing
	 *            deaktiviert ist
	 * @throws IllegalStateException
	 *             , falls eine Änderung nicht möglich ist
	 */
	public void setMode(String mode) {
		table.changeMode(Mode.valueOf(mode));
	}

	/**
	 * @return den aktuellen Anzeigemodus 'TABLE' oder 'FORM' zurück
	 */
	public String getDisplayMode() {
		return table.getDisplayMode().name();
	}

	/**
	 * Schließt eine evtl. geöffnete Formularansicht
	 */
	public void closeEditForm() {
		table.changeDisplayMode(DisplayMode.TABLE);
	}

	/**
	 * 
	 * @return Closure mit zwei Parametern { ScriptTable it, String mode -> ...
	 *         }, die beim Wechseln zwischen "VIEW" und "EDIT"-Mode ausgeführt
	 *         werden soll.
	 */
	public Closure<?> getOnModeChange() {
		return onModeChange;
	}

	/**
	 * 
	 * @param onModeChange
	 *            Closure mit zwei Parametern { ScriptTable it, String mode ->
	 *            ... }, die beim Wechseln zwischen "VIEW" und "EDIT"-Mode
	 *            ausgeführt werden soll.
	 */
	public void setOnModeChange(Closure<?> onModeChange) {
		this.onModeChange = onModeChange;
	}

	/**
	 * @param columnName
	 *            der Spaltenname
	 * @return die onRender-Closure oder <code>null</code>, falls keine gesetzt
	 */
	public Closure<?> getOnRender(String columnName) {
		return generatedColumnClosures.get(columnName);
	}

	/**
	 * @param columnName
	 *            der Spaltenname der generierten Spalte
	 * @param onRender
	 *            die onRender-Closure. Als Parameter der Closure wird ein
	 *            {@link VaadinBuilder} übergeben
	 */
	public void addOnRender(String columnName, Closure<?> onRender) {
		generatedColumnClosures.put(columnName, onRender);
	}

	void setContainer(ScriptContainer scriptContainer) {
		this.container = scriptContainer;
	}

	/**
	 * @return den Container zur Tabelle
	 */
	public ScriptContainer getContainer() {
		return container;
	}

	private void initializeEventHandler(final Table table) {
		table.addSelectionEventHandler(new SelectionEventHandler() {
			@Override
			public void onSelectionChange(SelectionEvent selectionEvent) {
				Set<ScriptRowId> selectedRowIds = new HashSet<ScriptRowId>();
				for (ContainerRowId id : selectionEvent.getSelection()) {
					selectedRowIds.add(new ScriptRowId(id));
				}

				currentSelection = new ScriptTableSelection(table
						.getContainer(), unmodifiableSet(selectedRowIds));

				if (onSelectionChange != null) {
					onSelectionChange.call(ScriptTable.this, currentSelection);
				}

			}
		});

		table.addModeChangeEventHandler(new ModeChangeEventHandler<Table, Mode>() {
			@Override
			public void onModeChange(ModeChangeEvent<Table, Mode> event) {
				if (onModeChange != null) {
					onModeChange.call(ScriptTable.this, event.getMode().name());
				}

			}
		});

		table.addRowChangeEventHandler(new RowChangeEventHandler() {
			@Override
			public void rowChange(RowChangeEvent event) {
				if (onRowChange != null) {
					ScriptRow scriptRow = new ScriptRow(event.getSource());
					onRowChange.call(ScriptTable.this, scriptRow,
							event.getChangedValues());
				}
			}
		});

		table.addInitializeEventHandler(new InitializeEventHandler<Table>() {
			@Override
			public void onInitialize(InitializeEvent<Table> event) {
				if (onInitialize != null) {
					onInitialize.call(ScriptTable.this);
				}
			}
		});
	}

	/**
	 * Aktualisiert die Daten auf der Tabelle.
	 */
	public void refresh() {
		table.refresh();
	}

	/**
	 * @return die Closure die bei einem Zeilenwechsel ausgeführt wird.
	 */
	public Closure<?> getOnRowChange() {
		return onRowChange;
	}

	/**
	 * @param onRowChange
	 *            Closure mit den Parametern { ScriptTable it, ScriptRow row,
	 *            Map<String,Object> changedValues -> ... }, die nach Änderung
	 *            von Zeilenwerten durch die UI aufgerufen wird.
	 * 
	 *            <code>changedValues</code> enthält eine Map der geänderten
	 *            Spaltennamen mit den ursprünglichen Werte.
	 */
	public void setOnRowChange(Closure<?> onRowChange) {
		this.onRowChange = onRowChange;
	}

	/**
	 * @return die onInitialize-Closure
	 */
	public Closure<?> getOnInitialize() {
		return onInitialize;
	}

	/**
	 * @param onInitialize
	 *            die onInitialize-Closure
	 */
	public void setOnInitialize(Closure<?> onInitialize) {
		this.onInitialize = onInitialize;
	}

	/**
	 * Fügt der Tabelle dynamisch eine neue Spalte hinzu.
	 * 
	 * @param columnName
	 *            Name und Titel der neuen Spalte
	 * @param generator
	 *            die Closure, die den Inhalt der einzelnen Zellen generiert
	 */
	public void addGeneratedColumn(final String columnName,
			final Closure<Component> generator) {
		addGeneratedColumn(columnName, columnName, generator);
	}

	/**
	 * Fügt der Tabelle dynamisch eine neue Spalte hinzu.
	 * 
	 * @param columnName
	 *            der Name der Spalte
	 * @param columnTitle
	 *            der Titel der Spalte
	 * @param generator
	 *            die Closure, die den Inhalt der einzelnen Zellen generiert
	 */
	public void addGeneratedColumn(final String columnName,
			final String columnTitle, final Closure<Component> generator) {
		table.addGeneratedColumn(columnName, columnTitle,
				new ColumnGenerator() {
					private static final long serialVersionUID = 1L;

					@Override
					public Component generateCell(com.vaadin.ui.Table source,
							Object itemIdVaadin, Object columnIdVaadin) {
						ScriptRow row = new ScriptRow(table
								.getRowByItemId(itemIdVaadin));
						return generator.call(row, new VaadinBuilder());
					}
				});
	}

	/**
	 * Entfernt eine zuvor per {@link #addGeneratedColumn(String, Closure)}
	 * hinzugefügte Spalte aus der Tabelle.
	 * 
	 * @param columnId
	 *            die ID der Spalte, die bei
	 *            {@link #addGeneratedColumn(String, Closure)} verwendet wurde
	 */
	public void removeGeneratedColumn(String columnId) {
		table.removeGeneratedColumn(columnId);
	}

	/**
	 * Führt Änderungen an der Tabelle zu wie beispielsweise dynamisches
	 * Hinzufügen und Entfernen von Spalten. Das Content-Refreshing
	 * (Rerendering) wird vor dem Ausführen von {@code change} ausgeschaltet und
	 * (falls es vorher eingeschaltet war) danach wieder eingeschaltet.
	 * 
	 * @param dynamicColumnChanges
	 *            Closure, die die durchzuführenden Änderungen kapselt
	 */
	public void renderOnce(final Closure<?> dynamicColumnChanges) {
		table.renderOnce(new DynamicColumnChanges() {
			@Override
			public void apply() {
				dynamicColumnChanges.call();
			}
		});
	}

	/**
	 * @param columnId
	 *            die Spalten-ID/Spalten-Überschrift.
	 * @return {@code true} wenn die Tabelle eine generierte Spalte des
	 *         angegebenen Namens hat.
	 */
	public boolean hasGeneratedColumn(String columnId) {
		return table.hasGeneratedColumn(columnId);
	}

	/**
	 * Entfernt alle generierten Spalten.
	 */
	public void clearAllGeneratedColumns() {
		table.clearAllGeneratedColumns();
	}

	/**
	 * @return die IDs der sichtbaren Spalten in der Reihenfolge, in der sie
	 *         angezeigt werden.
	 * @see #setVisibleColumns(List)
	 */
	public List<String> getVisibleColumns() {
		return table.getVisibleColumns();
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
		table.setVisibleColumns(visibleColumns);
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
	public ScriptRow createNewRow(Map<String, Object> values) {
		return new ScriptRow(table.createNewRow(mapValuesToModel(values)));
	}

	private Map<String, Object> mapValuesToModel(Map<String, Object> values) {
		if (values == null) {
			return Collections.<String, Object> emptyMap();
		} else {
			return new TransformedEntryMap<String, Object, Object>(values,
					ScriptField.SCRIPT_TO_MODEL_VALUE_TRANSFORMER);
		}
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
		table.setTableActionVisibility(id, visible);
	}
}
