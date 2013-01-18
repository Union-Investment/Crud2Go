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

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Dialog;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelContentView;

/**
 * Presenter für einen Dialog.
 * 
 * @author Bastian Krol
 */
public class DialogPresenter extends PanelContentPresenter {

	private static final long serialVersionUID = 1L;

	/**
	 * Das Panel, in dem dieser Dialog momentan attached ist, falls er attached
	 * ist, ansonsten null.
	 */
	private PanelPresenter parentPanelPresenter;

	protected Button backButton;

	private Dialog model;

	/**
	 * Erzeugt einen DialogPresenter.
	 * 
	 * @param view
	 *            die View für diesen Presenter
	 * @param model
	 *            das Modell-Objekt
	 */
	public DialogPresenter(PanelContentView view, Dialog model) {
		super(view, model);
		this.model = model;
		addBackButton(model.getBackButtonCaption());
	}

	private void addBackButton(String caption) {
		backButton = new Button(caption);
		backButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				DialogPresenter.this.detach();
			}
		});

		getView().addComponent(backButton);

		// FIXME There is no guarantee that the PanelContentView-implementation
		// given will provide any of those.
		// In consequence, the alignment of the component can currently not
		// really be configured by the presenter. Perhaps some kind of
		// "alignable"-interface needs to be provided.
		AbstractOrderedLayout layout = null;
		if (getView() instanceof AbstractOrderedLayout) {
			layout = (AbstractOrderedLayout) getView();
		} else if (getView() instanceof Panel) {
			ComponentContainer content = ((Panel) getView()).getContent();
			if (content instanceof AbstractOrderedLayout) {
				layout = (AbstractOrderedLayout) content;
			}
		}
		if (layout != null) {
			layout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
		}

	}

	/**
	 * Setzt den Dialog darüber in Kenntnis, dass er im PanelPresenter
	 * {@code parent} hinzugefügt (attached) wird. Sollte der Dialog momentan
	 * bereits irgendwo anders hinzugefügt sein, wird er dort zuerst abgetrennt
	 * (detached).
	 * 
	 * @param parent
	 *            der {@link PanelPresenter}, zu dem der Dialog hinzugefügt wird
	 */
	public void notifyAboutBeingAttached(PanelPresenter parent) {
		this.detach();
		model.fireShowEvent();
		parentPanelPresenter = parent;
	}

	/**
	 * Setzt den Dialog darüber in Kenntnis, dass er abgetrennt (detached) wird.
	 */
	public void notifyAboutBeingDetached() {
		parentPanelPresenter = null;
	}

	/**
	 * Trennt den Dialog ab, falls er momentan irgendwo hinzugefügt ist. Ist
	 * dies nicht der Fall, hat der Aufruf keine Auswirkungen. Diese Methode
	 * wird auch aufgerufen, wenn der Zurück-Button gedrückt wird.
	 */
	void detach() {
		if (isAttached()) {
			parentPanelPresenter.detachDialog();
			parentPanelPresenter = null;
		}
	}

	/**
	 * Gibt {@code true} zurück, falls der Dialog momentan irgendwo hinzugefügt
	 * ist.
	 * 
	 * @return {@code true}, falls der Dialog momentan irgendwo hinzugefügt ist
	 */
	public boolean isAttached() {
		return parentPanelPresenter != null;
	}
}
