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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Repräsentiert eine den Inhalt einer Seite des Portlets. Dies kann ein Seite
 * (XML-Tag {@code page}), ein Tab (XML-Tag {@code tab}) oder ein Dialog
 * (XML-Tag {@code dialog}, siehe {@link DialogPresenter}) sein. Dieser
 * Presenter ist dafür zuständig, die einzelnen Element, die die betreffende
 * Seite enthält, anzuzeigen. Im Gegensatz dazu ist der {@link PanelPresenter}
 * ein Wrapper, der es ermöglicht, den Inhalt eines Panels komplett
 * auszutauschen (z. B. für Dialoge).
 * 
 * @author carsten.mjartan
 * @see PanelPresenter
 */
public class PanelContentPresenter implements ComponentPresenter {

	private static final long serialVersionUID = 1L;

	private final PanelContentView view;

	private Panel model;

	private List<ComponentPresenter> components = new ArrayList<ComponentPresenter>();

	/**
	 * Initialisiert die Seite.
	 * 
	 * @param view
	 *            das {@link View}-Objekt
	 * 
	 * @param model
	 *            Panel
	 */
	public PanelContentPresenter(PanelContentView view, Panel model) {
		this.view = view;
		this.model = model;
	}

	@Override
	public PanelContentView getView() {
		return view;
	}

	public String getTitle() {
		return model instanceof Tab ? ((Tab) model).getTitle() : null;
	}

	/**
	 * @param presenter
	 *            Presenter
	 */
	public void addComponent(ComponentPresenter presenter) {
		this.components.add(presenter);
		view.addComponent(presenter.getView());
	}

	List<ComponentPresenter> getComponents() {
		return unmodifiableList(components);
	}
}
