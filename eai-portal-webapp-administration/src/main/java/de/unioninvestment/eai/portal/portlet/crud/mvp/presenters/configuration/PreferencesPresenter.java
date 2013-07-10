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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.configuration;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.server.VaadinPortletService;

import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.DefaultPreferencesView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PreferencesView;
import de.unioninvestment.eai.portal.support.vaadin.mvp.AbstractPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * UI Logic for the {@link PreferencesView}.
 * 
 * @author carsten.mjartan
 */
@Configurable
public class PreferencesPresenter extends AbstractPresenter<PreferencesView>
		implements PreferencesView.Presenter {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PreferencesPresenter.class);

	private EventBus eventBus;

	public PreferencesPresenter(DefaultPreferencesView view, EventBus eventBus) {
		super(view);
		this.eventBus = eventBus;
	}

	@Override
	public void storePreferencesAndFireConfigChange() {
		try {
			getView().commit();

			VaadinPortletService.getCurrentPortletRequest().getPreferences()
					.store();
			eventBus.fireEvent(new ConfigurationUpdatedEvent(true));

			getView().showNotification(
					getMessage("portlet.crud.page.edit.storedSettings"));

		} catch (Exception e) {
			LOGGER.error("Error storing preferences", e);
			getView().showError(
					getMessage("portlet.crud.error.storingPreferences"));
		}
	}
}
