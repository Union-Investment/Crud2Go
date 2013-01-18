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

import de.unioninvestment.eai.portal.portlet.crud.config.DialogConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Repräsentiert einen Dialog für Unterseiten/Detailseiten.
 * 
 * @author Bastian Krol
 * @author Jan Malcomess (codecentric AG)
 */
public class Dialog extends Panel implements LazyInitializable<Dialog> {

	private static final long serialVersionUID = 1L;

	/**
	 * @deprecated only available for compatibility to
	 *             <code>{@link RowEditingFormDialog}</code>.
	 */
	private final String id;

	/**
	 * @deprecated only available for compatibility to
	 *             <code>{@link RowEditingFormDialog}</code>.
	 */
	private final String backButtonCaption;

	private final EventRouter<ShowEventHandler<Dialog>, ShowEvent<Dialog>> showEventRouter = new EventRouter<ShowEventHandler<Dialog>, ShowEvent<Dialog>>();

	/**
	 * Konstruktor.
	 * 
	 * @param id
	 *            Id des Dialogs
	 * @param backButtonCaption
	 *            Beschriftung des Zurück-Buttons
	 * @deprecated This constructor is only available for compatibility to
	 *             <code>{@link RowEditingFormDialog}</code>.
	 */
	protected Dialog(String id, String backButtonCaption) {
		super();
		this.id = id;
		this.backButtonCaption = backButtonCaption;
	}

	/**
	 * @param config
	 *            the configuration of this dialog.
	 * @since 1.45
	 */
	public Dialog(DialogConfig config) {
		super(config);
		this.id = null;
		this.backButtonCaption = null;
	}

	public String getId() {
		return (getConfig() == null ? this.id : getConfig().getId());
	}

	public String getBackButtonCaption() {
		return (getConfig() == null ? this.backButtonCaption : getConfig()
				.getBackButtonCaption());
	}

	@Override
	public void addShowEventListener(ShowEventHandler<Dialog> showEventListener) {
		this.showEventRouter.addHandler(showEventListener);
	}

	/**
	 * Feuert ein {@link ShowEvent}.
	 */
	public void fireShowEvent() {
		ShowEvent<Dialog> event = new ShowEvent<Dialog>(this);
		showEventRouter.fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DialogConfig getConfig() {
		return (DialogConfig) super.getConfig();
	}
}
