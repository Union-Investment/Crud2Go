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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Window.Notification;

import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsPasswordConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.CredentialsUsernameConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RoleConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.RolesConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.DefaultPortletRolesView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.DefaultPreferencesView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletConfigurationView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletRoleTO;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PortletRolesView;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PreferenceTO;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.configuration.PreferencesView;
import de.unioninvestment.eai.portal.portlet.crud.persistence.ConfigurationMetaData;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;
import de.unioninvestment.eai.portal.portlet.crud.validation.ConfigurationUploadValidator;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplication;
import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;
import de.unioninvestment.eai.portal.support.vaadin.mvp.AbstractPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

/**
 * Presenter Object der Crud PortletPresenter Konfiguration (Edit Mode).
 * 
 * @author markus.bonsch
 * 
 */
@Configurable
public class PortletConfigurationPresenter extends
		AbstractPresenter<PortletConfigurationView> implements
		PortletConfigurationView.Presenter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PortletConfigurationPresenter.class);

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(PortletConfigurationPresenter.class);

	private ConfigurationReceiver receiver;

	@Autowired
	private transient ConfigurationService configurationService;

	@Autowired
	private transient EventBus eventBus;

	private Settings settings;

	private PreferencesView prefsView;
	private PreferencesView authView;

	private String portletId;

	private long communityId;

	private String currentUsername;

	private PortletRolesView rolesView;

	/**
	 * 
	 * @param portletConfigurationView
	 *            die View
	 * @param configurationService
	 *            Service Bean für die Portletkonfiguration.
	 * @param eventBus
	 *            für applikationsweites Eventhandler.
	 * @param settings
	 */
	public PortletConfigurationPresenter(
			PortletConfigurationView portletConfigurationView,
			ConfigurationService configurationService, EventBus eventBus,
			Settings settings) {

		super(portletConfigurationView);
		this.configurationService = configurationService;
		this.eventBus = eventBus;
		this.settings = settings;
		this.receiver = new ConfigurationReceiver();
		getView().setPresenter(this);
		getView().getUpload().setReceiver(receiver);
		getView().getUpload().addListener(new ConfigUploadFinishedListener());
		getView().getUploadVcsButton().addListener(
				new ConfigUploadVcsFinishedListener());

		LiferayApplication app = LiferayApplication.getCurrentApplication();
		PortletRequest request = PortletApplication.getCurrentRequest();

		portletId = app.getPortletId();
		communityId = app.getCommunityId();
		currentUsername = request.getRemoteUser();

		initStatus();
	}

	/**
	 * Persistiert die Konfiguration in die Datenbank.
	 * 
	 * @param fileName
	 *            Dateinameder zu der Konfiguration gespeichert werden soll
	 * @param configurationXml
	 *            der XML-Content
	 */
	void saveConfig(String fileName, byte[] configurationXml) {

		configurationService.storeConfigurationFile(fileName, configurationXml,
				portletId, communityId, currentUsername);

		LOG.info("Saving config for portlet with portletId: " + portletId
				+ " successfull.");
	}

	private void initStatus() {
		ConfigurationMetaData metaData = configurationService
				.getConfigurationMetaData(portletId, communityId);

		if (metaData == null) {
			getView().setStatus("portlet.crud.page.status.config.notAvailable");

		} else {
			getView().setStatus("portlet.crud.page.status.config.available",
					metaData.getUser(), metaData.getCreated(),
					metaData.getFileName());
		}
	}

	/**
	 * Listener für erfolgreiche Vaadin Upload Events.
	 * 
	 * @author markus.bonsch
	 * 
	 */
	final class ConfigUploadFinishedListener implements Upload.FinishedListener {

		private static final long serialVersionUID = 1L;
		private ConfigurationUploadValidator validator = new ConfigurationUploadValidator();

		/**
		 * {@inheritDoc}
		 * 
		 * @see com.vaadin.ui.Upload.FinishedListener#uploadFinished(com.vaadin.ui.Upload.FinishedEvent)
		 */
		public void uploadFinished(FinishedEvent event) {
			if (validator.isValid(receiver.getConfigurationXML())) {
				saveConfig(receiver.getFilename(),
						receiver.getConfigurationXML());

				boolean configurable = refresh();
				eventBus.fireEvent(new ConfigurationUpdatedEvent(configurable));

			} else {
				getView().showNotification(
						getMessage("portlet.crud.page.upload.invalid"),
						Notification.TYPE_ERROR_MESSAGE);
			}
			getView().getUpload().setVisible(true);
		}

		void setValidator(ConfigurationUploadValidator validator) {
			this.validator = validator;
		}
	}

	/**
	 * Listener fuer erfolgreiche VCS Upload Events.
	 * 
	 * @author polina.vinarski
	 * 
	 */
	final class ConfigUploadVcsFinishedListener implements Button.ClickListener {

		private static final long serialVersionUID = 1L;
		private ConfigurationUploadValidator validator = new ConfigurationUploadValidator();

		/**
		 * {@inheritDoc}
		 * 
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			String vcsUri = (String) getView().getUploadVcsUri().getValue();
			byte[] configValueVcs = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				if (StringUtils.hasText(settings.getVcsUser())) {
					httpClient.getCredentialsProvider().setCredentials(
							AuthScope.ANY,
							new UsernamePasswordCredentials(settings
									.getVcsUser(), settings.getVcsPassword()));
				}

				HttpGet request = new HttpGet(vcsUri);
				HttpResponse response = httpClient.execute(request);

				InputStream ins = response.getEntity().getContent();
				configValueVcs = IOUtils.toByteArray(ins);
				if (validator.isValid(configValueVcs)) {
					saveConfig(vcsUri, configValueVcs);

					boolean configurable = refresh();
					eventBus.fireEvent(new ConfigurationUpdatedEvent(
							configurable));

				} else {
					getView().showNotification(
							getMessage("portlet.crud.page.upload.invalid"),
							Notification.TYPE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				getView().showNotification(
						getMessage("portlet.crud.page.upload.vcs.error", e),
						Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Ermoeglicht Authentifizierung beim Zugriff auf SVN und Mercurial
	 * 
	 * @author polina.vinarski
	 */
	static class VcsAuthenticator extends Authenticator {

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.net.Authenticator#getPasswordAuthentication()
		 */
		public PasswordAuthentication getPasswordAuthentication() {
			return (new PasswordAuthentication(
					getMessage("portlet.crud.page.upload.vcs.user"),
					getMessage("portlet.crud.page.upload.vcs.password")
							.toCharArray()));
		}
	}

	/**
	 * Receiver Implementation für den Konfigurations-Upload.
	 * 
	 */
	public class ConfigurationReceiver implements Receiver {

		private static final long serialVersionUID = 1L;

		private String filename;
		private String mimetype;
		private ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

		/**
		 * 
		 * @see com.vaadin.ui.Upload.Receiver#receiveUpload(java.lang.String,
		 *      java.lang.String)
		 */
		@Override
		public OutputStream receiveUpload(String filename, String mimetype) {
			this.filename = filename;
			this.mimetype = mimetype;
			baos.reset();
			return baos;
		}

		public ByteArrayOutputStream getBaos() {
			return baos;
		}

		public String getMimetype() {
			return mimetype;
		}

		public String getFilename() {
			return filename;
		}

		public byte[] getConfigurationXML() {
			return baos.toByteArray();
		}

	}

	public void setReceiver(ConfigurationReceiver receiver) {
		this.receiver = receiver;
	}

	public boolean refresh() {
		Config config = configurationService.getPortletConfig(portletId,
				communityId);
		return refresh(config);
	}

	/**
	 * Aktualisiert die Ansicht. Die Links zum Ändern der Berechtigungen, werden
	 * ein- und ausgeblendet.
	 * 
	 * @param portletConfig
	 * @return <code>true</code>, falls Konfigurationstabs existieren
	 */
	public boolean refresh(Config portletConfig) {
		LOGGER.info("Refreshing portlet configuration view!");
		updatePortletRoles(portletConfig);
		updatePortletPreferences(portletConfig);
		updateAuthenticationPreferences(portletConfig);
		return (rolesView != null) || (prefsView != null) || (authView != null);
	}

	private void updatePortletRoles(Config portletConfig) {
		if (rolesView != null) {
			getView().removeTab(rolesView);
			rolesView = null;
		}
		if (portletConfig != null) {
			List<PortletRoleTO> roles = findPortletRoles(portletConfig);
			if (roles.size() > 0) {
				rolesView = new DefaultPortletRolesView(
						getMessage("portlet.crud.page.edit.securityHeader"));
				getView().displayTab(rolesView);
				rolesView.display(roles);
			}
		}
	}

	private List<PortletRoleTO> findPortletRoles(Config portletConfig) {
		List<PortletRoleTO> results = new LinkedList<PortletRoleTO>();
		RolesConfig rolesConfig = portletConfig.getPortletConfig().getRoles();
		if (rolesConfig != null) {
			for (RoleConfig config : rolesConfig.getRole()) {
				if (config.getPortalRole() == null) {
					String resourceId = PortletRole.createRoleResourceId(
							portletId, communityId, config.getName());
					Long primaryKey = portletConfig.getRoleResourceIDs().get(
							resourceId);
					results.add(new PortletRoleTO(config.getName(), primaryKey
							.toString()));
				}
			}
		}
		return results;
	}

	private void updatePortletPreferences(Config portletConfig) {
		if (prefsView != null) {
			getView().removeTab(prefsView);
			prefsView = null;
		}
		if (portletConfig != null) {
			List<PreferenceTO> preferences = findPortletPreferences(portletConfig);
			if (preferences.size() > 0) {
				prefsView = createPreferencesView(getMessage("portlet.crud.page.edit.preferencesHeader"));
				getView().displayTab(prefsView);
				prefsView.display(preferences);
			}
		}
	}

	private void updateAuthenticationPreferences(Config portletConfig) {
		if (authView != null) {
			getView().removeTab(authView);
			authView = null;
		}
		if (portletConfig != null) {
			List<PreferenceTO> preferences = findAuthenticationPreferences(portletConfig);
			if (preferences.size() > 0) {
				authView = createPreferencesView(getMessage("portlet.crud.page.edit.authenticationHeader"));
				getView().displayTab(authView);
				authView.display(preferences);
			}
		}
	}

	private PreferencesView createPreferencesView(String caption) {
		DefaultPreferencesView subView = new DefaultPreferencesView(caption);
		subView.setPresenter(new PreferencesPresenter(subView, eventBus));
		return subView;
	}

	private List<PreferenceTO> findPortletPreferences(Config config) {
		LinkedList<PreferenceTO> results = new LinkedList<PreferenceTO>();

		if (config != null && config.getPortletConfig() != null
				&& config.getPortletConfig().getPreferences() != null) {
			for (PreferenceConfig cfg : config.getPortletConfig()
					.getPreferences().getPreference()) {

				results.add(new PreferenceTO(cfg.getKey(), generateTitle(cfg),
						false, null));
			}
		}
		return results;
	}

	private String generateTitle(PreferenceConfig cfg) {
		return cfg.getTitle() == null ? cfg.getKey() : cfg.getTitle();
	}

	private List<PreferenceTO> findAuthenticationPreferences(
			Config portletConfig) {
		LinkedList<PreferenceTO> results = new LinkedList<PreferenceTO>();

		if (portletConfig != null && portletConfig.getPortletConfig() != null
				&& portletConfig.getPortletConfig().getAuthentication() != null) {
			List<AuthenticationRealmConfig> realms = portletConfig
					.getPortletConfig().getAuthentication().getRealm();
			for (AuthenticationRealmConfig realm : realms) {
				if (realm.getCredentials() != null) {
					CredentialsUsernameConfig username = realm.getCredentials()
							.getUsername();
					if (username != null && username.getPreferenceKey() != null) {
						results.add(new PreferenceTO(username
								.getPreferenceKey(), realm.getName()
								+ " Username", false, null));
					}
					CredentialsPasswordConfig password = realm.getCredentials()
							.getPassword();
					if (password != null && password.getPreferenceKey() != null) {
						results.add(new PreferenceTO(password
								.getPreferenceKey(), realm.getName()
								+ " Passwort", true, password
								.getEncryptionAlgorithm()));
					}
				}
			}
		}
		return results;
	}

	public void switchToAuthenticationPreferences() {
		if (authView != null) {
			getView().switchTo(authView);
		}
	}
}
