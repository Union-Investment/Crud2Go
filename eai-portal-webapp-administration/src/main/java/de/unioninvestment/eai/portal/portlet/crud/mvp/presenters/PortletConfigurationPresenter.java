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

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

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

import de.unioninvestment.eai.portal.portlet.crud.CrudPortletApplication;
import de.unioninvestment.eai.portal.portlet.crud.CrudPortletApplication.ConfigStatus;
import de.unioninvestment.eai.portal.portlet.crud.Settings;
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.mvp.events.ConfigurationUpdatedEvent;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.PortletConfigurationView;
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
		LiferayApplication app = LiferayApplication.getCurrentApplication();
		PortletRequest request = PortletApplication.getCurrentRequest();

		configurationService.storeConfigurationFile(fileName, configurationXml,
				app.getPortletId(), app.getCommunityId(),
				request.getRemoteUser());

		LOG.info("Saving config for portlet with portletId: "
				+ app.getPortletId() + " successfull.");
	}

	private void initStatus() {
		LiferayApplication app = LiferayApplication.getCurrentApplication();

		ConfigurationMetaData metaData = configurationService
				.getConfigurationMetaData(app.getPortletId(),
						app.getCommunityId());

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
				eventBus.fireEvent(new ConfigurationUpdatedEvent());

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
					eventBus.fireEvent(new ConfigurationUpdatedEvent());
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

	/**
	 * Aktualisiert die Ansicht. Die Links zum Ändern der Berechtigungen, werden
	 * ein- und ausgeblendet.
	 * 
	 * @param portletConfig
	 * @param status
	 * 
	 * @param portletDomain
	 *            Portlet Model
	 */
	public void refresh(ConfigStatus status, Config portletConfig,
			Portlet portletDomain) {
		if (portletDomain != null) {
			getView().displayRoles(portletDomain.getRoles());
		} else {
			getView().hideRoles();
		}
		if (portletConfig != null) {
			getView().displayAuthenticationPreferences(portletConfig);
		} else {
			getView().hideAuthenticationPreferences();
		}
	}

	@Override
	public void storePreferencesAndFireConfigChange() {
		try {
			CrudPortletApplication.getCurrentRequest().getPreferences().store();
			eventBus.fireEvent(new ConfigurationUpdatedEvent());

		} catch (Exception e) {
			LOGGER.error("Error storing preferences", e);
			getView().showNotification(
					getMessage("portlet.crud.error.storingPreferences"),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void switchToAuthenticationPreferences() {
		getView().switchToAuthenticationPreferences();
	}
}
