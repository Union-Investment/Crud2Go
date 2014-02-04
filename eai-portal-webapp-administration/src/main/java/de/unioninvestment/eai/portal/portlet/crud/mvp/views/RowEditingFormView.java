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

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;

/**
 * 
 * Bei einspaltiger Ansicht werden die Felder untereinander, und links davon als
 * Label jeweils die Titel angezeigt. Bei mehrspaltiger Ansicht werden die Label
 * über den Eingabefeldern angezeigt.
 * 
 * Unter den Eingabefeldern werden Aktions-Buttons horizontal angeordnet
 * dargestellt.
 * 
 */
public interface RowEditingFormView extends PanelContentView {

	/**
	 * Interface für den Zugrifft auf den Presenter für die View.
	 * 
	 * 
	 * @author siva.selvarajah
	 */
	public interface Presenter {
		/**
		 * Speichert den aktuellen Datensatz.
		 */
		void save();

		/**
		 * Löscht den aktuellen Datensatz.
		 */
		void delete();

		/**
		 * @return <code>true</code>, falls es eine vorherige Zeile gibt
		 */
		boolean hasPreviousRow();

		/**
		 * @return <code>true</code>, falls es eine nachfolgende Zeile gibt
		 */
		boolean hasNextRow();

		/**
		 * Läd die nächste Zeile.
		 * 
		 * @return Ob, eine nächste Zeile existiert
		 */
		boolean nextRow();

		/**
		 * Läd die vorherige Zeile.
		 * 
		 * @return Ob, eine vorherige Zeile existiert
		 */
		boolean previousRow();

		/**
		 * Setzt die Werte aller Felder zurück.
		 */
		void resetFields();

		/**
		 * Schießt den Editierdilog.
		 */
		void cancel();

		public List<String> getVisibleFields();

		void addClobFields(Item item);

		void changeMode();
	}

	/**
	 * Initialisiert die View.
	 * 
	 * @param presenter
	 *            EditingForm-Presenter
	 */
	void initialize(Presenter presenter, Table tableModel);

	/**
	 * Zeigt einen Datensatz im Formular-Dialog an.
	 * 
	 * @param row
	 * @param editable
	 *            <code>true</code>, if the form should provide editing
	 *            capabilities
	 * @param deletable
	 *            <code>true</code>, if the form should provide row deletion
	 * 
	 * @param item
	 *            Datensatz
	 */
	void displayRow(ContainerRow row, boolean editable, boolean deletable);

	boolean isFieldModifed(String fieldName);

	public void addBlobField(TableColumn tableColumn,
			ContainerBlob containerBlob, boolean readonly);

	void discard();

	void commit() throws CommitException;

	void showFormError(String message);

	void addClobField(TableColumn tableColumn, boolean readOnly);

	void hideFormError();

	void updateButtonsForViewMode();

	void updateButtonsForEditMode();

}
