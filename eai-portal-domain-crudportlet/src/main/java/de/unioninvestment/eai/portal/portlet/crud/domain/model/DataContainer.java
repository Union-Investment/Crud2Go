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

import java.io.Serializable;
import java.text.Format;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.BeforeCommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.support.vaadin.filter.NothingFilter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Modell Representation eines Daten-Container Backends.
 * 
 * @author markus.bonsch
 * 
 */
public interface DataContainer extends Serializable {

	/**
	 * Permission Actions.
	 */
	public enum Permission {
		INSERT, UPDATE, DELETE
	}

	/**
	 * Filter-Verhalten des Containers.
	 * 
	 * @author carsten.mjartan
	 */
	public enum FilterPolicy {
		/**
		 * Standardverhalten. Filter werden so angewendet wie konfiguriert.
		 */
		ALL,

		/**
		 * Bei der Initialisierung des Containers wird ein {@link NothingFilter}
		 * angewendet, so dass kein Ergebnis erscheint.
		 */
		NOTHING,

		/**
		 * Bei der Initialisierung und bei jeder Entfernung aller Filter wird
		 * stattdessen der {@link NothingFilter} angewendet.
		 */
		NOTHING_AT_ALL
	};

	/**
	 * EachRowRow Callback Objekt.
	 * 
	 * @author markus.bonsch
	 * 
	 */
	public interface EachRowCallback {

		/**
		 * @param row
		 *            eine Tabellenzeile
		 */
		void doWithRow(ContainerRow row);

	}

	/**
	 * Callbackinterface für Transaktionen.
	 * 
	 * @param <R>
	 *            der Typ des Rückgabewertes
	 */
	interface TransactionCallback<R> {

		/**
		 * 
		 * @return Rückgabewert
		 */
		R doInTransaction();
	}

	/**
	 * Callback-Interface für Exporte.
	 * 
	 * @author carsten.mjartan
	 */
	public interface ExportWithExportSettings {
		/**
		 * Callback Methode
		 */
		public void export();
	}

	/**
	 * 
	 * @return liefert true wenn Inserts zugelassen sind.
	 */
	boolean isInsertable();

	/**
	 * 
	 * @return liefert true wenn Updates zugelassen sind.
	 */
	boolean isUpdateable();

	/**
	 * 
	 * @return liefert true wenn Deletes zugelassen sind.
	 */
	boolean isDeleteable();

	/**
	 * Entfernt alle nicht-dauerhaften Datenbankfilter.
	 */
	void removeAllFilters();

	/**
	 * Entfernt alle Datenbankfilter.
	 * 
	 * @param removeDurable
	 *            gibt an ob auch dauerhafte Filter aus dem Container entfernt
	 *            werden
	 */
	void removeAllFilters(boolean removeDurable);

	/**
	 * Liefert den Datentyp für eine Spalte.
	 * 
	 * @param name
	 *            - Spaltenname
	 * @return - Datentyp oder <code>null</code>, wenn Spalte unbekannt
	 */
	Class<?> getType(String name);

	/**
	 * Liefert den Formatter für eine Spalte.
	 * 
	 * @param name
	 *            - Spaltenname
	 * @return - Ein Java Formatobjekt
	 */
	Format getFormat(String name);

	/**
	 * Ersetzt die bestehenden Filter durch Neue.
	 * 
	 * @param newFilters
	 *            die neue Filterliste
	 * @param removeDurable
	 *            <code>true</code>, wenn auch "durable" Filter ersetzt werden
	 *            sollen
	 */
	void replaceFilters(List<Filter> newFilters, boolean removeDurable);

	/**
	 * Ersetzt die bestehenden Filter durch neue.
	 * 
	 * @param newFilters
	 *            die Liste der neuen Filter.
	 * @param removeDurable
	 *            <code>true</code>, wenn auch "durable" Filter ersetzt werden
	 *            sollen.
	 * @param forceReplace
	 *            Ersetzen der Filter auch dann, wenn sich gegenüber der letzten
	 *            Filterung keine Änderung ergeben hat. Dies führt in jedem Fall
	 *            zu einem Neuladen der Inhalte.
	 * @param timeout
	 *            die Timeout-Zeit in Sekunden.
	 */
	void replaceFilters(List<Filter> newFilters, boolean removeDurable,
			boolean forceReplace, int timeout);

	/**
	 * Ersetzt die bestehenden Filter durch neue.
	 * 
	 * @param newFilters
	 *            die Liste der neuen Filter.
	 * @param removeDurable
	 *            <code>true</code>, wenn auch "durable" Filter ersetzt werden
	 *            sollen.
	 * @param timeout
	 *            die Timeout-Zeit in Sekunden.
	 * @param forceReplace
	 *            Ersetzen der Filter auch dann, wenn sich gegenüber der letzten
	 *            Filterung keine Änderung ergeben hat. Dies führt in jedem Fall
	 *            zu einem Neuladen der Inhalte.
	 */
	void replaceFilters(List<Filter> newFilters, boolean removeDurable,
			boolean forceReplace);

	/**
	 * Fügt den bestehenden Filtern neue hinzu.
	 * 
	 * @param filter
	 *            {@link Filter}
	 */
	void addFilters(List<Filter> filter);

	/**
	 * Gibt die Filterliste zurück.
	 * 
	 * @return Filterliste.
	 */
	List<Filter> getFilterList();

	/**
	 * Liefert den Vaadin Datencontainer.
	 * 
	 * @return - Vaadin SQLContainer
	 */
	Container getDataSourceContainer();

	/**
	 * Ein für DB Transaktions Rollback aus.
	 * 
	 */
	void rollback();

	/**
	 * Ein für DB Transaktions Commit aus.
	 * 
	 */
	void commit();

	/**
	 * Löscht alle Zeilen einer Abfrage.
	 */
	void removeAllRows();

	/**
	 * Fügt eine neue Zeile zum Container. Diese Methode setzt ggf. voraus, dass
	 * eine Transaktion besteht und wirft andernfalls eine Exception.
	 * 
	 * @return die neue erstellte ContainerRow
	 */
	ContainerRow addRow();

	/**
	 * Gibt die {@link ContainerRowId}s aller in diesem Container enthaltenen
	 * Zeilen zurück.
	 * 
	 * @return Liste der Ids
	 */
	List<ContainerRowId> getRowIds();

	/**
	 * Liefert die vorherige Zeile.
	 * 
	 * @param currentRowId
	 *            die aktuelle Zeile
	 * @return die vorhergehende Zeile gemäß aktueller Sortierung oder
	 *         <code>null</code>, falls es keine vorherige Zeile gibt
	 */
	ContainerRowId previousRowId(ContainerRowId currentRowId);

	/**
	 * Liefert die nächste Zeile.
	 * 
	 * @param currentRowId
	 *            die aktuelle Zeile
	 * @return die nächste Zeile gemäß aktueller Sortierung oder
	 *         <code>null</code>, falls es keine nächste Zeile gibt
	 */
	ContainerRowId nextRowId(ContainerRowId currentRowId);

	/**
	 * Startet ggf. eine neue Transaktion und führt die im callback übergebene
	 * Methode auf. Eine bestehende Benutzertransaktion wird vorher committed.
	 * 
	 * @param <T>
	 *            der Rückgabetyp des Callbacks
	 * @param callback
	 *            Transaktionales Callback Object
	 * @return Rückgabewert des Callbacks
	 */
	<T> T withTransaction(TransactionCallback<T> callback);

	/**
	 * Prüft ob eine Transaktion (gestartet mit
	 * {@link #withTransaction(TransactionCallback)} existiert und ruft den
	 * übergebenen Callback auf.
	 * 
	 * @param <T>
	 *            Typ des Rückgabewertes
	 * @param callback
	 *            Transaktionales Callback Object
	 * @return Rückgabewert der Transaktion
	 * @throws IllegalStateException
	 *             falls keine Transaktion besteht
	 */
	<T> T withExistingTransaction(TransactionCallback<T> callback);

	/**
	 * Liefert die Namen der Id Spalten.
	 * 
	 * @return - eine Liste aller primary Id Spalten
	 */
	List<String> getPrimaryKeyColumns();

	/**
	 * @return die Namen aller Spalten
	 */
	List<String> getColumns();

	/**
	 * Fügt ein Insert-Eventhandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	void addInsertEventHandler(InsertEventHandler handler);

	/**
	 * Fügt ein Delete-Eventhandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	void addDeleteEventHandler(DeleteEventHandler handler);

	/**
	 * Fügt ein Update-Eventhandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	void addUpdateEventHandler(UpdateEventHandler handler);

	/**
	 * Fügt ein Commit-Eventhandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	void addCommitEventHandler(CommitEventHandler handler);

	/**
	 * Fügt ein Create-Eventhandler hinzu.
	 * 
	 * @param handler
	 *            Eventhandler
	 */
	void addCreateEventHandler(CreateEventHandler handler);

	/**
	 * Konvertiert eine Vaadin-Container-spezifische Zeile
	 * 
	 * @param item
	 *            das Vaadin-Item
	 * @param transactional
	 *            ob die Zeile transaktional ist
	 * @param immutable
	 *            ob die Zeile Veränderungen zulässt
	 * @return das Zeilenobjekt
	 */
	ContainerRow convertItemToRow(Item item, boolean transactional,
			boolean immutable);

	/**
	 * Konvertiert die Primärschlüssel.
	 * 
	 * @param internalId
	 *            die Container-interne RowId
	 * @return ContainerRowId die Zeilen-ID
	 */
	ContainerRowId convertInternalRowId(Object internalId);

	/**
	 * Liefert die ContainerRow zu dem übergebenen Container-internen
	 * Primärschlüssel.
	 * 
	 * @param internalId
	 *            die Container-interne RowId
	 * @param transactional
	 *            ob die Zeile transaktional ist
	 * @param immutable
	 *            ob die Zeile Veränderungen zulässt
	 * @return ContainerRow das Zeilenobjekt
	 */
	ContainerRow getRowByInternalRowId(Object internalId,
			boolean transactional, boolean immutable);

	/**
	 * Fügt ein BeforeCommit-Eventhandler hinzu.
	 * 
	 * @param beforeCommitEventHandler
	 *            Eventhandler
	 */
	void addBeforeCommitEventHandler(
			BeforeCommitEventHandler beforeCommitEventHandler);

	/**
	 * Löscht die Zeilen.
	 * 
	 * @param ids
	 *            Primärschlüssel
	 */
	void removeRows(Set<ContainerRowId> ids);

	/**
	 * Liest die angegebenen Zeilen aus der Datenquelle und übergibt sie einer
	 * Callback-Methode.
	 * 
	 * @param ids
	 *            die IDs der zu verarbeitenden Zeilen
	 * @param eachRowCallback
	 *            das Callback-Objekt
	 */
	void eachRow(Set<ContainerRowId> ids, EachRowCallback eachRowCallback);

	/**
	 * Liest die aktuellen (gefilterten) Tabelleninhalte aus der Datenquelle und
	 * übergibt sie einer Callback-Methode.
	 * 
	 * @param ids
	 *            die IDs der zu verarbeitenden Zeilen
	 * @param eachRowCallback
	 *            das Callback-Objekt
	 */
	void eachRow(EachRowCallback eachRowCallback);

	/**
	 * Gibt eine Tabellenzeile zurück.
	 * 
	 * @param rowId
	 *            Primäschlüssel
	 * @param transactional
	 *            gibt an, ob bei Operationen ein transaktionaler Kontext
	 *            vorausgesetzt werden soll (Aufruf innerhalb von
	 *            {@link DataContainer#withTransaction(TransactionCallback)})
	 * @param immutable
	 *            gibt an, ob Änderungen an der Zeile erlaubt sind
	 * @return Zeile
	 */
	ContainerRow getRow(ContainerRowId rowId, boolean transactional,
			boolean immutable);

	/**
	 * Gibt eine Tabellenzeile zurück, wenn sie im Cache ist.
	 * 
	 * @param rowId
	 *            Primäschlüssel
	 * @param transactional
	 *            gibt an, ob bei Operationen ein transaktionaler Kontext
	 *            vorausgesetzt werden soll (Aufruf innerhalb von
	 *            {@link DataContainer#withTransaction(TransactionCallback)})
	 * @param immutable
	 *            gibt an, ob Änderungen an der Zeile erlaubt sind
	 * @return Zeile
	 */
	ContainerRow getCachedRow(ContainerRowId rowId, boolean transactional,
			boolean immutable);

	/**
	 * Aktualisiert seine Daten aus der DB.
	 */
	void refresh();

	/**
	 * 
	 * @param columnName
	 *            der Spaltenname
	 * @return die passende {@link EditorSupport}-Instanz oder NULL, falls keine
	 *         gefunden wurde
	 */
	EditorSupport findEditor(String columnName);

	/**
	 * @param columnName
	 *            der Spaltenname
	 * @return die passende {@link DisplaySupport}-Instanz oder NULL, falls
	 *         keine gefunden wurde
	 */
	DisplaySupport findDisplayer(String columnName);

	/**
	 * Läd ein Clob Feld aus der Datenbank nach.
	 * 
	 * @param rowId
	 *            der Primäschlüssel
	 * @param columnName
	 *            der Spaltenname
	 * @return ClobWapper Object
	 */
	ContainerClob getCLob(ContainerRowId rowId, String columnName);

	/**
	 * Prüft ob das Feld vom Typ CLob ist.
	 * 
	 * @param columnName
	 *            der Spaltenname
	 * @return <code>true</code>, falls die Spalte eine CLob-Spalte ist
	 */
	boolean isCLob(String columnName);

	/**
	 * Gibt true zurück wenn das CLob verändert wurde.
	 * 
	 * @param containerRowId
	 * @param columnName
	 * @return <code>true</code>, falls das Clob in der Spalte modifiziert wurde
	 */
	boolean isCLobModified(ContainerRowId containerRowId, String columnName);

	/**
	 * Gibt true zurück wenn das BLob verändert wurde.
	 * 
	 * @param containerRowId
	 * @param columnName
	 * @return <code>true</code>, falls das BLob verändert wurde
	 */
	boolean isBLobModified(ContainerRowId containerRowId, String columnName);

	/**
	 * Läd ein BLob Feld aus der Datenbank nach.
	 * 
	 * @param rowId
	 *            der Primäschlüssel
	 * @param columnName
	 *            der Spaltenname
	 * @return BLobWapper Object
	 */
	ContainerBlob getBLob(ContainerRowId rowId, String columnName);

	/**
	 * @param rowId
	 *            der Primärschlüssel
	 * @param columnName
	 *            der Spaltenname der BLobSpalte
	 * @return <code>true</code>, falls kein Wert in der Blob-Spalte steht
	 */
	boolean isBLobEmpty(ContainerRowId rowId, String columnName);

	/**
	 * Prüft ob das Feld vom Typ BLob ist.
	 * 
	 * @param columnName
	 *            der Spaltenname
	 * @return <code>true</code>, wenn das Feld vom Typ BLob ist
	 */
	boolean isBLob(String columnName);

	/**
	 * @param exportCallback
	 *            Callback im Kontext bestimmter Container-Anpassungen.
	 */
	void withExportSettings(ExportWithExportSettings exportCallback);

	/**
	 * Wendet die konfigurierte Default-Sortierung auf den Container an (dies
	 * wirkt sich z. Zt. nicht auf die Hints im Header anzeigende Tabellen aus)
	 */
	void applyDefaultOrder();

	/**
	 * @return the FilterPolicy configuration of the container
	 */
	FilterPolicy getFilterPolicy();

	/**
	 * @return a readonly stream of data rows
	 */
	DataStream getStream();

}
