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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.crud2go.spi.security.Cryptor;
import de.unioninvestment.crud2go.spi.security.CryptorFactory;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.PreferenceProperty;
import de.unioninvestment.eai.portal.portlet.crud.ui.security.EncryptionFormatter;
import de.unioninvestment.eai.portal.portlet.crud.ui.security.SecurePasswordField;

@Configurable
@SuppressWarnings("serial")
public class DefaultPreferencesView extends CustomComponent implements
		PreferencesView {

	private static final long serialVersionUID = 1L;

	private Form form;

	protected Presenter presenter;

	@Autowired
	private CryptorFactory cryptorFactory;

	public DefaultPreferencesView(String caption) {

		setCaption(caption);

		this.form = createForm();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(form);

		setCompositionRoot(layout);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	
	private Form createForm() {
		final Form form = new Form();
		form.setBuffered(true);

		Button submitButton = new Button("Einstellungen speichern",
				new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						presenter.storePreferencesAndFireConfigChange();
					}
				});

		Button revertButton = new Button("Zurücksetzen", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				form.discard();
			}
		});

		HorizontalLayout footerLayout = new HorizontalLayout();
		footerLayout.setSpacing(true);
		footerLayout.addComponent(submitButton);
		footerLayout.addComponent(revertButton);

		form.getFooter().addComponent(footerLayout);
		return form;
	}

	@Override
	public void commit() {
		form.commit();
	}

	@Override
	public void display(List<PreferenceTO> preferences) {
		for (PreferenceTO preference : preferences) {
			String key = preference.getPreferenceKey();

			AbstractTextField field;
			if (preference.isPassword()) {
				Cryptor cryptor = cryptorFactory.getCryptor(preference
						.getEncryptionAlgorithm());
				PreferenceProperty preferenceProperty = new PreferenceProperty(
						key);
				EncryptionFormatter encryptionFormatter = new EncryptionFormatter(
						cryptor);
				field = new SecurePasswordField(preference.getTitle(),
						preferenceProperty);
				field.setConverter(encryptionFormatter);

			} else {
				field = new TextField(preference.getTitle(),
						new PreferenceProperty(key));
			}
			field.addStyleName(createValidClassName(key));
			field.setNullRepresentation("");
			field.setInputPrompt(preference.getDefaultValue());
			field.setWidth("100%");
			form.addField(key, field);
		}
	}

	@Override
	public void showError(String message) {
		Notification.show(message, Notification.Type.ERROR_MESSAGE);
	}

	@Override
	public void showNotification(String message) {
		Notification.show(message, Notification.Type.HUMANIZED_MESSAGE);
	}

	private String createValidClassName(String preferenceKey) {
		return preferenceKey.replaceAll("[^A-Za-z0-9_\\-]", "_");
	}

}
