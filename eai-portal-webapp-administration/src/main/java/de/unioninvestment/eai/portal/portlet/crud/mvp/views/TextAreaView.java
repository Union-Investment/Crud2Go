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

import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * {@link View} für RichText, der sich ggf. inline editieren lässt.
 * 
 * @author carsten.mjartan
 */
public interface TextAreaView extends View {

	/**
	 * {@link Presenter} Interface für die TextArea-Logik.
	 * 
	 */
	public interface Presenter {
		/**
		 * Es wurde auf den inhalt geklickt.
		 */
		void contentAreaClicked();

		/**
		 * Der Inhalt wurde geändert
		 */
		void contentChanged(String content);

		/**
		 * Das Ändern des Inhalts wurde abgebrochen
		 */
		void cancelEditing();
	}

	/**
	 * XHTML anzeigen.
	 * 
	 * @param xhtml
	 */
	void showContent(String xhtml);

	/**
	 * Editor zur Pflege des XHTML-Content anzeigen.
	 * 
	 * @param xhtml
	 */
	void showEditor(String xhtml);

	/**
	 * Komponente ausblenden.
	 */
	void hide();

	/**
	 * Setter für den Presenter, an den alle Interaktionen delegiert werden.
	 * 
	 * @param textAreaPresenter
	 */
	void setPresenter(Presenter textAreaPresenter);
}
