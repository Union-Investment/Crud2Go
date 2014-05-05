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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import groovy.lang.Closure;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.Reindeer;

/**
 * Closure Implementation, um aus dem Skriptkontext einen ConfirmationDialog
 * anzuzeigen.
 * 
 * @author carsten.mjartan
 */
public class ConfirmationDialogProvider extends Closure<String> {

	/**
	 * Factory-Klasse, die für eine umgekehrte Reihenfolge der Buttons sorgt
	 * (Standard ist Gnome-Style).
	 * 
	 * @author carsten.mjartan
	 */
	private static final class ReverseButtonOrderConfirmDialogFactory extends
			DefaultConfirmDialogFactory {
		private static final long serialVersionUID = 1L;

		// We change the default order of the buttons
		@Override
		public ConfirmDialog create(String caption, String message,
				String okCaption, String cancelCaption) {
			ConfirmDialog d = super.create(caption, message, okCaption,
					cancelCaption);

			// Find the buttons and change the order
			Button ok = d.getOkButton();
			ok.setDisableOnClick(true);
			HorizontalLayout buttons = (HorizontalLayout) ok.getParent();
			buttons.removeComponent(ok);
			buttons.addComponent(ok, 1);
			buttons.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT);

			// Change the default
			Button cancel = d.getCancelButton();
			cancel.setDisableOnClick(true);
			cancel.setStyleName(Reindeer.BUTTON_DEFAULT);
			ok.removeStyleName(Reindeer.BUTTON_DEFAULT);
			cancel.focus();

			return d;
		}
	}

	/**
	 * Interface für das Ergebnis des Dialogs, das an die angegebene Closure als
	 * Parameter übergeben wird.
	 * 
	 * @author carsten.mjartan
	 */
	public interface Result {
		boolean isConfirmed();
	}

	private static final long serialVersionUID = 1L;

	static {
		ConfirmDialog.Factory df = new ReverseButtonOrderConfirmDialogFactory();
		ConfirmDialog.setFactory(df);
	}

	/**
	 * Konstruktor
	 * 
	 * @param owner
	 *            das Main-Script
	 */
	public ConfirmationDialogProvider(Object owner) {
		super(owner);
	}

	/**
	 * @param title
	 *            der Fenstertitel
	 * @param message
	 *            die anzuzeigende Meldung
	 * @param confirmButtonText
	 *            der Text des Bestätigungs-Buttons
	 * @param declineButtonText
	 *            der Text des Ablehnen-Buttons
	 * @param action
	 *            die aufzurufende Closure. Als Parameter wird das
	 *            {@link Result} übergeben.
	 */
	public void doCall(String title, String message, String confirmButtonText,
			String declineButtonText, final Closure<?> action) {
		ConfirmDialog.show(UI.getCurrent(), title, message, confirmButtonText,
				declineButtonText, new Listener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClose(final ConfirmDialog dialog) {
						action.call(new Result() {
							@Override
							public boolean isConfirmed() {
								return dialog.isConfirmed();
							}
						});
					}
				});
	}
}
