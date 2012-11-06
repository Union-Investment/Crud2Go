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

import java.util.Map;
import java.util.Stack;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Panel;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PanelView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.Presenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Presenter für ein Panel. Das Panel kann entweder die Hauptseite (page, tab)
 * oder einen Dialog anzeigen, der per {@link #attachDialog(String)} geöffnet
 * wurde.
 * 
 * @author Bastian Krol
 */
public class PanelPresenter implements Presenter {

	private static final long serialVersionUID = 1L;

	private final PanelView view;

	/**
	 * Der Content-Presenter der Hauptseite (Page oder Tab).
	 */
	private PanelContentPresenter defaultPanelContentPresenter;

	private final Map<String, DialogPresenter> dialogPresenterMap;

	private Stack<DialogPresenter> dialogStack = new Stack<DialogPresenter>();

	public PanelPresenter(PanelView view, Panel model,
			Map<String, DialogPresenter> dialogPresenterMap) {
		super();
		this.view = view;
		this.dialogPresenterMap = dialogPresenterMap;

		if (model instanceof Tab) {
			view.setCaption(((Tab) model).getTitle());
		}

		model.setPresenter(new Panel.Presenter() {

			/**
			 * {@inheritDoc}
			 * 
			 * @see Panel.Presenter#attachDialog(String)
			 */
			@Override
			public void attachDialog(String dialogId, boolean withMargin) {
				PanelPresenter.this.attachDialog(dialogId, withMargin);
			}

			/**
			 * {@inheritDoc}
			 * 
			 * @see Panel.Presenter#detachDialog()
			 */
			@Override
			public void detachDialog() {
				PanelPresenter.this.detachDialog();
			}
		});
	}

	@Override
	public View getView() {
		return view;
	}

	public void setDefaultPresenter(PanelContentPresenter panelContentPresenter) {
		this.defaultPanelContentPresenter = panelContentPresenter;
		this.view.setContent(panelContentPresenter.getView());
	}

	/**
	 * Ersetzt die Hauptseite oder eine beliebige Unterseite durch die
	 * Unterseite ({@code dialog}) mit der übergebenen ID. Falls diese
	 * Unterseite bereits angezeigt wird, hat der Aufruf keine Auswirkungen.
	 * 
	 * @param dialogId
	 *            die ID des dialog-Tags im XML
	 * @param withMargin 
	 * @throws IllegalArgumentException
	 *             falls kein Dialog mit der gegebenen {@code dialogId}
	 *             existiert
	 */
	public void attachDialog(String dialogId, boolean withMargin) {
		// detach current dialog
		if (!dialogStack.isEmpty()) {
			dialogStack.peek().notifyAboutBeingDetached();
		}

		DialogPresenter dialogPresenter = dialogPresenterMap.get(dialogId);
		if (dialogPresenter == null) {
			throw new IllegalArgumentException(
					"Es existiert kein Dialog mit der ID " + dialogId + ".");
		}
		attachInternal(dialogPresenter);
		
		dialogPresenter.getView().setMargin(withMargin);
		
		dialogStack.push(dialogPresenter);
	}

	private void attachInternal(DialogPresenter dialogPresenter) {
		dialogPresenter.notifyAboutBeingAttached(this);
		view.setContent(dialogPresenter.getView());
	}

	public void detachDialog() {
		if (dialogStack.isEmpty()) {
			return;
		}

		DialogPresenter attachedDialogPresenter = dialogStack.pop();
		attachedDialogPresenter.notifyAboutBeingDetached();

		// Go back
		if (!dialogStack.isEmpty()) {
			attachInternal(dialogStack.peek());
		} else {
			setDefaultView();
		}
	}

	PanelContentPresenter getCurrentContentPresenter() {
		if (dialogStack.isEmpty()) {
			return defaultPanelContentPresenter;
		} else {
			return dialogStack.peek();
		}
	}

	private void setDefaultView() {
		if (defaultPanelContentPresenter != null) {
			view.setContent(defaultPanelContentPresenter.getView());
		} else {
			throw new IllegalStateException(
					"Es wurde kein defaultPanelContentPresenter gesetzt.");
		}
	}
}
