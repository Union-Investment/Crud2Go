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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.Mode;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Beschreibt die Erwartungen des Presenters an die View.
 */
public interface TableView extends View {

	/**
	 * 
	 * {@link Presenter} Beschreibt die Erwartungen der View an den Presenter.
	 * 
	 */
	public interface Presenter {
		/**
		 * 
		 * @return liefert, ob das Hinzufügen neuer Zeilen zugelassen ist.
		 */
		boolean isInsertable();

		/**
		 * 
		 * @return liefert, ob das Bearbeiten von Zeilen zugelassen ist.
		 */
		boolean isUpdateable();

		/**
		 * 
		 * @return liefert, ob das Löschen von Zeilen zugelassen ist.
		 */
		boolean isDeleteable();
		
		/**
		 * 
		 * @param itemId die ItemId der Zeile
		 * @return <code>true</code>, falls die Zeile gelöscht werden darf
		 */
		boolean isRowDeletable(Object itemId);

		/**
		 * Holt, die zur Tabelle gehörende SQL-Connection und bindet es an das
		 * Closure und fürht diesen aus.
		 * 
		 * @param action
		 *            Tabellenbutton
		 */
		void callClosure(TableAction action);

		/**
		 * 
		 * @param viewMode
		 *            Ansichtsmodus
		 */
		void switchMode(Mode viewMode);

		/**
		 * 
		 * @param selection
		 *            Selektionen
		 */
		void changeSelection(Set<Object> selection);

		/**
		 * 
		 * @param item
		 *            Datensatz
		 */
		void doubleClick(Item item);

		/**
		 * Wird aufgerufen, wenn eine Zeile verändert wird.
		 * 
		 * @param containerRow
		 *            Zeile
		 * @param changedValues
		 *            Map mit geänderten Spaltennamen und alten Werten
		 */
		void rowChange(Item containerRow, Map<String, Object> changedValues);

		/**
		 * Wird aufgerufen, wenn die Tabelle fertig initialiert wurde.
		 */
		void doInitialize();

		/**
		 * Gibt zurück, ob der Excel-ExportCallback aktiviert ist.
		 * 
		 * @return ob Excel-ExportCallback aktiviert ist
		 * @deprecated Export capabilities are no longer to be configured using
		 *             the global attribute, but instead to use special
		 *             export-actions (@see {@link TableAction#isExportAction()}
		 *             ).
		 */
		public boolean isExcelExport();

		/**
		 * Gibt zurück, ob der CSV-ExportCallback aktiviert ist.
		 * 
		 * @return ob CSV-ExportCallback aktiviert ist
		 * @deprecated Export capabilities are no longer to be configured using
		 *             the global attribute, but instead to use special
		 *             export-actions (@see {@link TableAction#isExportAction()}
		 *             ).
		 */
		public boolean isCSVExport();

		/**
		 * Gibt zurück ob der FormularEdit-Modus aktiviert ist.
		 * 
		 * @return Ob FormularEdit-Modus aktiviert ist
		 */
		public boolean isFormEditEnabled();

		public void openRowEditingForm();
	}

	/**
	 * 
	 * @param presenter
	 *            Presenter
	 * @param dataContainer
	 *            Der Vaadin Daten-Container
	 * @param model
	 *            das Modell
	 * @param pageLength
	 *            Anzahl der angezeigten Zeilen
	 * @param cacheRate
	 *            Faktor an Zeilen im Verhältnis zu den angezeigten Zeilen, die
	 *            gerendert werden sollen.
	 */
	void initialize(TableView.Presenter presenter, DataContainer dataContainer,
			Table model, int pageLength, double cacheRate);

	/**
	 * @param msgKey
	 *            Message Key
	 * @param notificationType
	 *            Notification Type
	 */
	void showNotification(String msgKey, Type notificationType);

	/**
	 * Action Methode fuer das Zuruecksetzen von Aenderungen in der
	 * Datentabelle.
	 */
	public void onRevertChanges();

	/**
	 * Fügt der Tabelle dynamisch eine neue Spalte hinzu.
	 * 
	 * @param id
	 *            die ID der Spalte
	 * @param columnTitle
	 *            die Überschrift der Spalte
	 * @param columnGenerator
	 *            der Generator, der den Inhalt der einzelnen Zellen generiert
	 */
	void addGeneratedColumn(String id, String columnTitle,
			ColumnGenerator columnGenerator);

	/**
	 * Entfernt eine zuvor per
	 * {@link #addGeneratedColumn(String, ColumnGenerator)} hinzugefügte Spalte
	 * aus der Tabelle.
	 * 
	 * @param id
	 *            die ID der Spalte, die bei
	 *            {@link #addGeneratedColumn(String, ColumnGenerator)} verwendet
	 *            wurde
	 */
	void removeGeneratedColumn(String id);

	/**
	 * Schaltet das automatische Refreshing ein, falls
	 * {@code refreshContent == true} wird auch direkt ein Refresh durchgeführt.
	 * 
	 * @param refreshContent
	 *            wenn {@code true}, wird direkt ein requestRepaint abgesetzt,
	 *            ansonsten wird nur das automatische Refreshing wieder
	 *            eingeschaltet
	 */
	public void enableContentRefreshing(boolean refreshContent);

	/**
	 * Schaltet das automatische Refreshing ab.
	 * 
	 * @return ob das Refresh vorher eingeschaltet war.
	 */
	public boolean disableContentRefreshing();

	/**
	 * @return die IDs der sichtbaren Spalten in der Reihenfolge, in der sie
	 *         angezeigt werden.
	 * @see #setVisibleColumns(List)
	 */
	public List<String> getVisibleColumns();

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
	public void setVisibleColumns(List<String> visibleColumns);

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
	public void setTableActionVisibility(String id, boolean visible);

	/**
	 * Fügt der Tabelle eine neue Zeile hinzu.
	 * 
	 * @return die Item ID
	 */
	public Object addItemToTable();

	/**
	 * Wählt eine Zeile zum Editieren aus.
	 * 
	 * @param itemId
	 *            die ItemID der neuen Zeile
	 * @param suppressCommit
	 *            unterdrückt das implizite Commit, das ggf. beim Deselektieren
	 *            der alten Zeile ausgelöst wird. (Genau genommen wird die
	 *            komplette Verarbeitung des onSelectionChange-Listeners
	 *            unterdrückt).
	 */
	void selectItemForEditing(Object itemId, boolean suppressCommit);

	/**
	 * Update the displayed table selection.
	 * 
	 * @param selection
	 *            the new selection
	 */
	void selectionUpdatedExternally(Set<Object> selection);

	/**
	 * Open a document for download with the content being generated by the
	 * 'download' parameter.
	 * 
	 * @param download the generator
	 */
	void download(Download download);

	void switchToViewMode();

	void switchToEditMode();

}