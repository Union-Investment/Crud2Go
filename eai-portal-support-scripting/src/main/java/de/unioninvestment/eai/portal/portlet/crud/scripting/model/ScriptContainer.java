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

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;

/**
 * Basisklasse für Container, die Event-Handler(onInsert, onCreate, onDelete,
 * onUpdate, onCommit) in Form von Groovy-Skripten zur Verfügung stellen.
 */
public class ScriptContainer extends ScriptComponent {

	protected DataContainer container;
	private Closure<?> onInsert;
	private Closure<?> onCreate;
	private Closure<?> onDelete;
	private Closure<?> onUpdate;
	private Closure<?> onCommit;

	public ScriptContainer(DataContainer container) {
		super();
		this.container = container;
	}

	/**
	 * @param closure
	 *            eine Closure, in der die Methoden aus der
	 *            {@link ScriptFilterFactory} direkt aufgerufen werden können,
	 *            um Filter auf dem Container zu setzen.
	 */
	public void addFilters(Closure<?> closure) {
		Object oldDelegate = closure.getDelegate();
		try {
			List<Filter> filters = new FilterClosureCallable(closure).call();
			container.addFilters(filters);
		} finally {
			closure.setDelegate(oldDelegate);
		}

	}

	/**
	 * 
	 * @param closure
	 *            eine Closure, in der die Methoden aus der
	 *            {@link ScriptFilterFactory} direkt aufgerufen werden können,
	 *            um Filter auf dem Container zu setzen.
	 */
	public void replaceFilters(Closure<?> closure) {
		Object oldDelegate = closure.getDelegate();
		try {
			List<Filter> filters = new FilterClosureCallable(closure).call();
			container.replaceFilters(filters, false);
		} finally {
			closure.setDelegate(oldDelegate);
		}

	}

	/**
	 * @param namedArgs
	 *            optional parameters, <code>timeout</code>: Timeout-Zeit in
	 *            Sekunden
	 * @param closure
	 *            eine Closure, in der die Methoden aus der
	 *            {@link ScriptFilterFactory} direkt aufgerufen werden können,
	 *            um Filter auf dem Container zu setzen.
	 */
	public void replaceFilters(Map<String, Object> namedArgs, Closure<?> closure) {
		Object oldDelegate = closure.getDelegate();
		try {
			List<Filter> filters = new FilterClosureCallable(closure).call();

			int timeout = extractTimeoutValue(namedArgs);
			container.replaceFilters(filters, false, timeout);
		} finally {
			closure.setDelegate(oldDelegate);
		}

	}

	private int extractTimeoutValue(Map<String, Object> namedArgs) {
		int timeout = 0;
		Object timeoutValue = namedArgs.get("timeout");
		if (timeoutValue != null) {
			if (timeoutValue instanceof Number) {
				timeout = ((Number) timeoutValue).intValue();
			} else {
				throw new IllegalArgumentException(
						"'timeout' needs to be of type Integer");
			}
		}
		return timeout;
	}

	/**
	 * Entfernt alle auf dem Container gesetzten Filter (durable verbleiben)
	 */
	public void removeAllFilters() {
		container.removeAllFilters();
	}

	/**
	 * Entfernt alle auf dem Container gesetzten Filter.
	 * 
	 * @param removeDurable
	 *            ob die durable-Filter auch mit entfernt werden sollen
	 */
	public void removeAllFilters(boolean removeDurable) {
		container.removeAllFilters(removeDurable);
	}

	/**
	 * Löschen aller Zeilen im Container die den aktuellen Filterkriterien
	 * entsprechen
	 * 
	 * Bei großen Datenmengen inperformant, da die komplette gefilterte Tabelle
	 * eingelesen wird. Bitte in diesem Fall die Filter selbst auslesen und per
	 * sql()-API löschen
	 */
	public void removeAllRows() {
		container.removeAllRows();
	}

	/**
	 * Fügt eine neue Zeile hinzu.
	 * 
	 * @return Neue Zeile
	 */
	public ScriptRow addRow() {
		ContainerRow row = container.addRow();
		return new ScriptRow(row);
	}

	/**
	 * Liefert die {@link ScriptRowId}s aller in diesem Container enthaltenen
	 * Zeilen zurück.
	 * 
	 * @return Liste der Ids
	 */
	public List<ScriptRowId> getRowIds() {
		List<ScriptRowId> result = new ArrayList<ScriptRowId>();
		for (ContainerRowId rowId : container.getRowIds()) {
			result.add(new ScriptRowId(rowId));
		}
		return result;
	}

	/**
	 * @return Closure mit zwei Parametern { ScriptContainer it, ScriptRow row
	 *         -> ... }, die nach dem Speichern einer neuen Zeile aufgerufen
	 *         wird
	 */
	public Closure<?> getOnInsert() {
		return onInsert;
	}

	/**
	 * @param onInsert
	 *            Closure mit zwei Parametern { ScriptContainer it, ScriptRow
	 *            row -> ... }, die nach dem Erstellen einer neuen Zeile
	 *            aufgerufen wird
	 */
	public void setOnInsert(Closure<?> onInsert) {
		this.onInsert = onInsert;
	}

	/**
	 * @return Closure mit zwei Parametern { ScriptContainer it, ScriptRow row
	 *         -> ... }, die nach dem Erstellen einer neuen Zeile aufgerufen
	 *         wird
	 */
	public Closure<?> getOnCreate() {
		return onCreate;
	}

	/**
	 * @param onCreate
	 *            Closure mit zwei Parametern { ScriptContainer it, ScriptRow
	 *            row -> ... }, die nach dem Speichern einer neuen Zeile
	 *            aufgerufen wird
	 */
	public void setOnCreate(Closure<?> onCreate) {
		this.onCreate = onCreate;
	}

	/**
	 * @return Closure mit zwei Parametern { ScriptContainer it, ScriptRow row
	 *         -> ... }, die nach dem Löschen einer Zeile aufgerufen wird
	 */
	public Closure<?> getOnDelete() {
		return onDelete;
	}

	/**
	 * @param onDelete
	 *            Closure mit zwei Parametern { ScriptContainer it, ScriptRow
	 *            row -> ... }, die nach dem Löschen einer Zeile aufgerufen wird
	 */
	public void setOnDelete(Closure<?> onDelete) {
		this.onDelete = onDelete;
	}

	/**
	 * @return Closure mit zwei Parametern { ScriptContainer it, ScriptRow row
	 *         -> ... }, die nach dem Update einer Zeile aufgerufen wird
	 */
	public Closure<?> getOnUpdate() {
		return onUpdate;
	}

	/**
	 * @param onUpdate
	 *            Closure mit zwei Parametern { ScriptContainer it, ScriptRow
	 *            row -> ... }, die nach dem Update einer Zeile aufgerufen wird
	 */
	public void setOnUpdate(Closure<?> onUpdate) {
		this.onUpdate = onUpdate;
	}

	/**
	 * @return Closure mit dem Parameter { ScriptContainer it -> ... }, die nach
	 *         einem Commit aufgerufen wird.
	 */
	public Closure<?> getOnCommit() {
		return onCommit;
	}

	/**
	 * Committed alle Änderungen des Containers bzw. in gerade editierten
	 * Änderungen der Tabelle in die Datenbank
	 */
	public void commit() {
		container.commit();
	}

	/**
	 * @param onCommit
	 *            Closure mit dem Parameter { ScriptContainer it -> ... }, die
	 *            nach einem Commit aufgerufen wird.
	 */
	public void setOnCommit(Closure<?> onCommit) {
		this.onCommit = onCommit;
	}

	protected void initializeEventHandler() {
		container.addInsertEventHandler(new InsertEventHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onInsert(InsertEvent event) {

				if (onInsert != null) {
					onInsert.call(ScriptContainer.this,
							createScriptRow(event.getRow()));
				}

			}

		});

		container.addCreateEventHandler(new CreateEventHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onCreate(CreateEvent event) {

				if (onCreate != null) {
					onCreate.call(ScriptContainer.this,
							createScriptRow(event.getRow()));
				}

			}
		});

		container.addDeleteEventHandler(new DeleteEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(DeleteEvent event) {

				if (onDelete != null) {
					onDelete.call(ScriptContainer.this,
							createScriptRow(event.getRow()));
				}
			}
		});

		container.addCommitEventHandler(new CommitEventHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onCommit(CommitEvent event) {

				if (onCommit != null) {
					onCommit.call(ScriptContainer.this);
				}

			}
		});

		container.addUpdateEventHandler(new UpdateEventHandler() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onUpdate(UpdateEvent event) {

				if (onUpdate != null) {
					onUpdate.call(ScriptContainer.this,
							createScriptRow(event.getRow()));
				}

			}
		});
	}

	private ScriptRow createScriptRow(ContainerRow row) {
		return new ScriptRow(row);
	}

	/**
	 * Aktualisiert die Daten auf der Tabelle.
	 */
	public void refresh() {
		container.refresh();
	}

	DataContainer getContainer() {
		return container;
	}

}