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

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.crud2go.spi.security.Cryptor;
import de.unioninvestment.crud2go.spi.security.CryptorFactory;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsPasswordConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsUsernameConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.PreferenceProperty;
import de.unioninvestment.eai.portal.portlet.crud.ui.security.EncryptionFormatter;
import de.unioninvestment.eai.portal.portlet.crud.ui.security.SecurePasswordField;

/**
 * View-Objekt der CRUD PortletPresenter Konfiguration (PortletPresenter Edit
 * Mode).
 * 
 * @author markus.bonsch
 * @author polina.vinarski
 * 
 */
public class DefaultPortletConfigurationView extends VerticalLayout implements
		PortletConfigurationView {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultPortletConfigurationView.class);

	private Upload upload;

	private HorizontalLayout uploadVcsLayout = new HorizontalLayout();

	private TextField uploadVcsUri;

	private Button uploadVcsButton;

	private Label status;

	VerticalLayout rolesLayout;

	VerticalLayout authenticationPreferencesLayout;

	private Presenter presenter;

	private CryptorFactory cryptorFactory;

	TabSheet tabsheet;

	/**
	 * Konstruktor für die View der Portletkonfigurationsseite.
	 */
	public DefaultPortletConfigurationView(CryptorFactory cryptorFactory) {
		this.cryptorFactory = cryptorFactory;

		tabsheet = new TabSheet();

		VerticalLayout statusLayout = createStatusLayout();
		tabsheet.addComponent(statusLayout);

		addComponent(tabsheet);
	}

	private VerticalLayout createStatusLayout() {

		status = new Label();
		status.setContentMode(Label.CONTENT_RAW);

		upload = new Upload();
		upload.setImmediate(true);
		upload.setButtonCaption(getMessage("portlet.crud.page.upload.button"));

		uploadVcsUri = new TextField();
		uploadVcsUri.setWidth(400, Sizeable.UNITS_PIXELS);
		uploadVcsButton = new Button();
		uploadVcsButton
				.setCaption(getMessage("portlet.crud.page.upload.vcs.button"));

		uploadVcsLayout.addComponent(uploadVcsButton);
		uploadVcsLayout.addComponent(uploadVcsUri);
		uploadVcsLayout.setSpacing(true);

		VerticalLayout statusLayout = new VerticalLayout();
		statusLayout
				.setCaption(getMessage("portlet.crud.page.edit.statusHeader"));
		statusLayout.setSpacing(true);
		statusLayout.setMargin(true);

		statusLayout.addComponent(status);
		statusLayout.addComponent(uploadVcsLayout);
		statusLayout.addComponent(upload);
		return statusLayout;
	}

	public Button getUploadVcsButton() {
		return uploadVcsButton;
	}

	public TextField getUploadVcsUri() {
		return uploadVcsUri;
	}

	public Upload getUpload() {
		return upload;
	}

	@Override
	public void showNotification(String msgKey, int type) {
		getApplication().getMainWindow().showNotification(msgKey, type);
	}

	@Override
	public void setStatus(String string, Object... args) {
		if (args == null || args.length < 1) {
			status.setValue(getMessage(string));
		} else {
			status.setValue(getMessage(string, args));
		}
	}

	Label getStatusLabel() {
		return status;
	}

	@Override
	public void displayRoles(Set<Role> roles) {
		if (roles.size() > 0) {
			if (rolesLayout != null) {
				rolesLayout.removeAllComponents();
			} else {
				rolesLayout = new VerticalLayout();
				rolesLayout
						.setCaption(getMessage("portlet.crud.page.edit.securityHeader"));
				rolesLayout.setMargin(true);
				rolesLayout.setSpacing(true);
				tabsheet.addComponent(rolesLayout);
			}

			rolesLayout.addComponent(new Label(
					getMessage("portlet.crud.page.edit.roles.description")));

			for (Role role : roles) {
				ExternalResource res = new ExternalResource(
						role.getPermissionsURL());
				rolesLayout.addComponent(new Link(role.getName(), res));
			}
		} else {
			hideRoles();
		}
	}

	@Override
	public void hideRoles() {
		if (rolesLayout != null) {
			tabsheet.removeComponent(rolesLayout);
			rolesLayout = null;
		}
	}

	@Override
	public void displayAuthenticationPreferences(Config portletConfig) {

		final Form form = createAuthenticationPreferencesForm();
		boolean hasPreferences = fillOutAuthenticationPreferencesForm(
				portletConfig, form);
		if (hasPreferences) {
			if (authenticationPreferencesLayout != null) {
				authenticationPreferencesLayout.removeAllComponents();
			} else {
				authenticationPreferencesLayout = new VerticalLayout();
				authenticationPreferencesLayout
						.setCaption(getMessage("portlet.crud.page.edit.authenticationHeader"));
				authenticationPreferencesLayout.setMargin(true);
				tabsheet.addComponent(authenticationPreferencesLayout);
			}

			authenticationPreferencesLayout
					.addComponent(new Label(
							getMessage("portlet.crud.page.edit.authentication.description")));

			authenticationPreferencesLayout.addComponent(form);
		} else {
			hideAuthenticationPreferences();
		}
	}

	private boolean fillOutAuthenticationPreferencesForm(Config portletConfig,
			final Form form) {
		boolean hasPreferences = false;
		if (portletConfig != null && portletConfig.getPortletConfig() != null
				&& portletConfig.getPortletConfig().getAuthentication() != null) {
			List<AuthenticationRealmConfig> realms = portletConfig
					.getPortletConfig().getAuthentication().getRealm();
			for (AuthenticationRealmConfig realm : realms) {
				if (realm.getCredentials() != null) {
					CredentialsUsernameConfig username = realm.getCredentials()
							.getUsername();
					if (username != null && username.getPreferenceKey() != null) {
						TextField field = new TextField(realm.getName()
								+ " Username",
								new PreferenceProperty(
										username.getPreferenceKey()));
						field.addStyleName(createValidClassName(username
								.getPreferenceKey()));
						field.setNullRepresentation("");
						form.addField(username.getPreferenceKey(), field);
						hasPreferences = true;
					}
					CredentialsPasswordConfig password = realm.getCredentials()
							.getPassword();
					if (password != null && password.getPreferenceKey() != null) {
						Cryptor cryptor = cryptorFactory.getCryptor(password
								.getEncryptionAlgorithm());
						Property preferenceProperty = new PreferenceProperty(
								password.getPreferenceKey());
						EncryptionFormatter encryptionFormatter = new EncryptionFormatter(
								cryptor,
								preferenceProperty);
						SecurePasswordField field = new SecurePasswordField(
								realm.getName() + " Passwort",
								encryptionFormatter);
						field.addStyleName(createValidClassName(password
								.getPreferenceKey()));
						field.setNullRepresentation("");
						form.addField(password.getPreferenceKey(), field);
						hasPreferences = true;
					}
				}
			}
		}
		return hasPreferences;
	}

	private String createValidClassName(String preferenceKey) {
		return preferenceKey.replaceAll("[^A-Za-z0-9_\\-]", "_");
	}

	private Form createAuthenticationPreferencesForm() {
		final Form form = new Form();
		form.setWriteThrough(false);

		Button submitButton = new Button("Einstellungen speichern",
				new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						form.commit();
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
	public void hideAuthenticationPreferences() {
		if (authenticationPreferencesLayout != null) {
			tabsheet.removeComponent(authenticationPreferencesLayout);
			authenticationPreferencesLayout = null;
		}
	}

	@Override
	public void switchToAuthenticationPreferences() {
		if (authenticationPreferencesLayout != null) {
			tabsheet.setSelectedTab(authenticationPreferencesLayout);
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
